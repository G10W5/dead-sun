package com.example.deadsun.neoforge;

import com.example.deadsun.DeadSunMod;
import com.example.deadsun.config.ModConfig;
import com.example.deadsun.item.LootBagItem;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.Identifier;
import net.minecraft.world.item.Item;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.common.Mod;
import net.neoforged.neoforge.registries.RegisterEvent;

@Mod("deadsun")
public class DeadSunNeoForge {
    public DeadSunNeoForge(IEventBus modEventBus) {
        ModConfig.load();
        modEventBus.addListener(this::onRegister);
        DeadSunMod.LOGGER.info("Dead Sun NeoForge initialized");
    }

    private void onRegister(RegisterEvent event) {
        event.register(Registries.ITEM, helper -> {
            com.example.deadsun.registry.ModItems.register();
        });
    }
}
