package net.milkev.milkevsoreminers.common.blocks;

import com.mojang.serialization.MapCodec;
import net.milkev.milkevsmultiblocklibrary.common.MilkevsMultiBlockLibrary;
import net.milkev.milkevsoreminers.common.MilkevsOreMiners;
import net.milkev.milkevsoreminers.common.blockEntities.miningRig.MiningRigBaseBlockEntity;
import net.milkev.milkevsoreminers.common.blockEntities.miningRig.BasicMiningRigBlockEntity;
import net.minecraft.block.*;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityTicker;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.util.ActionResult;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

import java.util.Map;

public class MiningRigBlock extends BlockWithEntity implements BlockEntityProvider {
    
    int tier;
    
    public MiningRigBlock(Settings settings, int tier) {
        super(settings);
        this.tier = tier;
    }

    @Override
    public ActionResult onUse(BlockState blockState, World world, BlockPos blockPos, PlayerEntity playerEntity, BlockHitResult blockHitResult) {
        if(world.isClient) {return ActionResult.SUCCESS;}
        MiningRigBaseBlockEntity miningRigBlockEntity = (MiningRigBaseBlockEntity) world.getBlockEntity(blockPos);
        if(miningRigBlockEntity.isStructureValid()) {
            miningRigBlockEntity.getEnergyStorage().setAmount(1000 + miningRigBlockEntity.getEnergyStorage().getAmount());
            //insert case for tier
            playerEntity.openHandledScreen(null);
            return ActionResult.CONSUME;
        }
        //id love to move this to the multiblock library, but unfortunately im pretty sure its impossible to do just from the block entity
        if(playerEntity.getStackInHand(playerEntity.getActiveHand()).getItem() != MilkevsMultiBlockLibrary.MULTIBLOCK_BUILDER
                && !playerEntity.isSneaking()) {
            playerEntity.sendMessage(MilkevsOreMiners.makeTranslation("notification.incomplete_structure"));
            return ActionResult.CONSUME;
        }

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

    @Nullable
    @Override
    public BlockEntity createBlockEntity(BlockPos blockPos, BlockState blockState) {
        
            return switch (tier) {
                case 1 -> 
                    new BasicMiningRigBlockEntity(blockPos, blockState);
                case 2 ->
                    new BasicMiningRigBlockEntity(blockPos, blockState);
                case 3 ->
                    new BasicMiningRigBlockEntity(blockPos, blockState);
                case 4 ->
                    new BasicMiningRigBlockEntity(blockPos, blockState);
                default -> 
                    null;
            };
    }
}
