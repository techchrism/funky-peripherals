package me.techchrism.funkyperipherals;

import dan200.computercraft.api.ComputerCraftAPI;
import me.techchrism.funkyperipherals.turtle.TurtleStorageUpgrade;
import me.techchrism.funkyperipherals.turtle.TurtleTeleporterUpgrade;

import java.util.concurrent.atomic.AtomicBoolean;

import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;

import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.event.registry.RegistryEntryAddedCallback;
import net.fabricmc.loader.api.FabricLoader;

public class FunkyPeripherals implements ModInitializer
{
    public static final Item STORAGE_PERIPHERAL = new Item(new Item.Settings().group(ItemGroup.MISC));
    
    @Override
    public void onInitialize()
    {
        final boolean teleportersExist = FabricLoader.getInstance().isModLoaded("simpleteleporters");
        
        Registry.register(Registry.ITEM, new Identifier("funkyperipherals", "storage_peripheral"), STORAGE_PERIPHERAL);
        ComputerCraftAPI.registerTurtleUpgrade(new TurtleStorageUpgrade(new Identifier("funkyperipherals", "storage_upgrade")));
        if(teleportersExist)
        {
            AtomicBoolean found = new AtomicBoolean(
                    !Registry.ITEM.get(new Identifier("simpleteleporters", "teleporter"))
                            .equals(Registry.ITEM.get((Identifier) null)));
            if(!found.get())
            {
                RegistryEntryAddedCallback.event(Registry.ITEM).register((i, identifier, item) ->
                {
                    if(!found.get() && identifier.equals(new Identifier("simpleteleporters", "teleporter")))
                    {
                        found.set(true);
                        addTeleporterIntegration();
                    }
                });
            }
            else
            {
                addTeleporterIntegration();
            }
        }
    }
    
    private void addTeleporterIntegration()
    {
        ComputerCraftAPI.registerTurtleUpgrade(new TurtleTeleporterUpgrade(new Identifier("funkyperipherals", "teleporter_upgrade")));
    }
}
