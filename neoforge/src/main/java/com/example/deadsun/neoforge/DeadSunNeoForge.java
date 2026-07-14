package com.example.deadsun.neoforge;

import com.example.deadsun.DeadSunMod;
import com.example.deadsun.config.ModConfig;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.Mod;
import net.neoforged.neoforge.registries.RegisterEvent;

@Mod("deadsun")
public class DeadSunNeoForge {
    public DeadSunNeoForge(ModContainer container, IEventBus modEventBus) {
        ModConfig.load();
        container.registerExtensionPoint(
                net.neoforged.neoforge.client.gui.IConfigScreenFactory.class,
                (mc, parent) -> ConfigScreenBuilder.buildScreen(parent)
        );
        modEventBus.addListener(this::onRegister);
        DeadSunMod.LOGGER.info("Dead Sun NeoForge initialized");
    }

    private void onRegister(RegisterEvent event) {
        event.register(net.minecraft.core.registries.Registries.ITEM, helper -> {
            com.example.deadsun.registry.ModItems.register();
        });
    }
}
