package net.milkev.milkevsoreminers.common.blocks.miningRig;

import net.milkev.milkevsmultiblocklibrary.common.blockEntities.MultiBlockEntity;
import net.milkev.milkevsoreminers.common.blockEntities.miningRig.BasicMiningRigBlockEntity;
import net.minecraft.block.BlockState;
import net.minecraft.util.math.BlockPos;

public class BasicMiningRigBlock extends BaseMiningRigBlock {
    
    public BasicMiningRigBlock(Settings settings) {
        super(settings, null);
    }

    @Override
    public MultiBlockEntity makeBlockEntity(BlockPos blockPos, BlockState blockState) {
        return new BasicMiningRigBlockEntity(blockPos, blockState);
    }
}
