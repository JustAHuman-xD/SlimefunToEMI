package me.justahuman.slimefun_essentials.api;

import com.google.gson.JsonObject;
import com.mojang.blaze3d.systems.RenderSystem;
import me.justahuman.slimefun_essentials.client.BasicComponentType;
import me.justahuman.slimefun_essentials.client.DrawMode;
import me.justahuman.slimefun_essentials.client.FillingComponentType;
import me.justahuman.slimefun_essentials.client.SlimefunRecipe;
import me.justahuman.slimefun_essentials.mixins.minecraft.DrawContextInvoker;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.tooltip.HoveredTooltipPositioner;
import net.minecraft.client.gui.tooltip.TooltipComponent;
import net.minecraft.util.Identifier;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public interface DisplayComponentType {
    Map<String, DisplayComponentType> COMPONENT_TYPES = new HashMap<>();

    int width();
    int height();
    List<TooltipComponent> tooltip(DrawMode drawMode, SlimefunRecipe recipe);

    void draw(SlimefunRecipe recipe, DrawMode mode, DrawContext context, int x, int y, int mouseX, int mouseY, List<TooltipComponent> tooltip);

    default void draw(SlimefunRecipe recipe, DrawContext context, Identifier identifier, List<TooltipComponent> tooltip, int x, int y, int mouseX, int mouseY, int width, int height, int u, int v, int textureWidth, int textureHeight) {
        RenderSystem.enableBlend();
        RenderSystem.setShaderColor(1.0f, 1.0f, 1.0f, 1.0f);
        context.drawTexture(identifier, x, y, width, height, u, v, width, height, textureWidth, textureHeight);

        if (!tooltip.isEmpty() && mouseX >= x && mouseX < x + width && mouseY >= y && mouseY < y + height) {
            ((DrawContextInvoker) context).callDrawTooltip(MinecraftClient.getInstance().textRenderer, tooltip, x, y, HoveredTooltipPositioner.INSTANCE);
        }
    }

    static DisplayComponentType get(String id) {
        return COMPONENT_TYPES.get(id);
    }

    static void deserialize(String id, JsonObject type) {
        if (COMPONENT_TYPES.containsKey(id)) {
            // TODO: ??
            return;
        }

        CustomRenderable light = CustomRenderable.deserialize(type.getAsJsonObject("light"));
        CustomRenderable dark = CustomRenderable.deserialize(type.getAsJsonObject("dark"));
        if (type.has("light_fill")) {
            CustomRenderable lightFill = CustomRenderable.deserialize(type.getAsJsonObject("light_fill"));
            CustomRenderable darkFill = CustomRenderable.deserialize(type.getAsJsonObject("dark_fill"));
            boolean horizontal = type.get("horizontal").getAsBoolean();
            RecipeCondition emptyToFull = RecipeCondition.deserialize(type.get("empty_to_full"));
            RecipeCondition startToEnd = RecipeCondition.deserialize(type.get("start_to_end"));
            String timeToFill = type.get("time_to_fill").getAsString();
            if (timeToFill.equals(FillingComponentType.RECIPE_TIME)) {
                COMPONENT_TYPES.put(id, new FillingComponentType(light, lightFill, dark, darkFill, horizontal, SlimefunRecipe::millis, emptyToFull, startToEnd));
            } else {
                int millis = 1000;
                try {
                    millis = Integer.parseInt(timeToFill);
                } catch (NumberFormatException ignored) {}
                COMPONENT_TYPES.put(id, new FillingComponentType(light, lightFill, dark, darkFill, horizontal, millis, emptyToFull, startToEnd));
            }
        } else {
            COMPONENT_TYPES.put(id, new BasicComponentType(light, dark));
        }
    }
}
