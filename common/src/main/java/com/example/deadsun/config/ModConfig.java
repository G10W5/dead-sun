package com.example.deadsun.config;

import com.example.deadsun.DeadSunMod;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

public class ModConfig {
    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();
    private static ModConfig INSTANCE = new ModConfig();

    private int torchRadius = 8;
    private float lootBagDropChance = 0.05f;
    private int spawnDensity = 1;
    private float enderPearlChance = 0.30f;
    private int spawnRadius = 32;
    private int minSpawnDistance = 24;
    private int ticksBetweenSpawns = 10;
    private int maxBlockLight = -1;
    private boolean requireOpenSky = false;

    public int getTorchRadius() {
        return torchRadius;
    }

    public void setTorchRadius(int torchRadius) {
        this.torchRadius = torchRadius;
    }

    public float getLootBagDropChance() {
        return lootBagDropChance;
    }

    public void setLootBagDropChance(float lootBagDropChance) {
        this.lootBagDropChance = lootBagDropChance;
    }

    public int getSpawnDensity() {
        return spawnDensity;
    }

    public void setSpawnDensity(int spawnDensity) {
        this.spawnDensity = spawnDensity;
    }

    public float getEnderPearlChance() {
        return enderPearlChance;
    }

    public void setEnderPearlChance(float enderPearlChance) {
        this.enderPearlChance = enderPearlChance;
    }

    public void setSpawnRadius(int spawnRadius) { this.spawnRadius = spawnRadius; }
    public void setMinSpawnDistance(int minSpawnDistance) { this.minSpawnDistance = minSpawnDistance; }
    public void setTicksBetweenSpawns(int ticksBetweenSpawns) { this.ticksBetweenSpawns = ticksBetweenSpawns; }
    public void setMaxBlockLight(int maxBlockLight) { this.maxBlockLight = maxBlockLight; }
    public void setRequireOpenSky(boolean requireOpenSky) { this.requireOpenSky = requireOpenSky; }

    public static void load() {
        Path configDir = Path.of("config");
        Path configFile = configDir.resolve("deadsun.json");

        try {
            if (Files.exists(configFile)) {
                String json = Files.readString(configFile);
                INSTANCE = GSON.fromJson(json, ModConfig.class);
                if (INSTANCE == null) INSTANCE = new ModConfig();
                DeadSunMod.LOGGER.info("Loaded Dead Sun config");
            } else {
                Files.createDirectories(configDir);
                String json = GSON.toJson(INSTANCE);
                Files.writeString(configFile, json);
                DeadSunMod.LOGGER.info("Created default Dead Sun config");
            }
        } catch (IOException e) {
            DeadSunMod.LOGGER.error("Failed to load config, using defaults", e);
            INSTANCE = new ModConfig();
        }
    }

    public static void save() {
        Path configDir = Path.of("config");
        Path configFile = configDir.resolve("deadsun.json");
        try {
            Files.createDirectories(configDir);
            String json = GSON.toJson(INSTANCE);
            Files.writeString(configFile, json);
        } catch (IOException e) {
            DeadSunMod.LOGGER.error("Failed to save config", e);
        }
    }

    public static ModConfig getInstance() {
        return INSTANCE;
    }

    public static int getTorchRadiusValue() {
        return INSTANCE.getTorchRadius();
    }

    public static float getLootBagDropChanceValue() {
        return INSTANCE.getLootBagDropChance();
    }

    public static int getSpawnDensityValue() {
        return INSTANCE.getSpawnDensity();
    }

    public static float getEnderPearlChanceValue() {
        return INSTANCE.getEnderPearlChance();
    }

    public int getSpawnRadius() { return spawnRadius; }
    public int getMinSpawnDistance() { return minSpawnDistance; }
    public int getTicksBetweenSpawns() { return ticksBetweenSpawns; }
    public int getMaxBlockLight() { return maxBlockLight; }
    public boolean isRequireOpenSky() { return requireOpenSky; }

    public static int getSpawnRadiusValue() { return INSTANCE.getSpawnRadius(); }
    public static int getMinSpawnDistanceValue() { return INSTANCE.getMinSpawnDistance(); }
    public static int getTicksBetweenSpawnsValue() { return INSTANCE.getTicksBetweenSpawns(); }
    public static int getMaxBlockLightValue() { return INSTANCE.getMaxBlockLight(); }
    public static boolean isRequireOpenSkyValue() { return INSTANCE.isRequireOpenSky(); }
}
