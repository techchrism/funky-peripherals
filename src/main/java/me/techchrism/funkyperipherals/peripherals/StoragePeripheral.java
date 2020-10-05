package me.techchrism.funkyperipherals.peripherals;

import dan200.computercraft.api.lua.ILuaContext;
import dan200.computercraft.api.lua.LuaException;
import dan200.computercraft.api.lua.LuaFunction;
import dan200.computercraft.api.lua.LuaValues;
import dan200.computercraft.api.peripheral.IPeripheral;
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
        InteractDirection dir = LuaValues.checkEnum(1, InteractDirection.class, direction.orElse("FORWARD"));
        BlockEntity entity = getBlockEntity(dir);
        if(!(entity instanceof Inventory))
        {
            throw new LuaException("Not targeting an inventory!");
        }
        
        Inventory inv = (Inventory) entity;
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
        table.put("itemName", storage.getDisplayedStack().getName().getString());
        table.put("itemID", Registry.ITEM.getId(storage.getDisplayedStack().getItem()).toString());
        return table;
    }
    
    protected abstract BlockEntity getBlockEntity(InteractDirection direction);
}
