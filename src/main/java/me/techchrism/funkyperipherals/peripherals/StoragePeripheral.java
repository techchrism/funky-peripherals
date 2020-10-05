package me.techchrism.funkyperipherals.peripherals;

import dan200.computercraft.api.lua.ILuaContext;
import dan200.computercraft.api.lua.LuaFunction;
import dan200.computercraft.api.peripheral.IPeripheral;
import dan200.computercraft.shared.turtle.core.InteractDirection;
import org.jetbrains.annotations.NotNull;

import java.util.Optional;

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
    public final boolean isStorage(ILuaContext context, Optional<InteractDirection> direction)
    {
        InteractDirection dir = direction.orElse(InteractDirection.FORWARD);
        return checkIfIsStorage(dir);
    }
    
    protected abstract boolean checkIfIsStorage(InteractDirection direction);
}
