package com.ajsbrewing.blocks;

import com.ajsbrewing.AJsBrewingMod;
import com.ajsbrewing.items.EmptyVialItem;
import com.ajsbrewing.items.VialItem;
import com.ajsbrewing.items.WitchcraftTome;
import net.fabricmc.fabric.api.object.builder.v1.block.FabricBlockSettings;
import net.minecraft.block.*;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.fluid.FluidState;
import net.minecraft.fluid.Fluids;
import net.minecraft.item.*;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtElement;
import net.minecraft.nbt.NbtList;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.potion.PotionUtil;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.BooleanProperty;
import net.minecraft.state.property.Properties;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.function.BooleanBiFunction;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.random.Random;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.util.shape.VoxelShapes;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;
import net.minecraft.world.WorldAccess;
import org.jetbrains.annotations.Nullable;

import java.util.Locale;


public class CookingPot extends HorizontalFacingBlock  implements Waterloggable,BlockEntityProvider {

    public static final BooleanProperty FILLED = BooleanProperty.of("filled");
    public static final BooleanProperty PREPARED = BooleanProperty.of("prepared");
    public static final BooleanProperty WATERLOGGED = Properties.WATERLOGGED;
    private static final VoxelShape RAYCAST_SHAPE = createCuboidShape(3.0, 4.0, 3.0, 13.0, 14.0, 13.0);
    protected static final VoxelShape OUTLINE_SHAPE =
            VoxelShapes.combineAndSimplify(
                    createCuboidShape(1, 0, 1, 15, 14, 15),
//                    VoxelShapes.fullCube(),
                    RAYCAST_SHAPE,
                    BooleanBiFunction.ONLY_FIRST
            );


    public static CookingPot INSTANCE = new CookingPot((FabricBlockSettings
            .create()
            .strength(1.0f)
            .requiresTool()
            .nonOpaque()
            .notSolid()
            .noBlockBreakParticles()
    ));


    public static BlockItem ITEM_INSTANCE = new BlockItem(INSTANCE, new Item.Settings());

    public CookingPot(Settings settings) {
        super(settings);
        setDefaultState(getDefaultState()
                .with(FILLED, false)
                .with(PREPARED, false)
                .with(Properties.HORIZONTAL_FACING, Direction.EAST)
                .with(WATERLOGGED, false)
        );
    }


    public VoxelShape getOutlineShape(BlockState state, BlockView world, BlockPos pos, ShapeContext context) {
        return OUTLINE_SHAPE;
    }

    public VoxelShape getRaycastShape(BlockState state, BlockView world, BlockPos pos) {
        return RAYCAST_SHAPE;
    }

    @Override
    public BlockRenderType getRenderType(BlockState state) {
        // With inheriting from BlockWithEntity this defaults to INVISIBLE, so we need to change that!
        return BlockRenderType.MODEL;
    }

    @Override
    protected void appendProperties(StateManager.Builder<Block, BlockState> builder) {
        builder.add(FILLED, WATERLOGGED, PREPARED, Properties.HORIZONTAL_FACING);
    }


    @Override
    public BlockState getPlacementState(ItemPlacementContext ctx) {
        return super.getPlacementState(ctx)
                .with(Properties.HORIZONTAL_FACING, ctx.getHorizontalPlayerFacing().getOpposite())
                .with(WATERLOGGED, ctx.getWorld().getFluidState(ctx.getBlockPos()).isOf(Fluids.WATER));
    }

    @Override
    public FluidState getFluidState(BlockState state) {
        return state.get(WATERLOGGED) ? Fluids.WATER.getStill(false) : super.getFluidState(state);
    }


    @Override
    public BlockState getStateForNeighborUpdate(BlockState state, Direction direction, BlockState neighborState, WorldAccess world, BlockPos pos, BlockPos neighborPos) {
        if (state.get(WATERLOGGED)) {
            // This is for 1.17 and below: world.getFluidTickScheduler().schedule(pos, Fluids.WATER, Fluids.WATER.getTickRate(world));
            world.scheduleFluidTick(pos, Fluids.WATER, Fluids.WATER.getTickRate(world));
        }

        return super.getStateForNeighborUpdate(state, direction, neighborState, world, pos, neighborPos);
    }

    @Nullable
    @Override
    public BlockEntity createBlockEntity(BlockPos pos, BlockState state) {
        return new CookingPotEntity(pos, state);
    }

    @Override
    public void onEntityCollision(BlockState state, World world, BlockPos pos, Entity entity) {
        BlockEntity blockEntity = world.getBlockEntity(pos);
        if (blockEntity instanceof CookingPotEntity) {
            if (state.get(FILLED) && state.get(PREPARED)) {
                if(CookingPotEntity.onEntityCollided(world, pos, state, entity, (CookingPotEntity) blockEntity)){
                    world.setBlockState(pos, state.with(PREPARED, false).with(FILLED, false));
                    AJsBrewingMod.LOGGER.info("GODDAMN IT");
                }
            }
        }
    }


    @Override
    public ActionResult onUse(BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand, BlockHitResult hit) {
        if (world.isClient()) return ActionResult.PASS;

        if (world.getBlockEntity(pos) instanceof CookingPotEntity potBlockEntity) {
            ItemStack item = player.getStackInHand(hand);
            if (state.get(FILLED)) {
                if (state.get(PREPARED)) {
                    if (item.isOf(WitchcraftTome.INSTANCE)) {
                        NbtCompound nbt = item.getOrCreateNbt();
                        NbtCompound nbt2 = new NbtCompound();
                        potBlockEntity.writeNbt(nbt2);
                        NbtList nbtl = nbt.getList("recipes", NbtElement.COMPOUND_TYPE);
                        nbtl.add(nbt2);
                        nbt.put("recipes", nbtl);
                    }

                    if (item.isOf(EmptyVialItem.INSTANCE)) {
                        item.decrement(1);
                        player.giveItemStack(potBlockEntity.getPotionItem());
                        potBlockEntity.clear();
                        //PROB
                        world.setBlockState(pos, state.with(PREPARED, true).with(FILLED, false));
                        return ActionResult.CONSUME;
                    }
                }
            } else if (item.isOf(VialItem.INSTANCE) && PotionUtil.getPotionEffects(item).isEmpty()) {
                item.decrement(1);
                player.giveItemStack(new ItemStack(EmptyVialItem.INSTANCE));
                world.setBlockState(pos, state.with(PREPARED, true).with(FILLED, true));
                potBlockEntity.color = world.getBiome(pos).value().getWaterColor();
                return ActionResult.CONSUME;
            }
        }

        return ActionResult.PASS;
    }

    public void randomDisplayTick(BlockState state, World world, BlockPos pos, Random random) {
        if (state.get(FILLED) && state.get(PREPARED))
            world.addParticle(ParticleTypes.EFFECT,
                    pos.getX() + random.nextFloat(), pos.getY() + .8, pos.getZ() + random.nextFloat(),
                    0.0D, 10.0D, 0.0D);

        if (state.get(FILLED)) {
            BlockState neighborState = world.getBlockState(pos.down());
            if (!state.get(PREPARED) && (
                    neighborState.isOf(Blocks.FIRE) ||
                            neighborState.isOf(Blocks.LAVA) ||
                            neighborState.isOf(Blocks.MAGMA_BLOCK) ||
                            neighborState.isOf(Blocks.SOUL_FIRE)
            )) {
                if (world.getBlockEntity(pos) instanceof CookingPotEntity potBlockEntity)
                    potBlockEntity.setPreparer(neighborState);

                world.setBlockState(pos, state.with(PREPARED, true), 3);
            }
        }
    }

}