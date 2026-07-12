package com.example.deadsun.neoforge;

import com.example.deadsun.DeadSunMod;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.common.Mod;

@Mod("deadsun")
public class DeadSunNeoForge {
    public DeadSunNeoForge(IEventBus modEventBus) {
        DeadSunMod.init();
    }
}
