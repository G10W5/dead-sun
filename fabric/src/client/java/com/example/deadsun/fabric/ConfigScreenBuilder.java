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

        general.addEntry(entry.startIntSlider(Component.translatable("config.deadsun.spawnDensity"), config.getSpawnDensity(), 1, 10)
                .setDefaultValue(1)
                .setSaveConsumer(config::setSpawnDensity)
                .setTooltip(Component.translatable("config.deadsun.spawnDensity.tooltip"))
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
