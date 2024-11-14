package me.justahuman.slimefun_essentials.utils;

import com.google.common.io.ByteArrayDataInput;
import com.google.common.io.ByteStreams;
import me.justahuman.slimefun_essentials.client.payloads.DisabledItemPayload;
import me.justahuman.slimefun_essentials.client.payloads.SlimefunAddonPayload;
import me.justahuman.slimefun_essentials.client.payloads.SlimefunBlockPayload;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.packet.CustomPayload;
import net.minecraft.util.Identifier;

import java.util.function.Function;

public class Payloads {
    public static final String PLUGIN_ID = "slimefun_server_essentials";
    public static final CustomPayload.Id<SlimefunAddonPayload> ADDON_CHANNEL = newChannel("addon");
    public static final CustomPayload.Id<SlimefunBlockPayload> BLOCK_CHANNEL = newChannel("block");
    public static final CustomPayload.Id<DisabledItemPayload> ITEM_CHANNEL = newChannel("item");

    public static <P extends CustomPayload> CustomPayload.Id<P> newChannel(String channel) {
        return new CustomPayload.Id<>(Identifier.of(PLUGIN_ID, channel));
    }

    public static <P extends CustomPayload>PacketCodec<PacketByteBuf, P> newCodec(Function<ByteArrayDataInput, P> decoder) {
        return PacketCodec.of((value, buf) -> {}, buf -> {
            byte[] bytes = new byte[buf.readableBytes()];
            for (int i = 0; i < bytes.length; i++) {
                bytes[i] = buf.readByte();
            }
            return decoder.apply(ByteStreams.newDataInput(bytes));
        });
    }
}
