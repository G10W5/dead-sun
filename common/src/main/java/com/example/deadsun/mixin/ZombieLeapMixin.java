package com.example.deadsun.mixin;

import com.example.deadsun.awareness.AlphaZombieHandler;
import com.example.deadsun.awareness.LightTrackingHandler;
import com.example.deadsun.awareness.NoisyZombieHandler;
import com.example.deadsun.config.ModConfig;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.monster.zombie.Zombie;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.List;

@Mixin(Zombie.class)
public abstract class ZombieLeapMixin {

    @Unique
    private int deadsun$leapCooldown = 0;

    @Unique
    private int deadsun$pileUpCooldown = 0;

    @Unique
    private int deadsun$stuckTicks = 0;

    @Inject(method = "tick", at = @At("RETURN"))
    private void deadsun$zombieTick(CallbackInfo ci) {
        Zombie self = (Zombie) (Object) this;
        if (self.level().isClientSide()) return;
        if (!isFeaturesActive(self)) return;

        ServerLevel level = (ServerLevel) self.level();

        deadsun$tryLeap(self);
        deadsun$tryPileUp(self);

        LightTrackingHandler.tick(level, self);
        NoisyZombieHandler.tick(level, self);
        AlphaZombieHandler.tickParticles(self);
    }

    @Unique
    private void deadsun$tryLeap(Zombie self) {
        if (!ModConfig.isZombieLeapValue()) return;
        if (deadsun$leapCooldown > 0) { deadsun$leapCooldown--; return; }
        if (!self.onGround()) return;

        ServerLevel level = (ServerLevel) self.level();
        net.minecraft.world.entity.player.Player nearest = level.getNearestPlayer(self, 8.0);
        if (!(nearest instanceof ServerPlayer target)) return;
        if (target.isCreative() || target.isSpectator()) return;
        if (target.isCrouching() && !self.hasLineOfSight(target)) return;

        double dist = self.distanceTo(target);
        if (dist > 4.0 || dist < 1.5) return;

        Vec3 dir = target.position().subtract(self.position()).normalize();
        float strength = ModConfig.getLeapStrengthValue();
        float height = ModConfig.getLeapHeightValue();

        self.setDeltaMovement(dir.x * strength, height, dir.z * strength);
        deadsun$leapCooldown = ModConfig.getLeapCooldownValue();
    }

    @Unique
    private void deadsun$tryPileUp(Zombie self) {
        if (!ModConfig.isZombiePileUpValue()) return;
        if (deadsun$pileUpCooldown > 0) { deadsun$pileUpCooldown--; return; }
        if (self.getTarget() == null) return;

        ServerLevel level = (ServerLevel) self.level();

        float yRot = self.getYRot();
        double dirX = -Math.sin(Math.toRadians(yRot));
        double dirZ = Math.cos(Math.toRadians(yRot));

        BlockPos front = self.blockPosition().offset(
                (int) Math.round(dirX), 0, (int) Math.round(dirZ));

        int wallHeight = deadsun$wallHeight(level, front);
        if (wallHeight < 2) {
            deadsun$stuckTicks = Math.max(0, deadsun$stuckTicks - 2);
            return;
        }

        boolean navGaveUp = self.getNavigation().isDone();
        if (!self.horizontalCollision && !navGaveUp) {
            deadsun$stuckTicks = Math.max(0, deadsun$stuckTicks - 2);
            return;
        }

        deadsun$stuckTicks++;
        if (deadsun$stuckTicks < 6) return;
        deadsun$stuckTicks = 0;

        int surfaceY = level.getHeight(Heightmap.Types.MOTION_BLOCKING, (int) self.getX(), (int) self.getZ());
        if (self.getY() > surfaceY + wallHeight + 1) return;

        double pileTopY = deadsun$nearbyPileHeight(level, self, front);
        double climbTarget = Math.min(pileTopY + 1.1, self.getY() + wallHeight + 0.5);
        double heightGain = Math.max(0.5, climbTarget - self.getY());

        float vertBoost = (float) Math.min(heightGain * 0.42, 0.55);
        float horizBoost = 0.22f + (wallHeight - 2) * 0.03f;

        self.setDeltaMovement(
                self.getDeltaMovement().x() + dirX * horizBoost,
                vertBoost + self.getRandom().nextFloat() * 0.05f,
                self.getDeltaMovement().z() + dirZ * horizBoost
        );
        self.fallDistance = 0;
        self.hurtMarked = true;
        self.addEffect(new MobEffectInstance(MobEffects.SLOW_FALLING, 8, 0, false, false));

        deadsun$pileUpCooldown = 10 + self.getRandom().nextInt(8);
    }

    @Unique
    private static double deadsun$nearbyPileHeight(ServerLevel level, Zombie self, BlockPos front) {
        double radius = 1.4;
        AABB box = new AABB(
                front.getX() - radius, self.getY() - 1.0, front.getZ() - radius,
                front.getX() + radius + 1, self.getY() + 4.0, front.getZ() + radius + 1
        );
        List<Zombie> nearby = level.getEntitiesOfClass(Zombie.class, box, z -> z != self && z.isAlive());
        double maxY = self.getY();
        for (Zombie z : nearby) {
            if (z.getY() > maxY) maxY = z.getY();
        }
        return maxY;
    }

    @Unique
    private static int deadsun$wallHeight(ServerLevel level, BlockPos front) {
        int height = 0;
        for (int i = 0; i < 3; i++) {
            if (level.getBlockState(front.above(i)).blocksMotion()) {
                height++;
            } else {
                break;
            }
        }
        return height;
    }

    @Unique
    private static boolean isFeaturesActive(Zombie self) {
        int days = ModConfig.getDaysBeforeActivationValue();
        if (days <= 0) return true;
        return self.level().getDefaultClockTime() / 24000 >= days;
    }
}
