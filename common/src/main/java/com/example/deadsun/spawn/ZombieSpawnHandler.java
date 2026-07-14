package com.example.deadsun.spawn;

import com.example.deadsun.DeadSunMod;
import com.example.deadsun.awareness.AlphaZombieHandler;
import com.example.deadsun.awareness.HordeHandler;
import com.example.deadsun.awareness.LightTrackingHandler;
import com.example.deadsun.awareness.SoundTrackingHandler;
import com.example.deadsun.config.ModConfig;
import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.EntitySpawnReason;
import net.minecraft.world.entity.EntityTypeIds;
import net.minecraft.world.entity.monster.zombie.Zombie;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LightLayer;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.world.phys.AABB;

import java.util.List;

public class ZombieSpawnHandler {

    private static final int NOT_FOUND = Integer.MIN_VALUE;

    private static int tickCounter = 0;

    @SuppressWarnings("unchecked")
    private static final net.minecraft.world.entity.EntityType<Zombie> ZOMBIE_TYPE =
            (net.minecraft.world.entity.EntityType<Zombie>) BuiltInRegistries.ENTITY_TYPE.getValue(EntityTypeIds.ZOMBIE.identifier());

    public static void tick(ServerLevel level) {
        if (!isFeaturesActive(level)) return;

        SoundTrackingHandler.tick(level);
        HordeHandler.tick(level);
        LightTrackingHandler.tick(level);

        int interval = ModConfig.getTicksBetweenSpawnsValue();
        tickCounter++;
        if (tickCounter % interval != 0) return;

        List<ServerPlayer> players = level.players();
        if (players.isEmpty()) return;

        int spawnDensity = ModConfig.getSpawnDensityValue();
        int spawnRadius = ModConfig.getSpawnRadiusValue();
        int minDist = ModConfig.getMinSpawnDistanceValue();
        int maxDimension = getMaxZombiesForDimension(level);

        for (ServerPlayer player : players) {
            if (player.isSpectator()) continue;

            int nearbyZombies = countNearbyZombies(level, player);
            int cap = Math.min(spawnDensity, maxDimension);
            int toSpawn = Math.min(cap - nearbyZombies, 5);
            if (toSpawn <= 0) continue;

            boolean playerOnSurface = level.canSeeSky(player.blockPosition().above());
            boolean isEnd = level.dimension() == Level.END;
            boolean isNether = level.dimension() == Level.NETHER;

            int effectiveMaxDist = spawnRadius;
            int effectiveMinDist = minDist;

            if (ModConfig.isGroupSpawningValue() && toSpawn >= 2 && !isEnd && !isNether) {
                spawnGroup(level, player, toSpawn, effectiveMaxDist, effectiveMinDist, playerOnSurface, isEnd, isNether);
            } else {
                for (int i = 0; i < toSpawn; i++) {
                    BlockPos pos = findSpawnPosition(level, player, effectiveMaxDist, effectiveMinDist, playerOnSurface, isEnd, isNether);
                    if (pos != null) spawnZombie(level, pos);
                }
            }
        }

        tickSoundPathfinding(level);
    }

    private static void tickSoundPathfinding(ServerLevel level) {
        if (!ModConfig.isSoundTrackingValue()) return;

        List<ServerPlayer> players = level.players();
        for (ServerPlayer player : players) {
            if (player.isSpectator()) continue;

            int range = ModConfig.getSoundHearRangeValue();
            AABB box = new AABB(
                    player.getX() - range, player.getY() - 16, player.getZ() - range,
                    player.getX() + range, player.getY() + 32, player.getZ() + range
            );

            List<Zombie> zombies = level.getEntitiesOfClass(Zombie.class, box, e -> e.getTarget() == null);
            for (Zombie zombie : zombies) {
                if (level.getRandom().nextInt(40) != 0) continue;

                BlockPos sound = SoundTrackingHandler.findNearestSound(level, zombie.blockPosition(), range);
                if (sound != null) {
                    zombie.getNavigation().moveTo(sound.getX() + 0.5, sound.getY(), sound.getZ() + 0.5, 0.8);
                }
            }
        }
    }

    private static int getMaxZombiesForDimension(ServerLevel level) {
        if (level.dimension() == Level.END) return ModConfig.getMaxZombiesEndValue();
        if (level.dimension() == Level.NETHER) return ModConfig.getMaxZombiesNetherValue();
        return ModConfig.getMaxZombiesOverworldValue();
    }

    private static int countNearbyZombies(ServerLevel level, ServerPlayer player) {
        int radius = ModConfig.getSpawnRadiusValue();
        AABB box = new AABB(
                player.getX() - radius, player.getY() - 32, player.getZ() - radius,
                player.getX() + radius, player.getY() + 32, player.getZ() + radius
        );
        return level.getEntitiesOfClass(Zombie.class, box, e -> true).size();
    }

