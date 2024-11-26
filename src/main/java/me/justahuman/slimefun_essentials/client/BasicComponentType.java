package me.justahuman.slimefun_essentials.client;

import me.justahuman.slimefun_essentials.api.CustomRenderable;
import me.justahuman.slimefun_essentials.api.DisplayComponentType;
import me.justahuman.slimefun_essentials.utils.Utils;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.tooltip.TooltipComponent;

import java.util.List;

public class BasicComponentType implements DisplayComponentType {
    protected final CustomRenderable light;
    protected final CustomRenderable dark;

    public BasicComponentType(CustomRenderable light, CustomRenderable dark) {
        this.light = light;
        this.dark = dark;
    }

    @Override
    public int width() {
        return Math.max(light.width(), dark.width());
    }

    @Override
    public int height() {
        return Math.max(light.height(), dark.height());
    }

    @Override
    public List<TooltipComponent> tooltip(DrawMode drawMode, SlimefunRecipe recipe) {
        return drawMode == DrawMode.LIGHT ? Utils.updateTooltip(light.tooltip(), recipe) : Utils.updateTooltip(dark.tooltip(), recipe);
    }

    @Override
    public void draw(SlimefunRecipe recipe, DrawMode mode, DrawContext context, int x, int y, int mouseX, int mouseY, List<TooltipComponent> tooltip) {
        CustomRenderable renderable = mode == DrawMode.LIGHT ? light : dark;
        renderable.update(recipe);
        draw(recipe, context, renderable.identifier(), tooltip, x, y, mouseX, mouseY, renderable.width(), renderable.height(), renderable.u(), renderable.v(), renderable.textureWidth(), renderable.textureHeight());
    }
}
