package net.milkev.milkevsoreminers.common.blocks;

import com.mojang.serialization.MapCodec;
import net.milkev.milkevsoreminers.common.blockEntities.miningRig.MiningRigBaseBlockEntity;
import net.milkev.milkevsoreminers.common.blockEntities.miningRig.MiningRigTier1BlockEntity;
import net.milkev.milkevsoreminers.common.blockEntities.miningRig.MiningRigTier2BlockEntity;
import net.minecraft.block.BlockEntityProvider;
import net.minecraft.block.BlockRenderType;
import net.minecraft.block.BlockState;
import net.minecraft.block.BlockWithEntity;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityTicker;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

public class MiningRigBlock extends BlockWithEntity implements BlockEntityProvider {
    
    int tier;
    
    public MiningRigBlock(Settings settings, int tier) {
        super(settings);
        this.tier = tier;
    }

    public ActionResult onUse(BlockState blockState, World world, BlockPos blockPos, PlayerEntity playerEntity, Hand hand, BlockHitResult blockHitResult) {
        if(world.isClient) {return ActionResult.SUCCESS;}
        MiningRigBaseBlockEntity miningRigBlockEntity = (MiningRigBaseBlockEntity) world.getBlockEntity(blockPos);

        return miningRigBlockEntity.interact(blockState, world, blockPos, playerEntity, hand, blockHitResult);
    }

    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(World world, BlockState state, BlockEntityType<T> type) {
        return (world1, pos, state1, blockEntity) -> {
            if (blockEntity instanceof BlockEntityTicker) {
                ((BlockEntityTicker) blockEntity).tick(world1, pos, state1, blockEntity);
            }
        };
    }

    /*@Override
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(World world, BlockState state, BlockEntityType<T> type) {
        return validateTicker(type, MilkevsOreMiners.SIFTER_BLOCK_ENTITY,
                ((world1, blockPos, blockState, blockEntity) -> AdvancedSifterBlockEntity.tick(world1, blockPos, blockState, blockEntity)));
    }*/

    @Override
    protected MapCodec<? extends BlockWithEntity> getCodec() {
        return null;
    }

    @Override
    public BlockRenderType getRenderType(BlockState state) {
        return BlockRenderType.MODEL;
    }

    @Nullable
    @Override
    public BlockEntity createBlockEntity(BlockPos blockPos, BlockState blockState) {
        
            return switch (tier) {
                case 1 -> 
                    new MiningRigTier1BlockEntity(blockPos, blockState);
                case 2 ->
                    new MiningRigTier2BlockEntity(blockPos, blockState);
                case 3 ->
                    new MiningRigTier1BlockEntity(blockPos, blockState);
                case 4 ->
                    new MiningRigTier1BlockEntity(blockPos, blockState);
                default -> 
                    null;
            };
    }
}
