package com.example.deadsun.mixin;

import com.example.deadsun.awareness.AlphaZombieHandler;
import com.example.deadsun.awareness.LightTrackingHandler;
import com.example.deadsun.awareness.NoisyZombieHandler;
import com.example.deadsun.config.ModConfig;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.MoverType;
import net.minecraft.world.entity.monster.zombie.Zombie;
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

    @Unique private int deadsun$leapCooldown = 0;
    @Unique private int deadsun$pileUpCooldown = 0;
    @Unique private int deadsun$stuckTicks = 0;

    @Unique private boolean deadsun$isClimbing = false;
    @Unique private double  deadsun$climbTargetY = 0;
    @Unique private double  deadsun$wallDirX = 0;
    @Unique private double  deadsun$wallDirZ = 0;
    @Unique private float   deadsun$climbSpeed = 0;

    @Unique private int    deadsun$crestTicks = 0;
    @Unique private double deadsun$crestDirX = 0;
    @Unique private double deadsun$crestDirZ = 0;

    @Inject(method = "tick", at = @At("RETURN"))
    private void deadsun$zombieTick(CallbackInfo ci) {
        Zombie self = (Zombie) (Object) this;
        if (self.level().isClientSide()) return;
        if (!isFeaturesActive(self)) return;

        ServerLevel level = (ServerLevel) self.level();

        deadsun$tryLeap(self, level);
        deadsun$tryPileUp(self, level);

        LightTrackingHandler.tick(level, self);
        NoisyZombieHandler.tick(level, self);
        AlphaZombieHandler.tickParticles(self);
    }

    @Unique
    private void deadsun$tryLeap(Zombie self, ServerLevel level) {
        if (!ModConfig.isZombieLeapValue()) return;
        if (deadsun$leapCooldown > 0) { deadsun$leapCooldown--; return; }
        if (!self.onGround()) return;

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
        self.hurtMarked = true;
        deadsun$leapCooldown = ModConfig.getLeapCooldownValue();
    }

    @Unique
    private void deadsun$tryPileUp(Zombie self, ServerLevel level) {
        if (!ModConfig.isZombiePileUpValue()) return;
        if (deadsun$pileUpCooldown > 0) { deadsun$pileUpCooldown--; return; }

        if (deadsun$isClimbing || deadsun$crestTicks > 0) {
            if (self.getTarget() == null || (self.hurtTime > 0 && deadsun$isClimbing)) {
                deadsun$stopClimb(self);
                return;
            }
            if (deadsun$crestTicks > 0) deadsun$continueCrest(self);
            else deadsun$continueClimb(self, level);
            return;
        }

        if (self.getTarget() == null) return;

        Vec3 toTarget = self.getTarget().position().subtract(self.position());
        Vec3 horizDir = new Vec3(toTarget.x, 0, toTarget.z).normalize();
        if (horizDir.lengthSqr() < 0.001) return;

        BlockPos front = self.blockPosition().offset(
                (int) Math.round(horizDir.x), 0, (int) Math.round(horizDir.z));

        int wallHeight = deadsun$wallHeight(level, front);
        if (wallHeight < 2) {
            deadsun$stuckTicks = Math.max(0, deadsun$stuckTicks - 2);
            return;
        }

        if (!self.horizontalCollision && !self.getNavigation().isDone()
                && !deadsun$targetBehindWall(self, self.getTarget(), level)) {
            deadsun$stuckTicks = Math.max(0, deadsun$stuckTicks - 2);
            return;
        }
        deadsun$stuckTicks++;
        if (deadsun$stuckTicks < 6) return;
        deadsun$stuckTicks = 0;

        boolean supported = self.onGround() || deadsun$hasZombieSupport(level, self);
        if (!supported) return;

        int minGroup = ModConfig.getPileUpMinGroupSizeValue();
        int packSize = level.getEntitiesOfClass(Zombie.class,
                self.getBoundingBox().inflate(3.0), Zombie::isAlive).size();
        if (packSize < minGroup) return;

        double pileTopY = deadsun$nearbyPileHeight(level, self, front);
        float stepMin = ModConfig.getClimbStepMinValue();
        float stepMax = ModConfig.getClimbStepMaxValue();
        double stepHeight = stepMin + self.getRandom().nextDouble() * (stepMax - stepMin);
        double maxY = self.getY() + wallHeight + 0.5;
        double targetY = Math.min(pileTopY + stepHeight, maxY);

        if (targetY - self.getY() < 0.4) return;

        deadsun$isClimbing = true;
        deadsun$climbTargetY = targetY;
        deadsun$wallDirX = horizDir.x;
        deadsun$wallDirZ = horizDir.z;

        float sMin = ModConfig.getClimbSpeedMinValue();
        float sMax = ModConfig.getClimbSpeedMaxValue();
        deadsun$climbSpeed = sMin + self.getRandom().nextFloat() * (sMax - sMin);

        deadsun$crestDirX = horizDir.x;
        deadsun$crestDirZ = horizDir.z;

        self.setNoGravity(true);
        self.hurtMarked = true;

        deadsun$pileUpCooldown = 8 + self.getRandom().nextInt(12);
    }

    @Unique
    private void deadsun$continueClimb(Zombie self, ServerLevel level) {
        if (self.getY() >= deadsun$climbTargetY) {
            deadsun$isClimbing = false;
            deadsun$crestTicks = 10 + self.getRandom().nextInt(6);
            return;
        }

        if (self.tickCount % 8 == 0) {
            self.swing(InteractionHand.MAIN_HAND);
            level.playSound(null, self.blockPosition(), SoundEvents.ZOMBIE_STEP,
                    SoundSource.HOSTILE, 0.4f, 0.7f + self.getRandom().nextFloat() * 0.5f);
        }

        double wobbleX = (self.getRandom().nextDouble() - 0.5) * 0.04;
        double wobbleZ = (self.getRandom().nextDouble() - 0.5) * 0.04;

        double pressStrength = 0.04;

        double remaining = deadsun$climbTargetY - self.getY();
        double speedMult = Math.min(1.0, remaining / 1.5);
        double speed = deadsun$climbSpeed * (0.5 + 0.5 * speedMult);

        self.setDeltaMovement(
                deadsun$wallDirX * pressStrength + wobbleX,
                speed,
                deadsun$wallDirZ * pressStrength + wobbleZ
        );
        self.fallDistance = 0;
    }

    @Unique
    private void deadsun$continueCrest(Zombie self) {
        deadsun$crestTicks--;
        if (deadsun$crestTicks <= 0) {
            deadsun$stopClimb(self);
            return;
        }

        double progress = 1.0 - (deadsun$crestTicks / 16.0);
        double fwdSpeed = 0.18 + 0.10 * progress;
        double vertSpeed = 0.06 * (1.0 - progress * 1.5);

        double wobbleX = (self.getRandom().nextDouble() - 0.5) * 0.06;
        double wobbleZ = (self.getRandom().nextDouble() - 0.5) * 0.06;

        self.setDeltaMovement(
                deadsun$crestDirX * fwdSpeed + wobbleX,
                vertSpeed,
                deadsun$crestDirZ * fwdSpeed + wobbleZ
        );
        self.fallDistance = 0;
    }

    @Unique
    private void deadsun$stopClimb(Zombie self) {
        deadsun$isClimbing = false;
        deadsun$crestTicks = 0;
        self.setNoGravity(false);
    }

    @Unique
    private static boolean deadsun$hasZombieSupport(ServerLevel level, Zombie self) {
        AABB below = new AABB(
                self.getX() - 0.4, self.getY() - 0.6, self.getZ() - 0.4,
                self.getX() + 0.4, self.getY() - 0.05, self.getZ() + 0.4
        );
        List<Zombie> supporters = level.getEntitiesOfClass(Zombie.class, below, z -> z != self && z.isAlive());
        return !supporters.isEmpty();
    }

    @Unique
    private static double deadsun$nearbyPileHeight(ServerLevel level, Zombie self, BlockPos front) {
        double radius = 1.6;
        AABB box = new AABB(
                front.getX() - radius, self.getY() - 1.0, front.getZ() - radius,
                front.getX() + radius + 1, self.getY() + 6.0, front.getZ() + radius + 1
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
        int maxScan = ModConfig.getMaxWallScanValue();
        int height = 0;
        for (int i = 0; i < maxScan; i++) {
            if (level.getBlockState(front.above(i)).blocksMotion()) {
                height++;
            } else {
                break;
            }
        }
        return height;
    }

    @Unique
    private static boolean deadsun$targetBehindWall(Zombie self, net.minecraft.world.entity.LivingEntity target, ServerLevel level) {
        double dist = self.distanceTo(target);
        if (dist > 16.0 || dist < 1.0) return false;

        Vec3 dir = target.position().subtract(self.position()).normalize();
        int steps = (int) Math.ceil(dist);
        for (int i = 1; i <= steps; i++) {
            BlockPos check = self.blockPosition().offset(
                    (int) Math.round(dir.x * i), 0, (int) Math.round(dir.z * i));
            if (level.getBlockState(check).blocksMotion()) {
                return true;
            }
        }
        return false;
    }

    @Unique
    private static boolean isFeaturesActive(Zombie self) {
        int days = ModConfig.getDaysBeforeActivationValue();
        if (days <= 0) return true;
        return self.level().getDefaultClockTime() / 24000 >= days;
    }
}
