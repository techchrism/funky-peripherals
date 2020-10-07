package me.techchrism.funkyperipherals.client;

import net.minecraft.client.util.ModelIdentifier;
import net.minecraft.util.Identifier;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.model.ModelLoadingRegistry;
import net.fabricmc.loader.api.FabricLoader;

public class FunkyPeripheralsClient implements ClientModInitializer
{
    @Override
    public void onInitializeClient()
    {
        final boolean teleportersExist = FabricLoader.getInstance().isModLoaded("simpleteleporters");
        ModelLoadingRegistry.INSTANCE.registerAppender((resourceManager, consumer) ->
        {
            consumer.accept(new ModelIdentifier(new Identifier("funkyperipherals", "turtle_storage_upgrade_left"), "inventory"));
            consumer.accept(new ModelIdentifier(new Identifier("funkyperipherals", "turtle_storage_upgrade_right"), "inventory"));
            if(teleportersExist)
            {
                consumer.accept(new ModelIdentifier(new Identifier("funkyperipherals", "turtle_teleporter_upgrade_left"), "inventory"));
                consumer.accept(new ModelIdentifier(new Identifier("funkyperipherals", "turtle_teleporter_upgrade_right"), "inventory"));
            }
        });
    }
}
