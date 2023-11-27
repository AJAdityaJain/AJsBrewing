package com.ajsbrewing.blocks;

import com.ajsbrewing.AJsBrewingMod;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.network.listener.ClientPlayPacketListener;
import net.minecraft.network.packet.Packet;
import net.minecraft.network.packet.s2c.play.BlockEntityUpdateS2CPacket;
import net.minecraft.util.math.BlockPos;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

public class CookingPotEntity extends BlockEntity {
    public  int color = 0x00F000;

    public List<StatusEffectInstance> effects = new ArrayList<>();

    public CookingPotEntity(BlockPos pos, BlockState state) {
        super(AJsBrewingMod.COOKING_POT_ENTITY_TYPE, pos, state);
    }


    @Override
    public void writeNbt(NbtCompound nbt) {
        nbt.putInt("color", color);
        for (StatusEffectInstance effect : effects) {
            
        }
        super.writeNbt(nbt);
    }

    @Override
    public void readNbt(NbtCompound nbt) {
        super.readNbt(nbt);

        color = nbt.getInt("color");
    }

    @Nullable
    @Override
    public Packet<ClientPlayPacketListener> toUpdatePacket() {
        return BlockEntityUpdateS2CPacket.create(this);
    }

    @Override
    public NbtCompound toInitialChunkDataNbt() {
        return createNbt();
    }

}