    private static void spawnGroup(ServerLevel level, ServerPlayer player, int total, int maxDist, int minDist,
                                   boolean playerOnSurface, boolean isEnd, boolean isNether) {
        BlockPos center = findSpawnPosition(level, player, maxDist, minDist, playerOnSurface, isEnd, isNether);
        if (center == null) return;

        int groupSize;
        if (level.getRandom().nextFloat() < 0.3f) {
            groupSize = 8 + level.getRandom().nextInt(5);
        } else {
            groupSize = 2 + level.getRandom().nextInt(2);
        }
        groupSize = Math.min(groupSize, total);

        spawnZombie(level, center);

        for (int i = 1; i < groupSize; i++) {
            double offX = (level.getRandom().nextDouble() - 0.5) * 4.0;
            double offZ = (level.getRandom().nextDouble() - 0.5) * 4.0;
            int gx = center.getX() + (int) offX;
            int gz = center.getZ() + (int) offZ;

            int groundY = findGroundY(level, gx, gz, center.getY(), isNether);
            if (groundY == NOT_FOUND) continue;

            BlockPos gpos = new BlockPos(gx, groundY + 1, gz);
            if (isValidSpawnPos(level, gpos) && !checkBlockLight(level, gpos) && !isNearbyTorch(level, gpos)) {
                spawnZombie(level, gpos);
            }
        }
    }

    private static BlockPos findSpawnPosition(ServerLevel level, ServerPlayer player, int maxDist, int minDist,
                                              boolean playerOnSurface, boolean isEnd, boolean isNether) {
        int attempts = playerOnSurface || isEnd || isNether ? 32 : 64;
        for (int attempt = 0; attempt < attempts; attempt++) {
            double angle = level.getRandom().nextDouble() * Math.PI * 2;
            double dist = minDist + level.getRandom().nextDouble() * (maxDist - minDist);
            int x = (int) (player.getX() + Math.cos(angle) * dist);
            int z = (int) (player.getZ() + Math.sin(angle) * dist);

            int spawnY;
            if (isNether) {
                int netherCeiling = level.dimensionType().logicalHeight() + level.getMinY() - 3;
                int startY = Math.min(netherCeiling, (int) player.getY() + 16);
                spawnY = findGroundY(level, x, z, startY, true);
                if (spawnY == NOT_FOUND) continue;
            } else if (playerOnSurface) {
                spawnY = findSurfaceGroundY(level, x, z);
                if (spawnY == NOT_FOUND) continue;
            } else {
                int startY = (int) player.getY() + 4;
                int endY = level.getMinY() + 1;
                spawnY = findCaveGroundY(level, x, z, startY, endY);
                if (spawnY == NOT_FOUND) continue;
            }

            BlockPos pos = new BlockPos(x, spawnY + 1, z);

            if (isEnd && !level.canSeeSky(pos)) continue;
            BlockState below = level.getBlockState(pos.below());
            BlockState at = level.getBlockState(pos);
            BlockState above = level.getBlockState(pos.above());
            if (!below.blocksMotion() || at.blocksMotion() || above.blocksMotion() || !at.getFluidState().isEmpty()) {
                continue;
            }
            if (!checkBlockLight(level, pos)) continue;
            if (!isNether && isNearbyTorch(level, pos)) continue;

            return pos;
        }
        return null;
    }

    private static int findSurfaceGroundY(ServerLevel level, int x, int z) {
        int heightmapY = level.getHeight(Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, x, z);
        int minY = level.getMinY();
        for (int y = heightmapY; y >= minY; y--) {
            if (level.getBlockState(new BlockPos(x, y, z)).blocksMotion()) {
                return y;
            }
        }
        return NOT_FOUND;
    }

    private static int findCaveGroundY(ServerLevel level, int x, int z, int startY, int endY) {
        int minY = Math.max(level.getMinY(), endY);
        for (int y = startY; y >= minY; y--) {
            BlockPos pos = new BlockPos(x, y, z);
            BlockPos below = new BlockPos(x, y - 1, z);
            if (!level.getBlockState(pos).blocksMotion() && level.getBlockState(below).blocksMotion()) {
                return y - 1;
            }
        }
        return NOT_FOUND;
    }

    private static int findGroundY(ServerLevel level, int x, int z, int startY, boolean isNether) {
        int heightmapY = isNether
                ? Math.min(level.getHeight(Heightmap.Types.MOTION_BLOCKING, x, z), level.dimensionType().logicalHeight() + level.getMinY() - 3)
                : level.getHeight(Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, x, z);

        int fromY = Math.min(startY, heightmapY);
        int minY = level.getMinY();

        for (int y = fromY; y >= minY; y--) {
            BlockPos check = new BlockPos(x, y, z);
            if (level.getBlockState(check).blocksMotion()) {
                return y;
            }
        }
        return NOT_FOUND;
    }

    private static boolean isValidSpawnPos(ServerLevel level, BlockPos pos) {
        BlockState below = level.getBlockState(pos.below());
        BlockState at = level.getBlockState(pos);
        BlockState above = level.getBlockState(pos.above());
        return below.blocksMotion() && !at.blocksMotion() && !above.blocksMotion();
    }

    private static boolean checkBlockLight(ServerLevel level, BlockPos pos) {
        int maxLight = ModConfig.getMaxBlockLightValue();
        if (maxLight < 0) return true;
        return level.getBrightness(LightLayer.BLOCK, pos) <= maxLight;
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
        if (AlphaZombieHandler.shouldSpawnAlpha(level)) {
            AlphaZombieHandler.markAsAlpha(zombie);
        }
        level.addFreshEntity(zombie);
    }

    private static boolean isFeaturesActive(ServerLevel level) {
        int days = ModConfig.getDaysBeforeActivationValue();
        if (days <= 0) return true;
        return level.getDefaultClockTime() / 24000 >= days;
    }
}
