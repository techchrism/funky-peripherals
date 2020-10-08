package me.techchrism.funkyperipherals.peripherals;

import dan200.computercraft.api.lua.ILuaContext;
import dan200.computercraft.api.lua.LuaException;
import dan200.computercraft.api.lua.LuaFunction;
import dan200.computercraft.api.lua.LuaValues;
import dan200.computercraft.api.peripheral.IPeripheral;
import dan200.computercraft.api.turtle.ITurtleAccess;
import dan200.computercraft.shared.turtle.core.InteractDirection;
import me.techchrism.funkyperipherals.util.InventoryTransferResult;
import me.techchrism.funkyperipherals.util.InventoryUtil;
import org.jetbrains.annotations.NotNull;
import techreborn.blockentity.storage.item.StorageUnitBaseBlockEntity;

import java.util.*;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.InventoryProvider;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.registry.Registry;

public abstract class StoragePeripheral implements IPeripheral
{
    @Override
    public @NotNull String getType()
    {
        return "storage";
    }
    
    @Override
    public boolean equals(IPeripheral other)
    {
        return other != null && this.getType().equals(other.getType());
    }
    
    protected Inventory getInventory(Optional<String> direction, int index) throws LuaException
    {
        InteractDirection dir = LuaValues.checkEnum(2, InteractDirection.class, direction.orElse("FORWARD"));
        BlockEntity entity = getBlockEntity(dir);
        if(!(entity instanceof Inventory))
        {
            Block block = getBlock(dir);
            if(!(block instanceof InventoryProvider))
            {
                throw new LuaException("Not targeting an inventory!");
            }
            InventoryProvider provider = (InventoryProvider) block;
            BlockPos pos = getTurtle().getPosition().offset(dir.toWorldDir(getTurtle()));
            return provider.getInventory(getBlockState(dir), getTurtle().getWorld(), pos);
        }
        return (Inventory) entity;
    }
    
    @LuaFunction(mainThread = true)
    public final boolean isStorage(ILuaContext context, Optional<String> direction) throws LuaException
    {
        try
        {
            getInventory(direction, 1);
            return true;
        }
        catch(Exception ignored) {}
        return false;
    }
    
    @LuaFunction(mainThread = true)
    public final Map<String, Object> getInfo(ILuaContext context, Optional<String> direction) throws LuaException
    {
        Inventory inv = getInventory(direction, 1);
        HashMap<String, Object> table = new HashMap<>();
        table.put("size", inv.size());
        table.put("empty", inv.isEmpty());
        table.put("maxCountPerStack", inv.getMaxCountPerStack());
        int itemCount = 0;
        int slotsUsed = 0;
        for(int i = 0; i < inv.size(); i++)
        {
            ItemStack item = inv.getStack(i);
            if(!item.isEmpty())
            {
                itemCount += item.getCount();
                slotsUsed++;
            }
        }
        table.put("itemCount", itemCount);
        table.put("slotsUsed", slotsUsed);
        return table;
    }
    
    @LuaFunction(mainThread = true)
    public final boolean isTRStorageUnit(ILuaContext context, Optional<String> direction) throws LuaException
    {
        InteractDirection dir = LuaValues.checkEnum(1, InteractDirection.class, direction.orElse("FORWARD"));
        BlockEntity entity = getBlockEntity(dir);
        return entity instanceof StorageUnitBaseBlockEntity;
    }
    
    @LuaFunction(mainThread = true)
    public final Map<String, Object> getTRStorageUnitInfo(ILuaContext context, Optional<String> direction) throws LuaException
    {
        InteractDirection dir = LuaValues.checkEnum(1, InteractDirection.class, direction.orElse("FORWARD"));
        BlockEntity entity = getBlockEntity(dir);
        if(!(entity instanceof StorageUnitBaseBlockEntity))
        {
            throw new LuaException("Not a storage unit");
        }
        StorageUnitBaseBlockEntity storage = (StorageUnitBaseBlockEntity) entity;
        HashMap<String, Object> table = new HashMap<>();
        table.put("stored", storage.getCurrentCapacity());
        table.put("capacity", storage.getMaxCapacity());
        table.put("locked", storage.isLocked());
        table.put("full", storage.isFull());
        table.put("empty", storage.isEmpty());
        table.put("item", serializeItemStack(storage.getDisplayedStack()));
        return table;
    }
    
    protected Map<String, Object> serializeItemStack(@NotNull ItemStack stack)
    {
        HashMap<String, Object> table = new HashMap<>();
        table.put("name", stack.getItem().getName().getString());
        table.put("id", Registry.ITEM.getId(stack.getItem()).toString());
        table.put("count", stack.getCount());
        if(stack.isDamageable())
        {
            table.put("damage", stack.getDamage());
            table.put("maxDamage", stack.getMaxDamage());
        }
        if(stack.hasCustomName())
        {
            table.put("customName", stack.getName().getString());
        }
        if(stack.hasTag())
        {
            table.put("nbt", stack.getTag().asString());
        }
        return table;
    }
    
    @LuaFunction(mainThread = true)
    public final Map<String, Object> getItemInSlot(ILuaContext context, int slot, Optional<String> direction) throws LuaException
    {
        Inventory inv = getInventory(direction, 2);
        slot--;
        if(slot < 0 || slot >= inv.size())
        {
            throw new LuaException("Invalid slot");
        }
        return serializeItemStack(inv.getStack(slot));
    }
    
    private Object[] generateTransferResult(int count, String message)
    {
        return new Object[]{count, message};
    }
    
    @LuaFunction(mainThread = true)
    public final Object[] transferFrom(ILuaContext context, int from, Optional<Integer> to, Optional<Integer> limitOption, Optional<String> direction) throws LuaException
    {
        Inventory inv = getInventory(direction, 4);
        ITurtleAccess turtle = getTurtle();
        from--;
        int toSlot = to.orElse(turtle.getSelectedSlot() + 1) - 1;
        InventoryTransferResult result = InventoryUtil.transfer(inv, from, turtle.getInventory(), toSlot, limitOption);
        return new Object[]{result.getAmount(), result.getMessage()};
    }
    
    @LuaFunction(mainThread = true)
    public final Object[] transferTo(ILuaContext context, int to, Optional<Integer> from, Optional<Integer> limitOption, Optional<String> direction) throws LuaException
    {
        Inventory inv = getInventory(direction, 4);
        ITurtleAccess turtle = getTurtle();
        to--;
        int fromSlot = from.orElse(turtle.getSelectedSlot() + 1) - 1;
        InventoryTransferResult result = InventoryUtil.transfer(turtle.getInventory(), fromSlot, inv, to, limitOption);
        return new Object[]{result.getAmount(), result.getMessage()};
    }
    
    @LuaFunction(mainThread = true)
    public final List<Map<String, Object>> getInventoryContents(ILuaContext context, Optional<String> direction) throws LuaException
    {
        Inventory inv = getInventory(direction, 1);
        List<Map<String, Object>> contents = new ArrayList<>(inv.size());
        for(int i = 0; i < inv.size(); i++)
        {
            contents.add(serializeItemStack(inv.getStack(i)));
        }
        return contents;
    }
    
    protected abstract BlockEntity getBlockEntity(InteractDirection direction);
    
    protected abstract Block getBlock(InteractDirection direction);
    
    protected abstract BlockState getBlockState(InteractDirection direction);
    
    protected abstract ITurtleAccess getTurtle();
}
