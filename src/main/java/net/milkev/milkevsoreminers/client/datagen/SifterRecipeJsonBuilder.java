package net.milkev.milkevsoreminers.client.datagen;

import net.fabricmc.fabric.api.datagen.v1.provider.FabricRecipeProvider;
import net.milkev.milkevsoreminers.common.MilkevsOreMiners;
import net.milkev.milkevsoreminers.common.recipes.SifterRecipe;
import net.minecraft.advancement.*;
import net.minecraft.advancement.criterion.Criteria;
import net.minecraft.advancement.criterion.CriterionConditions;
import net.minecraft.advancement.criterion.RecipeUnlockedCriterion;
import net.minecraft.data.server.recipe.RecipeExporter;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Identifier;

import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Objects;

public class SifterRecipeJsonBuilder {
    
    ItemStack input;
    int chance;
    Map<ItemStack, Integer> output;
    private final Map<String, AdvancementCriterion<?>> criteria = new LinkedHashMap<>();

    SifterRecipeJsonBuilder(ItemStack input, int chance, Map<ItemStack, Integer> output) {
        this.input = input;
        this.chance = chance;
        this.output = output;
    }
    
    public static SifterRecipeJsonBuilder create(ItemStack input, int chance, Map<ItemStack, Integer> output) {
        return new SifterRecipeJsonBuilder(input, chance, output);
    }
    
    public void offerTo(RecipeExporter recipeExporter) {
        this.validate();
        System.out.println("1");
        Identifier identifier = Identifier.of(MilkevsOreMiners.MOD_ID, input.getItem().getRegistryEntry().registryKey().getValue().toString().replace(':', '_'));
        System.out.println("2");
        SifterRecipe sifterRecipe = new SifterRecipe(input, chance, Collections.singletonList(output.keySet().iterator().next().getItem().getRegistryEntry().registryKey().getValue().toString()));
        System.out.println("3");
        System.out.println("recipe: "+ sifterRecipe);
        System.out.println("id: " + identifier);
        System.out.println("advancement: " + Advancement.Builder.create().build(identifier));
        recipeExporter.accept(identifier, sifterRecipe, Advancement.Builder.create().build(identifier));
        System.out.println("4");
    }
    
    private void validate() {
        if (this.input.isEmpty()) {
            throw new IllegalStateException("No way of obtaining recipe");
        }
    }
}
