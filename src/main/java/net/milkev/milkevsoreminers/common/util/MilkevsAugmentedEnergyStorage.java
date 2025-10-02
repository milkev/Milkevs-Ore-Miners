package net.milkev.milkevsoreminers.common.util;

import net.fabricmc.fabric.api.transfer.v1.transaction.TransactionContext;
import net.fabricmc.fabric.api.transfer.v1.transaction.base.SnapshotParticipant;
import team.reborn.energy.api.EnergyStorage;
import team.reborn.energy.api.base.SimpleEnergyStorage;

public class MilkevsAugmentedEnergyStorage extends SnapshotParticipant<Long> implements EnergyStorage {
    
    private long amount;
    private long capacity;
    private float ioFactor;
    private boolean insert;
    private boolean extract;
    
    public MilkevsAugmentedEnergyStorage(long capacity, long ioFactor, boolean insert, boolean extract) {
        if(ioFactor > 1.01 || ioFactor < -0.01) {
            throw new RuntimeException("ioFactor outside range! keep it between 0 and 1 please!");
        }
        this.capacity = capacity;
        this.ioFactor = ioFactor;
        this.insert = insert;
        this.extract = extract;
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
            long inserted = Math.min(this.getIoRate(), Math.min(amount, this.capacity - this.amount));
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
            long inserted = Math.min(this.getIoRate(), Math.min(amount, this.capacity));
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
        return this.insert;
    }

    @Override
    public boolean supportsExtraction() {
        return this.extract;
    }

    @Override
    public long getAmount() {
        return this.amount;
    }

    @Override
    public long getCapacity() {
        return this.capacity;
    }
    
    public long getIoRate() {
        return (long) (this.ioFactor * this.capacity);
    }
    
    public void setCapacity(long newCapacity) {
        this.capacity = newCapacity;
        if(this.amount > newCapacity) {
            this.amount = newCapacity;
        }
    }
    
    //hard set amount, just implemented for reading from nbt data
    public void setAmount(long amount) {
        this.amount = Math.min(this.capacity, amount);
    }
}
