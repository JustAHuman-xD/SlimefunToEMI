package me.justahuman.slimefun_essentials.client.payloads;

import me.justahuman.slimefun_essentials.utils.Payloads;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.packet.CustomPayload;

public record LoadingStatePayload(int typePackets, int itemPackets, int categoryPackets, int displayPackets) implements CustomPayload {
    public static final PacketCodec<PacketByteBuf, LoadingStatePayload> CODEC = Payloads.newCodec(input -> new LoadingStatePayload(
            input.readInt(),
            input.readInt(),
            input.readInt(),
            input.readInt()
    ));

    @Override
    public Id<? extends CustomPayload> getId() {
        return Payloads.LOADING_STATE_CHANNEL;
    }
}
