package net.milkev.milkevsoreminers.common.blocks.miningRig;

import com.mojang.serialization.MapCodec;
import net.milkev.milkevsmultiblocklibrary.common.MilkevsMultiBlockLibrary;
import net.milkev.milkevsmultiblocklibrary.common.blockEntities.MultiBlockEntity;
import net.milkev.milkevsoreminers.common.MilkevsOreMiners;
import net.milkev.milkevsoreminers.common.blockEntities.miningRig.MiningRigBaseBlockEntity;
import net.minecraft.block.*;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityTicker;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.screen.NamedScreenHandlerFactory;
import net.minecraft.util.ActionResult;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.World;

public abstract class MiningRigBlock extends BlockWithEntity implements BlockEntityProvider {
    
    NamedScreenHandlerFactory screenFactory;
    public MiningRigBlock(Settings settings, NamedScreenHandlerFactory factory) {
        super(settings);
        screenFactory = factory;
    }

    @Override
    public ActionResult onUse(BlockState blockState, World world, BlockPos blockPos, PlayerEntity playerEntity, BlockHitResult blockHitResult) {
        if(world.isClient) {return ActionResult.SUCCESS;}
        MiningRigBaseBlockEntity miningRigBlockEntity = (MiningRigBaseBlockEntity) world.getBlockEntity(blockPos);
        if(miningRigBlockEntity.isStructureValid()) {
            miningRigBlockEntity.getEnergyStorage().setAmount(1000 + miningRigBlockEntity.getEnergyStorage().getAmount());
            //insert case for tier
            playerEntity.openHandledScreen(miningRigBlockEntity);
            return ActionResult.CONSUME;
        }
        //id love to move this to the multiblock library, but unfortunately im pretty sure its impossible to do just from the block entity
        if(playerEntity.getStackInHand(playerEntity.getActiveHand()).getItem() != MilkevsMultiBlockLibrary.MULTIBLOCK_BUILDER
                && !playerEntity.isSneaking()) {
            playerEntity.sendMessage(MilkevsOreMiners.makeTranslation("notification.incomplete_structure"), true);
            return ActionResult.CONSUME;
        }
        
        //this call base multiblockentity which is needed for logic to enable/disable preview
        return miningRigBlockEntity.interact(blockState, world, blockPos, playerEntity, blockHitResult);
    }

    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(World world, BlockState state, BlockEntityType<T> type) {
        if(world.isClient) {
            return null;
        }
        return (world1, pos, state1, blockEntity) -> {
            if (blockEntity instanceof BlockEntityTicker) {
                ((BlockEntityTicker) blockEntity).tick(world1, pos, state1, blockEntity);
            }
        };
    }

    @Override
    protected MapCodec<? extends BlockWithEntity> getCodec() {
        return null;
    }

    @Override
    public BlockRenderType getRenderType(BlockState state) {
        return BlockRenderType.MODEL;
    }
    
    public abstract MultiBlockEntity makeBlockEntity(BlockPos blockPos, BlockState blockState);
    
    @Override
    public BlockEntity createBlockEntity(BlockPos blockPos, BlockState blockState) {
        MultiBlockEntity blockEntity = makeBlockEntity(blockPos, blockState);
        setDirection(blockEntity);
        return blockEntity;
    }
    
    private void setDirection(MultiBlockEntity blockEntity) {
        if(blockEntity != null) {
            if (startDir == Direction.SOUTH || startDir == Direction.NORTH) {
                blockEntity.setDirection(startDir.rotateYClockwise());
            } else if (startDir == Direction.EAST || startDir == Direction.WEST) {
                blockEntity.setDirection(startDir.rotateYCounterclockwise());
            }
        }
    }
    
    Direction startDir;
    
    @Override
	public BlockState getPlacementState(ItemPlacementContext ctx) {
        startDir = ctx.getHorizontalPlayerFacing().getOpposite();
        return super.getPlacementState(ctx);
    }
}
