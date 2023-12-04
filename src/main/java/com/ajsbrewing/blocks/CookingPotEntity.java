package com.ajsbrewing.blocks;

import com.ajsbrewing.AJsBrewingMod;
import com.ajsbrewing.items.VialItem;
import com.ajsbrewing.data.PotionCookingRecipe;
import net.minecraft.block.Block;
import net.minecraft.block.BlockEntityProvider;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityTicker;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.entity.Entity;
import net.minecraft.entity.ItemEntity;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.inventory.SimpleInventory;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.nbt.*;
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

import java.util.*;

enum PREPARERS {
    MAGMA(2),
    FIRE(3),
    SOUL_FIRE(4),
    LAVA(5);

    public final int value;
    PREPARERS(int value) {
        this.value = value;
    }
}

public class CookingPotEntity extends BlockEntity  {
    int color;
    int seed;
    public static VoxelShape INSIDE_SHAPE = Block.createCuboidShape(3.0, 3.0, 3.0, 13.0, 12.0, 13.0);

    
    private List<StatusEffectInstance> effects = new ArrayList<>();
    private List<ItemStack> ingredients = new ArrayList<>();
    private PREPARERS preparer = PREPARERS.MAGMA;

    
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

    public void clear() {
        effects.clear();
        ingredients.clear();
        seed = 0;
        setColor(0);
    }


    @Override
    public void writeNbt(NbtCompound nbt) {
        nbt.putInt("color", getColor());
        nbt.putInt("seed", seed);
        nbt.putInt("preparer", preparer.value);

        NbtList effectsNbt = new NbtList();
        for (StatusEffectInstance effect : effects) {
            effectsNbt.add(effect.writeNbt(new NbtCompound()));
        }
        nbt.put("effects", effectsNbt);

        NbtList ingredientsNbt = new NbtList();
        for (ItemStack ingredient : ingredients) {
            ingredientsNbt.add(ingredient.writeNbt(new NbtCompound()));
        }
        nbt.put("ingredients", ingredientsNbt);



        super.writeNbt(nbt);
    }

    @Override
    public void readNbt(NbtCompound nbt) {
        super.readNbt(nbt);

        NbtList effectsNbt = nbt.getList("effects", NbtElement.COMPOUND_TYPE);

        for (int i = 0; i < effectsNbt.size(); i++) {
            NbtCompound effectNbt = effectsNbt.getCompound(i);
            addEffect(StatusEffectInstance.fromNbt(effectNbt));
        }

        NbtList ingredientsNbt = nbt.getList("ingredients", NbtElement.COMPOUND_TYPE);
        for (int i = 0; i < ingredientsNbt.size(); i++) {
            NbtCompound ingredientNbt = ingredientsNbt.getCompound(i);
            ingredients.add(ItemStack.fromNbt(ingredientNbt));
        }


        for (PREPARERS p : PREPARERS.values()) {
            if (p.value == nbt.getInt("preparer")) {
                preparer = p;
                break;
            }
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

    public static boolean onEntityCollided(World world, BlockPos pos, BlockState state, Entity entity, CookingPotEntity blockEntity) {
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
                        return true;
                    } else {
                        itemEntity.setVelocity(-0.069f, .23f, 0.05f);
                    }
                }
            }
        }
        return false;
    }

    public void addIngredient(PotionCookingRecipe recipe) {
        seed += recipe.seed;

        ingredients.addAll(Arrays.asList(recipe.input.getMatchingStacks()));
        for (StatusEffectInstance sei : recipe.getEffects()) {
            addEffect(sei);
        }

        setColor();
    }

    public ItemStack getPotionItem() {
        Random random = new Random(seed);

        int e = preparer.value;
        AJsBrewingMod.LOGGER.info(preparer.name());

        List<StatusEffectInstance> SE = new ArrayList<>();
        for(int i = 0; i < e; i++) {
            if (effects.isEmpty()) break;
            int g = random.nextInt(effects.size());
            SE.add(effects.get(g));
            effects.remove(g);
        }

        return PotionUtil.setCustomPotionEffects(new ItemStack(VialItem.INSTANCE, 1),SE);
    }

    public boolean hasEffects() {
        return !effects.isEmpty();
    }

    public void setPreparer(BlockState preparer) {
        if (preparer.isOf(Blocks.SOUL_FIRE)) {
            this.preparer = PREPARERS.SOUL_FIRE;
        } else if (preparer.isOf(Blocks.FIRE)) {
            this.preparer = PREPARERS.FIRE;
        } else if (preparer.isOf(Blocks.LAVA)) {
            this.preparer = PREPARERS.LAVA;
        } else {
            this.preparer = PREPARERS.MAGMA;
        }
    }
}