package me.justahuman.slimefun_essentials.client.payloads;

import me.justahuman.slimefun_essentials.client.SlimefunItemGroup;
import me.justahuman.slimefun_essentials.utils.JsonUtils;
import me.justahuman.slimefun_essentials.utils.Payloads;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.packet.CustomPayload;

public class ItemGroupsPayload implements CustomPayload {
    private static final ItemGroupsPayload EMPTY = new ItemGroupsPayload();
    public static final PacketCodec<PacketByteBuf, ItemGroupsPayload> CODEC =
            Payloads.newSplitCodec(input -> {
                int size = input.readInt();
                for (int i = 0; i < size; i++) {
                    SlimefunItemGroup.deserialize(input.readUTF(), JsonUtils.toJson(input.readUTF()));
                }
                return EMPTY;
            }, EMPTY);

    @Override
    public Id<? extends CustomPayload> getId() {
        return Payloads.ITEM_GROUPS_CHANNEL;
    }
}
