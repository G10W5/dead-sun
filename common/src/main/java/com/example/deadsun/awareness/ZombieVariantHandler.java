package com.example.deadsun.awareness;

import com.example.deadsun.config.ModConfig;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.monster.zombie.Zombie;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import net.minecraft.resources.Identifier;

import java.util.HashMap;
import java.util.Map;

public class ZombieVariantHandler {

    public static final String VARIANT_TAG = "deadsun_variant";
    public static final String RUNNER = "runner";
    public static final String JUMPER = "jumper";
    public static final String EXPLODER = "exploder";
    public static final String BRUTE = "brute";

    private static final Identifier SPEED_MOD_ID = Identifier.fromNamespaceAndPath("deadsun", "runner_speed");
    private static final Identifier BRUTE_SPEED_MOD_ID = Identifier.fromNamespaceAndPath("deadsun", "brute_speed");
    private static final Map<Integer, Integer> FUSE_TIMERS = new HashMap<>();

    public static void assignVariant(Zombie zombie) {
        if (!ModConfig.isZombieVariantsValue()) return;
        if (isVariant(zombie)) return;

        float roll = zombie.getRandom().nextFloat();
        float runnerChance = ModConfig.getRunnerChanceValue();
        float jumperChance = ModConfig.getJumperChanceValue();
        float exploderChance = ModConfig.getExploderChanceValue();
        float bruteChance = ModConfig.getBruteChanceValue();

        String variant = null;
        if (roll < runnerChance) {
            variant = RUNNER;
        } else if (roll < runnerChance + jumperChance) {
            variant = JUMPER;
        } else if (roll < runnerChance + jumperChance + exploderChance) {
            variant = EXPLODER;
        } else if (roll < runnerChance + jumperChance + exploderChance + bruteChance) {
            variant = BRUTE;
        }

        if (variant == null) return;

        zombie.addTag(VARIANT_TAG);
        zombie.addTag("deadsun_" + variant);

        if (variant.equals(RUNNER)) {
            applyRunnerSpeed(zombie);
        } else if (variant.equals(JUMPER)) {
            zombie.setItemSlot(net.minecraft.world.entity.EquipmentSlot.MAINHAND, new ItemStack(Items.FEATHER));
        } else if (variant.equals(EXPLODER)) {
            zombie.setItemSlot(net.minecraft.world.entity.EquipmentSlot.MAINHAND, new ItemStack(Items.TNT));
        } else if (variant.equals(BRUTE)) {
            applyBruteStats(zombie);
        }
    }

    private static void applyRunnerSpeed(Zombie zombie) {
        AttributeInstance speedAttr = zombie.getAttribute(Attributes.MOVEMENT_SPEED);
        if (speedAttr != null) {
            float boost = ModConfig.getRunnerSpeedBoostValue();
            speedAttr.addPermanentModifier(new AttributeModifier(
                    SPEED_MOD_ID, boost, AttributeModifier.Operation.ADD_MULTIPLIED_TOTAL));
        }
    }

    private static void applyBruteStats(Zombie zombie) {
        float speedMult = ModConfig.getBruteSpeedMultiplierValue() - 1.0f;
        AttributeInstance speedAttr = zombie.getAttribute(Attributes.MOVEMENT_SPEED);
        if (speedAttr != null) {
            speedAttr.addPermanentModifier(new AttributeModifier(
                    BRUTE_SPEED_MOD_ID, speedMult, AttributeModifier.Operation.ADD_MULTIPLIED_TOTAL));
        }
        zombie.setItemSlot(net.minecraft.world.entity.EquipmentSlot.MAINHAND, new ItemStack(Items.IRON_AXE));
        int strengthLevel = ModConfig.getBruteStrengthLevelValue();
        zombie.addEffect(new MobEffectInstance(MobEffects.STRENGTH, -1, strengthLevel - 1, false, false));
    }

    public static boolean isVariant(Zombie zombie) {
        return zombie.entityTags().contains(VARIANT_TAG);
    }

    public static boolean isRunner(Zombie zombie) {
        return zombie.entityTags().contains("deadsun_runner");
    }

