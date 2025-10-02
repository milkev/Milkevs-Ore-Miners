package net.milkev.milkevsoreminers.common.util;

import net.fabricmc.fabric.api.transfer.v1.transaction.TransactionContext;
import net.fabricmc.fabric.api.transfer.v1.transaction.base.SnapshotParticipant;
import team.reborn.energy.api.EnergyStorage;
import team.reborn.energy.api.base.SimpleEnergyStorage;

public class MilkevsAugmentedEnergyStorage extends SnapshotParticipant<Long> implements EnergyStorage {
    
    private long amount;
    private long capacity;
    private long maxInsert;
    private long maxExtract;
    
    public MilkevsAugmentedEnergyStorage(long capacity, long maxInsert, long maxExtract) {
        this.capacity = capacity;
        this.maxInsert = maxInsert;
        this.maxExtract = maxExtract;
    }

    public boolean hasEnoughEnergy(long amount) {
        if(this.amount >= amount) {
            return true;
        }
        return false;
    }

    @Override
    protected Long createSnapshot() {
        return this.amount;
    }

    @Override
    protected void readSnapshot(Long snapshot) {
        this.amount = snapshot;
    }

    @Override
    public long insert(long amount, TransactionContext transactionContext) {
        if(!this.supportsInsertion()) { return 0;}
        if(amount > 0) {
            long inserted = Math.min(this.maxInsert, Math.min(amount, this.capacity - this.amount));
            if(inserted > 0) {
                this.updateSnapshots(transactionContext);
                this.amount += inserted;
                return inserted;
            }
        }
        return 0;
    }

    @Override
    public long extract(long amount, TransactionContext transactionContext) {
        if(!this.supportsExtraction()) { return 0;}
        if(amount > 0) {
            long inserted = Math.min(this.maxExtract, Math.min(amount, this.capacity));
            if(inserted > 0) {
                this.updateSnapshots(transactionContext);
                this.amount -= inserted;
                return inserted;
            }
        }
        return 0;
    }
    
    public boolean useExactly(long amount) {
        if(this.amount >= amount) {
            this.amount -= amount;
            return true;
        }
        return false;
    }
    
    @Override
    public boolean supportsInsertion() {
        return this.maxInsert > 0;
    }

    @Override
    public boolean supportsExtraction() {
        return this.maxExtract > 0;
    }

    @Override
    public long getAmount() {
        return this.amount;
    }

    @Override
    public long getCapacity() {
        return this.capacity;
    }
    
    //hard set amount, just implemented for reading from nbt data
    public void setAmount(long amount) {
        this.amount = Math.min(this.capacity, amount);
    }
}
