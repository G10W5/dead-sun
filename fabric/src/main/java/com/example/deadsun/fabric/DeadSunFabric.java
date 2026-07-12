package com.example.deadsun.fabric;

import com.example.deadsun.DeadSunMod;
import net.fabricmc.api.ModInitializer;

public class DeadSunFabric implements ModInitializer {
    @Override
    public void onInitialize() {
        DeadSunMod.init();
    }
}
