package com.example.deadsun.spawn;

import com.example.deadsun.DeadSunMod;
import com.example.deadsun.config.ModConfig;
import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.EntitySpawnReason;
import net.minecraft.world.entity.EntityTypeIds;
import net.minecraft.world.entity.monster.zombie.Zombie;
import net.minecraft.world.level.LightLayer;
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
        if (players.isEmpty()) return;

        int spawnDensity = ModConfig.getSpawnDensityValue();
        int spawnRadius = ModConfig.getSpawnRadiusValue();
        int minDist = ModConfig.getMinSpawnDistanceValue();

        for (ServerPlayer player : players) {
            if (player.isSpectator() || player.isCreative()) continue;

            int nearbyZombies = countNearbyZombies(level, player);
            int toSpawn = Math.min(spawnDensity - nearbyZombies, 3);
            if (toSpawn <= 0) {
                DeadSunMod.LOGGER.info("DeadSun: density cap reached ({}/{}), skipping", nearbyZombies, spawnDensity);
                continue;
            }

            DeadSunMod.LOGGER.info("DeadSun: trying to spawn {} zombies for {} (nearby: {})", toSpawn, player.getName().getString(), nearbyZombies);

            for (int i = 0; i < toSpawn; i++) {
                BlockPos pos = findSpawnPosition(level, player, spawnRadius, minDist);
                if (pos == null) {
                    DeadSunMod.LOGGER.info("DeadSun: no valid position found after 32 attempts");
                    continue;
                }
                DeadSunMod.LOGGER.info("DeadSun: spawning zombie at {}", pos);
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
        boolean isEnd = level.dimension() == net.minecraft.world.level.Level.END;
        boolean isNether = level.dimension() == net.minecraft.world.level.Level.NETHER;

        for (int attempt = 0; attempt < 32; attempt++) {
            double angle = level.getRandom().nextDouble() * Math.PI * 2;
            double dist = minDist + level.getRandom().nextDouble() * (maxDist - minDist);
            int x = (int) (player.getX() + Math.cos(angle) * dist);
            int z = (int) (player.getZ() + Math.sin(angle) * dist);

            int surfaceY;
            if (isNether) {
                surfaceY = level.getHeight(Heightmap.Types.MOTION_BLOCKING, x, z);
            } else {
                surfaceY = level.getHeight(Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, x, z);
            }

            int y = surfaceY + 1;
            BlockPos pos = new BlockPos(x, y, z);

            if (isEnd && !level.canSeeSky(pos)) {
                DeadSunMod.LOGGER.info("DeadSun attempt {}: End position {} rejected - no sky", attempt, pos);
                continue;
            }

            BlockState below = level.getBlockState(pos.below());
            BlockState at = level.getBlockState(pos);
            BlockState above = level.getBlockState(pos.above());

            if (!below.blocksMotion()) {
                DeadSunMod.LOGGER.info("DeadSun attempt {}: pos {} rejected - block below ({}) doesn't block motion", attempt, pos, below.getBlock().toString());
                continue;
            }
            if (at.blocksMotion()) {
                DeadSunMod.LOGGER.info("DeadSun attempt {}: pos {} rejected - block at position ({}) blocks motion", attempt, pos, at.getBlock().toString());
                continue;
            }
            if (above.blocksMotion()) {
                DeadSunMod.LOGGER.info("DeadSun attempt {}: pos {} rejected - block above ({}) blocks motion", attempt, pos, above.getBlock().toString());
                continue;
            }

            if (!checkBlockLight(level, pos)) {
                DeadSunMod.LOGGER.info("DeadSun attempt {}: pos {} rejected - block light too high", attempt, pos);
                continue;
            }

            if (!isNether && isNearbyTorch(level, pos)) {
                DeadSunMod.LOGGER.info("DeadSun attempt {}: pos {} rejected - nearby torch", attempt, pos);
                continue;
            }

            return pos;
        }
        return null;
    }

    private static boolean checkBlockLight(ServerLevel level, BlockPos pos) {
        int maxLight = ModConfig.getMaxBlockLightValue();
        if (maxLight < 0) return true;
        int blockLight = level.getBrightness(LightLayer.BLOCK, pos);
        return blockLight <= maxLight;
    }

    private static boolean isNearbyTorch(ServerLevel level, BlockPos pos) {
        int radius = Math.min(ModConfig.getTorchRadiusValue(), 8);
        for (int x = -radius; x <= radius; x++) {
            for (int z = -radius; z <= radius; z++) {
                for (int y = -2; y <= 4; y++) {
                    if (x * x + z * z > radius * radius) continue;
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
