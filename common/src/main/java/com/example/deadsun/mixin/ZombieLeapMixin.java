package com.example.deadsun.mixin;

import com.example.deadsun.config.ModConfig;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.monster.zombie.Zombie;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.world.phys.Vec3;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Zombie.class)
public abstract class ZombieLeapMixin {

    @Unique
    private int deadsun$leapCooldown = 0;

    @Unique
    private int deadsun$pileUpCooldown = 0;

    @Inject(method = "tick", at = @At("RETURN"))
    private void deadsun$zombieTick(CallbackInfo ci) {
        Zombie self = (Zombie) (Object) this;
        if (self.level().isClientSide()) return;

        deadsun$tryLeap(self);
        deadsun$tryPileUp(self);
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

        double dist = self.distanceTo(target);
        if (dist > 4.0 || dist < 1.5) return;

        Vec3 dir = target.position().subtract(self.position()).normalize();
        float strength = ModConfig.getLeapStrengthValue();
        float height = ModConfig.getLeapHeightValue();

        self.setDeltaMovement(dir.x * strength, height, dir.z * strength);
        deadsun$leapCooldown = 20 + self.getRandom().nextInt(15);
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
        boolean wallAhead = level.getBlockState(front).blocksMotion();

        if (!wallAhead) return;

        int surfaceY = level.getHeight(Heightmap.Types.MOTION_BLOCKING, (int) self.getX(), (int) self.getZ());
        if (self.getY() > surfaceY + 4) return;

        float rand = 0.18f + self.getRandom().nextFloat() * 0.06f;

        self.setDeltaMovement(
                self.getDeltaMovement().x() + (rand / 15) * (self.getRandom().nextFloat() * 2 - 1),
                rand,
                self.getDeltaMovement().z() + (rand / 15) * (self.getRandom().nextFloat() * 2 - 1)
        );
        self.fallDistance = 0;
        self.hurtMarked = true;
        self.addEffect(new MobEffectInstance(MobEffects.SLOW_FALLING, 8, 0, false, false));

        deadsun$pileUpCooldown = 4 + self.getRandom().nextInt(6);
    }
}
