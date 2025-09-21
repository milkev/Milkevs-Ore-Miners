package net.milkev.milkevsoreminers.common.recipes;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.milkev.milkevsoreminers.common.MilkevsOreMiners;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.codec.PacketCodecs;
import net.minecraft.recipe.Recipe;
import net.minecraft.recipe.RecipeSerializer;
import net.minecraft.recipe.RecipeType;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.world.World;

import java.util.List;

public record SifterRecipe(ItemStack input, float chance, float rolls, List<String> output) implements Recipe<MilkevsSingleRecipeInput.Single> {

    @Override
    public boolean matches(MilkevsSingleRecipeInput.Single recipeInput, World world) {
        return input().getItem().equals(recipeInput.get().getItem());
    }

    @Override
    public ItemStack craft(MilkevsSingleRecipeInput.Single recipeInput, RegistryWrapper.WrapperLookup wrapperLookup) {
        return Items.CHEST.getDefaultStack();
    }

    @Override
    public boolean fits(int i, int j) {
        return false;
    }

    @Override
    public ItemStack getResult(RegistryWrapper.WrapperLookup wrapperLookup) {
        return Items.CHEST.getDefaultStack();
    }

    @Override
    public RecipeSerializer<?> getSerializer() {
        return MilkevsOreMiners.SIFTER_RECIPE_SERIALIZER;
    }

    @Override
    public RecipeType<?> getType() {
        return MilkevsOreMiners.SIFTER_RECIPE_TYPE;
    }
    
    public static class MyRecipeSerializer implements RecipeSerializer<SifterRecipe> {
        
        public static final MapCodec<SifterRecipe> CODEC = RecordCodecBuilder.mapCodec(inst -> {
            return inst.group(
                ItemStack.VALIDATED_CODEC.fieldOf("input").forGetter(SifterRecipe::input),
                Codec.FLOAT.fieldOf("chance").forGetter(SifterRecipe::chance),
                Codec.FLOAT.fieldOf("rolls").forGetter(SifterRecipe::rolls),
                Codec.list(Codec.STRING).fieldOf("output").forGetter(SifterRecipe::output)
                ).apply(inst, SifterRecipe::new);});
        
        public static final PacketCodec<RegistryByteBuf, SifterRecipe> PACKET_CODEC = PacketCodec.tuple(
                ItemStack.PACKET_CODEC, SifterRecipe::input,
                PacketCodecs.FLOAT, SifterRecipe::chance,
                PacketCodecs.FLOAT, SifterRecipe::rolls,
                PacketCodecs.STRING.collect(PacketCodecs.toList()), SifterRecipe::output,
                SifterRecipe::new
        );

        @Override
        public MapCodec<SifterRecipe> codec() {
            return CODEC;
        }

        @Override
        public PacketCodec<RegistryByteBuf, SifterRecipe> packetCodec() {
            return PACKET_CODEC;
        }
    }
   
}
