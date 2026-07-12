package com.example.deadsun.mixin;

import com.example.deadsun.config.ModConfig;
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
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.chunk.ChunkGenerator;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.chunk.LevelChunk;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.util.RandomSource;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.Optional;

@Mixin(NaturalSpawner.class)
public abstract class NaturalSpawnerMixin {

    @Shadow
    private static boolean canSpawnMobAt(
            ServerLevel level,
            net.minecraft.world.level.StructureManager structureManager,
            ChunkGenerator chunkGenerator,
            MobCategory category,
            MobSpawnSettings.SpawnerData spawnerData,
            BlockPos pos) {
        throw new AssertionError();
    }

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

    @Inject(
            method = "getRandomPosWithin",
            at = @At("HEAD"),
            cancellable = true
    )
    private static void deadsun$forceSurfaceSpawn(
            Level level, LevelChunk chunk,
            CallbackInfoReturnable<BlockPos> cir
    ) {
        if (level.dimension() == Level.NETHER) return;

        ChunkPos chunkPos = chunk.getPos();
        int x = chunkPos.getMinBlockX() + level.getRandom().nextInt(16);
        int z = chunkPos.getMinBlockZ() + level.getRandom().nextInt(16);
        int y = level.getHeight(Heightmap.Types.WORLD_SURFACE, x, z) + 1;
        cir.setReturnValue(new BlockPos(x, y, z));
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
        EntityType<?> zombieType = deadsun$entity(EntityTypeIds.ZOMBIE);

        if (!deadsun$isZombieVariant(entityType)) {
            cir.setReturnValue(Optional.of(new MobSpawnSettings.SpawnerData(
                    zombieType, data.minCount(), data.maxCount())));
        }
    }

    @Redirect(
            method = "isValidSpawnPostitionForType",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/world/level/NaturalSpawner;canSpawnMobAt(Lnet/minecraft/server/level/ServerLevel;Lnet/minecraft/world/level/StructureManager;Lnet/minecraft/world/level/chunk/ChunkGenerator;Lnet/minecraft/world/entity/MobCategory;Lnet/minecraft/world/level/biome/MobSpawnSettings$SpawnerData;Lnet/minecraft/core/BlockPos;)Z"
            )
    )
    private static boolean deadsun$bypassCanSpawnMobAt(
            ServerLevel level,
            net.minecraft.world.level.StructureManager structureManager,
            ChunkGenerator chunkGenerator,
            MobCategory category,
            MobSpawnSettings.SpawnerData spawnerData,
            BlockPos pos
    ) {
        if (deadsun$isZombieVariant(spawnerData.type())) {
            return true;
        }
        return canSpawnMobAt(level, structureManager, chunkGenerator, category, spawnerData, pos);
    }
}
