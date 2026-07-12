package com.example.deadsun.mixin;

import net.minecraft.world.entity.Mob;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Mob.class)
public abstract class MobSunMixin {

    @Inject(method = "burnUndead", at = @At("HEAD"), cancellable = true)
    private void deadsun$preventSunBurn(CallbackInfo ci) {
        ci.cancel();
    }
}
