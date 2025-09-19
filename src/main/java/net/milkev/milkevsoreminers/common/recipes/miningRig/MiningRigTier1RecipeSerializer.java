package net.milkev.milkevsoreminers.common.recipes.miningRig;

import com.mojang.serialization.MapCodec;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;

public class MiningRigTier1RecipeSerializer implements net.minecraft.recipe.RecipeSerializer<MiningRigTier1Recipe> {
    
    @Override
    public MapCodec<MiningRigTier1Recipe> codec() {
        return null;
    }

    @Override
    public PacketCodec<RegistryByteBuf, MiningRigTier1Recipe> packetCodec() {
        return null;
    }
}
