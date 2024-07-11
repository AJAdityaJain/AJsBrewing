package com.ajsbrewing.items;


import com.ajsbrewing.AJsBrewingMod;
import com.ajsbrewing.NetworkingConstants;
import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.*;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.Hand;
import net.minecraft.util.TypedActionResult;
import net.minecraft.world.World;

public class WitchcraftTome extends Item {

    public static WitchcraftTome INSTANCE = new WitchcraftTome(new Settings());

    public WitchcraftTome(Settings settings) {
        super(settings);
    }

    @Override
    public boolean hasGlint(ItemStack stack) {
        return false;
    }

    public TypedActionResult<ItemStack> use(World world, PlayerEntity user, Hand hand) {
        PacketByteBuf buf = PacketByteBufs.create();
        buf.writeNbt(user.getStackInHand(hand).getNbt());

        NbtCompound tag = user.getStackInHand(hand).getNbt();
        if (tag != null) {
            AJsBrewingMod.LOGGER.warn(tag.toString());
        }


        if (!world.isClient()) {
            ServerPlayNetworking.send((ServerPlayerEntity) user, NetworkingConstants.OPEN_BOOK_PACKET_ID, buf);
            return TypedActionResult.consume(user.getStackInHand(hand));
        }
        return TypedActionResult.consume(user.getStackInHand(hand));
    }
}