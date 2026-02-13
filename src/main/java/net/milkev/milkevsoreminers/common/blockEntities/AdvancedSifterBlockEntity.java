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
import net.minecraft.registry.Registries;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.Identifier;
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


    public MilkevsAugmentedEnergyStorage energyStorage = new MilkevsAugmentedEnergyStorage(10000, 1, true, false) {
        @Override
        protected void onFinalCommit() {
            markDirty();
        }
        @Override
        public boolean supportsExtraction() {
            return false;
        }
    };

    //base speed is set from recipe
    long powerUsageSpeed = 0;
    //tracks how much power has been used in the current operation (counts down from the energy cost of the recipe)
    long progress = 0;
    //tracks the total cost of the recipe being processed for use in the gui
    long powerCost = 0;
    //slot 0 is the only slot that can be inserted to, and cant be extracted from, as this is the input slot. the other 12 slots are the output slots, which cannot be inserted to, but can be extracted from
    private final MilkevsAugmentedInventory inventory = MilkevsAugmentedInventory.of(DefaultedList.ofSize(13, ItemStack.EMPTY));
    //cache match getter as it improves performance by ~30%
    RecipeManager.MatchGetter<MilkevsSingleRecipeInput.Single, AdvancedSifterRecipe> cacheMatchGetter;
    //didnt test performance improvements here but since its an expensive function to build the output, its probably a pretty good improvement as well
    Map<Item, List<Item>> cacheOutputStackList = new HashMap<>();
    //if this is EMPTY then there is no recipe in progress
    Item activeRecipe = ItemStack.EMPTY.getItem();

    public AdvancedSifterBlockEntity(BlockPos blockPos, BlockState blockState) {
        super(MilkevsOreMiners.ADVANCED_SIFTER_BLOCK_ENTITY, blockPos, blockState);
        cacheMatchGetter = RecipeManager.createCachedMatchGetter(MilkevsOreMiners.ADVANCED_SIFTER_RECIPE_TYPE);
        energyStorage.setCapacity(MilkevsOreMiners.PowerCapacity.get("sifter"));
    }

    @Override
    public void tick(World world, BlockPos blockPos, BlockState blockState, AdvancedSifterBlockEntity blockEntity) {
        if(!getWorld().isClient) {
            if (!inventory.getStack(0).isEmpty()) {
                Optional<RecipeEntry<AdvancedSifterRecipe>> match = cacheMatchGetter.getFirstMatch(new MilkevsSingleRecipeInput.Single(inventory.getStack(0)), world);
                if (match.isPresent()) {
                    //System.out.println("Match is present!");
                    AdvancedSifterRecipe recipe = match.get().value();
                    if (activeRecipe == ItemStack.EMPTY.getItem()) {
                        //if no recipe in progress, set power usage to the energy cost of the recipe
                        progress = recipe.powerCost();
                        powerCost = progress;
                        activeRecipe = recipe.input().getItem();
                        powerUsageSpeed = recipe.basePowerConsumption();
                        markDirty();
                        world.updateListeners(blockPos, blockState, blockState, Block.NOTIFY_ALL);
                    }
                    if (energyStorage.hasEnoughEnergy(progress)) {
                        //System.out.println("Enough Energy!");
                        if(activeRecipe != recipe.input().getItem()) {
                            //if input item has changed, reset recipe progress and active recipe
                            activeRecipe = ItemStack.EMPTY.getItem();
                        } else {
                            long use = getPowerUsageSpeed();
                            if(energyStorage.useExactly(use)) {
                                progress -= use;
                            }
                            if (progress <= 0) {
                                if (!cacheOutputStackList.containsKey(recipe.input().getItem())) {
                                    cacheOutputStackList.put(recipe.input().getItem(), RecipeUtils.generateItemList(recipe.output(), world));
                                }
                                RecipeUtils.handleDrops(cacheOutputStackList.get(recipe.input().getItem()), recipe.rolls(), recipe.chance())
                                        .iterator().forEachRemaining(stack -> inventory.addItemStack(stack, 1));
                                inventory.removeStack(0, 1);
                                if(inventory.getStack(0).getItem() != activeRecipe) {
                                    activeRecipe = ItemStack.EMPTY.getItem();
                                    powerCost = 0;
                                }
                            }
                            markDirty();
                            world.updateListeners(blockPos, blockState, blockState, Block.NOTIFY_ALL);
                        }
                    }
                }
            }
        }
    }

    //remove for release
    //just in for testing purposes
    public ActionResult interact(BlockState blockState, World world, BlockPos blockPos, PlayerEntity playerEntity, Hand hand, BlockHitResult blockHitResult) {
        energyStorage.setAmount(1000 + energyStorage.getAmount());
        markDirty();
        world.updateListeners(blockPos, blockState, blockState, Block.NOTIFY_ALL);
        return ActionResult.CONSUME;
    }

    //would also contain logic for upgrades if i decide to add them for the sifter
    //for now just makes sure recipes dont use more power than specified
    public long getPowerUsageSpeed() {
        if(progress < powerUsageSpeed) {
            return progress;
        }
        return powerUsageSpeed;
    }
    
    public float getProgress() {
        return this.progress <= 0 ? 0 : (float) (this.powerCost - this.progress)/ (float) this.powerCost;
    }

    @Override
    public void writeNbt(NbtCompound nbt, RegistryWrapper.WrapperLookup registries) {

        nbt.putLong("progress", progress);
        nbt.putLong("cost", powerCost);
        nbt.putLong("energy", energyStorage.getAmount());
        nbt.putString("activerecipe", activeRecipe.toString());
        Inventories.writeNbt(nbt, inventory.getItems(), registries);

        super.writeNbt(nbt, registries);
    }

    @Override
    public void readNbt(NbtCompound nbt, RegistryWrapper.WrapperLookup registries) {
        super.readNbt(nbt, registries);

                                           progress = nbt.getInt("progress");
                                          powerCost = nbt.getInt("cost");
                             energyStorage.setAmount(nbt.getLong("energy"));
        activeRecipe = Registries.ITEM.get(Identifier.of(nbt.getString("activerecipe")));
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

    private static final Text displayName = MilkevsOreMiners.makeTranslation("container.advanced_sifter");
    
    @Override
    public Text getDisplayName() {
        return displayName;
    }

    @Override
    public @Nullable ScreenHandler createMenu(int syncId, PlayerInventory playerInventory, PlayerEntity playerEntity) {
        return new AdvancedSifterScreenHandler(syncId, playerInventory, this);
    }
}
