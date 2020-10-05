package me.techchrism.funkyperipherals;

import dan200.computercraft.api.ComputerCraftAPI;
import me.techchrism.funkyperipherals.turtle.TurtleStorageUpgrade;

import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;

import net.fabricmc.api.ModInitializer;

public class FunkyPeripherals implements ModInitializer
{
    public static final Item STORAGE_PERIPHERAL = new Item(new Item.Settings().group(ItemGroup.MISC));
    
    @Override
    public void onInitialize()
    {
        Registry.register(Registry.ITEM, new Identifier("funkyperipherals", "storage_peripheral"), STORAGE_PERIPHERAL);
    
        ComputerCraftAPI.registerTurtleUpgrade(new TurtleStorageUpgrade(new Identifier("funkyperipherals", "storage_upgrade")));
    }
}
