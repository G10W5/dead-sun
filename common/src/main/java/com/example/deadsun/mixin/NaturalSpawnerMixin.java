package com.example.deadsun.mixin;

import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.MobCategory;
import net.minecraft.world.level.NaturalSpawner;
import net.minecraft.world.level.StructureManager;
import net.minecraft.world.level.biome.MobSpawnSettings;
import net.minecraft.world.level.chunk.ChunkGenerator;
import net.minecraft.util.RandomSource;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.Optional;

@Mixin(NaturalSpawner.class)
public abstract class NaturalSpawnerMixin {

    @Inject(
            method = "getRandomSpawnMobAt",
            at = @At("RETURN"),
            cancellable = true
    )
    private static void deadsun$killAllMonsterSpawns(
            ServerLevel level, StructureManager structureManager,
            ChunkGenerator chunkGenerator, MobCategory category,
            RandomSource random, BlockPos pos,
            CallbackInfoReturnable<Optional<MobSpawnSettings.SpawnerData>> cir
    ) {
        if (category != MobCategory.MONSTER) return;
        cir.setReturnValue(Optional.empty());
    }
}
