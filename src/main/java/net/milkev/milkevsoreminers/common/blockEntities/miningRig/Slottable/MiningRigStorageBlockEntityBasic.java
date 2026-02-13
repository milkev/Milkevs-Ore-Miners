package net.milkev.milkevsoreminers.common.blockEntities.miningRig.Slottable;

import net.milkev.milkevsoreminers.common.util.MilkevsAugmentedInventory;
import net.minecraft.block.BlockState;
import net.minecraft.util.math.BlockPos;

public class MiningRigStorageBlockEntityBasic extends BaseMiningRigStorageBlockEntity{
    
    private final MilkevsAugmentedInventory inventory;

    public MiningRigStorageBlockEntityBasic(BlockPos blockPos, BlockState blockState) {
        super(blockPos, blockState);
        this.inventory =  MilkevsAugmentedInventory.ofSize(9);
        this.transferCooldown = 4;
        this.transferRate = 1;
    }

    @Override
    public MilkevsAugmentedInventory getInventory() {
        return inventory;
    }
}
