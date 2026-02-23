package net.milkev.milkevsoreminers.common.blocks.miningRig.Slottable.io;

import net.milkev.milkevsoreminers.common.blockEntities.miningRig.Slottable.io.BasicMiningRigEnergyBlockEntity;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.util.math.BlockPos;
import org.jetbrains.annotations.Nullable;

public class BasicMiningRigEnergyBlock extends BaseMiningRigEnergyBlock{
    public BasicMiningRigEnergyBlock(Settings settings) {
        super(settings);
    }

    @Override
    public @Nullable BlockEntity createBlockEntity(BlockPos blockPos, BlockState blockState) {
        return new BasicMiningRigEnergyBlockEntity(blockPos, blockState);
    }
}
