package com.ajsbrewing;

import com.ajsbrewing.blocks.CookingPot;
import com.ajsbrewing.items.VialItem;
import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.inventory.SimpleInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.potion.PotionUtil;
import net.minecraft.recipe.Ingredient;
import net.minecraft.recipe.Recipe;
import net.minecraft.recipe.RecipeSerializer;
import net.minecraft.recipe.RecipeType;
import net.minecraft.registry.DynamicRegistryManager;
import net.minecraft.registry.Registries;
import net.minecraft.util.Identifier;
import net.minecraft.world.World;

import java.util.List;

public class PotionCookingRecipe implements Recipe<SimpleInventory> {
    public final Ingredient input;

    public final StatusEffect effect;

    public final int duration;

    public final int amplifier;

    public final int seed;

    public PotionCookingRecipe(Ingredient input, String effect, int duration, int amplifier, int seed) {
        this.input = input;
        this.effect = Registries.STATUS_EFFECT.get(Identifier.tryParse(effect));
        this.duration = duration;
        this.amplifier = amplifier;
        this.seed = seed;
    }
    public String getEffect() {
        return effect.getName().getString();
    }

    @Override
    public boolean matches(SimpleInventory inventory, World world) {
        return input.test(inventory.getStack(0));
    }
    public ItemStack createIcon() {
        return new ItemStack(CookingPot.INSTANCE);
    }
    @Override
    public ItemStack craft(SimpleInventory inventory, DynamicRegistryManager registryManager) {
        return null;
    }

    @Override
    public boolean fits(int width, int height) {
        return false;
    }

    @Override
    public ItemStack getResult(DynamicRegistryManager registryManager) {
        AJsBrewingMod.LOGGER.info("Amplifier: " + this.amplifier);
        AJsBrewingMod.LOGGER.info("Duration: " + this.duration);
        return PotionUtil.setCustomPotionEffects(new ItemStack(VialItem.INSTANCE, 1),
                List.of(new StatusEffectInstance(this.effect, this.duration, this.amplifier, true, true, false))
        );
    }

    @Override
    public RecipeSerializer<?> getSerializer() {
        return PotionCookingRecipeSerializer.INSTANCE;
    }

    public static class Type implements RecipeType<PotionCookingRecipe> {
        private Type() {
        }

        public static final Type INSTANCE = new Type();

        public static final String ID = "potion_cooking_recipe";
    }

    @Override
    public RecipeType<?> getType() {
        return Type.INSTANCE;
    }
}