package me.techchrism.funkyperipherals.turtle;

import dan200.computercraft.api.client.TransformedModel;
import dan200.computercraft.api.lua.LuaException;
import dan200.computercraft.api.lua.LuaValues;
import dan200.computercraft.api.peripheral.IPeripheral;
import dan200.computercraft.api.turtle.AbstractTurtleUpgrade;
import dan200.computercraft.api.turtle.ITurtleAccess;
import dan200.computercraft.api.turtle.TurtleSide;
import dan200.computercraft.api.turtle.TurtleUpgradeType;
import dan200.computercraft.shared.turtle.core.InteractDirection;
import me.techchrism.funkyperipherals.peripherals.TeleporterPeripheral;
import org.jetbrains.annotations.NotNull;
import party.lemons.simpleteleporters.block.entity.TeleporterBlockEntity;

import java.util.Optional;

import net.minecraft.block.entity.BlockEntity;
import net.minecraft.client.util.ModelIdentifier;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.registry.Registry;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

public class TurtleTeleporterUpgrade extends AbstractTurtleUpgrade
{
    @Environment(EnvType.CLIENT) private ModelIdentifier m_leftModel;
    @Environment(EnvType.CLIENT) private ModelIdentifier m_rightModel;
    
    public TurtleTeleporterUpgrade(Identifier id)
    {
        super(id, TurtleUpgradeType.PERIPHERAL, Registry.ITEM.get(new Identifier("simpleteleporters", "teleporter")));
    }
    
    @Override
    public IPeripheral createPeripheral(@NotNull ITurtleAccess turtle, @NotNull TurtleSide side)
    {
        return new TurtleTeleporterUpgrade.Peripheral(turtle);
    }
    
    @Environment (EnvType.CLIENT)
    private void loadModelLocations()
    {
        if(this.m_leftModel == null)
        {
            this.m_leftModel = new ModelIdentifier("funkyperipherals:turtle_teleporter_upgrade_left", "inventory");
            this.m_rightModel = new ModelIdentifier("funkyperipherals:turtle_teleporter_upgrade_right", "inventory");
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
    
    private static class Peripheral extends TeleporterPeripheral
    {
        private final ITurtleAccess turtle;
    
        Peripheral(ITurtleAccess turtle)
        {
            this.turtle = turtle;
        }
    
        @Override
        protected TeleporterBlockEntity getTeleporter(Optional<String> direction, int index) throws LuaException
        {
            InteractDirection dir = LuaValues.checkEnum(2, InteractDirection.class, direction.orElse("FORWARD"));
            BlockPos pos = turtle.getPosition().offset(dir.toWorldDir(turtle));
            BlockEntity entity = turtle.getWorld().getBlockEntity(pos);
            if(!(entity instanceof TeleporterBlockEntity))
            {
                throw new LuaException("Not targeting an teleporter block!");
            }
            return (TeleporterBlockEntity) entity;
        }
    
        @Override
        protected ITurtleAccess getTurtle()
        {
            return turtle;
        }
    }
}
