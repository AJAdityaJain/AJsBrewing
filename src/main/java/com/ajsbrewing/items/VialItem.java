package com.ajsbrewing.items;

import net.fabricmc.fabric.api.item.v1.FabricItemSettings;
import net.minecraft.advancement.criterion.Criteria;
import net.minecraft.client.item.TooltipContext;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemUsage;
import net.minecraft.item.Vanishable;
import net.minecraft.potion.PotionUtil;
import net.minecraft.potion.Potions;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.stat.Stats;
import net.minecraft.text.Text;
import net.minecraft.util.Hand;
import net.minecraft.util.Rarity;
import net.minecraft.util.TypedActionResult;
import net.minecraft.util.UseAction;
import net.minecraft.world.World;
import net.minecraft.world.event.GameEvent;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class VialItem extends Item implements Vanishable{
    public static VialItem INSTANCE = new VialItem(new FabricItemSettings()
            .maxDamage(16)
            .rarity(Rarity.UNCOMMON)
    );
    public VialItem(Settings settings) {
        super(settings);
    }


    public ItemStack getDefaultStack() {
        return PotionUtil.setPotion(super.getDefaultStack(), Potions.WATER);
    }

    public ItemStack finishUsing(ItemStack stack, World world, LivingEntity user) {
        PlayerEntity playerEntity = user instanceof PlayerEntity ? (PlayerEntity)user : null;
        if (playerEntity instanceof ServerPlayerEntity) {
            Criteria.CONSUME_ITEM.trigger((ServerPlayerEntity)playerEntity, stack);
        }

        if (!world.isClient) {
            List<StatusEffectInstance> list = PotionUtil.getPotionEffects(stack);

            for (StatusEffectInstance statusEffectInstance : list) {
                if (statusEffectInstance.getEffectType().isInstant()) {
                    statusEffectInstance.getEffectType().applyInstantEffect(playerEntity, playerEntity, user, statusEffectInstance.getAmplifier(), 1.0);
                } else
                {
                    user.addStatusEffect(new StatusEffectInstance(statusEffectInstance));
                }
            }
        }

        if (playerEntity != null) {
            playerEntity.incrementStat(Stats.USED.getOrCreateStat(this));

            stack.damage(1, playerEntity, (e) -> e.sendEquipmentBreakStatus(EquipmentSlot.MAINHAND));

            if(!stack.isDamaged() &&!stack.isDamageable() && (stack.getDamage() == 0)){
                return new ItemStack(INSTANCE);
            }

        }
        user.emitGameEvent(GameEvent.DRINK);
        return stack;
    }


    public int getMaxUseTime(ItemStack stack) {
        return 16;
    }

    public UseAction getUseAction(ItemStack stack) {
        return UseAction.DRINK;
    }

    public TypedActionResult<ItemStack> use(World world, PlayerEntity user, Hand hand) {
//        PotionItem
//        PotionUtil.getColor(PotionUtil.getPotionEffects(user.getStackInHand(hand)));
        return ItemUsage.consumeHeldItem(world, user, hand);
    }

    public static int getColor(ItemStack stack){

        List<StatusEffectInstance> list = PotionUtil.getPotionEffects(stack);
    if(list.size() == 0){
        return 0xFFFFFF;
    }
        int sumRed = 0;
        int sumGreen = 0;
        int sumBlue = 0;

        // Convert hexadecimal colors to RGB values and accumulate the sums
        for(StatusEffectInstance statusEffectInstance : list) {
            int hexColor = statusEffectInstance.getEffectType().getColor();
            int red = (hexColor >> 16) & 0xFF;
            int green = (hexColor >> 8) & 0xFF;
            int blue = hexColor & 0xFF;

            sumRed += red;
            sumGreen += green;
            sumBlue += blue;
        }

        // Calculate the average RGB values
        int averageRed = sumRed / list.size();
        int averageGreen = sumGreen / list.size();
        int averageBlue = sumBlue / list.size();

        // Create the average color using the RGB values

        // Return the decimal representation of the average color
        return (averageRed << 16) | (averageGreen << 8) | averageBlue;
    }

//    public String getTranslationKey(ItemStack stack) {
//        return PotionUtil.getPotion(stack).finishTranslationKey(this.getTranslationKey() + ".effect.");
//    }
    public int getEnchantability() {
        return 0;
    }
    public void appendTooltip(ItemStack stack, @Nullable World world, List<Text> tooltip, TooltipContext context) {
        PotionUtil.buildTooltip(stack, tooltip, 1.0F);
    }
}