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
    private int spawnDensity = 50;
    private float enderPearlChance = 0.30f;
    private int spawnRadius = 128;
    private int minSpawnDistance = 32;
    private int ticksBetweenSpawns = 10;
    private int maxBlockLight = -1;
    private boolean requireOpenSky = false;
    private boolean zombieLeap = true;
    private boolean zombiePileUp = true;
    private float leapStrength = 0.6f;
    private float leapHeight = 0.4f;
    private int maxZombiesOverworld = 50;
    private int maxZombiesEnd = 150;
    private int maxZombiesNether = 100;
    private boolean groupSpawning = true;
    private int minGroupSize = 2;
    private int maxGroupSize = 8;

    public int getTorchRadius() { return torchRadius; }
    public float getLootBagDropChance() { return lootBagDropChance; }
    public int getSpawnDensity() { return spawnDensity; }
    public float getEnderPearlChance() { return enderPearlChance; }
    public int getSpawnRadius() { return spawnRadius; }
    public int getMinSpawnDistance() { return minSpawnDistance; }
    public int getTicksBetweenSpawns() { return ticksBetweenSpawns; }
    public int getMaxBlockLight() { return maxBlockLight; }
    public boolean isRequireOpenSky() { return requireOpenSky; }
    public boolean isZombieLeap() { return zombieLeap; }
    public boolean isZombiePileUp() { return zombiePileUp; }
    public float getLeapStrength() { return leapStrength; }
    public float getLeapHeight() { return leapHeight; }
    public int getMaxZombiesOverworld() { return maxZombiesOverworld; }
    public int getMaxZombiesEnd() { return maxZombiesEnd; }
    public int getMaxZombiesNether() { return maxZombiesNether; }
    public boolean isGroupSpawning() { return groupSpawning; }
    public int getMinGroupSize() { return minGroupSize; }
    public int getMaxGroupSize() { return maxGroupSize; }

    public void setTorchRadius(int v) { this.torchRadius = v; }
    public void setLootBagDropChance(float v) { this.lootBagDropChance = v; }
    public void setSpawnDensity(int v) { this.spawnDensity = v; }
    public void setEnderPearlChance(float v) { this.enderPearlChance = v; }
    public void setSpawnRadius(int v) { this.spawnRadius = v; }
    public void setMinSpawnDistance(int v) { this.minSpawnDistance = v; }
    public void setTicksBetweenSpawns(int v) { this.ticksBetweenSpawns = v; }
    public void setMaxBlockLight(int v) { this.maxBlockLight = v; }
    public void setRequireOpenSky(boolean v) { this.requireOpenSky = v; }
    public void setZombieLeap(boolean v) { this.zombieLeap = v; }
    public void setZombiePileUp(boolean v) { this.zombiePileUp = v; }
    public void setLeapStrength(float v) { this.leapStrength = v; }
    public void setLeapHeight(float v) { this.leapHeight = v; }
    public void setMaxZombiesOverworld(int v) { this.maxZombiesOverworld = v; }
    public void setMaxZombiesEnd(int v) { this.maxZombiesEnd = v; }
    public void setMaxZombiesNether(int v) { this.maxZombiesNether = v; }
    public void setGroupSpawning(boolean v) { this.groupSpawning = v; }
    public void setMinGroupSize(int v) { this.minGroupSize = v; }
    public void setMaxGroupSize(int v) { this.maxGroupSize = v; }

    public static int getSpawnDensityValue() { return INSTANCE.spawnDensity; }
    public static int getSpawnRadiusValue() { return INSTANCE.spawnRadius; }
    public static int getMinSpawnDistanceValue() { return INSTANCE.minSpawnDistance; }
    public static int getTicksBetweenSpawnsValue() { return INSTANCE.ticksBetweenSpawns; }
    public static int getMaxBlockLightValue() { return INSTANCE.maxBlockLight; }
    public static boolean isRequireOpenSkyValue() { return INSTANCE.requireOpenSky; }
    public static int getTorchRadiusValue() { return INSTANCE.torchRadius; }
    public static float getLootBagDropChanceValue() { return INSTANCE.lootBagDropChance; }
    public static float getEnderPearlChanceValue() { return INSTANCE.enderPearlChance; }
    public static boolean isZombieLeapValue() { return INSTANCE.zombieLeap; }
    public static boolean isZombiePileUpValue() { return INSTANCE.zombiePileUp; }
    public static float getLeapStrengthValue() { return INSTANCE.leapStrength; }
    public static float getLeapHeightValue() { return INSTANCE.leapHeight; }
    public static int getMaxZombiesOverworldValue() { return INSTANCE.maxZombiesOverworld; }
    public static int getMaxZombiesEndValue() { return INSTANCE.maxZombiesEnd; }
    public static int getMaxZombiesNetherValue() { return INSTANCE.maxZombiesNether; }
    public static boolean isGroupSpawningValue() { return INSTANCE.groupSpawning; }
    public static int getMinGroupSizeValue() { return INSTANCE.minGroupSize; }
    public static int getMaxGroupSizeValue() { return INSTANCE.maxGroupSize; }

    public static ModConfig getInstance() { return INSTANCE; }

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
}
