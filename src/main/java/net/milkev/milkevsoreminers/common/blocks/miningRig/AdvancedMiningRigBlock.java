package net.milkev.milkevsoreminers.common.blocks.miningRig;

import net.milkev.milkevsmultiblocklibrary.common.blockEntities.MultiBlockEntity;
import net.milkev.milkevsoreminers.common.blockEntities.miningRig.AdvancedMiningRigBlockEntity;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.screen.NamedScreenHandlerFactory;
import net.minecraft.util.math.BlockPos;
import org.jetbrains.annotations.Nullable;

public class AdvancedMiningRigBlock extends MiningRigBlock{
    
    public AdvancedMiningRigBlock(Settings settings) {
        super(settings, null);
    }

    @Override
    public MultiBlockEntity makeBlockEntity(BlockPos blockPos, BlockState blockState) {
        return new AdvancedMiningRigBlockEntity(blockPos, blockState);
    }
}
