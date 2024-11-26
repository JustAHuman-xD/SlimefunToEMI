package me.justahuman.slimefun_essentials.api;

import com.google.gson.JsonObject;
import me.justahuman.slimefun_essentials.client.ConditionalRenderable;
import me.justahuman.slimefun_essentials.client.SimpleRenderable;
import me.justahuman.slimefun_essentials.client.SlimefunRecipe;
import net.minecraft.client.gui.tooltip.TooltipComponent;
import net.minecraft.util.Identifier;

import java.util.List;

public interface CustomRenderable {
    Identifier identifier();
    int width();
    int height();
    int u();
    int v();
    int textureWidth();
    int textureHeight();
    List<TooltipComponent> tooltip();

    default void update(SlimefunRecipe recipe) {}

    static CustomRenderable deserialize(JsonObject jsonObject) {
        if (jsonObject.has("condition")) {
            return new ConditionalRenderable(
                    deserialize(jsonObject.getAsJsonObject("passed")),
                    deserialize(jsonObject.getAsJsonObject("failed")),
                    RecipeCondition.deserialize(jsonObject.get("condition"))
            );
        } else {
            return SimpleRenderable.deserialize(jsonObject);
        }
    }
}
