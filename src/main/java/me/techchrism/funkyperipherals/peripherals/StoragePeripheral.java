package me.techchrism.funkyperipherals.peripherals;

import dan200.computercraft.api.lua.ILuaContext;
import dan200.computercraft.api.lua.LuaException;
import dan200.computercraft.api.lua.LuaFunction;
import dan200.computercraft.api.lua.LuaValues;
import dan200.computercraft.api.peripheral.IPeripheral;
import dan200.computercraft.api.turtle.ITurtleAccess;
import dan200.computercraft.api.turtle.TurtleCommandResult;
import dan200.computercraft.shared.turtle.core.InteractDirection;
import org.jetbrains.annotations.NotNull;
import techreborn.blockentity.storage.item.StorageUnitBaseBlockEntity;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import net.minecraft.block.entity.BlockEntity;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.ItemStack;
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
    
    @LuaFunction(mainThread = true)
    public final boolean isStorage(ILuaContext context, Optional<String> direction) throws LuaException
    {
        InteractDirection dir = LuaValues.checkEnum(1, InteractDirection.class, direction.orElse("FORWARD"));
        BlockEntity entity = getBlockEntity(dir);
        return entity instanceof Inventory;
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
    
    protected Inventory getInventory(Optional<String> direction, int index) throws LuaException
    {
        InteractDirection dir = LuaValues.checkEnum(2, InteractDirection.class, direction.orElse("FORWARD"));
        BlockEntity entity = getBlockEntity(dir);
        if(!(entity instanceof Inventory))
        {
            throw new LuaException("Not targeting an inventory!");
        }
        return (Inventory) entity;
    }
    
    private Object[] generateTransferResult(int count, String message)
    {
        return new Object[]{count, message};
    }
    
    @LuaFunction(mainThread = true)
    public final Object[] transferFrom(ILuaContext context, int from, Optional<Integer> to, Optional<Integer> limitOption, Optional<String> direction) throws LuaException
    {
        Inventory inv = getInventory(direction, 2);
        ITurtleAccess turtle = getTurtle();
        from--;
        int toSlot = to.orElse(turtle.getSelectedSlot() + 1) - 1;
        if(from < 0 || from >= inv.size())
        {
            return generateTransferResult(0, "Invalid \"from\" slot");
        }
        if(toSlot < 0 || toSlot >= turtle.getInventory().size())
        {
            return generateTransferResult(0, "Invalid \"to\" slot");
        }
        ItemStack fromStack = inv.getStack(from);
        ItemStack toStack = turtle.getInventory().getStack(toSlot);
        if(!toStack.isEmpty() && !toStack.isItemEqual(fromStack))
        {
            return generateTransferResult(0, "Slot already occupied");
        }
        
        int limit = limitOption.orElse(inv.getStack(from).getCount());
        
        // Either use the limit as the amount, the "from" stack size, or
        // the max amount until the "to" stack is maxed out, whichever is smallest
        int transferAmount = Math.min(Math.min(
                limit,
                inv.getStack(from).getMaxCount() - turtle.getInventory().getStack(toSlot).getCount()),
                inv.getStack(from).getCount());
    
        if(transferAmount < 1)
        {
            return generateTransferResult(0, "Transferred no items");
        }
        
        if(turtle.getInventory().getStack(toSlot).isItemEqual(inv.getStack(from)))
        {
            turtle.getInventory().getStack(toSlot).increment(transferAmount);
        }
        else
        {
            turtle.getInventory().setStack(toSlot, fromStack.copy());
            turtle.getInventory().getStack(toSlot).setCount(transferAmount);
        }
        inv.getStack(from).decrement(transferAmount);
        
        return generateTransferResult(transferAmount, "Transferred items");
    }
    
    protected abstract BlockEntity getBlockEntity(InteractDirection direction);
    
    protected abstract ITurtleAccess getTurtle();
}
