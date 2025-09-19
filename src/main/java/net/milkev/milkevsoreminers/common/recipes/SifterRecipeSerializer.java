package net.milkev.milkevsoreminers.common.recipes;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.item.ItemStack;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.codec.PacketCodecs;
import net.minecraft.recipe.RecipeSerializer;

public class SifterRecipeSerializer implements RecipeSerializer<SifterRecipe> {
        
        public static final MapCodec<SifterRecipe> CODEC = RecordCodecBuilder.mapCodec(inst -> {
            System.out.println("OMG");
            return inst.group(
                ItemStack.CODEC.fieldOf("input").forGetter(SifterRecipe::input),
                Codec.FLOAT.fieldOf("chance").forGetter(SifterRecipe::chance),
                Codec.list(Codec.STRING).fieldOf("output").forGetter(SifterRecipe::output)
                //Codec.unboundedMap(ItemStack.CODEC, Codec.INT).fieldOf("result").forGetter(SifterRecipe::output)
                ).apply(inst, SifterRecipe::new);});
        
        public static final PacketCodec<RegistryByteBuf, SifterRecipe> PACKET_CODEC = PacketCodec.tuple(
                ItemStack.PACKET_CODEC, SifterRecipe::input,
                PacketCodecs.FLOAT, SifterRecipe::chance,
                PacketCodecs.STRING.collect(PacketCodecs.toList()), SifterRecipe::output,
                //PacketCodecs.map(HashMap::new, ItemStack.PACKET_CODEC, PacketCodecs.INTEGER), SifterRecipe::output,
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
