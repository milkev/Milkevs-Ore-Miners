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

public record AdvancedSifterRecipe(ItemStack input, long powerCost, long basePowerConsumption, float chance, float rolls, List<String> output) implements Recipe<MilkevsSingleRecipeInput.Single> {
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
        return MilkevsOreMiners.ADVANCED_SIFTER_RECIPE_SERIALIZER;
    }

    @Override
    public RecipeType<?> getType() {
        return MilkevsOreMiners.ADVANCED_SIFTER_RECIPE_TYPE;
    }
    
    public static class MyRecipeSerializer implements RecipeSerializer<AdvancedSifterRecipe> {
        
        public static final MapCodec<AdvancedSifterRecipe> CODEC = RecordCodecBuilder.mapCodec(inst -> {
            return inst.group(
                ItemStack.VALIDATED_CODEC.fieldOf("input").forGetter(AdvancedSifterRecipe::input),
                Codec.LONG.fieldOf("powerCost").forGetter(AdvancedSifterRecipe::powerCost),
                Codec.LONG.fieldOf("basePowerConsumption").forGetter(AdvancedSifterRecipe::basePowerConsumption),
                Codec.FLOAT.fieldOf("chance").forGetter(AdvancedSifterRecipe::chance),
                Codec.FLOAT.fieldOf("rolls").forGetter(AdvancedSifterRecipe::rolls),
                Codec.list(Codec.STRING).fieldOf("output").forGetter(AdvancedSifterRecipe::output)
                ).apply(inst, AdvancedSifterRecipe::new);});
        
        public static final PacketCodec<RegistryByteBuf, AdvancedSifterRecipe> PACKET_CODEC = PacketCodec.tuple(
                ItemStack.PACKET_CODEC, AdvancedSifterRecipe::input,
                PacketCodecs.VAR_LONG, AdvancedSifterRecipe::powerCost,
                PacketCodecs.VAR_LONG, AdvancedSifterRecipe::basePowerConsumption,
                PacketCodecs.FLOAT, AdvancedSifterRecipe::chance,
                PacketCodecs.FLOAT, AdvancedSifterRecipe::rolls,
                PacketCodecs.STRING.collect(PacketCodecs.toList()), AdvancedSifterRecipe::output,
                AdvancedSifterRecipe::new
        );

        @Override
        public MapCodec<AdvancedSifterRecipe> codec() {
            return CODEC;
        }

        @Override
        public PacketCodec<RegistryByteBuf, AdvancedSifterRecipe> packetCodec() {
            return PACKET_CODEC;
        }
    }
}
