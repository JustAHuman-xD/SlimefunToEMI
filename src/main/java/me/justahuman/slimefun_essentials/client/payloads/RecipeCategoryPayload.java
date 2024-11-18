package me.justahuman.slimefun_essentials.client.payloads;

import me.justahuman.slimefun_essentials.client.SlimefunRecipeCategory;
import me.justahuman.slimefun_essentials.utils.JsonUtils;
import me.justahuman.slimefun_essentials.utils.Payloads;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.packet.CustomPayload;

public class RecipeCategoryPayload implements CustomPayload {
    private static final RecipeCategoryPayload EMPTY = new RecipeCategoryPayload();
    public static final PacketCodec<PacketByteBuf, RecipeCategoryPayload> CODEC =
            Payloads.newSplitCodec(input -> {
                SlimefunRecipeCategory.deserialize(input.readUTF(), JsonUtils.toJson(input.readUTF()));
                return EMPTY;
            }, EMPTY);

    @Override
    public Id<? extends CustomPayload> getId() {
        return Payloads.RECIPE_CATEGORY_CHANNEL;
    }
}
