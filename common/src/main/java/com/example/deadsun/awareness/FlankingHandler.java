package com.example.deadsun.awareness;

import com.example.deadsun.config.ModConfig;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.monster.zombie.Zombie;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;

import java.util.Comparator;
import java.util.List;

public class FlankingHandler {

    private static int tickCounter = 0;

    public static void tick(ServerLevel level) {
        if (!ModConfig.isCoordinatedFlankingValue()) return;

        tickCounter++;
        int interval = ModConfig.getFlankingUpdateIntervalValue();
        if (tickCounter % interval != 0) return;

        List<ServerPlayer> players = level.players();
        if (players.isEmpty()) return;

        int range = ModConfig.getFlankingRangeValue();
        int minGroupSize = ModConfig.getFlankingMinGroupSizeValue();

        for (ServerPlayer player : players) {
            if (player.isSpectator() || player.isCreative()) continue;

            AABB box = new AABB(
                    player.getX() - range, player.getY() - 16, player.getZ() - range,
                    player.getX() + range, player.getY() + 32, player.getZ() + range
            );

            List<Zombie> attackers = level.getEntitiesOfClass(Zombie.class, box,
                    e -> e.isAlive() && e.getTarget() == player);

            if (attackers.size() < minGroupSize) continue;

            attackers.sort(Comparator.comparing(z -> z.getUUID().toString()));
            applyFlankAssignments(attackers, player);
        }
    }

    private static void applyFlankAssignments(List<Zombie> attackers, ServerPlayer target) {
        int count = attackers.size();
        double flankRadius = ModConfig.getFlankingRadiusValue();
        double engageDistance = ModConfig.getFlankingEngageDistanceValue();
        double navSpeed = ModConfig.getFlankingNavSpeedValue();

        Vec3 targetPos = target.position();

        for (int i = 0; i < count; i++) {
            Zombie zombie = attackers.get(i);

            double distToTarget = zombie.position().distanceTo(targetPos);
            if (distToTarget <= engageDistance) continue;

            double angle = (2 * Math.PI * i) / count;
            double flankX = targetPos.x + Math.cos(angle) * flankRadius;
            double flankZ = targetPos.z + Math.sin(angle) * flankRadius;
            double flankY = zombie.getY();

            zombie.getNavigation().moveTo(flankX, flankY, flankZ, navSpeed);
        }
    }
}
