package com.example.deadsun;

import com.example.deadsun.config.ModConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class DeadSunMod {
    public static final String MOD_ID = "deadsun";
    public static final Logger LOGGER = LoggerFactory.getLogger("deadsun");

    public static void init() {
        ModConfig.load();
        LOGGER.info("Dead Sun initialized");
    }
}
