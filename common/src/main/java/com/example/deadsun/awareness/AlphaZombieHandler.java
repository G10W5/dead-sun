package com.example.deadsun.awareness;

import com.example.deadsun.config.ModConfig;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.resources.Identifier;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.monster.zombie.Zombie;

public class AlphaZombieHandler {

    private static final String ALPHA_TAG = "deadsun_alpha";
    private static final Identifier SCALE_ID = Identifier.fromNamespaceAndPath("deadsun", "alpha_scale");
    private static final Identifier HEALTH_ID = Identifier.fromNamespaceAndPath("deadsun", "alpha_health");
    private static final Identifier DAMAGE_ID = Identifier.fromNamespaceAndPath("deadsun", "alpha_damage");

    public static boolean shouldSpawnAlpha(ServerLevel level) {
        if (!ModConfig.isAlphaZombiesValue()) return false;
        return level.getRandom().nextFloat() < ModConfig.getAlphaSpawnChanceValue();
    }

    public static void markAsAlpha(Zombie zombie) {
        float scale = ModConfig.getAlphaScaleValue();
        float maxHp = ModConfig.getAlphaHealthValue();
        float attackDmg = ModConfig.getAlphaAttackDamageValue();

        AttributeInstance scaleAttr = zombie.getAttribute(Attributes.SCALE);
        if (scaleAttr != null) {
            scaleAttr.removeModifier(SCALE_ID);
            scaleAttr.addPermanentModifier(new AttributeModifier(
                    SCALE_ID, scale - 1.0, AttributeModifier.Operation.ADD_VALUE));
        }

        AttributeInstance healthAttr = zombie.getAttribute(Attributes.MAX_HEALTH);
        if (healthAttr != null) {
            healthAttr.removeModifier(HEALTH_ID);
            healthAttr.addPermanentModifier(new AttributeModifier(
                    HEALTH_ID, maxHp - 20.0, AttributeModifier.Operation.ADD_VALUE));
        }

        AttributeInstance damageAttr = zombie.getAttribute(Attributes.ATTACK_DAMAGE);
        if (damageAttr != null) {
            damageAttr.removeModifier(DAMAGE_ID);
            damageAttr.addPermanentModifier(new AttributeModifier(
                    DAMAGE_ID, attackDmg - 3.0, AttributeModifier.Operation.ADD_VALUE));
        }

        zombie.setHealth(zombie.getMaxHealth());
        zombie.addTag(ALPHA_TAG);
    }

    public static boolean isAlpha(Zombie zombie) {
        return zombie.entityTags().contains(ALPHA_TAG);
    }

    public static void tickParticles(Zombie zombie) {
        if (!isAlpha(zombie)) return;
        if (zombie.level().isClientSide()) return;
        if (zombie.tickCount % 10 != 0) return;

        ServerLevel level = (ServerLevel) zombie.level();
        level.sendParticles(ParticleTypes.ANGRY_VILLAGER,
                zombie.getX(), zombie.getY() + zombie.getBbHeight() * 0.7, zombie.getZ(),
                2, 0.3, 0.3, 0.3, 0.01);
    }
}
