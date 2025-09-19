package net.milkev.milkevsoreminers.common.util;

import team.reborn.energy.api.base.SimpleEnergyStorage;

public class MilkevsAugmentedEnergyStorage extends SimpleEnergyStorage {
    public MilkevsAugmentedEnergyStorage(long capacity, long maxInsert, long maxExtract) {
        super(capacity, maxInsert, maxExtract);
    }

    public boolean hasEnoughEnergy(int amount) {
        if(this.amount >= amount) {
            return true;
        }
        return false;
    }

    public boolean consumeEnergy(int amount) {
        if(hasEnoughEnergy(amount)) {
            this.amount -= amount;
        }
        return false;
    }

    public void insertEnergy(int amountInsert) {
        if(amountInsert + getAmount() > getCapacity()) {
            this.amount = getCapacity();
        } else {
            this.amount += amountInsert;
        }
    }

}
