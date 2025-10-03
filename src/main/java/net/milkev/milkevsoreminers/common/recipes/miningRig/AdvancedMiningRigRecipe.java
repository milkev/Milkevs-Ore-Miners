package net.milkev.milkevsoreminers.common.recipes.miningRig;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.milkev.milkevsoreminers.common.MilkevsOreMiners;
import net.milkev.milkevsoreminers.common.recipes.MilkevsSingleRecipeInput;
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

public record AdvancedMiningRigRecipe(long powerCost, long basePowerConsumption, float chance, float rolls, List<String> output) implements Recipe<MilkevsSingleRecipeInput.Single> {
    @Override
    public boolean matches(MilkevsSingleRecipeInput.Single recipeInput, World world) {
        return true;
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

    public static class MyRecipeSerializer implements RecipeSerializer<AdvancedMiningRigRecipe> {

        public static final MapCodec<AdvancedMiningRigRecipe> CODEC = RecordCodecBuilder.mapCodec(inst -> {
            return inst.group(
                    Codec.LONG.fieldOf("powerCost").forGetter(AdvancedMiningRigRecipe::powerCost),
                    Codec.LONG.fieldOf("basePowerConsumption").forGetter(AdvancedMiningRigRecipe::basePowerConsumption),
                    Codec.FLOAT.fieldOf("chance").forGetter(AdvancedMiningRigRecipe::chance),
                    Codec.FLOAT.fieldOf("rolls").forGetter(AdvancedMiningRigRecipe::rolls),
                    Codec.list(Codec.STRING).fieldOf("output").forGetter(AdvancedMiningRigRecipe::output)
            ).apply(inst, AdvancedMiningRigRecipe::new);
        });

        public static final PacketCodec<RegistryByteBuf, AdvancedMiningRigRecipe> PACKET_CODEC = PacketCodec.tuple(
                PacketCodecs.VAR_LONG, AdvancedMiningRigRecipe::powerCost,
                PacketCodecs.VAR_LONG, AdvancedMiningRigRecipe::basePowerConsumption,
                PacketCodecs.FLOAT, AdvancedMiningRigRecipe::chance,
                PacketCodecs.FLOAT, AdvancedMiningRigRecipe::rolls,
                PacketCodecs.STRING.collect(PacketCodecs.toList()), AdvancedMiningRigRecipe::output,
                AdvancedMiningRigRecipe::new
        );

        @Override
        public MapCodec<AdvancedMiningRigRecipe> codec() {
            return CODEC;
        }

        @Override
        public PacketCodec<RegistryByteBuf, AdvancedMiningRigRecipe> packetCodec() {
            return PACKET_CODEC;
        }
    }
    @Override
    public RecipeSerializer<?> getSerializer() {
        return MilkevsOreMiners.MINING_RIG.ADVANCED.RECIPE_SERIALIZER;
    }

    @Override
    public RecipeType<?> getType() {
        return MilkevsOreMiners.MINING_RIG.ADVANCED.RECIPE_TYPE;
    }
}
