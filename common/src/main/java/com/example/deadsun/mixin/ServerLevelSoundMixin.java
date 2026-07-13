package com.example.deadsun.mixin;

import com.example.deadsun.awareness.SoundTrackingHandler;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.Entity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ServerLevel.class)
public abstract class ServerLevelSoundMixin {

    @Inject(method = "playSeededSound(Lnet/minecraft/world/entity/Entity;DDDLnet/minecraft/core/Holder;Lnet/minecraft/sounds/SoundSource;FFJ)V", at = @At("HEAD"))
    private void deadsun$trackSoundPos(Entity entity, double x, double y, double z,
                                       Holder<SoundEvent> event, SoundSource source,
                                       float volume, float pitch, long seed,
                                       CallbackInfo ci) {
        ServerLevel self = (ServerLevel) (Object) this;
        if (!SoundTrackingHandler.isFeaturesActive(self)) return;
        if (source == SoundSource.MUSIC || source == SoundSource.RECORDS) return;

        boolean isPlayer = entity instanceof ServerPlayer;
        float strength = volume * 40;
        if (isPlayer && ((ServerPlayer) entity).isCrouching()) {
            strength *= 0.25f;
        }

        BlockPos pos = new BlockPos((int) x, (int) y, (int) z);
        SoundTrackingHandler.addSound(self, pos, strength);
    }

    @Inject(method = "playSeededSound(Lnet/minecraft/world/entity/Entity;Lnet/minecraft/world/entity/Entity;Lnet/minecraft/core/Holder;Lnet/minecraft/sounds/SoundSource;FFJ)V", at = @At("HEAD"))
    private void deadsun$trackSoundEnt(Entity emitter, Entity entity,
                                       Holder<SoundEvent> event, SoundSource source,
                                       float volume, float pitch, long seed,
                                       CallbackInfo ci) {
        ServerLevel self = (ServerLevel) (Object) this;
        if (!SoundTrackingHandler.isFeaturesActive(self)) return;
        if (source == SoundSource.MUSIC || source == SoundSource.RECORDS) return;

        boolean isPlayer = emitter instanceof ServerPlayer;
        float strength = volume * 40;
        if (isPlayer && ((ServerPlayer) emitter).isCrouching()) {
            strength *= 0.25f;
        }

        BlockPos pos = entity != null ? entity.blockPosition() : BlockPos.ZERO;
        SoundTrackingHandler.addSound(self, pos, strength);
    }
}
