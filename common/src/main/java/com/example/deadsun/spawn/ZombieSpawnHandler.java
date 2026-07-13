package com.example.deadsun.spawn;

import com.example.deadsun.config.ModConfig;
import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.EntitySpawnReason;
import net.minecraft.world.entity.EntityTypeIds;
import net.minecraft.world.entity.monster.zombie.Zombie;
import net.minecraft.world.level.LightLayer;
import net.minecraft.world.level.NaturalSpawner;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.world.phys.AABB;

import java.util.List;

public class ZombieSpawnHandler {

    private static int tickCounter = 0;

    @SuppressWarnings("unchecked")
    private static final net.minecraft.world.entity.EntityType<Zombie> ZOMBIE_TYPE =
            (net.minecraft.world.entity.EntityType<Zombie>) BuiltInRegistries.ENTITY_TYPE.getValue(EntityTypeIds.ZOMBIE.identifier());

    public static void tick(ServerLevel level) {
        int interval = ModConfig.getTicksBetweenSpawnsValue();
        tickCounter++;
        if (tickCounter % interval != 0) return;

        List<ServerPlayer> players = level.players();
        int spawnDensity = ModConfig.getSpawnDensityValue();
        int spawnRadius = ModConfig.getSpawnRadiusValue();
        int minDist = ModConfig.getMinSpawnDistanceValue();

        for (ServerPlayer player : players) {
            if (player.isSpectator() || player.isCreative()) continue;

            int zombiesToSpawn = countNearbyZombies(level, player) < spawnDensity ? 1 : 0;

            for (int i = 0; i < zombiesToSpawn; i++) {
                BlockPos pos = findSpawnPosition(level, player, spawnRadius, minDist);
                if (pos == null) continue;
                spawnZombie(level, pos);
            }
        }
    }

    private static int countNearbyZombies(ServerLevel level, ServerPlayer player) {
        int radius = ModConfig.getSpawnRadiusValue();
        AABB box = new AABB(
                player.getX() - radius, player.getY() - 32, player.getZ() - radius,
                player.getX() + radius, player.getY() + 32, player.getZ() + radius
        );
        return level.getEntitiesOfClass(Zombie.class, box, e -> true).size();
    }

    private static BlockPos findSpawnPosition(ServerLevel level, ServerPlayer player, int maxDist, int minDist) {
        for (int attempt = 0; attempt < 16; attempt++) {
            double angle = level.getRandom().nextDouble() * Math.PI * 2;
            double dist = minDist + level.getRandom().nextDouble() * (maxDist - minDist);
            int x = (int) (player.getX() + Math.cos(angle) * dist);
            int z = (int) (player.getZ() + Math.sin(angle) * dist);

            int surfaceY = level.getHeight(Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, x, z);
            int minY = level.getMinY();
            int y;
            if (level.getRandom().nextBoolean()) {
                y = surfaceY + 1;
            } else {
                y = minY + level.getRandom().nextInt(Math.max(1, surfaceY - minY));
            }

            BlockPos pos = new BlockPos(x, y, z);

            if (!isValidSpawnPos(level, pos)) continue;
            if (!checkBlockLight(level, pos)) continue;
            if (isNearbyTorch(level, pos)) continue;

            return pos;
        }
        return null;
    }

    private static boolean isValidSpawnPos(ServerLevel level, BlockPos pos) {
        BlockState state = level.getBlockState(pos);
        BlockState below = level.getBlockState(pos.below());
        BlockState above = level.getBlockState(pos.above());

        if (!below.blocksMotion()) return false;
        if (state.blocksMotion()) return false;
        if (above.blocksMotion()) return false;

        return NaturalSpawner.isValidEmptySpawnBlock(
                level, pos, state, state.getFluidState(), ZOMBIE_TYPE
        );
    }

    private static boolean checkBlockLight(ServerLevel level, BlockPos pos) {
        int maxLight = ModConfig.getMaxBlockLightValue();
        if (maxLight < 0) return true;
        int blockLight = level.getBrightness(LightLayer.BLOCK, pos);
        return blockLight <= maxLight;
    }

    private static boolean isNearbyTorch(ServerLevel level, BlockPos pos) {
        int radius = ModConfig.getTorchRadiusValue();
        for (int x = -radius; x <= radius; x++) {
            for (int y = -radius; y <= radius; y++) {
                for (int z = -radius; z <= radius; z++) {
                    if (x * x + y * y + z * z > radius * radius) continue;
                    BlockState state = level.getBlockState(pos.offset(x, y, z));
                    if (state.is(Blocks.TORCH) || state.is(Blocks.WALL_TORCH)
                            || state.is(Blocks.SOUL_TORCH) || state.is(Blocks.SOUL_WALL_TORCH)
                            || state.is(Blocks.COPPER_TORCH) || state.is(Blocks.COPPER_WALL_TORCH)) {
                        return true;
                    }
                }
            }
        }
        return false;
    }

    private static void spawnZombie(ServerLevel level, BlockPos pos) {
        Zombie zombie = new Zombie(ZOMBIE_TYPE, level);
        zombie.snapTo(pos.getX() + 0.5, pos.getY(), pos.getZ() + 0.5,
                level.getRandom().nextFloat() * 360.0f, 0.0f);
        zombie.finalizeSpawn(level, level.getCurrentDifficultyAt(pos),
                EntitySpawnReason.NATURAL, null);
        level.addFreshEntity(zombie);
    }
}
