package net.milkev.milkevsoreminers.common.blockEntities.miningRig.Slottable.io;

import net.milkev.milkevsoreminers.common.MilkevsOreMiners;
import net.minecraft.block.BlockState;
import net.minecraft.util.math.BlockPos;

public class BasicMiningRigEnergyBlockEntity extends BaseMiningRigEnergyBlockEntity {
    public BasicMiningRigEnergyBlockEntity(BlockPos blockPos, BlockState blockState) {
        super(MilkevsOreMiners.MINING_RIG.BASIC.ENERGY_BLOCK_ENTITY, blockPos, blockState);
        this.getEnergyStorage().setCapacity(25000);
    }
}
