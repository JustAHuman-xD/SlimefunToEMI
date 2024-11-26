package me.justahuman.slimefun_essentials.compat.emi;

import dev.emi.emi.api.recipe.EmiRecipe;
import dev.emi.emi.api.recipe.EmiRecipeCategory;
import dev.emi.emi.api.stack.EmiIngredient;
import dev.emi.emi.api.stack.EmiStack;
import dev.emi.emi.api.widget.Bounds;
import dev.emi.emi.api.widget.Widget;
import dev.emi.emi.api.widget.WidgetHolder;
import me.justahuman.slimefun_essentials.api.DisplayComponentType;
import me.justahuman.slimefun_essentials.client.DrawMode;
import me.justahuman.slimefun_essentials.client.RecipeCategory;
import me.justahuman.slimefun_essentials.client.RecipeDisplayComponent;
import me.justahuman.slimefun_essentials.client.SlimefunRecipe;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.tooltip.TooltipComponent;
import net.minecraft.util.Identifier;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

public class SlimefunEmiRecipe implements EmiRecipe {
    private final RecipeCategory category;
    private final SlimefunRecipe recipe;
    private final SlimefunEmiCategory emiCategory;
    private final List<EmiIngredient> inputs = new ArrayList<>();
    private final List<EmiStack> outputs = new ArrayList<>();

    public SlimefunEmiRecipe(RecipeCategory category, SlimefunRecipe recipe, SlimefunEmiCategory emiCategory) {
        this.category = category;
        this.recipe = recipe;
        this.emiCategory = emiCategory;
        this.inputs.addAll(EmiIntegration.RECIPE_INTERPRETER.getInputIngredients(recipe));
        this.outputs.addAll(EmiIntegration.RECIPE_INTERPRETER.getOutputStacks(recipe));
    }

    @Override
    public EmiRecipeCategory getCategory() {
        return this.emiCategory;
    }

    @Override
    public @Nullable Identifier getId() {
        return null;
    }

    @Override
    public List<EmiIngredient> getInputs() {
        return this.inputs;
    }

    @Override
    public List<EmiStack> getOutputs() {
        return this.outputs;
    }

    @Override
    public int getDisplayWidth() {
        return category.display().width(this.recipe);
    }

    @Override
    public int getDisplayHeight() {
        return category.display().height(this.recipe);
    }

    @Override
    public void addWidgets(WidgetHolder widgets) {
        for (RecipeDisplayComponent component : category.display().components(recipe)) {
            int x = component.x();
            int y = component.y();

            widgets.add(new Widget() {
                private final Bounds bounds = new Bounds(x, y, component.width(), component.height());
                private final List<TooltipComponent> tooltip = component.tooltip(DrawMode.LIGHT, recipe);

                @Override
                public Bounds getBounds() {
                    return bounds;
                }

                @Override
                public void render(DrawContext context, int mouseX, int mouseY, float delta) {
                    component.draw(recipe, DrawMode.LIGHT, context, bounds.x(), bounds.y());
                }

                @Override
                public List<TooltipComponent> getTooltip(int mouseX, int mouseY) {
                    if (bounds.contains(mouseX, mouseY)) {
                        return tooltip;
                    }
                    return List.of();
                }
            });

            if (component.type().equals("slot") || component.type().equals("large_slot")) {
                int index = component.index();
                boolean large = component.type().equals("large_slot");
                if (index <= -1) {
                    widgets.addSlot(EmiStack.of(recipe.parent().itemStack()), x, y).large(large).drawBack(false);
                } else if (!component.output() && index > 0 && index <= inputs.size()) {
                    widgets.addSlot(inputs.get(--index), x, y).large(large).drawBack(false);
                } else if (component.output() && index > 0 && index <= outputs.size()) {
                    widgets.addSlot(outputs.get(--index), x, y).recipeContext(this).large(large).drawBack(false);
                }
            }
        }
    }
}
