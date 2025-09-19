package net.milkev.milkevsoreminers.common.blockEntities;

import net.milkev.milkevsoreminers.common.MilkevsOreMiners;
import net.milkev.milkevsoreminers.common.util.MilkevsAugmentedInventory;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.ItemEntity;
import net.minecraft.inventory.Inventories;
import net.minecraft.inventory.SidedInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.network.listener.ClientPlayPacketListener;
import net.minecraft.network.packet.Packet;
import net.minecraft.network.packet.s2c.play.BlockEntityUpdateS2CPacket;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.util.ActionResult;
import net.minecraft.util.collection.DefaultedList;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import org.jetbrains.annotations.Nullable;

public class SifterBlockEntity extends BlockEntity implements MilkevsAugmentedInventory, SidedInventory {

    private int progress = 0;
    //just holds item being processed, cannot be inserted/extracted from
    private final MilkevsAugmentedInventory inventory = MilkevsAugmentedInventory.ofSize(1);

    public SifterBlockEntity(BlockPos blockPos, BlockState blockState) {
        super(MilkevsOreMiners.SIFTER_BLOCK_ENTITY, blockPos, blockState);
    }

    public ActionResult interact(ItemStack itemStack) {
        System.out.print("Recipes; " + world.getRecipeManager().listAllOfType(MilkevsOreMiners.SIFTER_RECIPE_TYPE));
        //if(!world.isClient()) {
            if (inventory.isEmpty()) {
                //insert block into sifter
                if (blockAllowedToBeSifted(itemStack)) {
                    System.out.println("TRUE");
                    ItemStack input = itemStack.copy();
                    input.setCount(1);
                    inventory.setStack(0, input);
                    System.out.println(inventory.getItems());
                    itemStack.decrement(1);
                    System.out.println("decremented!");
                    setProgress(10);
                    return ActionResult.CONSUME;
                }
            } else if (progress > 1) {
                //advance progress of sifter
                setProgress(progress - 1);
            } else {
                //finish sifter process and expel drops
                ItemStack drop = getDrop();
                if (drop != null) {
                    dropItem(drop);
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
        //Optional<RecipeEntry<SifterRecipe>> matches = world.getRecipeManager().getFirstMatch(SifterRecipe.Type.INSTANCE, new MyRecipeInput.Single(itemStack), world);
        //return matches.isPresent();
        return false;
    }
    
    private void dropItem(ItemStack itemStack) {
        if(itemStack != null) {
            world.spawnEntity(new ItemEntity(world, pos.getX(), pos.getY() + 1, pos.getZ(), itemStack));
        }
    }

    private ItemStack getDrop() {
        ItemStack itemStack = inventory.getStack(0);

        //Optional<RecipeEntry<SifterRecipe>> match = world.getRecipeManager().getFirstMatch(SifterRecipe.Type.INSTANCE, new MyRecipeInput.Single(itemStack), world);
        /*
        if(match.isPresent()) {
            if(((float) Random.create().nextBetween(0, 100))/100 < match.get().value().getChance()) {
                return match.get().value().getOutput().copy();
            } else {
                return null;
            }
        } else {
            return null;
        }
         */
        return null;
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
