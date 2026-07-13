package com.example.deadsun.fabric;

import com.example.deadsun.config.ModConfig;
import me.shedaniel.clothconfig2.api.ConfigBuilder;
import me.shedaniel.clothconfig2.api.ConfigCategory;
import me.shedaniel.clothconfig2.api.ConfigEntryBuilder;
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

        general.addEntry(entry.startIntSlider(Component.translatable("config.deadsun.torchRadius"), config.getTorchRadius(), 1, 32)
                .setDefaultValue(8)
                .setSaveConsumer(config::setTorchRadius)
                .setTooltip(Component.translatable("config.deadsun.torchRadius.tooltip"))
                .build());

        general.addEntry(entry.startFloatField(Component.translatable("config.deadsun.lootBagDropChance"), config.getLootBagDropChance())
                .setDefaultValue(0.05f)
                .setMin(0.0f)
                .setMax(1.0f)
                .setSaveConsumer(config::setLootBagDropChance)
                .setTooltip(Component.translatable("config.deadsun.lootBagDropChance.tooltip"))
                .build());

        general.addEntry(entry.startIntSlider(Component.translatable("config.deadsun.spawnDensity"), config.getSpawnDensity(), 1, 20)
                .setDefaultValue(5)
                .setSaveConsumer(config::setSpawnDensity)
                .setTooltip(Component.translatable("config.deadsun.spawnDensity.tooltip"))
                .build());

        general.addEntry(entry.startIntSlider(Component.translatable("config.deadsun.spawnRadius"), config.getSpawnRadius(), 16, 128)
                .setDefaultValue(128)
                .setSaveConsumer(config::setSpawnRadius)
                .setTooltip(Component.translatable("config.deadsun.spawnRadius.tooltip"))
                .build());

        general.addEntry(entry.startIntSlider(Component.translatable("config.deadsun.minSpawnDistance"), config.getMinSpawnDistance(), 16, 64)
                .setDefaultValue(32)
                .setSaveConsumer(config::setMinSpawnDistance)
                .setTooltip(Component.translatable("config.deadsun.minSpawnDistance.tooltip"))
                .build());

        general.addEntry(entry.startIntSlider(Component.translatable("config.deadsun.ticksBetweenSpawns"), config.getTicksBetweenSpawns(), 1, 40)
                .setDefaultValue(10)
                .setSaveConsumer(config::setTicksBetweenSpawns)
                .setTooltip(Component.translatable("config.deadsun.ticksBetweenSpawns.tooltip"))
                .build());

        general.addEntry(entry.startIntSlider(Component.translatable("config.deadsun.maxBlockLight"), config.getMaxBlockLight(), -1, 15)
                .setDefaultValue(-1)
                .setSaveConsumer(config::setMaxBlockLight)
                .setTooltip(Component.translatable("config.deadsun.maxBlockLight.tooltip"))
                .build());

        general.addEntry(entry.startBooleanToggle(Component.translatable("config.deadsun.zombieLeap"), config.isZombieLeap())
                .setDefaultValue(true)
                .setSaveConsumer(config::setZombieLeap)
                .setTooltip(Component.translatable("config.deadsun.zombieLeap.tooltip"))
                .build());

        general.addEntry(entry.startBooleanToggle(Component.translatable("config.deadsun.zombiePileUp"), config.isZombiePileUp())
                .setDefaultValue(true)
                .setSaveConsumer(config::setZombiePileUp)
                .setTooltip(Component.translatable("config.deadsun.zombiePileUp.tooltip"))
                .build());

        general.addEntry(entry.startFloatField(Component.translatable("config.deadsun.leapStrength"), config.getLeapStrength())
                .setDefaultValue(0.6f)
                .setMin(0.1f)
                .setMax(2.0f)
                .setSaveConsumer(config::setLeapStrength)
                .setTooltip(Component.translatable("config.deadsun.leapStrength.tooltip"))
                .build());

        general.addEntry(entry.startFloatField(Component.translatable("config.deadsun.leapHeight"), config.getLeapHeight())
                .setDefaultValue(0.4f)
                .setMin(0.1f)
                .setMax(2.0f)
                .setSaveConsumer(config::setLeapHeight)
                .setTooltip(Component.translatable("config.deadsun.leapHeight.tooltip"))
                .build());

        general.addEntry(entry.startFloatField(Component.translatable("config.deadsun.enderPearlChance"), config.getEnderPearlChance())
                .setDefaultValue(0.30f)
                .setMin(0.0f)
                .setMax(1.0f)
                .setSaveConsumer(config::setEnderPearlChance)
                .setTooltip(Component.translatable("config.deadsun.enderPearlChance.tooltip"))
                .build());

        return builder.build();
    }
}
