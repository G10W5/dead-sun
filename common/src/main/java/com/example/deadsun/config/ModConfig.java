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

    // --- Existing fields ---
    private int torchRadius = 8;
    private float lootBagDropChance = 0.25f;
    private float enderPearlDropChance = 0.02f;
    private int spawnDensity = 50;
    private float enderPearlChance = 0.30f;
    private int spawnRadius = 128;
    private int minSpawnDistance = 32;
    private int ticksBetweenSpawns = 20;
    private int maxBlockLight = -1;
    private boolean requireOpenSky = false;
    private boolean zombieLeap = true;
    private boolean zombiePileUp = true;
    private float leapStrength = 0.6f;
    private float leapHeight = 0.4f;
    private int leapCooldown = 120;
    private int pileUpMinGroupSize = 3;
    private float climbSpeedMin = 0.12f;
    private float climbSpeedMax = 0.22f;
    private float climbStepMin = 1.6f;
    private float climbStepMax = 2.1f;
    private int maxWallScan = 8;
    private int maxZombiesOverworld = 50;
    private int maxZombiesEnd = 150;
    private int maxZombiesNether = 100;
    private boolean groupSpawning = true;
    private int minGroupSize = 2;
    private int maxGroupSize = 8;
    private boolean allowBabyZombies = true;

    // --- New fields: Days-Before-Activation ---
    private int daysBeforeActivation = 0;

    // --- New fields: Sound Tracking ---
    private boolean soundTracking = true;
    private int soundHearRange = 12;
    private int soundDecayTime = 200;

    // --- New fields: Wandering Hordes ---
    private boolean wanderingHordes = true;
    private int hordeFrequency = 30;
    private int hordeRange = 128;

    // --- New fields: Light Tracking ---
    private boolean lightTracking = true;
    private int lightSearchRange = 32;
    private float lightMinBrightness = 0.2f;

    // --- New fields: Noisy Zombies ---
    private boolean noisyZombies = true;
    private int noisyZombieRange = 24;
    private int noisyZombieCooldown = 200;

    // --- New fields: Alpha Zombies ---
    private boolean alphaZombies = true;
    private float alphaSpawnChance = 0.02f;
    private int alphaHealth = 60;
    private float alphaScale = 1.5f;
    private float alphaAttackDamage = 5.0f;

    // --- New fields: Coordinated Flanking ---
    private boolean coordinatedFlanking = true;
    private int flankingUpdateInterval = 10;
    private int flankingRange = 32;
    private int flankingMinGroupSize = 3;
    private double flankingRadius = 6.0;
    private double flankingEngageDistance = 4.0;
    private double flankingNavSpeed = 1.0;

    // --- Existing getters ---
    public int getTorchRadius() { return torchRadius; }
    public float getLootBagDropChance() { return lootBagDropChance; }
    public float getEnderPearlDropChance() { return enderPearlDropChance; }
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
    public int getLeapCooldown() { return leapCooldown; }
    public int getPileUpMinGroupSize() { return pileUpMinGroupSize; }
    public float getClimbSpeedMin() { return climbSpeedMin; }
    public float getClimbSpeedMax() { return climbSpeedMax; }
    public float getClimbStepMin() { return climbStepMin; }
    public float getClimbStepMax() { return climbStepMax; }
    public int getMaxWallScan() { return maxWallScan; }
    public int getMaxZombiesOverworld() { return maxZombiesOverworld; }
    public int getMaxZombiesEnd() { return maxZombiesEnd; }
    public int getMaxZombiesNether() { return maxZombiesNether; }
    public boolean isGroupSpawning() { return groupSpawning; }
    public int getMinGroupSize() { return minGroupSize; }
    public int getMaxGroupSize() { return maxGroupSize; }
    public boolean isAllowBabyZombies() { return allowBabyZombies; }

    // --- New getters ---
    public int getDaysBeforeActivation() { return daysBeforeActivation; }
    public boolean isSoundTracking() { return soundTracking; }
    public int getSoundHearRange() { return soundHearRange; }
    public int getSoundDecayTime() { return soundDecayTime; }
    public boolean isWanderingHordes() { return wanderingHordes; }
    public int getHordeFrequency() { return hordeFrequency; }
    public int getHordeRange() { return hordeRange; }
    public boolean isLightTracking() { return lightTracking; }
    public int getLightSearchRange() { return lightSearchRange; }
    public float getLightMinBrightness() { return lightMinBrightness; }
    public boolean isNoisyZombies() { return noisyZombies; }
    public int getNoisyZombieRange() { return noisyZombieRange; }
    public int getNoisyZombieCooldown() { return noisyZombieCooldown; }
    public boolean isAlphaZombies() { return alphaZombies; }
    public float getAlphaSpawnChance() { return alphaSpawnChance; }
    public int getAlphaHealth() { return alphaHealth; }
    public float getAlphaScale() { return alphaScale; }
    public float getAlphaAttackDamage() { return alphaAttackDamage; }
    public boolean isCoordinatedFlanking() { return coordinatedFlanking; }
    public int getFlankingUpdateInterval() { return flankingUpdateInterval; }
    public int getFlankingRange() { return flankingRange; }
    public int getFlankingMinGroupSize() { return flankingMinGroupSize; }
    public double getFlankingRadius() { return flankingRadius; }
    public double getFlankingEngageDistance() { return flankingEngageDistance; }
    public double getFlankingNavSpeed() { return flankingNavSpeed; }

    // --- Existing setters ---
    public void setTorchRadius(int v) { this.torchRadius = v; }
    public void setLootBagDropChance(float v) { this.lootBagDropChance = v; }
    public void setEnderPearlDropChance(float v) { this.enderPearlDropChance = v; }
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
    public void setLeapCooldown(int v) { this.leapCooldown = v; }
    public void setPileUpMinGroupSize(int v) { this.pileUpMinGroupSize = v; }
    public void setClimbSpeedMin(float v) { this.climbSpeedMin = v; }
    public void setClimbSpeedMax(float v) { this.climbSpeedMax = v; }
    public void setClimbStepMin(float v) { this.climbStepMin = v; }
    public void setClimbStepMax(float v) { this.climbStepMax = v; }
    public void setMaxWallScan(int v) { this.maxWallScan = v; }
    public void setMaxZombiesOverworld(int v) { this.maxZombiesOverworld = v; }
    public void setMaxZombiesEnd(int v) { this.maxZombiesEnd = v; }
    public void setMaxZombiesNether(int v) { this.maxZombiesNether = v; }
    public void setGroupSpawning(boolean v) { this.groupSpawning = v; }
    public void setMinGroupSize(int v) { this.minGroupSize = v; }
    public void setMaxGroupSize(int v) { this.maxGroupSize = v; }
    public void setAllowBabyZombies(boolean v) { this.allowBabyZombies = v; }

    // --- New setters ---
    public void setDaysBeforeActivation(int v) { this.daysBeforeActivation = v; }
    public void setSoundTracking(boolean v) { this.soundTracking = v; }
    public void setSoundHearRange(int v) { this.soundHearRange = v; }
    public void setSoundDecayTime(int v) { this.soundDecayTime = v; }
    public void setWanderingHordes(boolean v) { this.wanderingHordes = v; }
    public void setHordeFrequency(int v) { this.hordeFrequency = v; }
    public void setHordeRange(int v) { this.hordeRange = v; }
    public void setLightTracking(boolean v) { this.lightTracking = v; }
    public void setLightSearchRange(int v) { this.lightSearchRange = v; }
    public void setLightMinBrightness(float v) { this.lightMinBrightness = v; }
    public void setNoisyZombies(boolean v) { this.noisyZombies = v; }
    public void setNoisyZombieRange(int v) { this.noisyZombieRange = v; }
    public void setNoisyZombieCooldown(int v) { this.noisyZombieCooldown = v; }
    public void setAlphaZombies(boolean v) { this.alphaZombies = v; }
    public void setAlphaSpawnChance(float v) { this.alphaSpawnChance = v; }
    public void setAlphaHealth(int v) { this.alphaHealth = v; }
    public void setAlphaScale(float v) { this.alphaScale = v; }
    public void setAlphaAttackDamage(float v) { this.alphaAttackDamage = v; }
    public void setCoordinatedFlanking(boolean v) { this.coordinatedFlanking = v; }
    public void setFlankingUpdateInterval(int v) { this.flankingUpdateInterval = v; }
    public void setFlankingRange(int v) { this.flankingRange = v; }
    public void setFlankingMinGroupSize(int v) { this.flankingMinGroupSize = v; }
    public void setFlankingRadius(double v) { this.flankingRadius = v; }
    public void setFlankingEngageDistance(double v) { this.flankingEngageDistance = v; }
    public void setFlankingNavSpeed(double v) { this.flankingNavSpeed = v; }

    // --- Existing static accessors ---
    public static int getSpawnDensityValue() { return INSTANCE.spawnDensity; }
    public static int getSpawnRadiusValue() { return INSTANCE.spawnRadius; }
    public static int getMinSpawnDistanceValue() { return INSTANCE.minSpawnDistance; }
    public static int getTicksBetweenSpawnsValue() { return INSTANCE.ticksBetweenSpawns; }
    public static int getMaxBlockLightValue() { return INSTANCE.maxBlockLight; }
    public static boolean isRequireOpenSkyValue() { return INSTANCE.requireOpenSky; }
    public static int getTorchRadiusValue() { return INSTANCE.torchRadius; }
    public static float getLootBagDropChanceValue() { return INSTANCE.lootBagDropChance; }
    public static float getEnderPearlDropChanceValue() { return INSTANCE.enderPearlDropChance; }
    public static float getEnderPearlChanceValue() { return INSTANCE.enderPearlChance; }
    public static boolean isZombieLeapValue() { return INSTANCE.zombieLeap; }
    public static boolean isZombiePileUpValue() { return INSTANCE.zombiePileUp; }
    public static float getLeapStrengthValue() { return INSTANCE.leapStrength; }
    public static float getLeapHeightValue() { return INSTANCE.leapHeight; }
    public static int getLeapCooldownValue() { return INSTANCE.leapCooldown; }
    public static int getPileUpMinGroupSizeValue() { return INSTANCE.pileUpMinGroupSize; }
    public static float getClimbSpeedMinValue() { return INSTANCE.climbSpeedMin; }
    public static float getClimbSpeedMaxValue() { return INSTANCE.climbSpeedMax; }
    public static float getClimbStepMinValue() { return INSTANCE.climbStepMin; }
    public static float getClimbStepMaxValue() { return INSTANCE.climbStepMax; }
    public static int getMaxWallScanValue() { return INSTANCE.maxWallScan; }
    public static int getMaxZombiesOverworldValue() { return INSTANCE.maxZombiesOverworld; }
    public static int getMaxZombiesEndValue() { return INSTANCE.maxZombiesEnd; }
    public static int getMaxZombiesNetherValue() { return INSTANCE.maxZombiesNether; }
    public static boolean isGroupSpawningValue() { return INSTANCE.groupSpawning; }
    public static int getMinGroupSizeValue() { return INSTANCE.minGroupSize; }
    public static int getMaxGroupSizeValue() { return INSTANCE.maxGroupSize; }
    public static boolean isAllowBabyZombiesValue() { return INSTANCE.allowBabyZombies; }

    // --- New static accessors ---
    public static int getDaysBeforeActivationValue() { return INSTANCE.daysBeforeActivation; }
    public static boolean isSoundTrackingValue() { return INSTANCE.soundTracking; }
    public static int getSoundHearRangeValue() { return INSTANCE.soundHearRange; }
    public static int getSoundDecayTimeValue() { return INSTANCE.soundDecayTime; }
    public static boolean isWanderingHordesValue() { return INSTANCE.wanderingHordes; }
    public static int getHordeFrequencyValue() { return INSTANCE.hordeFrequency; }
    public static int getHordeRangeValue() { return INSTANCE.hordeRange; }
    public static boolean isLightTrackingValue() { return INSTANCE.lightTracking; }
    public static int getLightSearchRangeValue() { return INSTANCE.lightSearchRange; }
    public static float getLightMinBrightnessValue() { return INSTANCE.lightMinBrightness; }
    public static boolean isNoisyZombiesValue() { return INSTANCE.noisyZombies; }
    public static int getNoisyZombieRangeValue() { return INSTANCE.noisyZombieRange; }
    public static int getNoisyZombieCooldownValue() { return INSTANCE.noisyZombieCooldown; }
    public static boolean isAlphaZombiesValue() { return INSTANCE.alphaZombies; }
    public static float getAlphaSpawnChanceValue() { return INSTANCE.alphaSpawnChance; }
    public static int getAlphaHealthValue() { return INSTANCE.alphaHealth; }
    public static float getAlphaScaleValue() { return INSTANCE.alphaScale; }
    public static float getAlphaAttackDamageValue() { return INSTANCE.alphaAttackDamage; }
    public static boolean isCoordinatedFlankingValue() { return INSTANCE.coordinatedFlanking; }
    public static int getFlankingUpdateIntervalValue() { return INSTANCE.flankingUpdateInterval; }
    public static int getFlankingRangeValue() { return INSTANCE.flankingRange; }
    public static int getFlankingMinGroupSizeValue() { return INSTANCE.flankingMinGroupSize; }
    public static double getFlankingRadiusValue() { return INSTANCE.flankingRadius; }
    public static double getFlankingEngageDistanceValue() { return INSTANCE.flankingEngageDistance; }
    public static double getFlankingNavSpeedValue() { return INSTANCE.flankingNavSpeed; }

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
