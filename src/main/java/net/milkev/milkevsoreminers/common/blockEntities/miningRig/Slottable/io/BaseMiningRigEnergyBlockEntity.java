package net.milkev.milkevsoreminers.common.blockEntities.miningRig.Slottable.io;

import net.fabricmc.fabric.api.transfer.v1.transaction.Transaction;
import net.milkev.milkevsoreminers.common.MilkevsOreMiners;
import net.milkev.milkevsoreminers.common.util.MilkevsAugmentedEnergyStorage;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.util.math.BlockPos;

public abstract class BaseMiningRigEnergyBlockEntity extends BlockEntity {
    
    private MilkevsAugmentedEnergyStorage energyStorage = new MilkevsAugmentedEnergyStorage(50000, 1, true, true) {
        @Override
        protected void onFinalCommit() {
            markDirty();
        }
    };

    public BaseMiningRigEnergyBlockEntity(BlockEntityType blockEntityType, BlockPos blockPos, BlockState blockState) {
        super(blockEntityType, blockPos, blockState);
    }
    
    public MilkevsAugmentedEnergyStorage getEnergyStorage() {
        return energyStorage;
    }
    
    public long extract(long amount, Transaction transaction) {
        markDirty();
        return energyStorage.extract(amount, transaction);
    }

    //nbt go brr
    @Override
    public void writeNbt(NbtCompound nbt, RegistryWrapper.WrapperLookup registries) {

        nbt.putLong("energy", energyStorage.getAmount());
        super.writeNbt(nbt, registries);
    }

    @Override
    public void readNbt(NbtCompound nbt, RegistryWrapper.WrapperLookup registries) {
        super.readNbt(nbt, registries);

        energyStorage.setAmount(nbt.getLong("energy"));
    }
}
