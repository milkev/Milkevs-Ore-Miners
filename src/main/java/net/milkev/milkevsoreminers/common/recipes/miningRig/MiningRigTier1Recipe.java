package net.milkev.milkevsoreminers.common.recipes.miningRig;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.milkev.milkevsoreminers.common.MilkevsOreMiners;
import net.milkev.milkevsoreminers.common.recipes.MilkevsSingleRecipeInput;
import net.minecraft.inventory.SimpleInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.codec.PacketCodecs;
import net.minecraft.recipe.Recipe;
import net.minecraft.recipe.RecipeSerializer;
import net.minecraft.recipe.RecipeType;
import net.minecraft.recipe.input.RecipeInput;
import net.minecraft.registry.DynamicRegistryManager;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.random.Random;
import net.minecraft.world.World;

import java.util.List;

public record MiningRigTier1Recipe(int powerCost, float chance, float rolls, List<String> output) implements Recipe<MilkevsSingleRecipeInput.Single> {
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

    public static class MyRecipeSerializer implements RecipeSerializer<MiningRigTier1Recipe> {

        public static final MapCodec<MiningRigTier1Recipe> CODEC = RecordCodecBuilder.mapCodec(inst -> {
            return inst.group(
                    Codec.INT.fieldOf("powerCost").forGetter(MiningRigTier1Recipe::powerCost),
                    Codec.FLOAT.fieldOf("chance").forGetter(MiningRigTier1Recipe::chance),
                    Codec.FLOAT.fieldOf("rolls").forGetter(MiningRigTier1Recipe::rolls),
                    Codec.list(Codec.STRING).fieldOf("output").forGetter(MiningRigTier1Recipe::output)
            ).apply(inst, MiningRigTier1Recipe::new);
        });

        public static final PacketCodec<RegistryByteBuf, MiningRigTier1Recipe> PACKET_CODEC = PacketCodec.tuple(
                PacketCodecs.INTEGER, MiningRigTier1Recipe::powerCost,
                PacketCodecs.FLOAT, MiningRigTier1Recipe::chance,
                PacketCodecs.FLOAT, MiningRigTier1Recipe::rolls,
                PacketCodecs.STRING.collect(PacketCodecs.toList()), MiningRigTier1Recipe::output,
                MiningRigTier1Recipe::new
        );

        @Override
        public MapCodec<MiningRigTier1Recipe> codec() {
            return CODEC;
        }

        @Override
        public PacketCodec<RegistryByteBuf, MiningRigTier1Recipe> packetCodec() {
            return PACKET_CODEC;
        }
    }
    @Override
    public RecipeSerializer<?> getSerializer() {
        return MilkevsOreMiners.MINING_RIG_TIER_1_RECIPE_SERIALIZER;
    }

    @Override
    public RecipeType<?> getType() {
        return MilkevsOreMiners.MINING_RIG_TIER_1_RECIPE_TYPE;
    }
}
