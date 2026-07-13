package com.example.deadsun.awareness;

import com.example.deadsun.config.ModConfig;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.monster.zombie.Zombie;
import net.minecraft.world.phys.AABB;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class NoisyZombieHandler {

    private static final Map<Integer, Long> LAST_CALL_TIME = new HashMap<>();

    public static void tick(ServerLevel level, Zombie zombie) {
        if (!ModConfig.isNoisyZombiesValue()) return;
        if (!isFeaturesActive(level)) return;

        int entityId = zombie.getId();
        long now = level.getGameTime();
        int cooldown = ModConfig.getNoisyZombieCooldownValue();

        Long lastCall = LAST_CALL_TIME.get(entityId);
        if (lastCall != null && now - lastCall < cooldown) return;

        ServerPlayer nearest = (ServerPlayer) level.getNearestPlayer(zombie, 12.0);
        if (nearest == null) return;
        if (nearest.isCreative() || nearest.isSpectator()) return;
        if (nearest.isCrouching()) return;

        if (!zombie.hasLineOfSight(nearest)) return;

        LAST_CALL_TIME.put(entityId, now);

        zombie.playSound(SoundEvents.ZOMBIE_AMBIENT, 1.0f, 0.8f + zombie.getRandom().nextFloat() * 0.4f);

        int range = ModConfig.getNoisyZombieRangeValue();
        AABB box = new AABB(
                zombie.getX() - range, zombie.getY() - 8, zombie.getZ() - range,
                zombie.getX() + range, zombie.getY() + 8, zombie.getZ() + range
        );

        List<Zombie> others = level.getEntitiesOfClass(Zombie.class, box,
                e -> e != zombie && e.getTarget() == null);

        for (Zombie other : others) {
            other.getNavigation().moveTo(
                    zombie.getX() + (other.getRandom().nextDouble() - 0.5) * 4,
                    zombie.getY(),
                    zombie.getZ() + (other.getRandom().nextDouble() - 0.5) * 4,
                    0.8
            );
        }
    }

    private static boolean isFeaturesActive(ServerLevel level) {
        int days = ModConfig.getDaysBeforeActivationValue();
        if (days <= 0) return true;
        return level.getDefaultClockTime() / 24000 >= days;
    }
}
