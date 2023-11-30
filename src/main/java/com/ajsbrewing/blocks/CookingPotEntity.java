package com.ajsbrewing.blocks;

import com.ajsbrewing.AJsBrewingMod;
import com.ajsbrewing.items.VialItem;
import com.ajsbrewing.recipe.cooking.potion.PotionCookingRecipe;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.Entity;
import net.minecraft.entity.ItemEntity;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.SimpleInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtList;
import net.minecraft.network.listener.ClientPlayPacketListener;
import net.minecraft.network.packet.Packet;
import net.minecraft.network.packet.s2c.play.BlockEntityUpdateS2CPacket;
import net.minecraft.potion.PotionUtil;
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
import java.util.Random;

public class CookingPotEntity extends BlockEntity {
    int color;
    int seed;
    public static VoxelShape INSIDE_SHAPE = Block.createCuboidShape(3.0, 3.0, 3.0, 13.0, 12.0, 13.0);

    List<StatusEffectInstance> effects = new ArrayList<>();

    public void addEffect(StatusEffectInstance effect) {
        for (StatusEffectInstance e : effects) {
            if (e.getEffectType().equals(effect.getEffectType())) {
                e.upgrade(effect);
                return;
            }
        }
        effects.add(effect);
    }


    public void setColor(int color) {
        this.color = color;
        if (this.getWorld() == null) return;
        this.getWorld().updateListeners(pos, getCachedState(), getCachedState(), 3);
        this.getWorld().markDirty(pos);

    }

    public void setColor() {
        this.color = VialItem.getColor(effects);
        if (this.getWorld() == null) return;
        this.getWorld().updateListeners(pos, getCachedState(), getCachedState(), 3);
        this.getWorld().markDirty(pos);

    }

    public int getColor() {
        return color;
    }


    public CookingPotEntity(BlockPos pos, BlockState state) {
        super(AJsBrewingMod.COOKING_POT_ENTITY_TYPE, pos, state);
    }


    @Override
    public void writeNbt(NbtCompound nbt) {
        nbt.putInt("color", getColor());
        nbt.putInt("seed", seed);

        NbtList effectsNbt = new NbtList();
        for (StatusEffectInstance effect : effects) {
            effectsNbt.add(effect.writeNbt(new NbtCompound()));

//            NbtCompound effectNbt = new NbtCompound();
//            effectNbt.putInt("duration", effect.getDuration());
//            effectNbt.putByte("amplifier", (byte) effect.getAmplifier());
//            effectNbt.putString("effect", effect.getEffectType().getName().getString());
//            effectsNbt.add(effectNbt);
////            nbt.put(effect.getEffectType().getName().getString(), effectNbt);
        }
        nbt.put("effects", effectsNbt);

        super.writeNbt(nbt);
    }

    @Override
    public void readNbt(NbtCompound nbt) {
        super.readNbt(nbt);

        NbtList effectsNbt = nbt.getList("effects", 9);

        for (int i = 0; i < effectsNbt.size(); i++) {
            NbtCompound effectNbt = effectsNbt.getCompound(i);
            addEffect(StatusEffectInstance.fromNbt(effectNbt));
        }

        seed = nbt.getInt("seed");
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
                    SimpleInventory inv = new SimpleInventory(itemEntity.getStack());
                    Optional<RecipeEntry<PotionCookingRecipe>> match = world.getRecipeManager()
                            .getFirstMatch(PotionCookingRecipe.Type.INSTANCE, inv, world);
                    if (match.isPresent()) {
                        blockEntity.addIngredient(match.get().value());
                        itemEntity.kill();

                    } else {
                        itemEntity.setVelocity(-0.069f, .23f, 0.05f);
                    }
                }
            }
        }
    }

    public void addIngredient(PotionCookingRecipe recipe) {
        seed += recipe.seed;

        Random random = new Random(seed);

        for(int i = 0; i < effects.size(); i++) {
            if(random.nextInt(4) == 1){
//                AJsBrewingMod.LOGGER.info("Removing Effect" + effects.get(i).getEffectType().getName().getString());
                effects.remove(i);
                i--;
            }
        }

        for (StatusEffectInstance sei : recipe.getEffects()) {
            addEffect(sei);
        }
        setColor();
    }

    public ItemStack getPotionItem() {
        return PotionUtil.setCustomPotionEffects(new ItemStack(VialItem.INSTANCE, 1),effects);
    }
}