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
                .setDefaultValue(10).setSaveConsumer(config::setTicksBetweenSpawns)
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
                .setDefaultValue(0.05f).setMin(0.0f).setMax(1.0f).setSaveConsumer(config::setLootBagDropChance)
                .setTooltip(Component.translatable("config.deadsun.lootBagDropChance.tooltip")).build());
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
        soundSub.add(entry.startIntSlider(Component.translatable("config.deadsun.soundHearRange"), config.getSoundHearRange(), 16, 128)
                .setDefaultValue(64).setSaveConsumer(config::setSoundHearRange)
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
                .setDefaultValue(100).setSaveConsumer(config::setNoisyZombieCooldown)
                .setTooltip(Component.translatable("config.deadsun.noisyZombieCooldown.tooltip")).build());
        general.addEntry(noisySub.build());

        return builder.build();
    }
}
