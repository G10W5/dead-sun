package com.example.deadsun.registry;

import com.example.deadsun.DeadSunMod;
import com.example.deadsun.item.LootBagItem;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.Identifier;
import net.minecraft.world.item.Item;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;

public class ModItems {
    public static Item LOOT_BAG;

    public static void register() {
        LOOT_BAG = register("loot_bag", props -> new LootBagItem(props.stacksTo(16)));
        DeadSunMod.LOGGER.info("Registered items for " + DeadSunMod.MOD_ID);
    }

    private static Item register(String name, java.util.function.Function<Item.Properties, Item> factory) {
        ResourceKey<Item> key = ResourceKey.create(
                Registries.ITEM,
                Identifier.fromNamespaceAndPath(DeadSunMod.MOD_ID, name));
        Item.Properties props = new Item.Properties().setId(key);
        Item item = factory.apply(props);
        Registry.register(BuiltInRegistries.ITEM, key, item);
        return item;
    }
}
