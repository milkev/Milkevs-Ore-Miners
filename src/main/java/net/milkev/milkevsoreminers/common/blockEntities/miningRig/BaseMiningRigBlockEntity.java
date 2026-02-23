package net.milkev.milkevsoreminers.common.blockEntities.miningRig;

import net.fabricmc.fabric.api.screenhandler.v1.ExtendedScreenHandlerFactory;
import net.fabricmc.fabric.api.transfer.v1.transaction.Transaction;
import net.milkev.milkevsmultiblocklibrary.common.blockEntities.MultiBlockEntity;
import net.milkev.milkevsoreminers.common.MilkevsOreMiners;
import net.milkev.milkevsoreminers.common.blockEntities.miningRig.Slottable.io.BaseMiningRigEnergyBlockEntity;
import net.milkev.milkevsoreminers.common.blockEntities.miningRig.Slottable.io.BaseMiningRigStorageBlockEntity;
import net.milkev.milkevsoreminers.common.recipes.RecipeUtils;
import net.milkev.milkevsoreminers.common.util.BlockPosPayload;
import net.milkev.milkevsoreminers.common.util.MilkevsAugmentedEnergyStorage;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import java.util.*;

public abstract class BaseMiningRigBlockEntity extends MultiBlockEntity implements ExtendedScreenHandlerFactory<BlockPosPayload> {
    
    public MilkevsAugmentedEnergyStorage energyStorage = new MilkevsAugmentedEnergyStorage(50000, 1, true, false) {
        @Override
        protected void onFinalCommit() {
            markDirty();
        }
    };
    //tracks how much power has been used in the current operation (counts down from the energy cost of the recipe)
    //power usage is set from recipes!
    long powerUsage = -666;
    //for use in gui
    float progress = 0;
    boolean laserHasLOS = false;
    boolean firstTick = true;
    int refreshValidateTimer = 0;
    private Optional<Map<BlockPos, Block>> ioItemBlocks = Optional.empty();
    private Optional<Map<BlockPos, Block>> ioEnergyBlocks = Optional.empty();
    private Optional<Map.Entry<BlockPos, Block>> laserBlock = Optional.empty();

    public BaseMiningRigBlockEntity(BlockEntityType blockEntityType, BlockPos blockPos, BlockState blockState) {
        super(blockEntityType, blockPos, blockState);
    }
    
    public abstract void cacheRecipe(World world);
    public abstract void decacheRecipe();
    public abstract List<Item> getRecipeOutput();
    public abstract long getPowerCost();
    public abstract long getPowerUsageSpeed();
    public abstract float getRecipeChance();
    public abstract float getRecipeRolls();
    public abstract List<Block> validItemIOBlocks();
    public abstract List<Block> validEnergyIOBlocks();
    public abstract List<Block> validLaserBlocks();

    //invalidate structure if it doesnt contain atleast 1 item IO and 1 energy IO block
    @Override
    public void validateStructure() {
        super.validateStructure();
        if(isStructureValid()) {
            if (!checkIoItemBlock()) {
                invalidateStructure(Text.translatable("milkevsoreminers.notification.missing_item_io"));
            } else if (!checkIoEnergyBlock()) {
                invalidateStructure(Text.translatable("milkevsoreminers.notification.missing_energy_io"));
            }
        }
        if(isStructureValid()) {
            laserBlock = findFirstBlockFromList(validLaserBlocks());
            ioItemBlocks = findAllOfBlockFromList(validItemIOBlocks());
            //caching the recipe so it doesnt get called potentially every tick. Im aware that this means it wont obey /reload, however this boosts performance a lot and /reload is basically not an option if you have more than a small handful of mods anyway
            //note that this is not saved, so simply breaking and replacing the block, or unloading and reloading the chunk should update the cache
            cacheRecipe(world);
        } else {
            laserBlock = Optional.empty();
            decacheRecipe();
        }
    }
    
    //void sum variables when the structure is invalidated
    @Override
    public void invalidateStructure(MutableText text) {
        super.invalidateStructure(text);
        laserBlock = Optional.empty();
        ioItemBlocks = Optional.empty();
        ioEnergyBlocks = Optional.empty();
        decacheRecipe();
    }
    
    @Override
    public void tick(World world, BlockPos blockPos, BlockState blockState, MultiBlockEntity blockEntity) {
        if(firstTick) {
            validateStructure();
        }
        //we'll use this to periodically check if the structure is still valid. this will also refresh references to io blocks and stuff, which should only really matter if they've been moved to a different spot on the structure but who knows
        refreshValidateTimer += MilkevsOreMiners.validStructureCheckTimer <= 0 ? 0 : 1;
        if(refreshValidateTimer > MilkevsOreMiners.validStructureCheckTimer) {
            validateStructure();
        }
        if(isStructureValid()) {
            if(laserHasLOS) {
                if(!world.isReceivingRedstonePower(this.pos)) {
                    usePower();
                    if (powerUsage <= 0) {
                        List<ItemStack> output = RecipeUtils.handleDrops(getRecipeOutput(), getRecipeRolls(), getRecipeChance());
                        if (ioItemBlocks.isPresent()) {
                            Set<BlockPos> ioItemBlocksPos = ioItemBlocks.get().keySet();
                            ioItemBlocksPos.iterator().forEachRemaining(ioBlockPos -> {
                                if (!output.isEmpty()) {
                                    BlockEntity storageBlockEntityUnsure = world.getBlockEntity(ioBlockPos);
                                    if (storageBlockEntityUnsure instanceof BaseMiningRigStorageBlockEntity storageBlockEntity) {
                                        List<ItemStack> remainder = storageBlockEntity.getInventory().addItems(output);
                                        output.clear();
                                        output.addAll(remainder);
                                    }
                                }
                            });
                        }
                        this.powerUsage = this.getPowerCost();
                        markDirty();
                    }
                }
            } else {
                laserHasLOS = checkLaserLOS();
                markDirty();
            }
            obtainPower(world);
        } else {
            validateStructure();
        }
    }
    
