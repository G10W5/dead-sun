package com.example.deadsun.mixin;

import com.example.deadsun.awareness.SoundTrackingHandler;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayerGameMode;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(ServerPlayerGameMode.class)
public abstract class PlayerActivityMixin {

    @Shadow
    protected net.minecraft.server.level.ServerPlayer player;

    @Inject(method = "destroyBlock", at = @At("RETURN"))
    private void deadsun$onBlockBreak(BlockPos pos, CallbackInfoReturnable<Boolean> cir) {
        if (!cir.getReturnValue()) return;

        if (player.level().isClientSide()) return;

        ServerLevel level = (ServerLevel) player.level();
        if (!SoundTrackingHandler.isFeaturesActive(level)) return;

        SoundTrackingHandler.addSound(level, pos, 80);
    }
}
