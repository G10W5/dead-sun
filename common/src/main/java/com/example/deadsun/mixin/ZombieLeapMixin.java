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
import net.minecraft.world.entity.MoverType;
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

    @Unique
    private boolean deadsun$isClimbing = false;

    @Unique
    private double deadsun$climbTargetY = 0;

    @Unique
    private int deadsun$crestTicks = 0;

    @Unique
    private double deadsun$crestDirX = 0;

    @Unique
    private double deadsun$crestDirZ = 0;

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

        ServerLevel level = (ServerLevel) self.level();

        if (deadsun$isClimbing || deadsun$crestTicks > 0) {
            if (self.getTarget() == null) {
                deadsun$isClimbing = false;
                deadsun$crestTicks = 0;
                return;
            }
            if (deadsun$crestTicks > 0) {
                deadsun$continueCrest(self, level);
            } else {
                deadsun$continueClimb(self, level);
            }
            return;
        }

        if (self.getTarget() == null) return;

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

        deadsun$isClimbing = true;
        deadsun$climbTargetY = self.getY() + heightGain;

        net.minecraft.world.entity.player.Player nearest = level.getNearestPlayer(self, 16.0);
        if (nearest != null) {
            Vec3 toTarget = nearest.position().subtract(self.position()).normalize();
            deadsun$crestDirX = toTarget.x;
            deadsun$crestDirZ = toTarget.z;
        } else {
            deadsun$crestDirX = dirX;
            deadsun$crestDirZ = dirZ;
        }

        deadsun$pileUpCooldown = 10 + self.getRandom().nextInt(8);
    }

    @Unique
    private void deadsun$continueClimb(Zombie self, ServerLevel level) {
        if (self.getY() >= deadsun$climbTargetY) {
            deadsun$isClimbing = false;
            deadsun$crestTicks = 6;
            return;
        }

        double climbSpeed = 0.2;
        self.setDeltaMovement(0, 0, 0);
        self.move(MoverType.SELF, new Vec3(0, climbSpeed, 0));
        self.fallDistance = 0;
        self.hurtMarked = true;
        self.addEffect(new MobEffectInstance(MobEffects.SLOW_FALLING, 8, 0, false, false));
    }

    @Unique
    private void deadsun$continueCrest(Zombie self, ServerLevel level) {
        deadsun$crestTicks--;
        if (deadsun$crestTicks <= 0) {
            return;
        }

        double crestSpeed = 0.3;
        self.setDeltaMovement(0, 0, 0);
        self.move(MoverType.SELF, new Vec3(
                deadsun$crestDirX * crestSpeed,
                0.05,
                deadsun$crestDirZ * crestSpeed
        ));
        self.fallDistance = 0;
        self.hurtMarked = true;
        self.addEffect(new MobEffectInstance(MobEffects.SLOW_FALLING, 8, 0, false, false));
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
        for (int i = 0; i < 6; i++) {
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
