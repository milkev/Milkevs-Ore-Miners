package net.milkev.milkevsoreminers.common.blockEntities;

import net.fabricmc.fabric.api.screenhandler.v1.ExtendedScreenHandlerFactory;
import net.milkev.milkevsoreminers.common.MilkevsOreMiners;
import net.milkev.milkevsoreminers.common.gui.AdvancedSifterScreenHandler;
import net.milkev.milkevsoreminers.common.recipes.AdvancedSifterRecipe;
import net.milkev.milkevsoreminers.common.recipes.RecipeUtils;
import net.milkev.milkevsoreminers.common.recipes.MilkevsSingleRecipeInput;
import net.milkev.milkevsoreminers.common.util.BlockPosPayload;
import net.milkev.milkevsoreminers.common.util.MilkevsAugmentedEnergyStorage;
import net.milkev.milkevsoreminers.common.util.MilkevsAugmentedInventory;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityTicker;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.Inventories;
import net.minecraft.inventory.Inventory;
import net.minecraft.inventory.SidedInventory;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.network.listener.ClientPlayPacketListener;
import net.minecraft.network.packet.Packet;
import net.minecraft.network.packet.s2c.play.BlockEntityUpdateS2CPacket;
import net.minecraft.recipe.RecipeEntry;
import net.minecraft.recipe.RecipeManager;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.collection.DefaultedList;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public class AdvancedSifterBlockEntity extends BlockEntity implements BlockEntityTicker<AdvancedSifterBlockEntity>, MilkevsAugmentedInventory, SidedInventory, ExtendedScreenHandlerFactory<BlockPosPayload> {


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
    //slot 0 is the only slot that can be inserted to, and cant be extracted from, as this is the input slot. the other 12 slots are the output slots, which cannot be inserted to, but can be extracted from
    private final MilkevsAugmentedInventory inventory = MilkevsAugmentedInventory.of(DefaultedList.ofSize(13, ItemStack.EMPTY));
    //cache match getter as it improves performance by ~30%
    RecipeManager.MatchGetter<MilkevsSingleRecipeInput.Single, AdvancedSifterRecipe> cacheMatchGetter;
    Map<Item, List<Item>> cacheOutputStackList = new HashMap<>();

    public AdvancedSifterBlockEntity(BlockPos blockPos, BlockState blockState) {
        super(MilkevsOreMiners.ADVANCED_SIFTER_BLOCK_ENTITY, blockPos, blockState);
        cacheMatchGetter = RecipeManager.createCachedMatchGetter(MilkevsOreMiners.ADVANCED_SIFTER_RECIPE_TYPE);
    }

    @Override
    public void tick(World world, BlockPos blockPos, BlockState blockState, AdvancedSifterBlockEntity blockEntity) {
        if(!getWorld().isClient) {
            if (!inventory.getStack(0).isEmpty()) {
                Optional<RecipeEntry<AdvancedSifterRecipe>> match = cacheMatchGetter.getFirstMatch(new MilkevsSingleRecipeInput.Single(inventory.getStack(0)), world);
                if (match.isPresent()) {
                    //System.out.println("Match is present!");
                    AdvancedSifterRecipe recipe = match.get().value();
                    if (powerUsage == -666) {
                        //if no recipe in progress, set power usage to the energy cost of the recipe
                        powerUsage = recipe.basePowerCost();
                        powerCost = powerUsage;
                        markDirty();
                        world.updateListeners(blockPos, blockState, blockState, Block.NOTIFY_ALL);
                    }
                    if (energyStorage.hasEnoughEnergy(powerUsage) && !inventory.getStack(0).isEmpty()) {
                        int use = getPowerUsageSpeed();
                        energyStorage.consumeEnergy(use);
                        powerUsage -= use;
                        if (powerUsage <= 0) {
                            //System.out.println("Finishing a recipe");
                            if (!cacheOutputStackList.containsKey(recipe.input())) {
                                cacheOutputStackList.put(recipe.input().getItem(), RecipeUtils.generateItemList(recipe.output(), world));
                            }
                            RecipeUtils.handleDrops(cacheOutputStackList.get(recipe.input().getItem()), recipe.rolls(), recipe.chance()).iterator().forEachRemaining(inventory::addStack);
                            inventory.removeStack(0, 1);
                            //once recipe is finished, set power usage to -666 to indicate the machine is ready to start another process
                            powerUsage = -666;
                            powerCost = 0;
                        }
                        markDirty();
                        world.updateListeners(blockPos, blockState, blockState, Block.NOTIFY_ALL);
                    }
                }
            }
        }
    }

    //remove for release
    //just in for testing purposes
    public ActionResult interact(BlockState blockState, World world, BlockPos blockPos, PlayerEntity playerEntity, Hand hand, BlockHitResult blockHitResult) {
        energyStorage.amount += 500;
                        markDirty();
                        world.updateListeners(blockPos, blockState, blockState, Block.NOTIFY_ALL);
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
    
    public float getProgress() {
        return this.powerUsage <= 0 ? 0 : (float) (this.powerCost - this.powerUsage)/ (float) this.powerCost;
    }

    @Override
    public void writeNbt(NbtCompound nbt, RegistryWrapper.WrapperLookup registries) {

        nbt.putInt("progress", powerUsage);
        nbt.putInt("cost", powerCost);
        nbt.putLong("energy", energyStorage.amount);
        Inventories.writeNbt(nbt, inventory.getItems(), registries);

        super.writeNbt(nbt, registries);
    }

    @Override
    public void readNbt(NbtCompound nbt, RegistryWrapper.WrapperLookup registries) {
        super.readNbt(nbt, registries);

        powerUsage = nbt.getInt("progress");
        powerCost = nbt.getInt("cost");
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
        return createNbt(registries);
    }

    @Override
    public DefaultedList<ItemStack> getItems() {
        return inventory.getItems();
    }
    
    public Inventory getInventory() {
        return inventory;
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
        return cacheMatchGetter.getFirstMatch(new MilkevsSingleRecipeInput.Single(itemStack), world).isPresent();
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

    @Override
    public BlockPosPayload getScreenOpeningData(ServerPlayerEntity player) {
        return new BlockPosPayload(this.pos);
    }

    private static final Text displayName = Text.translatable(MilkevsOreMiners.makeTranslation("container.advanced_sifter"));
    
    @Override
    public Text getDisplayName() {
        return displayName;
    }

    @Override
    public @Nullable ScreenHandler createMenu(int syncId, PlayerInventory playerInventory, PlayerEntity playerEntity) {
        return new AdvancedSifterScreenHandler(syncId, playerInventory, this);
    }
}
