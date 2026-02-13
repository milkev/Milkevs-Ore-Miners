package net.milkev.milkevsoreminers.common.util;

import net.fabricmc.fabric.api.transfer.v1.item.ItemVariant;
import net.fabricmc.fabric.api.transfer.v1.storage.Storage;
import net.fabricmc.fabric.api.transfer.v1.transaction.Transaction;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.Inventories;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.ItemStack;
import net.minecraft.util.collection.DefaultedList;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

public interface MilkevsAugmentedInventory extends Inventory {
 
    /**
     * Retrieves the item list of this inventory.
     * Must return the same instance every time it's called.
     */
    DefaultedList<ItemStack> getItems();

    /**
     * Creates an inventory from the item list.
     */
    static MilkevsAugmentedInventory of(DefaultedList<ItemStack> items) {
        return () -> items;
    }

    /**
     * Creates a new inventory with the specified size.
     */
    static MilkevsAugmentedInventory ofSize(int size) {
        return of(DefaultedList.ofSize(size, ItemStack.EMPTY));
    }
    
    /**
     * Returns the inventory size.
     */
    @Override
    default int size() {
        return getItems().size();
    }

    /**
     * Checks if the inventory is empty.
     * @return true if this inventory has only empty stacks, false otherwise.
     */
    @Override
    default boolean isEmpty() {
        for (int i = 0; i < size(); i++) {
            ItemStack stack = getStack(i);
            if (!stack.isEmpty()) {
                return false;
            }
        }
        return true;
    }

    /**
     * Retrieves the item in the slot.
     */
    @Override
    default ItemStack getStack(int slot) {
        return getItems().get(slot);
    }

    /**
     * Removes items from an inventory slot.
     * @param slot  The slot to remove from.
     * @param count How many items to remove. If there are less items in the slot than what are requested,
     *              takes all items in that slot.
     */
    @Override
    default ItemStack removeStack(int slot, int count) {
        ItemStack result = Inventories.splitStack(getItems(), slot, count);
        if (!result.isEmpty()) {
            markDirty();
        }
        return result;
    }

    /**
     * Removes all items from an inventory slot.
     * @param slot The slot to remove from.
     */
    @Override
    default ItemStack removeStack(int slot) {
        return Inventories.removeStack(getItems(), slot);
    }

    /**
     * Attempts to add the requested stack into the inventory, starting at the specified slot (intended for use with machine processing)
     * Returns the remainder of the stack
     **/
    default ItemStack addItemStack(ItemStack stack, int slotStart) {
        System.out.println("addItemStack debug | stack: " + stack + " slotStart: " + slotStart + " size: " + size() + " currentInventory: " + getItems());
        for(int i = slotStart; i < size(); i++) {
            System.out.println("Adding stack " + stack);
            if(getStack(i).isEmpty()) {
                //System.out.println("Set a stack (" + stack + ") in slot " + i);
                this.setStack(i, stack);
                stack = ItemStack.EMPTY;
                return stack;
            } else if(getStack(i).getCount() < getStack(i).getMaxCount() && getStack(i).isOf(stack.getItem())) {
                int moved = getStack(i).getCount() + stack.getCount() < getStack(i).getMaxCount() ? stack.getCount() : getStack(i).getCount() + stack.getCount() - stack.getMaxCount();
                System.out.println("moved: " + moved + " ourStack: " + getStack(i) + " theirStack: " + stack);
                this.getStack(i).increment(moved);
                stack.decrement(moved);
                if(stack.isEmpty()) {
                    return stack;
                }
            }
        }
        return stack;
    }
    /**
     * Same as above but add to any slot
     */
    default ItemStack addItemStack(ItemStack stack) {
        return addItemStack(stack, 0);
    }

    /**
     * Add a list of items to inventory. Returns list of items that were not added
     */
    default List<ItemStack> addItems(List<ItemStack> listStack) {
        List<ItemStack> remainder = new ArrayList<>();
        for(int i = 0; i < listStack.size(); i++) {
            System.out.println("Calling addItemStack with " + listStack.get(i));
            ItemStack stack = addItemStack(listStack.get(i));
            if(!stack.isEmpty()) {
                remainder.add(stack);
            }
        }
        return remainder;
    }

    /**
     * Replaces the current stack in an inventory slot with the provided stack.
     * @param slot  The inventory slot of which to replace the itemstack.
     * @param stack The replacing itemstack. If the stack is too big for
     *              this inventory ({@link Inventory#getMaxCountPerStack()}),
     *              it gets resized to this inventory's maximum amount.
     */
    @Override
    default void setStack(int slot, ItemStack stack) {
        getItems().set(slot, stack);
        if (stack.getCount() > stack.getMaxCount()) {
            stack.setCount(stack.getMaxCount());
        }
    }

    /**
     * Clears the inventory.
     */
    @Override
    default void clear() {
        getItems().clear();
    }

    /**
     * Marks the state as dirty.
     * Must be called after changes in the inventory, so that the game can properly save
     * the inventory contents and notify neighboring blocks of inventory changes.
     */
    @Override
    default void markDirty() {
    }

    /**
     * @return true if the player can use the inventory, false otherwise.
     */
    @Override
    default boolean canPlayerUse(PlayerEntity player) {
        return true;
    }

    default int getMaxCountPerStack() {
        return 64;
    }
    
    //return null if no items found
    //return slot number if found
    @Nullable
    default Integer getFirstFilledSlot() {
        if(isEmpty()) { return null; }
        for(int i = 0; i < this.size(); i++) {
            if(!getStack(i).isEmpty()) {
                return i;
            }
        }
        return null;
    }
    
    default long exportAny(Storage<ItemVariant> target, int amount, Transaction transaction) {
        int stacks = (int) Math.ceil((double) amount /64);
        long total = 0;
        for(int i = 0; i < stacks; i++) {
            Integer slot = getFirstFilledSlot();
            if(slot != null) {
                long moved = target.insert(ItemVariant.of(getStack(slot)), amount, transaction);
                this.removeStack(slot, (int) moved);
                total += moved;
            } else {
                break;
            }
        }
        return total;
    }
}
