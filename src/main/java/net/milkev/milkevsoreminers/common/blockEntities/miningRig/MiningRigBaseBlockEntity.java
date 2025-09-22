package net.milkev.milkevsoreminers.common.blockEntities.miningRig;

import net.fabricmc.fabric.api.transfer.v1.item.ItemStorage;
import net.fabricmc.fabric.api.transfer.v1.item.ItemVariant;
import net.fabricmc.fabric.api.transfer.v1.storage.Storage;
import net.fabricmc.fabric.api.transfer.v1.transaction.Transaction;
import net.milkev.milkevsmultiblocklibrary.common.blockEntities.MultiBlockEntity;
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

public abstract class MiningRigBaseBlockEntity extends MultiBlockEntity {

    //tracks how much power has been used in the current operation (counts down from the energy cost of the recipe)
    //power usage is set from recipes!
    int powerUsage = -666;
    //tracks the total cost of the recipe being processed for use in the gui
    int powerCost = 0;
    boolean laserHasLOS = false;


    public MiningRigBaseBlockEntity(BlockEntityType blockEntityType, BlockPos blockPos, BlockState blockState) {
        super(blockEntityType, blockPos, blockState);
    }
    
    public abstract void cacheRecipe(World world);
    public abstract List<Item> getRecipeOutput();
    public abstract int getPowerCost();
    public abstract int getPowerUsageSpeed();
    public abstract float getRecipeChance();
    public abstract float getRecipeRolls();
    public abstract MilkevsAugmentedEnergyStorage getEnergyStorage();

    @Override
    public void tick(World world, BlockPos blockPos, BlockState blockState, MultiBlockEntity blockEntity) {
        //caching the recipe so it doesnt get called potentially every tick. Im aware that this means it wont obey /reload, however this boosts performance a lot.
        cacheRecipe(world);
        if(isStructureValid()) {
            usePower();
            if(powerUsage<=0) {
                boolean hit = false;
                Iterator<Map.Entry<BlockPos, Block>> iterator = getBlocksInStructure().entrySet().iterator();
                while(iterator.hasNext()) {
                    Map.Entry<BlockPos, Block> entry = iterator.next();
                    Block blockStructure = entry.getValue();
                    BlockPos blockPosStructure = entry.getKey();
                    if (!hit) {
                        if (blockStructure == Blocks.BARREL) {
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
                                    hit = true;
                                    powerUsage = getPowerCost();
                                }
                            }
                        }
                    }
                }
            }
        } else {
            validateStructure();
        }
    }

    @Override
    public ActionResult interact(BlockState blockState, World world, BlockPos blockPos, PlayerEntity playerEntity, BlockHitResult blockHitResult) {
        //Open GUI
        //GUI needs; Power bar, Progress bar, If structure is valid, If laser has LOS, Output slots?
        validateStructure();
        //System.out.println(isStructureValid());
        getEnergyStorage().amount += 5000;
        if(!isStructureValid()) { return super.interact(blockState, world, blockPos, playerEntity, blockHitResult);}
        return ActionResult.FAIL;
    }
    
    public void usePower() {
        if(this.getEnergyStorage().hasEnoughEnergy(getPowerUsageSpeed())) {
            int use = getPowerUsageSpeed();
            this.getEnergyStorage().consumeEnergy(use);
            this.powerUsage -= use;
        }
    }
    @Override
    public void writeNbt(NbtCompound nbt, RegistryWrapper.WrapperLookup registries) {

        nbt.putInt("progress", powerUsage);
        nbt.putLong("energy", getEnergyStorage().amount);

        super.writeNbt(nbt, registries);
    }

    @Override
    public void readNbt(NbtCompound nbt, RegistryWrapper.WrapperLookup registries) {
        super.readNbt(nbt, registries);

        powerUsage = nbt.getInt("progress");
        getEnergyStorage().amount = nbt.getLong("energy");

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
