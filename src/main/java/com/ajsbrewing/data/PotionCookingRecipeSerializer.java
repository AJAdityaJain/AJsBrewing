package com.ajsbrewing.data;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.recipe.Ingredient;
import net.minecraft.recipe.RecipeSerializer;
import net.minecraft.util.Identifier;

public class PotionCookingRecipeSerializer implements RecipeSerializer<PotionCookingRecipe> {


    public static final PotionCookingRecipeSerializer INSTANCE = new PotionCookingRecipeSerializer();
    public static final Identifier ID = new Identifier("ajsbrewing:potion_cooking_recipe");

    public static final Codec<RecipeEffectInstance> SUB_CODEC = RecordCodecBuilder.create(instance ->
    {
        return instance.group(
                Codec.STRING.fieldOf("effect").forGetter((r) -> r.effect),
                Codec.INT.fieldOf("duration").forGetter((r) -> r.duration),
                Codec.INT.fieldOf("amplifier").forGetter((r) -> r.amplifier)
        ).apply(instance, RecipeEffectInstance::new);
    });
    public static final Codec<PotionCookingRecipe> CODEC = RecordCodecBuilder.create((instance) -> {
        return instance.group(
                Ingredient.ALLOW_EMPTY_CODEC.fieldOf("input").forGetter((recipe) -> recipe.input),
                Codec.INT.fieldOf("seed").forGetter((recipe) -> recipe.seed),
                Codec.list(SUB_CODEC).fieldOf("effects").forGetter((recipe) -> recipe.effects)



        ).apply(instance, PotionCookingRecipe::new);
    });


    @Override
    public Codec<PotionCookingRecipe> codec() {
        return CODEC;
    }

    @Override
    public PotionCookingRecipe read(PacketByteBuf buf) {
        return new PotionCookingRecipe(
                Ingredient.fromPacket(buf),
                buf.readInt(),
                buf.readList(RecipeEffectInstance::fromPacket));

    }

    @Override
    public void write(PacketByteBuf buf, PotionCookingRecipe recipe) {
//        recipe.input.write(buf);
//        buf.writeInt(recipe.seed);
//
//        buf.write
//        recipe.effects.forEach((rei) -> RecipeEffectInstance.toPacket(buf, rei));
//
//        buf.writeString(recipe.getEffect());
//        buf.writeInt(recipe.duration);
//        buf.writeInt(recipe.amplifier);
    }
}