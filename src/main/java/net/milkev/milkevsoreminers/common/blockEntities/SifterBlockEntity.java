package net.milkev.milkevsoreminers.common.blockEntities;

import net.milkev.milkevsoreminers.common.MilkevsOreMiners;
import net.milkev.milkevsoreminers.common.recipes.RecipeUtils;
import net.milkev.milkevsoreminers.common.recipes.MilkevsSingleRecipeInput;
import net.milkev.milkevsoreminers.common.recipes.SifterRecipe;
import net.milkev.milkevsoreminers.common.util.MilkevsAugmentedInventory;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.ItemEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.Inventories;
import net.minecraft.inventory.SidedInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.network.listener.ClientPlayPacketListener;
import net.minecraft.network.packet.Packet;
import net.minecraft.network.packet.s2c.play.BlockEntityUpdateS2CPacket;
import net.minecraft.recipe.RecipeEntry;
import net.minecraft.recipe.RecipeManager;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.util.ActionResult;
import net.minecraft.util.collection.DefaultedList;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.random.Random;
import org.jetbrains.annotations.Nullable;

import java.util.Optional;

public class SifterBlockEntity extends BlockEntity implements MilkevsAugmentedInventory, SidedInventory {

    private int progress = 0;
    //just holds item being processed, cannot be inserted/extracted from
    private final MilkevsAugmentedInventory inventory = MilkevsAugmentedInventory.ofSize(1);
    //cache match getter as it improves performance by ~30%
    RecipeManager.MatchGetter<MilkevsSingleRecipeInput.Single, SifterRecipe> cacheMatchGetter;

    public SifterBlockEntity(BlockPos blockPos, BlockState blockState) {
        super(MilkevsOreMiners.SIFTER_BLOCK_ENTITY, blockPos, blockState);
        cacheMatchGetter = RecipeManager.createCachedMatchGetter(MilkevsOreMiners.SIFTER_RECIPE_TYPE);
    }

    public ActionResult interact(ItemStack itemStack, PlayerEntity playerEntity) {
        //if(!world.isClient()) {
            if (inventory.isEmpty()) {
                //insert block into sifter
                if (blockAllowedToBeSifted(itemStack)) {
                    ItemStack input = itemStack.copy();
                    input.setCount(1);
                    inventory.setStack(0, input);
                    if(!playerEntity.isCreative()) {itemStack.decrement(1);}
                    setProgress(1);
                    return ActionResult.CONSUME;
                }
            } else if (progress > 1) {
                //advance progress of sifter
                setProgress(progress - 1);
            } else {
                //finish sifter process and expel drops
                ItemStack drop = getDrop();
                if (drop != null) {
                    Block.dropStack(world, this.pos.add(0, 1, 0), drop);
                }
                inventory.clear();
                setProgress(0);
            }
            return ActionResult.SUCCESS;
        //}
        //return ActionResult.PASS;
    }

    private boolean blockAllowedToBeSifted(ItemStack itemStack) {
        assert world != null;
        Optional<RecipeEntry<SifterRecipe>> matches = cacheMatchGetter.getFirstMatch(new MilkevsSingleRecipeInput.Single(itemStack), world);
        return matches.isPresent();
    }

    private ItemStack getDrop() {
        ItemStack itemStack = inventory.getStack(0);

        Optional<RecipeEntry<SifterRecipe>> matches = cacheMatchGetter.getFirstMatch(new MilkevsSingleRecipeInput.Single(itemStack), world);
        
        if(matches.isPresent()) {
            if(((float) Random.create().nextBetween(0, 100))/100 < matches.get().value().chance() || matches.get().value().chance() == 1) {
                
                return RecipeUtils.handleDrop(matches.get().value().output(), world);
            } else {
                return null;
            }
        } else {
            return null;
        }
    }

    public void setProgress(int i) {
        progress = i;
        markDirty();
    }

    @Override
    public void writeNbt(NbtCompound nbt, RegistryWrapper.WrapperLookup registries) {

        nbt.putInt("progress", progress);
        Inventories.writeNbt(nbt, inventory.getItems(), registries);

        super.writeNbt(nbt, registries);
    }

    @Override
    public void readNbt(NbtCompound nbt, RegistryWrapper.WrapperLookup registries) {
        super.readNbt(nbt, registries);

        progress = nbt.getInt("progress");
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
        return false;
    }

    @Override
    public boolean canExtract(int i, ItemStack itemStack, Direction direction) {
        return false;
    }
}
