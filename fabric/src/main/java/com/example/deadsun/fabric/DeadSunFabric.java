package com.example.deadsun.fabric;

import com.example.deadsun.DeadSunMod;
import com.example.deadsun.registry.ModItems;
import net.fabricmc.api.ModInitializer;

public class DeadSunFabric implements ModInitializer {
    @Override
    public void onInitialize() {
        DeadSunMod.init();
        ModItems.register();
    }
}
