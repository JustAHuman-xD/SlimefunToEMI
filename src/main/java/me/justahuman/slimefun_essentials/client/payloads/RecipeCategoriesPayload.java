package me.justahuman.slimefun_essentials.client.payloads;

import me.justahuman.slimefun_essentials.client.RecipeCategory;
import me.justahuman.slimefun_essentials.utils.JsonUtils;
import me.justahuman.slimefun_essentials.utils.Payloads;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.packet.CustomPayload;

public class RecipeCategoriesPayload implements CustomPayload {
    private static final RecipeCategoriesPayload EMPTY = new RecipeCategoriesPayload();
    public static final PacketCodec<PacketByteBuf, RecipeCategoriesPayload> CODEC =
            Payloads.newSplitCodec(input -> {
                int size = input.readInt();
                for (int i = 0; i < size; i++) {
                    RecipeCategory.deserialize(input.readUTF(), JsonUtils.toJson(input.readUTF()));
                }
                return EMPTY;
            }, EMPTY);

    @Override
    public Id<? extends CustomPayload> getId() {
        return Payloads.RECIPE_CATEGORIES_CHANNEL;
    }
}
