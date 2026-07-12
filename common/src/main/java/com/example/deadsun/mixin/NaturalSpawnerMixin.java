package com.example.deadsun.mixin;

import com.example.deadsun.config.ModConfig;
import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.ServerLevelAccessor;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.EntityTypeIds;
import net.minecraft.world.entity.MobCategory;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.NaturalSpawner;
import net.minecraft.world.level.StructureManager;
import net.minecraft.world.level.biome.MobSpawnSettings;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.chunk.ChunkGenerator;
import net.minecraft.util.RandomSource;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.Optional;

@Mixin(NaturalSpawner.class)
public abstract class NaturalSpawnerMixin {

    @Unique
    private static EntityType<?> deadsun$entity(ResourceKey<EntityType<?>> key) {
        return BuiltInRegistries.ENTITY_TYPE.getValue(key.identifier());
    }

    @Unique
    private static boolean deadsun$isZombieVariant(EntityType<?> type) {
        return type == deadsun$entity(EntityTypeIds.ZOMBIE)
                || type == deadsun$entity(EntityTypeIds.HUSK)
                || type == deadsun$entity(EntityTypeIds.DROWNED)
                || type == deadsun$entity(EntityTypeIds.ZOMBIE_VILLAGER);
    }

    @Unique
    private static boolean deadsun$isNearbyTorch(ServerLevelAccessor level, BlockPos pos) {
        int radius = ModConfig.getTorchRadiusValue();
        for (int x = -radius; x <= radius; x++) {
            for (int y = -radius; y <= radius; y++) {
                for (int z = -radius; z <= radius; z++) {
                    if (x * x + y * y + z * z > radius * radius) continue;
                    Block block = level.getBlockState(pos.offset(x, y, z)).getBlock();
                    if (block == Blocks.TORCH || block == Blocks.WALL_TORCH
                            || block == Blocks.SOUL_TORCH || block == Blocks.SOUL_WALL_TORCH
                            || block == Blocks.COPPER_TORCH || block == Blocks.COPPER_WALL_TORCH) {
                        return true;
                    }
                }
            }
        }
        return false;
    }

    @Unique
    private static EntityType<?> deadsun$zombieForDimension(ResourceKey<Level> dimension) {
        return deadsun$entity(EntityTypeIds.ZOMBIE);
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
        if (category != MobCategory.MONSTER) return;

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
            if (!deadsun$isZombieVariant(entityType)) {
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

    @Inject(
            method = "isValidSpawnPostitionForType",
            at = @At("HEAD"),
            cancellable = true
    )
    private static void deadsun$bypassSpawnChecks(
            ServerLevel level, MobCategory category,
            StructureManager structureManager, ChunkGenerator chunkGenerator,
            MobSpawnSettings.SpawnerData spawnerData,
            BlockPos.MutableBlockPos pos, double distance,
            CallbackInfoReturnable<Boolean> cir
    ) {
        EntityType<?> entityType = spawnerData.type();
        if (deadsun$isZombieVariant(entityType)) {
            if (deadsun$isNearbyTorch(level, pos)) {
                cir.setReturnValue(false);
            } else {
                cir.setReturnValue(true);
            }
        }
    }
}
