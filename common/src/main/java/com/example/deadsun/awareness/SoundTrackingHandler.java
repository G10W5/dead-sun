package com.example.deadsun.awareness;

import com.example.deadsun.config.ModConfig;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.level.ServerPlayerGameMode;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

public class SoundTrackingHandler {

    private static final Map<ServerLevel, List<SoundMarker>> SOUND_MAP = new HashMap<>();

    public static void addSound(ServerLevel level, BlockPos pos, float strength) {
        if (!ModConfig.isSoundTrackingValue()) return;

        List<SoundMarker> markers = SOUND_MAP.computeIfAbsent(level, k -> new ArrayList<>());

        for (SoundMarker m : markers) {
            if (m.pos.distSqr(pos) < 9) {
                m.strength = Math.min(m.strength + strength * 0.5f, 150);
                m.age = 0;
                return;
            }
        }

        markers.add(new SoundMarker(pos, strength));
    }

    public static BlockPos findNearestSound(ServerLevel level, BlockPos source, int range) {
        List<SoundMarker> markers = SOUND_MAP.get(level);
        if (markers == null || markers.isEmpty()) return null;

        BlockPos best = null;
        double bestDist = range * range;

        for (SoundMarker m : markers) {
            double dist = m.pos.distSqr(source);
            if (dist < bestDist) {
                bestDist = dist;
                best = m.pos;
            }
        }

        return best;
    }

    public static List<SoundMarker> getMarkers(ServerLevel level) {
        return SOUND_MAP.getOrDefault(level, new ArrayList<>());
    }

    public static void tick(ServerLevel level) {
        List<SoundMarker> markers = SOUND_MAP.get(level);
        if (markers == null || markers.isEmpty()) return;

        int decayTicks = ModConfig.getSoundDecayTimeValue();
        Iterator<SoundMarker> it = markers.iterator();
        while (it.hasNext()) {
            SoundMarker m = it.next();
            m.age++;
            if (m.age > decayTicks) {
                it.remove();
            }
        }
    }

    public static boolean isFeaturesActive(ServerLevel level) {
        int days = ModConfig.getDaysBeforeActivationValue();
        if (days <= 0) return true;
        return level.getDefaultClockTime() / 24000 >= days;
    }

    public static class SoundMarker {
        public final BlockPos pos;
        public float strength;
        public int age;

        public SoundMarker(BlockPos pos, float strength) {
            this.pos = pos;
            this.strength = strength;
            this.age = 0;
        }
    }
}
