package me.justahuman.slimefun_essentials.utils;

import com.google.common.io.ByteArrayDataInput;
import com.google.common.io.ByteStreams;
import me.justahuman.slimefun_essentials.client.payloads.ItemGroupsPayload;
import me.justahuman.slimefun_essentials.client.payloads.ItemsPayload;
import me.justahuman.slimefun_essentials.client.payloads.RecipeCategoryPayload;
import me.justahuman.slimefun_essentials.client.payloads.RecipeDisplayPayload;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.packet.CustomPayload;
import net.minecraft.util.Identifier;

import java.util.function.Function;

public class Payloads {
    public static final String PLUGIN_ID = "slimefun_server_essentials";
    public static final CustomPayload.Id<ItemsPayload> ITEM_CHANNEL = newChannel("items");
    public static final CustomPayload.Id<ItemGroupsPayload> ITEM_GROUPS_CHANNEL = newChannel("item_groups");
    public static final CustomPayload.Id<RecipeDisplayPayload> RECIPE_DISPLAY_CHANNEL = newChannel("recipe_displays");
    public static final CustomPayload.Id<RecipeCategoryPayload> RECIPE_CATEGORY_CHANNEL = newChannel("recipe_categories");

    public static <P extends CustomPayload> CustomPayload.Id<P> newChannel(String channel) {
        return new CustomPayload.Id<>(Identifier.of(PLUGIN_ID, channel));
    }

    public static <P extends CustomPayload>PacketCodec<PacketByteBuf, P> newSplitCodec(Function<ByteArrayDataInput, P> decoder, P empty) {
        return new PacketCodec<>() {
            private int piecesLeft = 0;
            private byte[] previous = null;

            @Override
            public void encode(PacketByteBuf buf, P value) {}

            @Override
            public P decode(PacketByteBuf buf) {
                byte[] bytes = new byte[buf.readableBytes()];
                for (int i = 0; i < bytes.length; i++) {
                    bytes[i] = buf.readByte();
                }

                if (piecesLeft > 0) {
                    byte[] temp = new byte[previous.length + bytes.length];
                    System.arraycopy(previous, 0, temp, 0, previous.length);
                    System.arraycopy(bytes, 0, temp, previous.length, bytes.length);
                    bytes = temp;
                    piecesLeft--;

                    if (piecesLeft > 0) {
                        previous = bytes;
                        return empty;
                    } else {
                        previous = null;
                        return decoder.apply(ByteStreams.newDataInput(bytes));
                    }
                }

                int pieces = bytes[0] << 24 | bytes[1] << 16 | bytes[2] << 8 | bytes[3];
                byte[] temp = new byte[bytes.length - 4];
                System.arraycopy(bytes, 4, temp, 0, bytes.length - 4);
                bytes = temp;

                if (pieces > 1) {
                    piecesLeft = pieces - 1;
                    previous = bytes;
                    return empty;
                }
                return decoder.apply(ByteStreams.newDataInput(bytes));
            }
        };
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
