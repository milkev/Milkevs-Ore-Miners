package net.milkev.milkevsoreminers.common.recipes;

import net.minecraft.item.ItemStack;

public class MilkevsSingleRecipeInput {

    public Single of(ItemStack itemStack) {
        return new Single(itemStack);
    }

    public record Single(ItemStack stack) implements net.minecraft.recipe.input.RecipeInput {

        public ItemStack get() {
            return stack;
        }
        
        @Override
        public ItemStack getStackInSlot(int i) {
            return stack;
        }

        @Override
        public int getSize() {
            return 1;
        }
    }
}
