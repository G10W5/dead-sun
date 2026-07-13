package com.example.deadsun.mixin;

import com.example.deadsun.spawn.ZombieSpawnHandler;
import net.minecraft.server.level.ServerLevel;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.function.BooleanSupplier;

@Mixin(ServerLevel.class)
public abstract class ServerLevelTickMixin {

    @Inject(method = "tick", at = @At("RETURN"))
    private void deadsun$tickSpawn(BooleanSupplier shouldKeepTicking, CallbackInfo ci) {
        ServerLevel self = (ServerLevel) (Object) this;
        ZombieSpawnHandler.tick(self);
    }
}
