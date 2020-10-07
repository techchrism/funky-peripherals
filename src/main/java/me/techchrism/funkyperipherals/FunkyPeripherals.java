package me.techchrism.funkyperipherals;

import dan200.computercraft.api.ComputerCraftAPI;
import me.techchrism.funkyperipherals.turtle.TurtleStorageUpgrade;
import me.techchrism.funkyperipherals.turtle.TurtleTeleporterUpgrade;

import net.minecraft.client.util.ModelIdentifier;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;

import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.client.model.ModelLoadingRegistry;
import net.fabricmc.loader.api.FabricLoader;

public class FunkyPeripherals implements ModInitializer
{
    public static final Item STORAGE_PERIPHERAL = new Item(new Item.Settings().group(ItemGroup.MISC));
    
    @Override
    public void onInitialize()
    {
        boolean teleportersExist = FabricLoader.getInstance().isModLoaded("simpleteleporters");
        
        Registry.register(Registry.ITEM, new Identifier("funkyperipherals", "storage_peripheral"), STORAGE_PERIPHERAL);
        ComputerCraftAPI.registerTurtleUpgrade(new TurtleStorageUpgrade(new Identifier("funkyperipherals", "storage_upgrade")));
        if(teleportersExist)
        {
            ComputerCraftAPI.registerTurtleUpgrade(new TurtleTeleporterUpgrade(new Identifier("funkyperipherals", "teleporter_upgrade")));
        }
        
        boolean finalTeleportersExist = teleportersExist;
        ModelLoadingRegistry.INSTANCE.registerAppender((resourceManager, consumer) ->
        {
            consumer.accept(new ModelIdentifier(new Identifier("funkyperipherals", "turtle_storage_upgrade_left"), "inventory"));
            consumer.accept(new ModelIdentifier(new Identifier("funkyperipherals", "turtle_storage_upgrade_right"), "inventory"));
            if(finalTeleportersExist)
            {
                consumer.accept(new ModelIdentifier(new Identifier("funkyperipherals", "turtle_teleporter_upgrade_left"), "inventory"));
                consumer.accept(new ModelIdentifier(new Identifier("funkyperipherals", "turtle_teleporter_upgrade_right"), "inventory"));
            }
        });
    }
}
