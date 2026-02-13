package net.milkev.milkevsoreminers.common.blockEntities.miningRig.Slottable;

import net.fabricmc.fabric.api.screenhandler.v1.ExtendedScreenHandlerFactory;
import net.fabricmc.fabric.api.transfer.v1.item.ItemStorage;
import net.fabricmc.fabric.api.transfer.v1.item.ItemVariant;
import net.fabricmc.fabric.api.transfer.v1.storage.Storage;
import net.fabricmc.fabric.api.transfer.v1.transaction.Transaction;
import net.milkev.milkevsoreminers.common.MilkevsOreMiners;
import net.milkev.milkevsoreminers.common.util.BlockPosPayload;
import net.milkev.milkevsoreminers.common.util.MilkevsAugmentedInventory;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityTicker;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.Inventories;
import net.minecraft.inventory.SidedInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.util.collection.DefaultedList;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

public abstract class BaseMiningRigStorageBlockEntity extends BlockEntity implements BlockEntityTicker<BaseMiningRigStorageBlockEntity>, MilkevsAugmentedInventory, SidedInventory, ExtendedScreenHandlerFactory<BlockPosPayload> {
    
    //how many ticks between transfer attempts
    public int transferCooldown = 0;
    private int transferProgress;
    //how many items are attempted to be transferred
    public int transferRate = 0;
    
    public BaseMiningRigStorageBlockEntity(BlockPos blockPos, BlockState blockState) {
        super(MilkevsOreMiners.MINING_RIG.BASIC.ITEM_STORAGE_BLOCK_ENTITY, blockPos, blockState);
    }
    
    public abstract MilkevsAugmentedInventory getInventory();

    @Override
    public void tick(World world, BlockPos blockPos, BlockState blockState, BaseMiningRigStorageBlockEntity blockEntity) {
        transferProgress--;
        if(transferProgress <= 0) {
            transferProgress = transferCooldown;
            if(tryExport(world, blockPos, Direction.NORTH)) {}
                else if(tryExport(world, blockPos, Direction.EAST)) {}
                else if(tryExport(world, blockPos, Direction.SOUTH)) {}
                else if(tryExport(world, blockPos, Direction.WEST)) {}
        }
        markDirty();
    }
    
    public boolean tryExport(World world, BlockPos blockPos, Direction direction) {
        BlockPos targetBlockPos = blockPos.add(direction.getVector());
        Storage<ItemVariant> target = ItemStorage.SIDED.find(world, targetBlockPos, direction.getOpposite());
        if(target == null) {return false;}
        
        try(Transaction transaction = Transaction.openOuter()) {
            long moved = getInventory().exportAny(target, transferRate, transaction);
            if(moved > 0) {
                transaction.commit();
                return true;
            }
        }
        
        return false;
    }

    @Override
    public void writeNbt(NbtCompound nbt, RegistryWrapper.WrapperLookup registries) {

        nbt.putInt("transferProgress", transferProgress);
        Inventories.writeNbt(nbt, getInventory().getItems(), registries);
        super.writeNbt(nbt, registries);
    }

    @Override
    public void readNbt(NbtCompound nbt, RegistryWrapper.WrapperLookup registries) {
        super.readNbt(nbt, registries);
        transferProgress = nbt.getInt("transferProgress");
        Inventories.readNbt(nbt, getInventory().getItems(), registries);
    }

    @Override
    public BlockPosPayload getScreenOpeningData(ServerPlayerEntity player) {
        return new BlockPosPayload(this.pos);
    }

    @Override
    public NbtCompound toInitialChunkDataNbt(RegistryWrapper.WrapperLookup registries) {
        return createNbt(registries);
    }

    @Override
    public DefaultedList<ItemStack> getItems() {
        return getInventory().getItems();
    }

    @Override
    public int[] getAvailableSlots(Direction direction) {
        int[] result = new int[getItems().size()];
        for(int i = 0; i <result.length; i++) {
            result[i] = i;
        }
        return result;
    }

    @Override
    public boolean canInsert(int i, ItemStack itemStack, @Nullable Direction direction) {
        return true;
    }

    @Override
    public boolean canExtract(int i, ItemStack itemStack, Direction direction) {
        return true;
    }

    @Override
    public Text getDisplayName() {
        return Text.translatable("milkevsoreminers.container.mining_rig.basic.storage");
    }

    @Override
    public @Nullable ScreenHandler createMenu(int i, PlayerInventory playerInventory, PlayerEntity playerEntity) {
        return null;
    }
}
