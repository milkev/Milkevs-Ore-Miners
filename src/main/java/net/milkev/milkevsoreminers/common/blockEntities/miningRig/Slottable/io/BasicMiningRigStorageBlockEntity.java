package net.milkev.milkevsoreminers.common.blockEntities.miningRig.Slottable.io;

import net.milkev.milkevsoreminers.common.util.MilkevsAugmentedInventory;
import net.minecraft.block.BlockState;
import net.minecraft.util.math.BlockPos;

public class BasicMiningRigStorageBlockEntity extends BaseMiningRigStorageBlockEntity{
    
    private final MilkevsAugmentedInventory inventory;

    public BasicMiningRigStorageBlockEntity(BlockPos blockPos, BlockState blockState) {
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
