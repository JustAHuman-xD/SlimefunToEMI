package me.justahuman.slimefun_essentials.client;

import com.google.gson.JsonObject;
import me.justahuman.slimefun_essentials.api.CustomRenderable;
import me.justahuman.slimefun_essentials.utils.JsonUtils;
import me.justahuman.slimefun_essentials.utils.TextureUtils;
import net.minecraft.client.gui.tooltip.TooltipComponent;
import net.minecraft.util.Identifier;

import java.util.List;

public record SimpleRenderable(Identifier identifier, int width, int height, int u, int v, int textureWidth, int textureHeight, List<TooltipComponent> tooltip) implements CustomRenderable {
    public static SimpleRenderable deserialize(JsonObject json) {
        return new SimpleRenderable(
            Identifier.tryParse(JsonUtils.get(json, "identifier", TextureUtils.WIDGETS.toString())),
            json.get("width").getAsInt(),
            json.get("height").getAsInt(),
            json.get("u").getAsInt(),
            json.get("v").getAsInt(),
            JsonUtils.get(json, "texture_width", 256),
            JsonUtils.get(json, "texture_height", 256),
            JsonUtils.getTooltip(json)
        );
    }
}
