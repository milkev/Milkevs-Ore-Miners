package net.milkev.milkevsoreminers.common.blocks.miningRig;

import net.milkev.milkevsmultiblocklibrary.common.blockEntities.MultiBlockEntity;
import net.milkev.milkevsoreminers.common.blockEntities.miningRig.AdvancedMiningRigBlockEntity;
import net.minecraft.block.BlockState;
import net.minecraft.util.math.BlockPos;

public class AdvancedMiningRigBlock extends BaseMiningRigBlock {
    
    public AdvancedMiningRigBlock(Settings settings) {
        super(settings, null);
    }

    @Override
    public MultiBlockEntity makeBlockEntity(BlockPos blockPos, BlockState blockState) {
        return new AdvancedMiningRigBlockEntity(blockPos, blockState);
    }
}