    //if the laser has los to the void
    private boolean checkLaserLOS() {
        Optional<Map.Entry<BlockPos, Block>> test = findFirstBlockFromList(validLaserBlocks());
        if(test.isEmpty()) {
            //System.out.println("couldnt find laser block");
            return false;
        }
        BlockPos laserPos = test.get().getKey();
        for(int i = -1; i >= world.getBottomY() + Math.abs(laserPos.getY()); i--) {
            //System.out.println("Checked: " + laserPos.add(0, i, 0).getY());
            if(world.getBlockState(laserPos.add(0, i, 0)).getBlock() == Blocks.BEDROCK) {
                return true;
            }
            if(world.getBlockState(laserPos.add(0, i, 0)).isOpaque()) {
                return false;
            }
        }
        return true;
    }
    
    //om nom energy storage
    public MilkevsAugmentedEnergyStorage getEnergyStorage() {
        return this.energyStorage;
    }
    
    //get progress of recipe
    public float getProgress() {return progress;}
    
    //consume power to progress recipe
    public void usePower() {
        if(this.getEnergyStorage().hasEnoughEnergy(getPowerUsageSpeed()) && this.powerUsage > 0) {
            long use = this.getPowerUsageSpeed();
            if(this.getEnergyStorage().useExactly(use)) {
                this.powerUsage -= use;
            }
            progress = 1 - ((float) powerUsage / (float) getPowerCost());
            markDirty();
        }
    }
    
    //steal power from all power io blocks in structure
    public void obtainPower(World world) {
        if(ioEnergyBlocks.isPresent() && !this.energyStorage.isFull()) {
            Iterator<BlockPos> iterator = ioEnergyBlocks.get().keySet().iterator();
            while(iterator.hasNext()) {
                BlockPos entry = iterator.next();
                BlockEntity be = world.getBlockEntity(entry);
                if(be != null) {
                    if(be instanceof BaseMiningRigEnergyBlockEntity miningRigPowerBlockEntity) {
                        try(Transaction tx = Transaction.openOuter()) {
                            //System.out.println("io block contains " + miningRigPowerBlockEntity.getEnergyStorage().getAmount());
                            long out = miningRigPowerBlockEntity.extract(this.energyStorage.getIoRate(), tx);
                            long leftover = out - this.energyStorage.insert(out, tx);
                            miningRigPowerBlockEntity.getEnergyStorage().insert(leftover, tx);
                            //System.out.println("Attempted to eat " + out + "rf from block at " + entry + " with " + leftover + "rf leftover");
                            tx.commit();
                        }
                        if(this.energyStorage.isFull()) {
                            iterator.remove();
                        }
                    }
                }
            }
        }
    }
    
    //return false if none found, update list if found
    public boolean checkIoItemBlock() {
        Optional<Map<BlockPos, Block>> check = findAllOfBlockFromList(validItemIOBlocks());
        if(check.isPresent()) {
            ioItemBlocks = check;
        } else {
            ioItemBlocks = Optional.empty();
        }
        return check.isPresent();
    }
    
    //return false if none found, update list if found
    public boolean checkIoEnergyBlock() {
        Optional<Map<BlockPos, Block>> check = findAllOfBlockFromList(validEnergyIOBlocks());
        if(check.isPresent()) {
            ioEnergyBlocks = check;
        } else {
            ioEnergyBlocks = Optional.empty();
        }
        return check.isPresent();
    }
    
    //nbt go brr
    @Override
    public void writeNbt(NbtCompound nbt, RegistryWrapper.WrapperLookup registries) {

        nbt.putLong("powerusage", powerUsage);
        nbt.putLong("energy", getEnergyStorage().getAmount());
        nbt.putBoolean("laserlos", laserHasLOS);
        nbt.putFloat("progress", progress);
        super.writeNbt(nbt, registries);
    }

    @Override
    public void readNbt(NbtCompound nbt, RegistryWrapper.WrapperLookup registries) {
        super.readNbt(nbt, registries);

        powerUsage = nbt.getLong("powerUsage");
        getEnergyStorage().setAmount(nbt.getLong("energy"));
        laserHasLOS = nbt.getBoolean("laserlos");
        progress = nbt.getFloat("progress");
    }

    //screen go brr
    @Override
    public BlockPosPayload getScreenOpeningData(ServerPlayerEntity player) {
        return new BlockPosPayload(this.pos);
    }

    @Override
    public NbtCompound toInitialChunkDataNbt(RegistryWrapper.WrapperLookup registries) {
        return createNbt(registries);
    }

    //still not implemented, one day TM
    @Override
    protected boolean structureCanRotateVertically() {
        return false;
    }
    
}
