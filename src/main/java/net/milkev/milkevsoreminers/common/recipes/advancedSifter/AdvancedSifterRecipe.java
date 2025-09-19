package net.milkev.milkevsoreminers.common.recipes.advancedSifter;

import net.minecraft.inventory.SimpleInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.recipe.Recipe;
import net.minecraft.recipe.RecipeSerializer;
import net.minecraft.recipe.RecipeType;
import net.minecraft.recipe.input.RecipeInput;
import net.minecraft.registry.DynamicRegistryManager;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.random.Random;
import net.minecraft.world.World;

public class AdvancedSifterRecipe implements Recipe<RecipeInput> {

    private final ItemStack input;
    private final int powerCost;
    private final ItemStack[] output;
    private final int[] weight;
    private final Identifier ID;

    public AdvancedSifterRecipe(Identifier id, ItemStack[] output, int[] weight , ItemStack input, int powerCost) {
        this.input = input;
        this.output = output;
        this.weight = weight;
        this.ID = id;
        this.powerCost = powerCost;
    }

    public ItemStack getInput() {
        return this.input;
    }

    public ItemStack getOutput() {

        //creates an array of outputs that is the length of the total weight of all outputs
        //each output will fill up an amount of slots in the array equal to the amount of weight it has
        ItemStack[] outputs = new ItemStack[totalWeight()];

        int j = -1; //tracks which output we are on
        int k = 0; //tracks how much weight has been used
        for(int i = 0; i < outputs.length; i++) {
            while(k == 0) {
                //everytime we reach 0 weight remaining for the current output,
                //move onto the next output and set the weight tracker to the weight of the next output
                //using while loop to skip all entries that may have a weight of 0
                //entries may have a weight of 0 either from the recipe.json itself, or that the output item id did not link to a valid item.
                j++;
                k = getWeight(j);
            }
            outputs[i] = this.getOutput(j);
            k--;
        }

        return outputs[Random.create().nextBetween(0, outputs.length-1)];
    }

    public ItemStack getOutput(int i) {
        return this.output[i];
    }

    public int getNumofOutputs() {
        return this.output.length;
    }

    public int getWeight(int i) {
        return this.weight[i];
    }

    public int totalWeight() {
        int total = 0;
        for(int i = 0; i < this.output.length; i++) {
            total+= this.weight[i];
        }
        return total;
    }

    public int getPowerCost() {
        return this.powerCost;
    }

    @Override
    public RecipeSerializer<?> getSerializer() {
        return null;
    }

    public Identifier getId() {
        return this.ID;
    }

    @Override
    public boolean matches(RecipeInput recipeInput, World world) {
        return recipeInput.getStackInSlot(0).isOf(input.getItem());
    }

    @Override
    public ItemStack craft(RecipeInput recipeInput, RegistryWrapper.WrapperLookup wrapperLookup) {
        return getOutput();
    }

    @Override
    public boolean fits(int i, int j) {
        return true;
    }

    @Override
    public ItemStack getResult(RegistryWrapper.WrapperLookup wrapperLookup) {
        return null;
    }

    public static class Type implements RecipeType<AdvancedSifterRecipe> {
        private Type() {}
        public static final Type INSTANCE = new Type();
        public static final String ID = "advancedsifter";
    }
    @Override
    public RecipeType<?> getType() {
        return Type.INSTANCE;
    }
}
