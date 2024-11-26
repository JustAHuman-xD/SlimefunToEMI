package me.justahuman.slimefun_essentials.client;

import me.justahuman.slimefun_essentials.api.CustomRenderable;
import me.justahuman.slimefun_essentials.api.RecipeCondition;
import net.minecraft.client.gui.tooltip.TooltipComponent;
import net.minecraft.util.Identifier;

import java.util.List;

public class ConditionalRenderable implements CustomRenderable {
    protected final CustomRenderable passed;
    protected final CustomRenderable failed;
    protected final RecipeCondition condition;
    protected boolean passedCondition = false;

    public ConditionalRenderable(CustomRenderable passed, CustomRenderable failed, RecipeCondition condition) {
        this.passed = passed;
        this.failed = failed;
        this.condition = condition;
    }

    @Override
    public Identifier identifier() {
        return passedCondition ? passed.identifier() : failed.identifier();
    }

    @Override
    public int width() {
        return Math.max(passed.width(), failed.width());
    }

    @Override
    public int height() {
        return Math.max(passed.height(), failed.height());
    }

    @Override
    public int u() {
        return passedCondition ? passed.u() : failed.u();
    }

    @Override
    public int v() {
        return passedCondition ? passed.v() : failed.v();
    }

    @Override
    public int textureWidth() {
        return passedCondition ? passed.textureWidth() : failed.textureWidth();
    }

    @Override
    public int textureHeight() {
        return passedCondition ? passed.textureHeight() : failed.textureHeight();
    }

    @Override
    public List<TooltipComponent> tooltip() {
        return passedCondition ? passed.tooltip() : failed.tooltip();
    }

    @Override
    public void update(SlimefunRecipe recipe) {
        passedCondition = condition.passes(recipe);
        passed.update(recipe);
        failed.update(recipe);
    }
}
