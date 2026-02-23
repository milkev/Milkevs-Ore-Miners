package net.milkev.milkevsoreminers.common.blocks.miningRig.Slottable.io;

import net.milkev.milkevsoreminers.common.blockEntities.miningRig.Slottable.io.AdvancedMiningRigStorageBlockEntity;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.screen.NamedScreenHandlerFactory;
import net.minecraft.util.math.BlockPos;
import org.jetbrains.annotations.Nullable;

public class AdvancedMiningRigStorageBlock extends BaseMiningRigStorageBlock{
    public AdvancedMiningRigStorageBlock(Settings settings, NamedScreenHandlerFactory factory) {
        super(settings, factory);
    }

    @Override
    public @Nullable BlockEntity createBlockEntity(BlockPos blockPos, BlockState blockState) {
        return new AdvancedMiningRigStorageBlockEntity(blockPos, blockState);
    }
}
