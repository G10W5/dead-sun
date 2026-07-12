package com.example.deadsun.mixin;

import com.example.deadsun.config.ModConfig;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.EntitySpawnReason;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.monster.zombie.Zombie;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(Mob.class)
public abstract class MobSpawnMixin {

    private static boolean deadsun$isNearbyTorch(LevelAccessor level, BlockPos pos) {
        int radius = ModConfig.getTorchRadiusValue();
        for (int x = -radius; x <= radius; x++) {
            for (int y = -radius; y <= radius; y++) {
                for (int z = -radius; z <= radius; z++) {
                    if (x * x + y * y + z * z > radius * radius) continue;
                    Block block = level.getBlockState(pos.offset(x, y, z)).getBlock();
                    if (block == Blocks.TORCH || block == Blocks.WALL_TORCH
                            || block == Blocks.SOUL_TORCH || block == Blocks.SOUL_WALL_TORCH
                            || block == Blocks.COPPER_TORCH || block == Blocks.COPPER_WALL_TORCH) {
                        return true;
                    }
                }
            }
        }
        return false;
    }

    @Inject(method = "checkSpawnRules", at = @At("HEAD"), cancellable = true)
    private void deadsun$zombieSpawnRules(LevelAccessor level, EntitySpawnReason reason, CallbackInfoReturnable<Boolean> cir) {
        if ((Object) this instanceof Zombie) {
            BlockPos pos = ((Mob) (Object) this).blockPosition();
            if (deadsun$isNearbyTorch(level, pos)) {
                cir.setReturnValue(false);
            } else {
                cir.setReturnValue(true);
            }
        }
    }
}
