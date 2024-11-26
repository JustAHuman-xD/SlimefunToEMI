package me.justahuman.slimefun_essentials.client;

import me.justahuman.slimefun_essentials.api.CustomRenderable;
import me.justahuman.slimefun_essentials.api.DisplayComponentType;
import me.justahuman.slimefun_essentials.api.RecipeCondition;
import me.justahuman.slimefun_essentials.utils.Utils;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.tooltip.TooltipComponent;

import java.util.List;
import java.util.function.Function;

public class FillingComponentType implements DisplayComponentType {
    public static final String RECIPE_TIME = "%time_millis%";
    protected final CustomRenderable light;
    protected final CustomRenderable lightFill;
    protected final CustomRenderable dark;
    protected final CustomRenderable darkFill;
    protected final boolean horizontal;
    protected final Function<SlimefunRecipe, Integer> timeToFill;
    protected final RecipeCondition emptyToFull;
    protected final RecipeCondition startToEnd;

    public FillingComponentType(CustomRenderable light, CustomRenderable lightFill, CustomRenderable dark, CustomRenderable darkFill, boolean horizontal, int millis, RecipeCondition emptyToFull, RecipeCondition startToEnd) {
        this(light, lightFill, dark, darkFill, horizontal, recipe -> millis, emptyToFull, startToEnd);
    }

    public FillingComponentType(CustomRenderable light, CustomRenderable lightFill, CustomRenderable dark, CustomRenderable darkFill, boolean horizontal, Function<SlimefunRecipe, Integer> timeToFill, RecipeCondition emptyToFull, RecipeCondition startToEnd) {
        this.light = light;
        this.lightFill = lightFill;
        this.dark = dark;
        this.darkFill = darkFill;
        this.horizontal = horizontal;
        this.timeToFill = timeToFill;
        this.emptyToFull = emptyToFull;
        this.startToEnd = startToEnd;
    }

    @Override
    public int width() {
        return Math.max(Math.max(light.width(), dark.width()), Math.max(lightFill.width(), darkFill.width()));
    }

    @Override
    public int height() {
        return Math.max(Math.max(light.height(), dark.height()), Math.max(lightFill.height(), darkFill.height()));
    }

    @Override
    public List<TooltipComponent> tooltip(DrawMode drawMode, SlimefunRecipe recipe) {
        CustomRenderable base = drawMode == DrawMode.LIGHT ? light : dark;
        CustomRenderable fill = drawMode == DrawMode.LIGHT ? lightFill : darkFill;
        fill.update(recipe);
        return fill.canRender() ? Utils.updateTooltip(fill.tooltip(), recipe) : Utils.updateTooltip(base.tooltip(), recipe);
    }

    @Override
    public void draw(SlimefunRecipe recipe, DrawMode mode, DrawContext context, int x, int y) {
        int time = this.timeToFill.apply(recipe);
        int subTime = time <= 0 ? 0 : (int) (System.currentTimeMillis() % time);
        boolean emptyToFull = this.emptyToFull.passes(recipe);
        boolean startToEnd = this.startToEnd.passes(recipe);
        if (!startToEnd ^ !emptyToFull) {
            subTime = time - subTime;
        }

        draw(recipe, mode == DrawMode.LIGHT ? light : dark, context, x, y);

        CustomRenderable fill = mode == DrawMode.LIGHT ? lightFill : darkFill;
        fill.update(recipe);
        if (!fill.canRender()) {
            return;
        }

        int mx = x;
        int my = y;
        int w = fill.width();
        int mw = fill.width();
        int h = fill.height();
        int mh = fill.height();
        int u = fill.u();
        int mu = fill.u();
        int v = fill.v();
        int mv = fill.v();
        int rw = fill.width();
        int mrw = fill.width();
        int rh = fill.height();
        int mrh = fill.height();

        if (horizontal) {
            if (startToEnd) {
                mw = w * subTime / time;
                mrw = rw * subTime / time;
            } else {
                mx = x + w * subTime / time;
                mu = u + rw * subTime / time;
                mw = w - (mx - x);
                mrw = rw - (mu - u);
            }
        } else {
            if (startToEnd) {
                mh = h * subTime / time;
                mrh = rh * subTime / time;
            } else {
                my = y + h * subTime / time;
                mv = v + rh * subTime / time;
                mh = h - (my - y);
                mrh = rh - (mv - v);
            }
        }

        draw(recipe, context, fill.identifier(), mx, my, mw, mh, mu, mv, mrw, mrh, fill.textureWidth(), fill.textureHeight());
    }
}
