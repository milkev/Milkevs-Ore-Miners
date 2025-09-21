package net.milkev.milkevsoreminers.common.blockEntities;

import net.milkev.milkevsoreminers.common.MilkevsOreMiners;
import net.milkev.milkevsoreminers.common.recipes.AdvancedSifterRecipe;
import net.milkev.milkevsoreminers.common.recipes.RecipeUtils;
import net.milkev.milkevsoreminers.common.recipes.MilkevsSingleRecipeInput;
import net.milkev.milkevsoreminers.common.util.MilkevsAugmentedEnergyStorage;
import net.milkev.milkevsoreminers.common.util.MilkevsAugmentedInventory;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityTicker;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.Inventories;
import net.minecraft.inventory.SidedInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.network.listener.ClientPlayPacketListener;
import net.minecraft.network.packet.Packet;
import net.minecraft.network.packet.s2c.play.BlockEntityUpdateS2CPacket;
import net.minecraft.recipe.RecipeEntry;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.collection.DefaultedList;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

import java.util.Optional;

public class AdvancedSifterBlockEntity extends BlockEntity implements BlockEntityTicker<AdvancedSifterBlockEntity>, MilkevsAugmentedInventory, SidedInventory {


    public MilkevsAugmentedEnergyStorage energyStorage = new MilkevsAugmentedEnergyStorage(50000, 50000, 0) {
        @Override
        protected void onFinalCommit() {
            markDirty();
        }
        @Override
        public boolean supportsExtraction() {
            return false;
        }
    };

    //base speed is set from config *eventually*
    int powerUsageSpeed = 5;
    //tracks how much power has been used in the current operation (counts down from the energy cost of the recipe)
    int powerUsage = -666;
    //tracks the total cost of the recipe being processed for use in the gui
    int powerCost = 0;
    private final DefaultedList<ItemStack> inventoryList = DefaultedList.ofSize(10, ItemStack.EMPTY);
    private final MilkevsAugmentedInventory inventory = MilkevsAugmentedInventory.of(inventoryList);

    public AdvancedSifterBlockEntity(BlockPos blockPos, BlockState blockState) {
        super(null, blockPos, blockState);
    }

    @Override
    public void tick(World world, BlockPos blockPos, BlockState blockState, AdvancedSifterBlockEntity blockEntity) {
        if(!inventory.getStack(0).isEmpty()) {
            Optional<RecipeEntry<AdvancedSifterRecipe>> match = world.getRecipeManager().getFirstMatch(MilkevsOreMiners.ADVANCED_SIFTER_RECIPE_TYPE, new MilkevsSingleRecipeInput.Single(inventory.getStack(0)), world);
            if (match.isPresent()) {
                //System.out.println("Match is present!");
                AdvancedSifterRecipe recipe = match.get().value();                 
                inventory.addStack(RecipeUtils.handleDrop(recipe.output(), world));
                if (powerUsage == -666) {
                    //if no recipe in progress, set power usage to the energy cost of the recipe
                    powerUsage = recipe.basePowerCost();
                    powerCost = powerUsage;
                }
                if (energyStorage.hasEnoughEnergy(powerUsage) && !inventory.getStack(0).isEmpty()) {
                    energyStorage.consumeEnergy(getPowerUsageSpeed());
                    powerUsage -= getPowerUsageSpeed();
                    if (powerUsage <= 0) {
                        //System.out.println("Finishing a recipe");
                        inventory.addStack(RecipeUtils.handleDrop(recipe.output(), world));
                        inventory.removeStack(0, 1);
                        //once recipe is finished, set power usage to -666 to indicate the machine is ready to start another process
                        powerUsage = -666;
                    }
                    markDirty();
                }
            }
        }
    }

    public ActionResult interact(BlockState blockState, World world, BlockPos blockPos, PlayerEntity playerEntity, Hand hand, BlockHitResult blockHitResult) {
        //Open GUI
        //GUI needs; Power bar, Input slot, Progress bar, Output slots
        energyStorage.amount += 500;
        return ActionResult.FAIL;
    }

    //would also contain logic for upgrades if i decide to add them for the sifter
    //for now just makes sure recipes dont use more power than specified
    public int getPowerUsageSpeed() {
        if(powerUsage < powerUsageSpeed) {
            return powerUsage;
        }
        return powerUsageSpeed;
    }

    @Override
    public void writeNbt(NbtCompound nbt, RegistryWrapper.WrapperLookup registries) {

        nbt.putInt("progress", powerUsage);
        nbt.putLong("energy", energyStorage.amount);
        Inventories.writeNbt(nbt, inventory.getItems(), registries);

        super.writeNbt(nbt, registries);
    }

    @Override
    public void readNbt(NbtCompound nbt, RegistryWrapper.WrapperLookup registries) {
        super.readNbt(nbt, registries);

        powerUsage = nbt.getInt("progress");
        energyStorage.amount = nbt.getLong("energy");
        Inventories.readNbt(nbt, inventory.getItems(), registries);

    }

    @Nullable
    @Override
    public Packet<ClientPlayPacketListener> toUpdatePacket() {
        return BlockEntityUpdateS2CPacket.create(this);
    }

    @Override
    public NbtCompound toInitialChunkDataNbt(RegistryWrapper.WrapperLookup registries) {
        return createNbt( registries);
    }

    @Override
    public DefaultedList<ItemStack> getItems() {
        return inventoryList;
    }

    @Override
    public int[] getAvailableSlots(Direction direction) {
        int[] result = new int[getItems().size()];
        for(int i = 0; i <result.length; i++) {
            result[i] = i;
        }
        return result;
    }

    public boolean blockAllowedToBeSifted(ItemStack itemStack) {
        assert world != null;
        //return world.getRecipeManager().getFirstMatch(SifterRecipe.Type.INSTANCE, new MyRecipeInput.Single(itemStack), world).isPresent();
        return false;
    }

    @Override
    public boolean canInsert(int i, ItemStack itemStack, @Nullable Direction direction) {
        return i==0 &&
                blockAllowedToBeSifted(itemStack);
    }

    @Override
    public boolean canExtract(int i, ItemStack itemStack, Direction direction) {
        return i>0;
    }
}
