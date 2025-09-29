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

public record BasicMiningRigRecipe(int powerCost, float chance, float rolls, List<String> output) implements Recipe<MilkevsSingleRecipeInput.Single> {
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

    public static class MyRecipeSerializer implements RecipeSerializer<BasicMiningRigRecipe> {

        public static final MapCodec<BasicMiningRigRecipe> CODEC = RecordCodecBuilder.mapCodec(inst -> {
            return inst.group(
                    Codec.INT.fieldOf("powerCost").forGetter(BasicMiningRigRecipe::powerCost),
                    Codec.FLOAT.fieldOf("chance").forGetter(BasicMiningRigRecipe::chance),
                    Codec.FLOAT.fieldOf("rolls").forGetter(BasicMiningRigRecipe::rolls),
                    Codec.list(Codec.STRING).fieldOf("output").forGetter(BasicMiningRigRecipe::output)
            ).apply(inst, BasicMiningRigRecipe::new);
        });

        public static final PacketCodec<RegistryByteBuf, BasicMiningRigRecipe> PACKET_CODEC = PacketCodec.tuple(
                PacketCodecs.INTEGER, BasicMiningRigRecipe::powerCost,
                PacketCodecs.FLOAT, BasicMiningRigRecipe::chance,
                PacketCodecs.FLOAT, BasicMiningRigRecipe::rolls,
                PacketCodecs.STRING.collect(PacketCodecs.toList()), BasicMiningRigRecipe::output,
                BasicMiningRigRecipe::new
        );

        @Override
        public MapCodec<BasicMiningRigRecipe> codec() {
            return CODEC;
        }

        @Override
        public PacketCodec<RegistryByteBuf, BasicMiningRigRecipe> packetCodec() {
            return PACKET_CODEC;
        }
    }
    @Override
    public RecipeSerializer<?> getSerializer() {
        return MilkevsOreMiners.MINING_RIG.BASIC.RECIPE_SERIALIZER;
    }

    @Override
    public RecipeType<?> getType() {
        return MilkevsOreMiners.MINING_RIG.BASIC.RECIPE_TYPE;
    }
}
