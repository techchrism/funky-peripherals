package me.techchrism.funkyperipherals.turtle;

import dan200.computercraft.api.client.TransformedModel;
import dan200.computercraft.api.lua.ILuaContext;
import dan200.computercraft.api.lua.LuaException;
import dan200.computercraft.api.lua.LuaFunction;
import dan200.computercraft.api.lua.LuaValues;
import dan200.computercraft.api.peripheral.IPeripheral;
import dan200.computercraft.api.turtle.*;
import dan200.computercraft.shared.turtle.core.InteractDirection;
import dan200.computercraft.shared.util.InventoryUtil;
import me.techchrism.funkyperipherals.FunkyPeripherals;
import me.techchrism.funkyperipherals.peripherals.StoragePeripheral;
import org.jetbrains.annotations.NotNull;
import org.squiddev.cobalt.Lua;

import java.util.Map;
import java.util.Optional;

import net.minecraft.block.entity.BlockEntity;
import net.minecraft.client.util.ModelIdentifier;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

public class TurtleStorageUpgrade extends AbstractTurtleUpgrade
{
    @Environment (EnvType.CLIENT) private ModelIdentifier m_leftModel;
    @Environment (EnvType.CLIENT) private ModelIdentifier m_rightModel;
    
    public TurtleStorageUpgrade(Identifier id)
    {
        super(id, TurtleUpgradeType.PERIPHERAL, FunkyPeripherals.STORAGE_PERIPHERAL);
    }
    
    @Override
    public IPeripheral createPeripheral(@NotNull ITurtleAccess turtle, @NotNull TurtleSide side)
    {
        return new Peripheral(turtle);
    }
    
    @Environment (EnvType.CLIENT)
    private void loadModelLocations()
    {
        if(this.m_leftModel == null)
        {
            this.m_leftModel = new ModelIdentifier("funkyperipherals:turtle_storage_upgrade_left", "inventory");
            this.m_rightModel = new ModelIdentifier("funkyperipherals:turtle_storage_upgrade_right", "inventory");
        }
    }
    
    @Override
    @Environment(EnvType.CLIENT)
    public @NotNull TransformedModel getModel(ITurtleAccess turtle, @NotNull TurtleSide side)
    {
        loadModelLocations();
        return TransformedModel.of(side == TurtleSide.LEFT ? this.m_leftModel : this.m_rightModel);
    }
    
    @Override
    public void update(@NotNull ITurtleAccess turtle, @NotNull TurtleSide side)
    {
    
    }
    
    private static class Peripheral extends StoragePeripheral
    {
        private final ITurtleAccess turtle;
    
        Peripheral(ITurtleAccess turtle)
        {
            this.turtle = turtle;
        }
    
        @Override
        protected BlockEntity getBlockEntity(InteractDirection direction)
        {
            BlockPos pos = turtle.getPosition().offset(direction.toWorldDir(turtle));
            return turtle.getWorld().getBlockEntity(pos);
        }
    
        @Override
        protected ITurtleAccess getTurtle()
        {
            return turtle;
        }
    }
}
