package net.milkev.milkevsoreminers.common.blocks.miningRig.Slottable.io;

import com.mojang.serialization.MapCodec;
import net.milkev.milkevsoreminers.common.blockEntities.miningRig.Slottable.io.BaseMiningRigStorageBlockEntity;
import net.minecraft.block.*;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityTicker;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.screen.NamedScreenHandlerFactory;
import net.minecraft.util.ActionResult;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public abstract class BaseMiningRigStorageBlock extends BlockWithEntity implements BlockEntityProvider {
    
    NamedScreenHandlerFactory screenFactory;
    public BaseMiningRigStorageBlock(Settings settings, NamedScreenHandlerFactory factory) {
        super(settings);
        screenFactory = factory;
    }
    
    @Override
    public ActionResult onUse(BlockState blockState, World world, BlockPos blockPos, PlayerEntity playerEntity, BlockHitResult blockHitResult) {
        if(world.isClient) {return ActionResult.SUCCESS;}
        if(!playerEntity.isSneaking()) {
            playerEntity.openHandledScreen((BaseMiningRigStorageBlockEntity) world.getBlockEntity(blockPos));
            return ActionResult.CONSUME;
        }
        return super.onUse(blockState, world, blockPos, playerEntity, blockHitResult);
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

}
