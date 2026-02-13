package net.milkev.milkevsoreminers.common.blockEntities.miningRig.Slottable;

import net.milkev.milkevsoreminers.common.util.MilkevsAugmentedInventory;
import net.minecraft.block.BlockState;
import net.minecraft.util.math.BlockPos;

public class MiningRigStorageBlockEntityAdvanced extends BaseMiningRigStorageBlockEntity{
    private final MilkevsAugmentedInventory inventory;

    public MiningRigStorageBlockEntityAdvanced(BlockPos blockPos, BlockState blockState) {
        super(blockPos, blockState);
        this.inventory =  MilkevsAugmentedInventory.ofSize(12);
        this.transferCooldown = 3;
        this.transferRate = 4;
    }

    @Override
    public MilkevsAugmentedInventory getInventory() {
        return inventory;
    }
}
