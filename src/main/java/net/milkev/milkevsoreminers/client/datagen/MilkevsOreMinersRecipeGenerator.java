package net.milkev.milkevsoreminers.client.datagen;

import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricRecipeProvider;
import net.minecraft.data.server.recipe.RecipeExporter;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.registry.RegistryWrapper;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

public class MilkevsOreMinersRecipeGenerator extends FabricRecipeProvider {
    
    public MilkevsOreMinersRecipeGenerator(FabricDataOutput output, CompletableFuture<RegistryWrapper.WrapperLookup> registriesFuture) {
        super(output, registriesFuture);
    }

    @Override
    public void generate(RecipeExporter exporter) {
        
        Map<ItemStack, Integer> output = new HashMap<>();
        output.put(Items.RAW_COPPER.getDefaultStack(), 1);
        output.put(Items.RAW_IRON.getDefaultStack(), 1);
        SifterRecipeJsonBuilder.create(Items.SAND.getDefaultStack(), 100, output).offerTo(exporter);
        System.out.println("5");
    }
}
