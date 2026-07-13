package com.example.deadsun.item;

import com.example.deadsun.config.ModConfig;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.util.RandomSource;

public class LootBagItem extends Item {
    public LootBagItem(Properties properties) {
        super(properties);
    }

    @Override
    public InteractionResult use(Level level, Player player, InteractionHand hand) {
        ItemStack stack = player.getItemInHand(hand);

        if (!level.isClientSide() && level instanceof ServerLevel serverLevel) {
            ItemStack reward = rollReward(serverLevel.getRandom(), level);

            MutableComponent name = Component.literal(reward.getHoverName().getString())
                    .withStyle(ChatFormatting.GREEN);
            player.sendOverlayMessage(
                    Component.literal("You found ").append(name).append("!"));

            player.getInventory().add(reward);
            stack.shrink(1);

            level.playSound(null,
                    player.getX(), player.getY(), player.getZ(),
                    SoundEvents.BUNDLE_DROP_CONTENTS,
                    SoundSource.PLAYERS, 1.0f, 1.0f);
        }

        return InteractionResult.SUCCESS;
    }

    private ItemStack rollReward(RandomSource random, Level level) {
        if (level.dimension() == Level.END) {
            return rollEndReward(random);
        }
        if (level.dimension() == Level.NETHER) {
            return rollNetherReward(random);
        }
        return rollOverworldReward(random);
    }

    private ItemStack rollNetherReward(RandomSource random) {
        float roll = random.nextFloat();
        if (roll < 0.25f) {
            return new ItemStack(Items.GUNPOWDER, random.nextIntBetweenInclusive(1, 4));
        } else if (roll < 0.45f) {
            return new ItemStack(Items.BONE, random.nextIntBetweenInclusive(1, 3));
        } else if (roll < 0.60f) {
            return new ItemStack(Items.BLAZE_POWDER, random.nextIntBetweenInclusive(1, 2));
        } else if (roll < 0.75f) {
            return new ItemStack(Items.STRING, random.nextIntBetweenInclusive(2, 4));
        } else if (roll < 0.88f) {
            return new ItemStack(Items.GHAST_TEAR, random.nextIntBetweenInclusive(1, 2));
        } else {
            return new ItemStack(Items.EXPERIENCE_BOTTLE, random.nextIntBetweenInclusive(3, 7));
        }
    }

    private ItemStack rollOverworldReward(RandomSource random) {
        float roll = random.nextFloat();
        if (roll < 0.25f) {
            return new ItemStack(Items.GUNPOWDER, random.nextIntBetweenInclusive(1, 3));
        } else if (roll < 0.50f) {
            return new ItemStack(Items.BONE, random.nextIntBetweenInclusive(1, 3));
        } else if (roll < 0.75f) {
            return new ItemStack(Items.STRING, random.nextIntBetweenInclusive(2, 4));
        } else if (roll < 0.90f) {
            return new ItemStack(Items.SPIDER_EYE, random.nextIntBetweenInclusive(1, 2));
        } else {
            return new ItemStack(Items.EXPERIENCE_BOTTLE, random.nextIntBetweenInclusive(3, 7));
        }
    }

    private ItemStack rollEndReward(RandomSource random) {
        float pearlChance = ModConfig.getEnderPearlChanceValue();
        float roll = random.nextFloat();
        if (roll < pearlChance) {
            return new ItemStack(Items.ENDER_PEARL, random.nextIntBetweenInclusive(1, 3));
        } else if (roll < pearlChance + 0.30f) {
            return new ItemStack(Items.ENDER_EYE);
        } else {
            return new ItemStack(Items.EXPERIENCE_BOTTLE, random.nextIntBetweenInclusive(5, 12));
        }
    }
}
