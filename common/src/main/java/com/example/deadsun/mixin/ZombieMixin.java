package com.example.deadsun.mixin;

import com.example.deadsun.config.ModConfig;
import net.minecraft.world.entity.SpawnGroupData;
import net.minecraft.world.entity.monster.zombie.Zombie;
import net.minecraft.world.level.ServerLevelAccessor;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(Zombie.class)
public abstract class ZombieMixin {

    @Inject(method = "finalizeSpawn", at = @At("RETURN"))
    private void deadsun$forceAdult(ServerLevelAccessor level,
                                    net.minecraft.world.DifficultyInstance difficulty,
                                    net.minecraft.world.entity.EntitySpawnReason reason,
                                    SpawnGroupData spawnData,
                                    CallbackInfoReturnable<SpawnGroupData> cir) {
        if (!ModConfig.isAllowBabyZombiesValue()) {
            Zombie self = (Zombie) (Object) this;
            self.setBaby(false);
        }
    }
}
