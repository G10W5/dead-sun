package com.example.deadsun.mixin;

import com.example.deadsun.config.ModConfig;
import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.EntityTypeIds;
import net.minecraft.world.entity.SpawnPlacements;
import net.minecraft.world.level.ServerLevelAccessor;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(SpawnPlacements.class)
public abstract class SpawnPlacementsMixin {

    private static EntityType<?> deadsun$entity(ResourceKey<EntityType<?>> key) {
        return BuiltInRegistries.ENTITY_TYPE.getValue(key.identifier());
    }

    private static boolean deadsun$isZombieVariant(EntityType<?> type) {
        return type == deadsun$entity(EntityTypeIds.ZOMBIE)
                || type == deadsun$entity(EntityTypeIds.HUSK)
                || type == deadsun$entity(EntityTypeIds.DROWNED)
                || type == deadsun$entity(EntityTypeIds.ZOMBIE_VILLAGER);
    }

    private static boolean deadsun$isNearbyTorch(ServerLevelAccessor level, BlockPos pos) {
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
    private static <T extends net.minecraft.world.entity.Entity> void deadsun$allowZombieSpawns(
            EntityType<T> type, ServerLevelAccessor level,
            net.minecraft.world.entity.EntitySpawnReason reason,
            BlockPos pos, RandomSource random,
            CallbackInfoReturnable<Boolean> cir
    ) {
        if (deadsun$isZombieVariant(type)) {
            if (deadsun$isNearbyTorch(level, pos)) {
                cir.setReturnValue(false);
            } else {
                cir.setReturnValue(true);
            }
        }
    }
}
