package net.milkev.milkevsoreminers.common.blocks.miningRig;

import net.milkev.milkevsmultiblocklibrary.common.blockEntities.MultiBlockEntity;
import net.milkev.milkevsoreminers.common.blockEntities.miningRig.BasicMiningRigBlockEntity;
import net.milkev.milkevsoreminers.common.gui.BasicMiningRigSceenHandler;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.screen.NamedScreenHandlerFactory;
import net.minecraft.util.math.BlockPos;
import org.jetbrains.annotations.Nullable;

public class BasicMiningRigBlock extends MiningRigBlock{
    
    public BasicMiningRigBlock(Settings settings) {
        super(settings, null);
    }

    @Override
    public MultiBlockEntity makeBlockEntity(BlockPos blockPos, BlockState blockState) {
        return new BasicMiningRigBlockEntity(blockPos, blockState);
    }
}
