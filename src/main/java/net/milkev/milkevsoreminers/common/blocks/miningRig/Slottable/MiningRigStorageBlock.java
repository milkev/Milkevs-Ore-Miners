package net.milkev.milkevsoreminers.common.blocks.miningRig.Slottable;

import com.mojang.serialization.MapCodec;
import net.milkev.milkevsoreminers.common.blockEntities.miningRig.Slottable.BaseMiningRigStorageBlockEntity;
import net.milkev.milkevsoreminers.common.blockEntities.miningRig.Slottable.MiningRigStorageBlockEntityAdvanced;
import net.milkev.milkevsoreminers.common.blockEntities.miningRig.Slottable.MiningRigStorageBlockEntityBasic;
import net.minecraft.block.*;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityTicker;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

public class MiningRigStorageBlock extends BlockWithEntity implements BlockEntityProvider {
    
    private int tier;
    public MiningRigStorageBlock(Settings settings, int tier) {
        super(settings);
        tier = tier;
    }

    @Override
    protected MapCodec<? extends BlockWithEntity> getCodec() {
        return null;
    }

    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(World world, BlockState state, BlockEntityType<T> type) {
        return (world1, pos, state1, blockEntity) -> {
            if (blockEntity instanceof BlockEntityTicker) {
                ((BlockEntityTicker) blockEntity).tick(world1, pos, state1, blockEntity);
            }
        };
    }

    @Override
    protected void onStateReplaced(BlockState blockState, World world, BlockPos blockPos, BlockState blockState2, boolean bl) {
        BaseMiningRigStorageBlockEntity blockEntity = (BaseMiningRigStorageBlockEntity) world.getBlockEntity(blockPos);
        blockEntity.getItems().iterator().forEachRemaining(stack -> Block.dropStack(world, blockPos, stack));
        super.onStateReplaced(blockState, world, blockPos, blockState2, bl);
    }

    @Override
    public BlockRenderType getRenderType(BlockState state) {
        return BlockRenderType.MODEL;
    }

    @Nullable
    @Override
    public BlockEntity createBlockEntity(BlockPos blockPos, BlockState blockState) {
        switch (tier) {
            case 1: return new MiningRigStorageBlockEntityBasic(blockPos, blockState);
            case 2: return new MiningRigStorageBlockEntityAdvanced(blockPos, blockState);
            default: return null;
        }
    }
}
