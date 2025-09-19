package net.milkev.milkevsoreminers.client;

import io.netty.buffer.Unpooled;
import net.fabricmc.api.ClientModInitializer;
import net.milkev.milkevsoreminers.common.recipes.SifterRecipe;
import net.milkev.milkevsoreminers.common.recipes.SifterRecipeSerializer;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.recipe.RecipeSerializer;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.util.Identifier;

public class MilkevsOreMinersClient implements ClientModInitializer {

    @Override
    public void onInitializeClient() {

        //this will be needed for shaders in the future
        
    }
}
