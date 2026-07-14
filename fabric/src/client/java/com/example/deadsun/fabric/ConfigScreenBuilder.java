package com.example.deadsun.fabric;

import com.example.deadsun.config.ModConfig;
import me.shedaniel.clothconfig2.api.ConfigBuilder;
import me.shedaniel.clothconfig2.api.ConfigCategory;
import me.shedaniel.clothconfig2.api.ConfigEntryBuilder;
import me.shedaniel.clothconfig2.impl.builders.SubCategoryBuilder;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;

public class ConfigScreenBuilder {

    public static Screen buildScreen(Screen parent) {
        ModConfig config = ModConfig.getInstance();

        ConfigBuilder builder = ConfigBuilder.create()
                .setParentScreen(parent)
                .setTitle(Component.translatable("config.deadsun.title"))
                .setSavingRunnable(ModConfig::save);

        ConfigEntryBuilder entry = builder.entryBuilder();
        ConfigCategory general = builder.getOrCreateCategory(Component.translatable("config.deadsun.general"));

        // === Subcategory: Spawn Settings ===
        SubCategoryBuilder spawnSub = entry.startSubCategory(Component.translatable("config.deadsun.subcat.spawn"));
        spawnSub.add(entry.startIntSlider(Component.translatable("config.deadsun.spawnDensity"), config.getSpawnDensity(), 10, 150)
                .setDefaultValue(50).setSaveConsumer(config::setSpawnDensity)
                .setTooltip(Component.translatable("config.deadsun.spawnDensity.tooltip")).build());
        spawnSub.add(entry.startIntSlider(Component.translatable("config.deadsun.spawnRadius"), config.getSpawnRadius(), 16, 128)
                .setDefaultValue(128).setSaveConsumer(config::setSpawnRadius)
                .setTooltip(Component.translatable("config.deadsun.spawnRadius.tooltip")).build());
        spawnSub.add(entry.startIntSlider(Component.translatable("config.deadsun.minSpawnDistance"), config.getMinSpawnDistance(), 16, 64)
                .setDefaultValue(32).setSaveConsumer(config::setMinSpawnDistance)
                .setTooltip(Component.translatable("config.deadsun.minSpawnDistance.tooltip")).build());
        spawnSub.add(entry.startIntSlider(Component.translatable("config.deadsun.ticksBetweenSpawns"), config.getTicksBetweenSpawns(), 1, 40)
                .setDefaultValue(20).setSaveConsumer(config::setTicksBetweenSpawns)
                .setTooltip(Component.translatable("config.deadsun.ticksBetweenSpawns.tooltip")).build());
        spawnSub.add(entry.startIntSlider(Component.translatable("config.deadsun.maxBlockLight"), config.getMaxBlockLight(), -1, 15)
                .setDefaultValue(-1).setSaveConsumer(config::setMaxBlockLight)
                .setTooltip(Component.translatable("config.deadsun.maxBlockLight.tooltip")).build());
        spawnSub.add(entry.startBooleanToggle(Component.translatable("config.deadsun.groupSpawning"), config.isGroupSpawning())
                .setDefaultValue(true).setSaveConsumer(config::setGroupSpawning)
                .setTooltip(Component.translatable("config.deadsun.groupSpawning.tooltip")).build());
        spawnSub.add(entry.startIntSlider(Component.translatable("config.deadsun.minGroupSize"), config.getMinGroupSize(), 2, 6)
                .setDefaultValue(2).setSaveConsumer(config::setMinGroupSize)
                .setTooltip(Component.translatable("config.deadsun.minGroupSize.tooltip")).build());
        spawnSub.add(entry.startIntSlider(Component.translatable("config.deadsun.maxGroupSize"), config.getMaxGroupSize(), 4, 16)
                .setDefaultValue(8).setSaveConsumer(config::setMaxGroupSize)
                .setTooltip(Component.translatable("config.deadsun.maxGroupSize.tooltip")).build());
        spawnSub.add(entry.startBooleanToggle(Component.translatable("config.deadsun.allowBabyZombies"), config.isAllowBabyZombies())
                .setDefaultValue(true).setSaveConsumer(config::setAllowBabyZombies)
                .setTooltip(Component.translatable("config.deadsun.allowBabyZombies.tooltip")).build());
        general.addEntry(spawnSub.build());

        // === Subcategory: Dimension Caps ===
        SubCategoryBuilder dimSub = entry.startSubCategory(Component.translatable("config.deadsun.subcat.dimensions"));
        dimSub.add(entry.startIntSlider(Component.translatable("config.deadsun.maxZombiesOverworld"), config.getMaxZombiesOverworld(), 10, 200)
                .setDefaultValue(50).setSaveConsumer(config::setMaxZombiesOverworld)
                .setTooltip(Component.translatable("config.deadsun.maxZombiesOverworld.tooltip")).build());
        dimSub.add(entry.startIntSlider(Component.translatable("config.deadsun.maxZombiesEnd"), config.getMaxZombiesEnd(), 10, 300)
                .setDefaultValue(150).setSaveConsumer(config::setMaxZombiesEnd)
                .setTooltip(Component.translatable("config.deadsun.maxZombiesEnd.tooltip")).build());
        dimSub.add(entry.startIntSlider(Component.translatable("config.deadsun.maxZombiesNether"), config.getMaxZombiesNether(), 10, 200)
                .setDefaultValue(100).setSaveConsumer(config::setMaxZombiesNether)
                .setTooltip(Component.translatable("config.deadsun.maxZombiesNether.tooltip")).build());
        general.addEntry(dimSub.build());

        // === Subcategory: Loot ===
        SubCategoryBuilder lootSub = entry.startSubCategory(Component.translatable("config.deadsun.subcat.loot"));
        lootSub.add(entry.startFloatField(Component.translatable("config.deadsun.lootBagDropChance"), config.getLootBagDropChance())
                .setDefaultValue(0.25f).setMin(0.0f).setMax(1.0f).setSaveConsumer(config::setLootBagDropChance)
                .setTooltip(Component.translatable("config.deadsun.lootBagDropChance.tooltip")).build());
        lootSub.add(entry.startFloatField(Component.translatable("config.deadsun.enderPearlDropChance"), config.getEnderPearlDropChance())
                .setDefaultValue(0.02f).setMin(0.0f).setMax(1.0f).setSaveConsumer(config::setEnderPearlDropChance)
                .setTooltip(Component.translatable("config.deadsun.enderPearlDropChance.tooltip")).build());
        lootSub.add(entry.startFloatField(Component.translatable("config.deadsun.enderPearlChance"), config.getEnderPearlChance())
                .setDefaultValue(0.30f).setMin(0.0f).setMax(1.0f).setSaveConsumer(config::setEnderPearlChance)
                .setTooltip(Component.translatable("config.deadsun.enderPearlChance.tooltip")).build());
        general.addEntry(lootSub.build());

        // === Subcategory: Zombie Behavior ===
        SubCategoryBuilder behavSub = entry.startSubCategory(Component.translatable("config.deadsun.subcat.behavior"));
        behavSub.add(entry.startBooleanToggle(Component.translatable("config.deadsun.zombieLeap"), config.isZombieLeap())
                .setDefaultValue(true).setSaveConsumer(config::setZombieLeap)
                .setTooltip(Component.translatable("config.deadsun.zombieLeap.tooltip")).build());
        behavSub.add(entry.startBooleanToggle(Component.translatable("config.deadsun.zombiePileUp"), config.isZombiePileUp())
                .setDefaultValue(true).setSaveConsumer(config::setZombiePileUp)
                .setTooltip(Component.translatable("config.deadsun.zombiePileUp.tooltip")).build());
        behavSub.add(entry.startFloatField(Component.translatable("config.deadsun.leapStrength"), config.getLeapStrength())
                .setDefaultValue(0.6f).setMin(0.1f).setMax(2.0f).setSaveConsumer(config::setLeapStrength)
                .setTooltip(Component.translatable("config.deadsun.leapStrength.tooltip")).build());
        behavSub.add(entry.startFloatField(Component.translatable("config.deadsun.leapHeight"), config.getLeapHeight())
                .setDefaultValue(0.4f).setMin(0.1f).setMax(2.0f).setSaveConsumer(config::setLeapHeight)
                .setTooltip(Component.translatable("config.deadsun.leapHeight.tooltip")).build());
        behavSub.add(entry.startIntSlider(Component.translatable("config.deadsun.leapCooldown"), config.getLeapCooldown(), 20, 400)
                .setDefaultValue(120).setSaveConsumer(config::setLeapCooldown)
                .setTooltip(Component.translatable("config.deadsun.leapCooldown.tooltip")).build());
        behavSub.add(entry.startIntSlider(Component.translatable("config.deadsun.pileUpMinGroupSize"), config.getPileUpMinGroupSize(), 2, 8)
                .setDefaultValue(3).setSaveConsumer(config::setPileUpMinGroupSize)
                .setTooltip(Component.translatable("config.deadsun.pileUpMinGroupSize.tooltip")).build());
        behavSub.add(entry.startFloatField(Component.translatable("config.deadsun.climbSpeedMin"), config.getClimbSpeedMin())
                .setDefaultValue(0.12f).setMin(0.05f).setMax(0.3f).setSaveConsumer(config::setClimbSpeedMin)
                .setTooltip(Component.translatable("config.deadsun.climbSpeedMin.tooltip")).build());
        behavSub.add(entry.startFloatField(Component.translatable("config.deadsun.climbSpeedMax"), config.getClimbSpeedMax())
                .setDefaultValue(0.22f).setMin(0.1f).setMax(0.5f).setSaveConsumer(config::setClimbSpeedMax)
                .setTooltip(Component.translatable("config.deadsun.climbSpeedMax.tooltip")).build());
        behavSub.add(entry.startFloatField(Component.translatable("config.deadsun.climbStepMin"), config.getClimbStepMin())
                .setDefaultValue(1.6f).setMin(0.5f).setMax(3.0f).setSaveConsumer(config::setClimbStepMin)
                .setTooltip(Component.translatable("config.deadsun.climbStepMin.tooltip")).build());
        behavSub.add(entry.startFloatField(Component.translatable("config.deadsun.climbStepMax"), config.getClimbStepMax())
                .setDefaultValue(2.1f).setMin(1.0f).setMax(4.0f).setSaveConsumer(config::setClimbStepMax)
                .setTooltip(Component.translatable("config.deadsun.climbStepMax.tooltip")).build());
        behavSub.add(entry.startIntSlider(Component.translatable("config.deadsun.maxWallScan"), config.getMaxWallScan(), 2, 16)
                .setDefaultValue(8).setSaveConsumer(config::setMaxWallScan)
                .setTooltip(Component.translatable("config.deadsun.maxWallScan.tooltip")).build());
        general.addEntry(behavSub.build());

        // === Subcategory: Torch Safety ===
        SubCategoryBuilder torchSub = entry.startSubCategory(Component.translatable("config.deadsun.subcat.torch"));
        torchSub.add(entry.startIntSlider(Component.translatable("config.deadsun.torchRadius"), config.getTorchRadius(), 1, 32)
                .setDefaultValue(8).setSaveConsumer(config::setTorchRadius)
                .setTooltip(Component.translatable("config.deadsun.torchRadius.tooltip")).build());
        general.addEntry(torchSub.build());

        // === Subcategory: Days Before Activation ===
        SubCategoryBuilder activationSub = entry.startSubCategory(Component.translatable("config.deadsun.subcat.activation"));
        activationSub.add(entry.startIntSlider(Component.translatable("config.deadsun.daysBeforeActivation"), config.getDaysBeforeActivation(), 0, 100)
                .setDefaultValue(0).setSaveConsumer(config::setDaysBeforeActivation)
                .setTooltip(Component.translatable("config.deadsun.daysBeforeActivation.tooltip")).build());
        general.addEntry(activationSub.build());

        // === Subcategory: Sound Tracking ===
        SubCategoryBuilder soundSub = entry.startSubCategory(Component.translatable("config.deadsun.subcat.soundTracking"));
        soundSub.add(entry.startBooleanToggle(Component.translatable("config.deadsun.soundTracking"), config.isSoundTracking())
                .setDefaultValue(true).setSaveConsumer(config::setSoundTracking)
                .setTooltip(Component.translatable("config.deadsun.soundTracking.tooltip")).build());
        soundSub.add(entry.startIntSlider(Component.translatable("config.deadsun.soundHearRange"), config.getSoundHearRange(), 8, 128)
                .setDefaultValue(12).setSaveConsumer(config::setSoundHearRange)
                .setTooltip(Component.translatable("config.deadsun.soundHearRange.tooltip")).build());
        soundSub.add(entry.startIntSlider(Component.translatable("config.deadsun.soundDecayTime"), config.getSoundDecayTime(), 60, 600)
                .setDefaultValue(200).setSaveConsumer(config::setSoundDecayTime)
                .setTooltip(Component.translatable("config.deadsun.soundDecayTime.tooltip")).build());
        general.addEntry(soundSub.build());

        // === Subcategory: Light Tracking ===
        SubCategoryBuilder lightSub = entry.startSubCategory(Component.translatable("config.deadsun.subcat.lightTracking"));
        lightSub.add(entry.startBooleanToggle(Component.translatable("config.deadsun.lightTracking"), config.isLightTracking())
                .setDefaultValue(true).setSaveConsumer(config::setLightTracking)
                .setTooltip(Component.translatable("config.deadsun.lightTracking.tooltip")).build());
        lightSub.add(entry.startIntSlider(Component.translatable("config.deadsun.lightSearchRange"), config.getLightSearchRange(), 16, 128)
                .setDefaultValue(32).setSaveConsumer(config::setLightSearchRange)
                .setTooltip(Component.translatable("config.deadsun.lightSearchRange.tooltip")).build());
        lightSub.add(entry.startFloatField(Component.translatable("config.deadsun.lightMinBrightness"), config.getLightMinBrightness())
                .setDefaultValue(0.2f).setMin(0.1f).setMax(1.0f).setSaveConsumer(config::setLightMinBrightness)
                .setTooltip(Component.translatable("config.deadsun.lightMinBrightness.tooltip")).build());
        general.addEntry(lightSub.build());

        // === Subcategory: Wandering Hordes ===
        SubCategoryBuilder hordeSub = entry.startSubCategory(Component.translatable("config.deadsun.subcat.hordes"));
        hordeSub.add(entry.startBooleanToggle(Component.translatable("config.deadsun.wanderingHordes"), config.isWanderingHordes())
                .setDefaultValue(true).setSaveConsumer(config::setWanderingHordes)
                .setTooltip(Component.translatable("config.deadsun.wanderingHordes.tooltip")).build());
        hordeSub.add(entry.startIntSlider(Component.translatable("config.deadsun.hordeFrequency"), config.getHordeFrequency(), 10, 120)
                .setDefaultValue(30).setSaveConsumer(config::setHordeFrequency)
                .setTooltip(Component.translatable("config.deadsun.hordeFrequency.tooltip")).build());
        hordeSub.add(entry.startIntSlider(Component.translatable("config.deadsun.hordeRange"), config.getHordeRange(), 32, 256)
                .setDefaultValue(128).setSaveConsumer(config::setHordeRange)
                .setTooltip(Component.translatable("config.deadsun.hordeRange.tooltip")).build());
        general.addEntry(hordeSub.build());

        // === Subcategory: Noisy Zombies ===
        SubCategoryBuilder noisySub = entry.startSubCategory(Component.translatable("config.deadsun.subcat.noisy"));
        noisySub.add(entry.startBooleanToggle(Component.translatable("config.deadsun.noisyZombies"), config.isNoisyZombies())
                .setDefaultValue(true).setSaveConsumer(config::setNoisyZombies)
                .setTooltip(Component.translatable("config.deadsun.noisyZombies.tooltip")).build());
        noisySub.add(entry.startIntSlider(Component.translatable("config.deadsun.noisyZombieRange"), config.getNoisyZombieRange(), 8, 64)
                .setDefaultValue(24).setSaveConsumer(config::setNoisyZombieRange)
                .setTooltip(Component.translatable("config.deadsun.noisyZombieRange.tooltip")).build());
        noisySub.add(entry.startIntSlider(Component.translatable("config.deadsun.noisyZombieCooldown"), config.getNoisyZombieCooldown(), 40, 400)
                .setDefaultValue(200).setSaveConsumer(config::setNoisyZombieCooldown)
                .setTooltip(Component.translatable("config.deadsun.noisyZombieCooldown.tooltip")).build());
        general.addEntry(noisySub.build());

        // === Subcategory: Alpha Zombies ===
        SubCategoryBuilder alphaSub = entry.startSubCategory(Component.translatable("config.deadsun.subcat.alpha"));
        alphaSub.add(entry.startBooleanToggle(Component.translatable("config.deadsun.alphaZombies"), config.isAlphaZombies())
                .setDefaultValue(true).setSaveConsumer(config::setAlphaZombies)
                .setTooltip(Component.translatable("config.deadsun.alphaZombies.tooltip")).build());
        alphaSub.add(entry.startFloatField(Component.translatable("config.deadsun.alphaSpawnChance"), config.getAlphaSpawnChance())
                .setDefaultValue(0.02f).setMin(0.0f).setMax(0.1f).setSaveConsumer(config::setAlphaSpawnChance)
                .setTooltip(Component.translatable("config.deadsun.alphaSpawnChance.tooltip")).build());
        alphaSub.add(entry.startIntSlider(Component.translatable("config.deadsun.alphaHealth"), config.getAlphaHealth(), 20, 200)
                .setDefaultValue(60).setSaveConsumer(config::setAlphaHealth)
                .setTooltip(Component.translatable("config.deadsun.alphaHealth.tooltip")).build());
        alphaSub.add(entry.startFloatField(Component.translatable("config.deadsun.alphaScale"), config.getAlphaScale())
                .setDefaultValue(1.5f).setMin(1.0f).setMax(3.0f).setSaveConsumer(config::setAlphaScale)
                .setTooltip(Component.translatable("config.deadsun.alphaScale.tooltip")).build());
        alphaSub.add(entry.startFloatField(Component.translatable("config.deadsun.alphaAttackDamage"), config.getAlphaAttackDamage())
                .setDefaultValue(5.0f).setMin(3.0f).setMax(20.0f).setSaveConsumer(config::setAlphaAttackDamage)
                .setTooltip(Component.translatable("config.deadsun.alphaAttackDamage.tooltip")).build());
        general.addEntry(alphaSub.build());

        SubCategoryBuilder variantSub = entry.startSubCategory(Component.translatable("config.deadsun.subcat.variants"));
        variantSub.add(entry.startBooleanToggle(Component.translatable("config.deadsun.zombieVariants"), config.isZombieVariants())
                .setDefaultValue(true).setSaveConsumer(config::setZombieVariants)
                .setTooltip(Component.translatable("config.deadsun.zombieVariants.tooltip")).build());
        variantSub.add(entry.startFloatField(Component.translatable("config.deadsun.runnerChance"), config.getRunnerChance())
                .setDefaultValue(0.05f).setMin(0.0f).setMax(0.5f).setSaveConsumer(config::setRunnerChance)
                .setTooltip(Component.translatable("config.deadsun.runnerChance.tooltip")).build());
        variantSub.add(entry.startFloatField(Component.translatable("config.deadsun.runnerSpeedBoost"), config.getRunnerSpeedBoost())
                .setDefaultValue(1.0f).setMin(0.1f).setMax(3.0f).setSaveConsumer(config::setRunnerSpeedBoost)
                .setTooltip(Component.translatable("config.deadsun.runnerSpeedBoost.tooltip")).build());
        variantSub.add(entry.startFloatField(Component.translatable("config.deadsun.jumperChance"), config.getJumperChance())
                .setDefaultValue(0.03f).setMin(0.0f).setMax(0.5f).setSaveConsumer(config::setJumperChance)
                .setTooltip(Component.translatable("config.deadsun.jumperChance.tooltip")).build());
        variantSub.add(entry.startFloatField(Component.translatable("config.deadsun.jumperLeapBoost"), config.getJumperLeapBoost())
                .setDefaultValue(0.5f).setMin(0.1f).setMax(2.0f).setSaveConsumer(config::setJumperLeapBoost)
                .setTooltip(Component.translatable("config.deadsun.jumperLeapBoost.tooltip")).build());
        variantSub.add(entry.startFloatField(Component.translatable("config.deadsun.exploderChance"), config.getExploderChance())
                .setDefaultValue(0.02f).setMin(0.0f).setMax(0.5f).setSaveConsumer(config::setExploderChance)
                .setTooltip(Component.translatable("config.deadsun.exploderChance.tooltip")).build());
        variantSub.add(entry.startIntSlider(Component.translatable("config.deadsun.exploderFuseTime"), config.getExploderFuseTime(), 20, 80)
                .setDefaultValue(40).setSaveConsumer(config::setExploderFuseTime)
                .setTooltip(Component.translatable("config.deadsun.exploderFuseTime.tooltip")).build());
        variantSub.add(entry.startFloatField(Component.translatable("config.deadsun.exploderExplosionRadius"), config.getExploderExplosionRadius())
                .setDefaultValue(3.0f).setMin(1.0f).setMax(8.0f).setSaveConsumer(config::setExploderExplosionRadius)
                .setTooltip(Component.translatable("config.deadsun.exploderExplosionRadius.tooltip")).build());
        variantSub.add(entry.startBooleanToggle(Component.translatable("config.deadsun.exploderDestroysBlocks"), config.isExploderDestroysBlocks())
                .setDefaultValue(true).setSaveConsumer(config::setExploderDestroysBlocks)
                .setTooltip(Component.translatable("config.deadsun.exploderDestroysBlocks.tooltip")).build());
        variantSub.add(entry.startFloatField(Component.translatable("config.deadsun.bruteChance"), config.getBruteChance())
                .setDefaultValue(0.03f).setMin(0.0f).setMax(0.5f).setSaveConsumer(config::setBruteChance)
                .setTooltip(Component.translatable("config.deadsun.bruteChance.tooltip")).build());
        variantSub.add(entry.startFloatField(Component.translatable("config.deadsun.bruteSpeedMultiplier"), config.getBruteSpeedMultiplier())
                .setDefaultValue(0.6f).setMin(0.1f).setMax(1.0f).setSaveConsumer(config::setBruteSpeedMultiplier)
                .setTooltip(Component.translatable("config.deadsun.bruteSpeedMultiplier.tooltip")).build());
        variantSub.add(entry.startIntSlider(Component.translatable("config.deadsun.bruteStrengthLevel"), config.getBruteStrengthLevel(), 1, 5)
                .setDefaultValue(1).setSaveConsumer(config::setBruteStrengthLevel)
                .setTooltip(Component.translatable("config.deadsun.bruteStrengthLevel.tooltip")).build());
        general.addEntry(variantSub.build());

        SubCategoryBuilder flankSub = entry.startSubCategory(Component.translatable("config.deadsun.subcat.flanking"));
        flankSub.add(entry.startBooleanToggle(Component.translatable("config.deadsun.coordinatedFlanking"), config.isCoordinatedFlanking())
                .setDefaultValue(true).setSaveConsumer(config::setCoordinatedFlanking)
                .setTooltip(Component.translatable("config.deadsun.coordinatedFlanking.tooltip")).build());
        flankSub.add(entry.startIntSlider(Component.translatable("config.deadsun.flankingUpdateInterval"), config.getFlankingUpdateInterval(), 5, 40)
                .setDefaultValue(10).setSaveConsumer(config::setFlankingUpdateInterval)
                .setTooltip(Component.translatable("config.deadsun.flankingUpdateInterval.tooltip")).build());
        flankSub.add(entry.startIntSlider(Component.translatable("config.deadsun.flankingRange"), config.getFlankingRange(), 16, 64)
                .setDefaultValue(32).setSaveConsumer(config::setFlankingRange)
                .setTooltip(Component.translatable("config.deadsun.flankingRange.tooltip")).build());
        flankSub.add(entry.startIntSlider(Component.translatable("config.deadsun.flankingMinGroupSize"), config.getFlankingMinGroupSize(), 2, 8)
                .setDefaultValue(3).setSaveConsumer(config::setFlankingMinGroupSize)
                .setTooltip(Component.translatable("config.deadsun.flankingMinGroupSize.tooltip")).build());
        flankSub.add(entry.startFloatField(Component.translatable("config.deadsun.flankingRadius"), (float) config.getFlankingRadius())
                .setDefaultValue(6.0f).setMin(2.0f).setMax(16.0f).setSaveConsumer(v -> config.setFlankingRadius(v))
                .setTooltip(Component.translatable("config.deadsun.flankingRadius.tooltip")).build());
        flankSub.add(entry.startFloatField(Component.translatable("config.deadsun.flankingEngageDistance"), (float) config.getFlankingEngageDistance())
                .setDefaultValue(4.0f).setMin(1.0f).setMax(8.0f).setSaveConsumer(v -> config.setFlankingEngageDistance(v))
                .setTooltip(Component.translatable("config.deadsun.flankingEngageDistance.tooltip")).build());
        flankSub.add(entry.startFloatField(Component.translatable("config.deadsun.flankingNavSpeed"), (float) config.getFlankingNavSpeed())
                .setDefaultValue(1.0f).setMin(0.5f).setMax(2.0f).setSaveConsumer(v -> config.setFlankingNavSpeed(v))
                .setTooltip(Component.translatable("config.deadsun.flankingNavSpeed.tooltip")).build());
        general.addEntry(flankSub.build());

        return builder.build();
    }
}
