package com.ajsbrewing;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.recipe.Ingredient;
import net.minecraft.recipe.RecipeSerializer;
import net.minecraft.util.Identifier;

public class PotionCookingRecipeSerializer implements RecipeSerializer<PotionCookingRecipe> {

    public static final PotionCookingRecipeSerializer INSTANCE = new PotionCookingRecipeSerializer();
    public static final Identifier ID = new Identifier("ajsbrewing:potion_cooking_recipe");

    private static final Codec<PotionCookingRecipe> CODEC = RecordCodecBuilder.create((instance) -> {
        return instance.group(
                Ingredient.ALLOW_EMPTY_CODEC.fieldOf("input").forGetter((recipe) -> recipe.input),
                Codec.STRING.fieldOf("effect").forGetter(PotionCookingRecipe::getEffect),
                Codec.INT.fieldOf("duration").forGetter((recipe) -> recipe.duration),
                Codec.INT.fieldOf("amplifier").forGetter((recipe) -> recipe.amplifier),
                Codec.INT.fieldOf("seed").forGetter((recipe) -> recipe.seed)
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
                buf.readString(),
                buf.readInt(),
                buf.readInt(),
                buf.readInt()

        );
    }

    @Override
    public void write(PacketByteBuf buf, PotionCookingRecipe recipe) {
        recipe.input.write(buf);
        assert recipe.effect != null;
        buf.writeString(recipe.getEffect());
        buf.writeInt(recipe.duration);
        buf.writeInt(recipe.amplifier);
        buf.writeInt(recipe.seed);
    }
}