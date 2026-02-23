package net.milkev.milkevsoreminers.common.blocks.miningRig.Slottable.io;

import com.mojang.serialization.MapCodec;
import net.milkev.milkevsoreminers.common.blockEntities.miningRig.Slottable.io.BaseMiningRigEnergyBlockEntity;
import net.minecraft.block.*;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.ActionResult;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public abstract class BaseMiningRigEnergyBlock extends BlockWithEntity implements BlockEntityProvider {
    
    protected BaseMiningRigEnergyBlock(Settings settings) {
        super(settings);
    }

    @Override
    public ActionResult onUse(BlockState blockState, World world, BlockPos blockPos, PlayerEntity playerEntity, BlockHitResult blockHitResult) {
        if(world.isClient) {return ActionResult.SUCCESS;}
        ((BaseMiningRigEnergyBlockEntity) world.getBlockEntity(blockPos)).getEnergyStorage().setAmount(50000);
        return super.onUse(blockState, world, blockPos, playerEntity, blockHitResult);
    }

    @Override
    protected MapCodec<? extends BlockWithEntity> getCodec() {
        return null;
    }
    
    @Override
    public BlockRenderType getRenderType(BlockState state) {
        return BlockRenderType.MODEL;
    }
}
