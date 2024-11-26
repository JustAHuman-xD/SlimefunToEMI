package me.justahuman.slimefun_essentials.utils;

import com.google.common.io.ByteArrayDataInput;
import com.google.common.io.ByteStreams;
import me.justahuman.slimefun_essentials.SlimefunEssentials;
import me.justahuman.slimefun_essentials.client.payloads.ComponentTypePayload;
import me.justahuman.slimefun_essentials.client.payloads.ItemGroupsPayload;
import me.justahuman.slimefun_essentials.client.payloads.ItemsPayload;
import me.justahuman.slimefun_essentials.client.payloads.LoadingStatePayload;
import me.justahuman.slimefun_essentials.client.payloads.RecipeCategoryPayload;
import me.justahuman.slimefun_essentials.client.payloads.RecipeDisplayPayload;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.packet.CustomPayload;
import net.minecraft.util.Identifier;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

public class Payloads {
    public static final String PLUGIN_ID = "slimefun_server_essentials";
    public static final CustomPayload.Id<ComponentTypePayload> COMPONENT_TYPE_CHANNEL = newChannel("component_types");
    public static final CustomPayload.Id<ItemsPayload> ITEM_CHANNEL = newChannel("items");
    public static final CustomPayload.Id<ItemGroupsPayload> ITEM_GROUPS_CHANNEL = newChannel("item_groups");
    public static final CustomPayload.Id<RecipeDisplayPayload> RECIPE_DISPLAY_CHANNEL = newChannel("recipe_displays");
    public static final CustomPayload.Id<RecipeCategoryPayload> RECIPE_CATEGORY_CHANNEL = newChannel("recipe_categories");
    public static final CustomPayload.Id<LoadingStatePayload> LOADING_STATE_CHANNEL = newChannel("loading_state");

    private static final int MAX_MESSAGE_SIZE = 32766;
    private static final int SPLIT_MESSAGE_SIZE = MAX_MESSAGE_SIZE - 4 - 4 - 4;
    private static final Map<Class<?>, Integer> PACKET_COUNTS = new HashMap<>();
    private static final Map<Class<?>, Integer> EXPECTED_PACKETS = new HashMap<>();

    public static void expect(LoadingStatePayload payload) {
        EXPECTED_PACKETS.clear();
        EXPECTED_PACKETS.put(ComponentTypePayload.class, payload.typePackets());
        EXPECTED_PACKETS.put(ItemsPayload.class, payload.itemPackets());
        EXPECTED_PACKETS.put(RecipeCategoryPayload.class, payload.categoryPackets());
        EXPECTED_PACKETS.put(RecipeDisplayPayload.class, payload.displayPackets());
        if (passedExpected()) {
            SlimefunEssentials.LOGGER.info("Received every expected packet!!");
        }
    }

    private static boolean passedExpected() {
        for (Map.Entry<Class<?>, Integer> expected : EXPECTED_PACKETS.entrySet()) {
            if (PACKET_COUNTS.getOrDefault(expected.getKey(), 0) < expected.getValue()) {
                return false;
            } else if (PACKET_COUNTS.getOrDefault(expected.getKey(), 0) > expected.getValue()) {
                SlimefunEssentials.LOGGER.warn("Received more packets than expected for " + expected.getKey().getSimpleName());
            }
        }
        return !EXPECTED_PACKETS.isEmpty();
    }

    public static <P extends CustomPayload> CustomPayload.Id<P> newChannel(String channel) {
        return new CustomPayload.Id<>(Identifier.of(PLUGIN_ID, channel));
    }

    public static <P extends CustomPayload>PacketCodec<PacketByteBuf, P> newSplitCodec(Function<ByteArrayDataInput, P> decoder, P empty) {
        return new PacketCodec<>() {
            private final Map<Integer, byte[][]> received = new HashMap<>();

            @Override
            public void encode(PacketByteBuf buf, P value) {}

            @Override
            public P decode(PacketByteBuf buf) {
                PACKET_COUNTS.compute(empty.getClass(), (k, v) -> v == null ? 1 : v + 1);

                if (passedExpected()) {
                    SlimefunEssentials.LOGGER.info("Received every expected packet!!");
                }

                byte[] bytes = new byte[buf.readableBytes()];
                for (int i = 0; i < bytes.length; i++) {
                    bytes[i] = buf.readByte();
                }

                int id = bytes[0] << 24 | bytes[1] << 16 | bytes[2] << 8 | bytes[3];
                int pieces = bytes[4] << 24 | bytes[5] << 16 | bytes[6] << 8 | bytes[7];
                int piece = bytes[8] << 24 | bytes[9] << 16 | bytes[10] << 8 | bytes[11];
                byte[] pieceBytes = new byte[bytes.length - 12];
                System.arraycopy(bytes, 12, pieceBytes, 0, pieceBytes.length);
                byte[][] piecesBytes = received.computeIfAbsent(id, k -> new byte[pieces][]);
                piecesBytes[piece] = pieceBytes;

                boolean complete = true;
                for (byte[] totalMessagePiece : piecesBytes) {
                    if (totalMessagePiece == null) {
                        complete = false;
                        break;
                    }
                }

                if (complete) {
                    byte[] totalBytes = new byte[pieces * SPLIT_MESSAGE_SIZE];
                    for (int i = 0; i < pieces; i++) {
                        System.arraycopy(piecesBytes[i], 0, totalBytes, i * SPLIT_MESSAGE_SIZE, piecesBytes[i].length);
                    }
                    received.remove(id);
                    return decoder.apply(ByteStreams.newDataInput(totalBytes));
                }
                return empty;
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