    public static boolean isJumper(Zombie zombie) {
        return zombie.entityTags().contains("deadsun_jumper");
    }

    public static boolean isExploder(Zombie zombie) {
        return zombie.entityTags().contains("deadsun_exploder");
    }

    public static boolean isBrute(Zombie zombie) {
        return zombie.entityTags().contains("deadsun_brute");
    }

    public static void tickVariant(ServerLevel level, Zombie zombie) {
        if (!isVariant(zombie)) return;

        if (isRunner(zombie)) {
            tickRunner(level, zombie);
        } else if (isJumper(zombie)) {
            tickJumper(level, zombie);
        } else if (isExploder(zombie)) {
            tickExploder(level, zombie);
        } else if (isBrute(zombie)) {
            tickBrute(level, zombie);
        }
    }

    private static void tickRunner(ServerLevel level, Zombie zombie) {
        if (zombie.tickCount % 20 == 0) {
            level.sendParticles(ParticleTypes.SOUL_FIRE_FLAME,
                    zombie.getX(), zombie.getY() + 0.5, zombie.getZ(),
                    1, 0.2, 0.2, 0.2, 0.01);
        }
    }

    private static void tickJumper(ServerLevel level, Zombie zombie) {
        if (zombie.onGround() && zombie.getTarget() != null) {
            double dist = zombie.distanceTo(zombie.getTarget());
            if (dist < 8.0 && dist > 2.0 && zombie.getRandom().nextInt(30) == 0) {
                float boost = ModConfig.getJumperLeapBoostValue();
                zombie.setDeltaMovement(
                        zombie.getDeltaMovement().x(),
                        0.6 + boost,
                        zombie.getDeltaMovement().z()
                );
                zombie.fallDistance = 0;
                zombie.hurtMarked = true;
                level.sendParticles(ParticleTypes.CLOUD,
                        zombie.getX(), zombie.getY(), zombie.getZ(),
                        5, 0.4, 0.2, 0.4, 0.02);
                level.sendParticles(ParticleTypes.HAPPY_VILLAGER,
                        zombie.getX(), zombie.getY() + 0.5, zombie.getZ(),
                        3, 0.3, 0.3, 0.3, 0.0);
            }
        }
    }

    private static void tickBrute(ServerLevel level, Zombie zombie) {
    }

    private static void tickExploder(ServerLevel level, Zombie zombie) {
        Player nearest = level.getNearestPlayer(zombie, 16.0);
        if (nearest == null) return;
        if (nearest.isCreative() || nearest.isSpectator()) return;

        double dist = zombie.distanceTo(nearest);
        int entityId = zombie.getId();
        boolean alreadyFusing = FUSE_TIMERS.containsKey(entityId);

        boolean canSee = zombie.hasLineOfSight(nearest);
        boolean inRange = dist <= 3.0;

        if (inRange && canSee && !alreadyFusing) {
            FUSE_TIMERS.put(entityId, ModConfig.getExploderFuseTimeValue());
            zombie.playSound(SoundEvents.TNT_PRIMED, 1.0f, 1.0f);
        }

        if (alreadyFusing || (inRange && canSee)) {
            int fuse = FUSE_TIMERS.getOrDefault(entityId, ModConfig.getExploderFuseTimeValue());
            fuse--;
            FUSE_TIMERS.put(entityId, fuse);

            if (zombie.tickCount % 4 == 0) {
                level.sendParticles(ParticleTypes.SMOKE,
                        zombie.getX(), zombie.getY() + 1.0, zombie.getZ(),
                        2, 0.1, 0.1, 0.1, 0.02);
            }

            if (fuse <= 0) {
                FUSE_TIMERS.remove(entityId);
                float radius = ModConfig.getExploderExplosionRadiusValue();
                boolean blocks = ModConfig.isExploderDestroysBlocksValue();

                level.explode(zombie, zombie.getX(), zombie.getY() + 0.5, zombie.getZ(),
                        radius, false,
                        blocks ? Level.ExplosionInteraction.MOB : Level.ExplosionInteraction.NONE);

                zombie.discard();
            }
        }
    }
}
