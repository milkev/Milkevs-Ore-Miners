package net.milkev.milkevsoreminers.common.recipes.advancedSifter;

import com.mojang.serialization.MapCodec;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;

public class AdvancedSifterRecipeSerializer implements net.minecraft.recipe.RecipeSerializer<AdvancedSifterRecipe> {


    @Override
    public MapCodec<AdvancedSifterRecipe> codec() {
        return null;
    }

    @Override
    public PacketCodec<RegistryByteBuf, AdvancedSifterRecipe> packetCodec() {
        return null;
    }
}
