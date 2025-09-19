package net.milkev.milkevsoreminers.common.blockEntities.miningRig;

import net.fabricmc.fabric.api.transfer.v1.item.ItemStorage;
import net.fabricmc.fabric.api.transfer.v1.item.ItemVariant;
import net.fabricmc.fabric.api.transfer.v1.storage.Storage;
import net.fabricmc.fabric.api.transfer.v1.transaction.Transaction;
import net.milkev.milkevsmultiblocklibrary.common.blockEntities.MultiBlockEntity;
import net.milkev.milkevsoreminers.common.MilkevsOreMiners;
import net.milkev.milkevsoreminers.common.util.MilkevsAugmentedEnergyStorage;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityTicker;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.network.listener.ClientPlayPacketListener;
import net.minecraft.network.packet.Packet;
import net.minecraft.network.packet.s2c.play.BlockEntityUpdateS2CPacket;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

public abstract class MiningRigBaseBlockEntity extends MultiBlockEntity {

    //base speed is set from recipes!
    int powerUsageSpeed;
    //tracks how much power has been used in the current operation (counts down from the energy cost of the recipe)
    //power usage is set from recipes!
    int powerUsage = -666;
    //tracks the total cost of the recipe being processed for use in the gui
    int powerCost = 0;
    boolean laserHasLOS = false;


    public MiningRigBaseBlockEntity(BlockEntityType blockEntityType, BlockPos blockPos, BlockState blockState) {
        super(blockEntityType, blockPos, blockState);
    }

    @Override
    public void ticker(World world, BlockPos blockPos, BlockState blockState, MultiBlockEntity blockEntity) {
        //caching the recipe so it doesnt get called potentially every tick. Im aware that this means it wont obey /reload, however this should help performance? feel free to let me know if this doesnt matter lmao.
        cacheRecipe(world);
        if(isStructureValid()) {
            usePower();
            if(powerUsage<=0) {
                getBlocksInStructure().forEach((blockPosStructure, blockStructure) -> {
                    if(blockStructure == Blocks.BARREL) {
                        Storage<ItemVariant> itemStorage = ItemStorage.SIDED.find(getWorld(), blockPosStructure, Direction.DOWN);
                        if(itemStorage != null) {
                            try(Transaction transaction = Transaction.openOuter()) {
                                itemStorage.insert(ItemVariant.of(getRecipeOutput()), 1, transaction);
                                //System.out.println("commit transaction!");
                                transaction.commit();
                            }
                        }
                    }
                });
                powerUsage = getRecipePowerCost();
            }
        }
    }
    
    public abstract void cacheRecipe(World world); //also sets powerUsageSpeed
    public abstract ItemStack getRecipeOutput();
    public abstract int getRecipePowerCost();
    public abstract MilkevsAugmentedEnergyStorage getEnergyStorage();

    public ActionResult interact(BlockState blockState, World world, BlockPos blockPos, PlayerEntity playerEntity, Hand hand, BlockHitResult blockHitResult) {
        //Open GUI
        //GUI needs; Power bar, Progress bar, If structure is valid, If laser has LOS, Output slots?
        validateStructure();
        System.out.println(isStructureValid());
        getEnergyStorage().amount += 5000;
        return ActionResult.FAIL;
    }

    //would also contain logic for upgrades
    //for now just makes sure recipes dont use more power than specified
    public int getPowerUsageSpeed() {
        if(powerUsage < powerUsageSpeed) {
            return powerUsage;
        }
        return powerUsageSpeed;
    }

    public abstract void usePower();
    
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

    @Nullable
    @Override
    public Packet<ClientPlayPacketListener> toUpdatePacket() {
        return BlockEntityUpdateS2CPacket.create(this);
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
