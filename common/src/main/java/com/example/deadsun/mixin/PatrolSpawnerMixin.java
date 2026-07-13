package com.example.deadsun.mixin;

import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.levelgen.PatrolSpawner;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(PatrolSpawner.class)
public abstract class PatrolSpawnerMixin {

    @Inject(method = "tick", at = @At("HEAD"), cancellable = true)
    private void deadsun$cancelPatrol(ServerLevel level, boolean forceSpawn, CallbackInfo ci) {
        ci.cancel();
    }
}
