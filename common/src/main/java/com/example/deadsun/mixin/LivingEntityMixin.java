package com.example.deadsun.mixin;

import com.example.deadsun.config.ModConfig;
import com.example.deadsun.registry.ModItems;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.monster.zombie.Zombie;
import net.minecraft.world.item.Items;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(LivingEntity.class)
public abstract class LivingEntityMixin {

    @Inject(method = "dropAllDeathLoot", at = @At("HEAD"))
    private void deadsun$dropLootBag(ServerLevel level, DamageSource source, CallbackInfo ci) {
        if (!isFeaturesActive(level)) return;

        LivingEntity self = (LivingEntity) (Object) this;
        if (self instanceof Zombie) {
            float lootChance = ModConfig.getLootBagDropChanceValue();
            if (level.getRandom().nextFloat() < lootChance) {
                self.spawnAtLocation(level, ModItems.LOOT_BAG);
            }

            float pearlChance = ModConfig.getEnderPearlDropChanceValue();
            if (level.getRandom().nextFloat() < pearlChance) {
                self.spawnAtLocation(level, Items.ENDER_PEARL);
            }
        }
    }

    private static boolean isFeaturesActive(ServerLevel level) {
        int days = ModConfig.getDaysBeforeActivationValue();
        if (days <= 0) return true;
        return level.getDefaultClockTime() / 24000 >= days;
    }
}
