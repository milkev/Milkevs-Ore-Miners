package net.milkev.milkevsoreminers.common.recipes;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.milkev.milkevsoreminers.common.MilkevsOreMiners;
import net.minecraft.item.ItemStack;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.codec.PacketCodecs;
import net.minecraft.recipe.Recipe;
import net.minecraft.recipe.RecipeSerializer;
import net.minecraft.recipe.RecipeType;
import net.minecraft.registry.Registries;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.util.Identifier;
import net.minecraft.world.World;

import java.util.List;

public record SifterRecipe(ItemStack input, float chance, List<String> output) implements Recipe<MilkevsSingleRecipeInput.Single> {

    @Override
    public boolean matches(MilkevsSingleRecipeInput.Single recipeInput, World world) {
        return input().getItem().equals(recipeInput.get().getItem());
    }

    @Override
    public ItemStack craft(MilkevsSingleRecipeInput.Single recipeInput, RegistryWrapper.WrapperLookup wrapperLookup) {
        return Registries.ITEM.get(Identifier.of(output.get(0))).getDefaultStack();
    }

    @Override
    public boolean fits(int i, int j) {
        return false;
    }

    @Override
    public ItemStack getResult(RegistryWrapper.WrapperLookup wrapperLookup) {
        return null;
    }

    @Override
    public RecipeSerializer<?> getSerializer() {
        return MilkevsOreMiners.SIFTER_RECIPE_SERIALIZER;
    }

    @Override
    public RecipeType<?> getType() {
        return MilkevsOreMiners.SIFTER_RECIPE_TYPE;
    }
    
    
    
}
