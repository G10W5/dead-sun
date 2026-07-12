package com.example.deadsun.mixin;

import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.EntityTypeIds;
import net.minecraft.world.entity.MobCategory;
import net.minecraft.world.level.Level;
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

    private static EntityType<?> deadsun$entity(ResourceKey<EntityType<?>> key) {
        return BuiltInRegistries.ENTITY_TYPE.getValue(key.identifier());
    }

    private static boolean deadsun$isZombieVariant(EntityType<?> type) {
        return type == deadsun$entity(EntityTypeIds.ZOMBIE)
                || type == deadsun$entity(EntityTypeIds.HUSK)
                || type == deadsun$entity(EntityTypeIds.DROWNED)
                || type == deadsun$entity(EntityTypeIds.ZOMBIE_VILLAGER);
    }

    @Inject(
            method = "getRandomSpawnMobAt",
            at = @At("RETURN"),
            cancellable = true
    )
    private static void deadsun$filterSpawns(
            ServerLevel level, StructureManager structureManager,
            ChunkGenerator chunkGenerator, MobCategory category,
            RandomSource random, BlockPos pos,
            CallbackInfoReturnable<Optional<MobSpawnSettings.SpawnerData>> cir
    ) {
        Optional<MobSpawnSettings.SpawnerData> original = cir.getReturnValue();
        if (original.isEmpty()) return;

        MobSpawnSettings.SpawnerData data = original.get();
        EntityType<?> entityType = data.type();
        ResourceKey<Level> dimension = level.dimension();
        EntityType<?> zombieType = deadsun$entity(EntityTypeIds.ZOMBIE);

        if (dimension == Level.OVERWORLD) {
            if (!deadsun$isZombieVariant(entityType)) {
                cir.setReturnValue(Optional.of(new MobSpawnSettings.SpawnerData(
                        zombieType, data.minCount(), data.maxCount())));
            }
            return;
        }

        if (dimension == Level.NETHER) {
            if (entityType == deadsun$entity(EntityTypeIds.ZOMBIFIED_PIGLIN)) {
                cir.setReturnValue(Optional.of(new MobSpawnSettings.SpawnerData(
                        zombieType, data.minCount(), data.maxCount())));
            }
            return;
        }

        if (dimension == Level.END) {
            if (!deadsun$isZombieVariant(entityType)) {
                cir.setReturnValue(Optional.of(new MobSpawnSettings.SpawnerData(
                        zombieType, data.minCount(), data.maxCount())));
            }
        }
    }
}
