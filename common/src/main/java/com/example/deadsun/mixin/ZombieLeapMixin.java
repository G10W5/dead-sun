package com.example.deadsun.mixin;

import com.example.deadsun.config.ModConfig;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
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
        if (!self.onGround()) return;

        ServerLevel level = (ServerLevel) self.level();

        float yRot = self.getYRot();
        double dirX = -Math.sin(Math.toRadians(yRot));
        double dirZ = Math.cos(Math.toRadians(yRot));

        BlockPos front = self.blockPosition().offset(
                (int) Math.round(dirX), 0, (int) Math.round(dirZ));

        if (!level.getBlockState(front).blocksMotion()) return;

        double cx = self.getX() + dirX * 0.5;
        double cz = self.getZ() + dirZ * 0.5;

        AABB searchBox = new AABB(
                cx - 1.0, self.getY() - 0.5, cz - 1.0,
                cx + 1.0, self.getY() + 3.0, cz + 1.0
        );

        List<Zombie> nearby = level.getEntitiesOfClass(Zombie.class, searchBox,
                z -> z != self && z.onGround() && z.getY() >= self.getY() - 1.0 && z.getY() <= self.getY() + 1.0);

        if (nearby.isEmpty()) return;

        Zombie target = nearby.get(0);
        double newY = target.getY() + target.getBbHeight();
        BlockPos aboveTarget = BlockPos.containing(self.getX(), newY, self.getZ());

        if (!level.getBlockState(aboveTarget).blocksMotion()
                && !level.getBlockState(aboveTarget.above()).blocksMotion()) {
            self.snapTo(self.getX(), newY, self.getZ());
            self.setDeltaMovement(Vec3.ZERO);
            deadsun$pileUpCooldown = 10 + self.getRandom().nextInt(10);
        }
    }
}
