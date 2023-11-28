package com.ajsbrewing.blocks;

import com.ajsbrewing.AJsBrewingMod;
import com.ajsbrewing.PotionCookingRecipe;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.Entity;
import net.minecraft.entity.ItemEntity;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.inventory.SimpleInventory;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.network.listener.ClientPlayPacketListener;
import net.minecraft.network.packet.Packet;
import net.minecraft.network.packet.s2c.play.BlockEntityUpdateS2CPacket;
import net.minecraft.recipe.RecipeEntry;
import net.minecraft.util.function.BooleanBiFunction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.util.shape.VoxelShapes;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class CookingPotEntity extends BlockEntity {
    int color;


    public void setColor(int color) {
        this.color = color;

    }
    public void setColor(int color, World world) {
        this.color = color;
        AJsBrewingMod.LOGGER.info("Setting color to " + color);
        world.updateListeners(pos, getCachedState(),  getCachedState(), 3);
        world.markDirty(pos);

    }

    public int getColor() {
        return color;
    }

    public static VoxelShape INSIDE_SHAPE = Block.createCuboidShape(2.0, 3.0, 2.0, 14.0, 12.0, 14.0);

    public List<StatusEffectInstance> effects = new ArrayList<>();

    public CookingPotEntity(BlockPos pos, BlockState state) {
        super(AJsBrewingMod.COOKING_POT_ENTITY_TYPE, pos, state);
    }


    @Override
    public void writeNbt(NbtCompound nbt) {
        nbt.putInt("color", getColor());

//        NbtList effectsNbt = new NbtList();
//        for (StatusEffectInstance effect : effects) {
//            NbtCompound effectNbt = new NbtCompound();
//            effectNbt.putInt("duration", effect.getDuration());
//            effectNbt.putByte("amplifier", (byte) effect.getAmplifier());
//            effectNbt.putString("effect", effect.getEffectType().getName().getString());
//            effectsNbt.add(effectNbt);
////            nbt.put(effect.getEffectType().getName().getString(), effectNbt);
//        }
//        nbt.put("effects", effectsNbt);

        super.writeNbt(nbt);
    }

    @Override
    public void readNbt(NbtCompound nbt) {
        super.readNbt(nbt);

//            NbtList effectsNbt = nbt.getList("effects",10);

//            for (int i = 0; i < effectsNbt.size(); i++) {
//                NbtCompound effectNbt = effectsNbt.getCompound(i);
//                effects.add(
//                        new StatusEffectInstance(
//                                Objects.requireNonNull(Registries.STATUS_EFFECT.get(Identifier.tryParse(effectNbt.getString("effect")))),
//                                effectNbt.getInt("duration"),
//                                effectNbt.getByte("amplifier")
//                        )
//                );
//            }

        setColor(nbt.getInt("color"));
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

    public static void onEntityCollided(World world, BlockPos pos, BlockState state, Entity entity, CookingPotEntity blockEntity) {
        if (entity instanceof ItemEntity itemEntity) {
            if (
                    !itemEntity.getStack().isEmpty() &&
                            VoxelShapes.matchesAnywhere(
                                    VoxelShapes.cuboid(itemEntity.getBoundingBox().offset(
                                            (double) (-pos.getX()),
                                            (double) (-pos.getY()),
                                            (double) (-pos.getZ())
                                    )),
                                    CookingPotEntity.INSIDE_SHAPE,
                                    BooleanBiFunction.AND)
            ) {
                if (!world.isClient()) {
                    AJsBrewingMod.LOGGER.info("Item Entity collided with Cooking Pot");
                    SimpleInventory inv = new SimpleInventory(itemEntity.getStack());
                    Optional<RecipeEntry<PotionCookingRecipe>> match = world.getRecipeManager()
                            .getFirstMatch(PotionCookingRecipe.Type.INSTANCE, inv, world);
                    if (match.isPresent()) {
                        AJsBrewingMod.LOGGER.info("Match found");

                        blockEntity.effects.add(
                                new StatusEffectInstance(
                                        match.get().value().effect,
                                        match.get().value().duration,
                                        match.get().value().amplifier
                                )
                        );
                        blockEntity.setColor(match.get().value().effect.getColor(),world);

                        world.getClosestPlayer(pos.getX(), pos.getY(), pos.getZ(), 10, false)
                                .giveItemStack(match.get().value().getResult(world.getRegistryManager()));
                        itemEntity.kill();
                    } else {
                        AJsBrewingMod.LOGGER.info("No match found");
                        itemEntity.setVelocity(0, .05f, 0);
                    }
                }
            }
//                entity.setVelocity(.1,1,.1);
//                entity.kill();
        }
    }

}