package net.milkev.milkevsoreminers.common.blockEntities.miningRig;

import net.fabricmc.fabric.api.transfer.v1.item.ItemStorage;
import net.fabricmc.fabric.api.transfer.v1.item.ItemVariant;
import net.fabricmc.fabric.api.transfer.v1.storage.Storage;
import net.fabricmc.fabric.api.transfer.v1.transaction.Transaction;
import net.milkev.milkevsmultiblocklibrary.common.blockEntities.MultiBlockEntity;
import net.milkev.milkevsoreminers.common.MilkevsOreMiners;
import net.milkev.milkevsoreminers.common.recipes.RecipeUtils;
import net.milkev.milkevsoreminers.common.util.MilkevsAugmentedEnergyStorage;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.network.codec.PacketCodecs;
import net.minecraft.network.listener.ClientPlayPacketListener;
import net.minecraft.network.packet.Packet;
import net.minecraft.network.packet.s2c.play.BlockEntityUpdateS2CPacket;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.util.ActionResult;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public abstract class MiningRigBaseBlockEntity extends MultiBlockEntity {
    
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


    public MiningRigBaseBlockEntity(BlockEntityType blockEntityType, BlockPos blockPos, BlockState blockState) {
        super(blockEntityType, blockPos, blockState);
    }
    
    public abstract void cacheRecipe(World world);
    public abstract List<Item> getRecipeOutput();
    public abstract long getPowerCost();
    public abstract long getPowerUsageSpeed();
    public abstract float getRecipeChance();
    public abstract float getRecipeRolls();

    @Override
    public void tick(World world, BlockPos blockPos, BlockState blockState, MultiBlockEntity blockEntity) {
        //caching the recipe so it doesnt get called potentially every tick. Im aware that this means it wont obey /reload, however this boosts performance a lot and /reload is basically not an option if you have more than a small handful of mods anyway
        cacheRecipe(world);
        //add timer to periodically check validity of structure
        if(isStructureValid()) {
            if(laserHasLOS) {
                usePower();
                if (powerUsage <= 0) {
                    Optional<Map.Entry<BlockPos, Block>> test = findFirstBlock(MilkevsOreMiners.MINING_RIG.BASIC.IO_STORAGE);
                    if(test.isPresent()) {
                        Map.Entry<BlockPos, Block> entry = test.get();
                        Block blockStructure = entry.getValue();
                        BlockPos blockPosStructure = entry.getKey();
                        if (blockStructure == MilkevsOreMiners.MINING_RIG.BASIC.IO_STORAGE) {
                            //replace with not-pointless-transaction method to be built into IO_STORAGE block
                            Storage<ItemVariant> itemStorage = ItemStorage.SIDED.find(getWorld(), blockPosStructure, Direction.DOWN);
                            if (itemStorage != null) {
                                try (Transaction transaction = Transaction.openOuter()) {
                                    Iterator<ItemStack> iterator1 = RecipeUtils.handleDrops(getRecipeOutput(), getRecipeRolls(), getRecipeChance()).iterator();
                                    while (iterator1.hasNext()) {
                                        ItemStack itemStack = iterator1.next();
                                        itemStorage.insert(ItemVariant.of(itemStack), 1, transaction);
                                    }
                                    //System.out.println("commit transaction!");
                                    transaction.commit();
                                    powerUsage = getPowerCost();
                                }
                            }
                        }
                    }
                }
            } else {
                laserHasLOS = checkLaserLOS();
                markDirty();
            }
        } else {
            validateStructure();
        }
    }
    
    private boolean checkLaserLOS() {
        Optional<Map.Entry<BlockPos, Block>> test = findFirstBlock(Blocks.GRAY_CONCRETE);
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
    
    public MilkevsAugmentedEnergyStorage getEnergyStorage() {
        return this.energyStorage;
    }
    
    public void usePower() {
        if(this.getEnergyStorage().hasEnoughEnergy(getPowerUsageSpeed())) {
            long use = this.getPowerUsageSpeed();
            if(this.getEnergyStorage().useExactly(use)) {
                this.powerUsage -= use;
            }
            progress = 1 - ((float) powerUsage / (float) getPowerCost());
            markDirty();
        }
    }
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

    @Override
    public NbtCompound toInitialChunkDataNbt(RegistryWrapper.WrapperLookup registries) {
        return createNbt(registries);
    }

    @Override
    protected boolean structureCanRotateVertically() {
        return false;
    }
    
}
