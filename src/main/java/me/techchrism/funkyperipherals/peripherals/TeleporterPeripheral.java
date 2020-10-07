package me.techchrism.funkyperipherals.peripherals;

import dan200.computercraft.api.lua.ILuaContext;
import dan200.computercraft.api.lua.LuaException;
import dan200.computercraft.api.lua.LuaFunction;
import dan200.computercraft.api.lua.LuaValues;
import dan200.computercraft.api.peripheral.IPeripheral;
import dan200.computercraft.api.turtle.ITurtleAccess;
import dan200.computercraft.shared.turtle.core.InteractDirection;
import org.jetbrains.annotations.NotNull;
import party.lemons.simpleteleporters.block.entity.TeleporterBlockEntity;
import party.lemons.simpleteleporters.init.SimpleTeleportersItems;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.util.math.BlockPos;

public abstract class TeleporterPeripheral implements IPeripheral
{
    @Override
    public @NotNull String getType()
    {
        return "teleporter";
    }
    
    @Override
    public boolean equals(IPeripheral other)
    {
        return other != null && this.getType().equals(other.getType());
    }
    
    @LuaFunction(mainThread = true)
    public final boolean isTeleporter(ILuaContext context, Optional<String> direction)
    {
        try
        {
            getTeleporter(direction, 1);
        }
        catch(Exception e)
        {
            return false;
        }
        return true;
    }
    
    @LuaFunction(mainThread = true)
    public final boolean hasCrystal(ILuaContext context, Optional<String> direction) throws LuaException
    {
        return getTeleporter(direction, 1).hasCrystal();
    }
    
    @LuaFunction(mainThread = true)
    public final Map<String, Integer> getTeleportPosition(ILuaContext context, Optional<String> direction) throws LuaException
    {
        BlockPos pos = getTeleporter(direction, 1).getTeleportPosition();
        if(pos == null)
        {
            return null;
        }
        Map<String, Integer> table = new HashMap<>();
        table.put("x", pos.getX());
        table.put("y", pos.getY());
        table.put("z", pos.getZ());
        return table;
    }
    
    @LuaFunction(mainThread = true)
    public final boolean getCrystal(ILuaContext context, Optional<String> direction) throws LuaException
    {
        ITurtleAccess turtle = getTurtle();
        if(!turtle.getInventory().getStack(turtle.getSelectedSlot()).isEmpty())
        {
            return false;
        }
        TeleporterBlockEntity tele = getTeleporter(direction, 1);
        if(tele.hasCrystal())
        {
            turtle.getInventory().setStack(turtle.getSelectedSlot(), tele.getCrystal().copy());
            tele.setCrystal(ItemStack.EMPTY);
            return true;
        }
        return false;
    }
    
    @LuaFunction(mainThread = true)
    public final boolean setCrystal(ILuaContext context, Optional<String> direction) throws LuaException
    {
        ITurtleAccess turtle = getTurtle();
        if(!turtle.getInventory().getStack(turtle.getSelectedSlot()).getItem().equals(SimpleTeleportersItems.TELE_CRYSTAL))
        {
            return false;
        }
        TeleporterBlockEntity tele = getTeleporter(direction, 1);
        if(tele.hasCrystal())
        {
            ItemStack oldCrystal = tele.getCrystal().copy();
            tele.setCrystal(turtle.getInventory().getStack(turtle.getSelectedSlot()).copy());
            turtle.getInventory().setStack(turtle.getSelectedSlot(), oldCrystal);
        }
        else
        {
            tele.setCrystal(turtle.getInventory().getStack(turtle.getSelectedSlot()).copy());
            turtle.getInventory().setStack(turtle.getSelectedSlot(), ItemStack.EMPTY);
        }
        return true;
    }
    
    @LuaFunction(mainThread = true)
    public final void setCrystalPosition(ILuaContext context, Optional<String> direction) throws LuaException
    {
        ITurtleAccess turtle = getTurtle();
        if(!turtle.getInventory().getStack(turtle.getSelectedSlot()).getItem().equals(SimpleTeleportersItems.TELE_CRYSTAL))
        {
            throw new LuaException("Teleportation crystal not selected");
        }
        
        BlockPos pos;
        if(direction.isPresent())
        {
            InteractDirection dir = LuaValues.checkEnum(2, InteractDirection.class, direction.get());
            pos = turtle.getPosition().offset(dir.toWorldDir(turtle));
        }
        else
        {
            pos = turtle.getPosition();
        }
    
        ItemStack crystal = turtle.getInventory().getStack(turtle.getSelectedSlot());
        CompoundTag tags = crystal.getTag();
        if(tags == null)
        {
            crystal.setTag(new CompoundTag());
            tags = crystal.getTag();
        }
        tags.putInt("x", pos.getX());
        tags.putInt("y", pos.getY());
        tags.putInt("z", pos.getZ());
        tags.putString("dim", turtle.getWorld().getDimensionRegistryKey().getValue().toString());
        tags.putFloat("direction", turtle.getDirection().asRotation());
    }
    
    protected abstract TeleporterBlockEntity getTeleporter(Optional<String> direction, int index) throws LuaException;
    protected abstract ITurtleAccess getTurtle();
}
