package net.aris.tutorialmod.block.custom;

import net.minecraft.block.*;
import net.minecraft.entity.Entity;
import net.minecraft.fluid.FluidState;
import net.minecraft.fluid.Fluids;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.BooleanProperty;
import net.minecraft.state.property.Properties;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.minecraft.world.WorldAccess;

import static net.minecraft.state.property.Properties.WATERLOGGED;

public class AlgaeCarpetBlock extends CarpetBlock implements Waterloggable {


    public void onEntityCollision(BlockState state, World world, BlockPos pos, Entity entity) {

        Vec3d vel = entity.getVelocity();

        double slowX = vel.x * 0.25;
        double slowZ = vel.z * 0.25;

        // limit upward movement so jumping is weak
        double slowY = Math.min(vel.y, 0.05);

        entity.setVelocity(slowX, slowY, slowZ);
    }
    public static final BooleanProperty NORTH = Properties.NORTH;
    public static final BooleanProperty SOUTH = Properties.SOUTH;
    public static final BooleanProperty EAST = Properties.EAST;
    public static final BooleanProperty WEST = Properties.WEST;

    public AlgaeCarpetBlock(Settings settings) {
        super(settings);
        this.setDefaultState(this.stateManager.getDefaultState()
                .with(NORTH, false)
                .with(SOUTH, false)
                .with(EAST, false)
                .with(WEST, false)
                .with(WATERLOGGED, false));
    }

    @Override
    protected void appendProperties(StateManager.Builder<Block, BlockState> builder) {
        builder.add(NORTH, SOUTH, EAST, WEST, WATERLOGGED);
    }

    @Override
    public BlockState getPlacementState(ItemPlacementContext ctx) {
        FluidState fluidState = ctx.getWorld().getFluidState(ctx.getBlockPos());

        return updateConnections(ctx.getWorld(), ctx.getBlockPos())
                .with(WATERLOGGED, fluidState.getFluid() == Fluids.WATER);
    }

    @Override
    public BlockState getStateForNeighborUpdate(BlockState state, Direction dir,
                                                BlockState neighborState,
                                                WorldAccess world, BlockPos pos,
                                                BlockPos neighborPos) {

        if (state.get(WATERLOGGED)) {
            world.scheduleFluidTick(pos, Fluids.WATER, Fluids.WATER.getTickRate(world));
        }

        return updateConnections(world, pos).with(WATERLOGGED, state.get(WATERLOGGED));
    }

    private BlockState updateConnections(WorldAccess world, BlockPos pos) {
        return getDefaultState()
                .with(NORTH, world.getBlockState(pos.north()).isOf(this))
                .with(SOUTH, world.getBlockState(pos.south()).isOf(this))
                .with(EAST, world.getBlockState(pos.east()).isOf(this))
                .with(WEST, world.getBlockState(pos.west()).isOf(this));
    }

    @Override
    public FluidState getFluidState(BlockState state) {
        return state.get(WATERLOGGED)
                ? Fluids.WATER.getStill(false)
                : super.getFluidState(state);
    }
    @Override
    public boolean isSideInvisible(BlockState state, BlockState stateFrom, Direction direction) {
        if (stateFrom.isOf(this) && direction.getAxis().isHorizontal()) {
            return true;
        }
        return super.isSideInvisible(state, stateFrom, direction);
    }
}