package me.justahuman.slimefun_essentials.client;

import me.justahuman.slimefun_essentials.api.CustomRenderable;
import me.justahuman.slimefun_essentials.api.RecipeCondition;
import net.minecraft.client.gui.tooltip.TooltipComponent;
import net.minecraft.util.Identifier;

import java.util.List;

public class OptionalRenderable implements CustomRenderable {
    protected final CustomRenderable renderable;
    protected final RecipeCondition condition;
    protected boolean passedCondition = false;

    public OptionalRenderable(CustomRenderable renderable, RecipeCondition condition) {
        this.renderable = renderable;
        this.condition = condition;
    }

    @Override
    public Identifier identifier() {
        return this.renderable.identifier();
    }

    @Override
    public int width() {
        return this.renderable.width();
    }

    @Override
    public int height() {
        return this.renderable.height();
    }

    @Override
    public int u() {
        return this.renderable.u();
    }

    @Override
    public int v() {
        return this.renderable.v();
    }

    @Override
    public int textureWidth() {
        return this.renderable.textureWidth();
    }

    @Override
    public int textureHeight() {
        return this.renderable.textureHeight();
    }

    @Override
    public List<TooltipComponent> tooltip() {
        return this.renderable.tooltip();
    }

    @Override
    public void update(SlimefunRecipe recipe) {
        this.passedCondition = this.condition.passes(recipe);
        this.renderable.update(recipe);
    }

    @Override
    public boolean canRender() {
        return this.passedCondition;
    }
}
