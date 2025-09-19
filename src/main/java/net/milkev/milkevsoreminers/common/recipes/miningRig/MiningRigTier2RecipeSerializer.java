package net.milkev.milkevsoreminers.common.recipes.miningRig;

import com.mojang.serialization.MapCodec;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;

public class MiningRigTier2RecipeSerializer implements net.minecraft.recipe.RecipeSerializer<MiningRigTier2Recipe> {

    @Override
    public MapCodec<MiningRigTier2Recipe> codec() {
        return null;
    }

    @Override
    public PacketCodec<RegistryByteBuf, MiningRigTier2Recipe> packetCodec() {
        return null;
    }
}
