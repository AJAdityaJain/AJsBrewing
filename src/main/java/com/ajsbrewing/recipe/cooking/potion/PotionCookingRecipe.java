package com.ajsbrewing.recipe.cooking.potion;

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

import java.util.ArrayList;
import java.util.List;

public class PotionCookingRecipe implements Recipe<SimpleInventory> {
    public final Ingredient input;

    public final int seed;
    public final List<RecipeEffectInstance> effects;


//    public PotionCookingRecipe(Ingredient input, int seed, RecipeEffectInstance... effect) {
//        this.input = input;
//        this.effects = List.of(effect);
//        this.seed = seed;
//    }

    public PotionCookingRecipe(Ingredient input, int seed, List<RecipeEffectInstance> effect) {
        this.input = input;
        this.effects = effect;
        this.seed = seed;
    }

    //    public String getEffect() {
//        return effect.getName().getString();
//    }

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

    public List<StatusEffectInstance> getEffects(){
        List<StatusEffectInstance> effs = new ArrayList<>();
        for (RecipeEffectInstance e : effects) {
            StatusEffect s = Registries.STATUS_EFFECT.get(new Identifier(e.effect));
            assert s != null ;
            effs.add(new StatusEffectInstance(s, e.duration, e.amplifier, true, true, false));
        }
        return effs;
    }
    @Override
    public ItemStack getResult(DynamicRegistryManager registryManager) {

        return PotionUtil.setCustomPotionEffects(new ItemStack(VialItem.INSTANCE, 1),getEffects());
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