package net.milkev.milkevsoreminers.common.util;

import net.milkev.milkevsoreminers.common.MilkevsOreMiners;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.packet.CustomPayload;
import net.minecraft.util.math.BlockPos;

public record BlockPosPayload(BlockPos blockPos) implements CustomPayload {
    
    public static final Id<CustomPayload> id = new Id<>(MilkevsOreMiners.id("blockpos_payload"));
    
    public static final PacketCodec<RegistryByteBuf, BlockPosPayload> PACKET_CODEC = PacketCodec.tuple(BlockPos.PACKET_CODEC, BlockPosPayload::blockPos, BlockPosPayload::new);
    
    @Override
    public Id<? extends CustomPayload> getId() {
        return id;
    }
}
