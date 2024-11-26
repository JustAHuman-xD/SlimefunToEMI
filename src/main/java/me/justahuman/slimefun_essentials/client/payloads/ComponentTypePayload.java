package me.justahuman.slimefun_essentials.client.payloads;

import me.justahuman.slimefun_essentials.api.DisplayComponentType;
import me.justahuman.slimefun_essentials.utils.JsonUtils;
import me.justahuman.slimefun_essentials.utils.Payloads;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.packet.CustomPayload;

public class ComponentTypePayload implements CustomPayload {
    private static final ComponentTypePayload EMPTY = new ComponentTypePayload();
    public static final PacketCodec<PacketByteBuf, ComponentTypePayload> CODEC =
            Payloads.newSplitCodec(input -> {
                DisplayComponentType.deserialize(input.readUTF(), JsonUtils.toJson(input.readUTF()));
                return EMPTY;
            }, EMPTY);

    @Override
    public Id<? extends CustomPayload> getId() {
        return Payloads.COMPONENT_TYPE_CHANNEL;
    }
}
