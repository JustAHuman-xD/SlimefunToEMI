package me.justahuman.slimefun_essentials.client.payloads;

import me.justahuman.slimefun_essentials.client.RecipeDisplay;
import me.justahuman.slimefun_essentials.utils.JsonUtils;
import me.justahuman.slimefun_essentials.utils.Payloads;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.packet.CustomPayload;

public class RecipeDisplayPayload implements CustomPayload {
    private static final RecipeDisplayPayload EMPTY = new RecipeDisplayPayload();
    public static final PacketCodec<PacketByteBuf, RecipeDisplayPayload> CODEC =
            Payloads.newSplitCodec(input -> {
                RecipeDisplay.deserialize(input.readUTF(), JsonUtils.toJson(input.readUTF()));
                return EMPTY;
            }, EMPTY);

    @Override
    public Id<? extends CustomPayload> getId() {
        return Payloads.RECIPE_DISPLAY_CHANNEL;
    }
}
