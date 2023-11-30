package com.ajsbrewing.recipe.cooking.potion;

import net.minecraft.network.PacketByteBuf;

public class RecipeEffectInstance {
    public String effect;
    public int duration;
    public int amplifier;

    public RecipeEffectInstance(String effect, int duration, int amplifier) {
        this.effect = effect;
        this.duration = duration;
        this.amplifier = amplifier;
    }

    public static RecipeEffectInstance fromPacket(PacketByteBuf packetByteBuf) {
        return new RecipeEffectInstance(
                packetByteBuf.readString(),
                packetByteBuf.readInt(),
                packetByteBuf.readInt()

        );
    }

    public static PacketByteBuf toPacket(PacketByteBuf packetByteBuf, RecipeEffectInstance rei) {
        packetByteBuf.writeString(rei.effect);
        packetByteBuf.writeInt(rei.duration);
        packetByteBuf.writeInt(rei.amplifier);
        return packetByteBuf;
    }
}