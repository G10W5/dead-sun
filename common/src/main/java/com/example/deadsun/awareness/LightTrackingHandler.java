package com.example.deadsun.awareness;

import com.example.deadsun.config.ModConfig;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.monster.zombie.Zombie;
import net.minecraft.world.level.LightLayer;
import net.minecraft.world.phys.AABB;

import java.util.List;

public class LightTrackingHandler {

    private static int tickCounter = 0;

    public static void tick(ServerLevel level) {
        if (!ModConfig.isLightTrackingValue()) return;
        if (!isFeaturesActive(level)) return;

        tickCounter++;
        if (tickCounter % 40 != 0) return;

        List<ServerPlayer> players = level.players();
        if (players.isEmpty()) return;

        for (ServerPlayer player : players) {
            if (player.isSpectator()) continue;
            if (!isNight(level)) continue;

            int range = ModConfig.getLightSearchRangeValue();
            AABB box = new AABB(
                    player.getX() - range, player.getY() - 16, player.getZ() - range,
                    player.getX() + range, player.getY() + 32, player.getZ() + range
            );

            List<Zombie> zombies = level.getEntitiesOfClass(Zombie.class, box, e -> true);
            for (Zombie zombie : zombies) {
                if (zombie.distanceTo(player) <= 16) continue;
                if (!zombie.onGround()) continue;
                if (level.getRandom().nextInt(5) != 0) continue;

                tryPathToLight(level, zombie, range);
            }
        }
    }

    public static void tick(ServerLevel level, Zombie zombie) {
        if (!ModConfig.isLightTrackingValue()) return;
        if (!isFeaturesActive(level)) return;
        if (isNight(level)) return;

        if (level.getRandom().nextInt(40) != 0) return;
        if (!zombie.onGround()) return;

        int range = ModConfig.getLightSearchRangeValue();
        tryPathToLight(level, zombie, range);
    }

    private static void tryPathToLight(ServerLevel level, Zombie zombie, int range) {
        float minBrightness = ModConfig.getLightMinBrightnessValue();

        for (int attempt = 0; attempt < 8; attempt++) {
            int x = (int) zombie.getX() + level.getRandom().nextIntBetweenInclusive(-range, range);
            int y = (int) zombie.getY() + level.getRandom().nextIntBetweenInclusive(-8, 16);
            int z = (int) zombie.getZ() + level.getRandom().nextIntBetweenInclusive(-range, range);

            BlockPos pos = new BlockPos(x, y, z);
            if (!level.isLoaded(pos)) continue;

            float brightness = level.getBrightness(LightLayer.BLOCK, pos);
            if (brightness < minBrightness) continue;

            zombie.getNavigation().moveTo(x + 0.5, y, z + 0.5, 0.8);
            return;
        }
    }

    private static boolean isNight(ServerLevel level) {
        return !level.isBrightOutside();
    }

    private static boolean isFeaturesActive(ServerLevel level) {
        int days = ModConfig.getDaysBeforeActivationValue();
        if (days <= 0) return true;
        return level.getDefaultClockTime() / 24000 >= days;
    }
}
