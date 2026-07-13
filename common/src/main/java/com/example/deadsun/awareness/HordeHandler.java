package com.example.deadsun.awareness;

import com.example.deadsun.config.ModConfig;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.monster.zombie.Zombie;
import net.minecraft.world.level.LightLayer;
import net.minecraft.world.phys.AABB;

import java.util.List;

public class HordeHandler {

    private static final java.util.Map<java.util.UUID, Long> LAST_WAYPOINT_TIME = new java.util.HashMap<>();

    public static void tick(ServerLevel level) {
        if (!ModConfig.isWanderingHordesValue()) return;
        if (!isFeaturesActive(level)) return;

        long now = System.currentTimeMillis();
        int frequencyMs = ModConfig.getHordeFrequencyValue() * 1000;

        List<ServerPlayer> players = level.players();
        for (ServerPlayer player : players) {
            if (player.isSpectator()) continue;
            if (player.isCrouching()) continue;

            java.util.UUID playerId = player.getUUID();
            Long lastTime = LAST_WAYPOINT_TIME.get(playerId);
            if (lastTime != null && now - lastTime < frequencyMs) continue;

            LAST_WAYPOINT_TIME.put(playerId, now);
            spawnHorde(level, player);
        }
    }

    private static void spawnHorde(ServerLevel level, ServerPlayer player) {
        int range = ModConfig.getHordeRangeValue();

        double tryX = player.getX() + (level.getRandom().nextDouble() - 0.5) * range * 2;
        double tryZ = player.getZ() + (level.getRandom().nextDouble() - 0.5) * range * 2;
        int tryY = level.getHeight(net.minecraft.world.level.levelgen.Heightmap.Types.MOTION_BLOCKING, (int) tryX, (int) tryZ);

        BlockPos waypoint = new BlockPos((int) tryX, tryY, (int) tryZ);
        if (!level.isLoaded(waypoint)) return;

        int attractRange = 32;
        net.minecraft.world.phys.AABB box = new net.minecraft.world.phys.AABB(
                waypoint.getX() - attractRange, waypoint.getY() - 8, waypoint.getZ() - attractRange,
                waypoint.getX() + attractRange, waypoint.getY() + 16, waypoint.getZ() + attractRange
        );

        List<Zombie> zombies = level.getEntitiesOfClass(Zombie.class, box, e -> true);
        for (Zombie zombie : zombies) {
            if (zombie.distanceTo(player) > range * 1.5) continue;
            zombie.getNavigation().moveTo(waypoint.getX() + 0.5, waypoint.getY(), waypoint.getZ() + 0.5, 1.0);
        }
    }

    private static boolean isFeaturesActive(ServerLevel level) {
        int days = ModConfig.getDaysBeforeActivationValue();
        if (days <= 0) return true;
        return level.getDefaultClockTime() / 24000 >= days;
    }
}
