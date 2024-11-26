package me.justahuman.slimefun_essentials.compat.emi;

import dev.emi.emi.api.recipe.EmiRecipe;
import dev.emi.emi.api.recipe.EmiRecipeCategory;
import dev.emi.emi.api.stack.EmiIngredient;
import dev.emi.emi.api.stack.EmiStack;
import dev.emi.emi.api.widget.WidgetHolder;
import me.justahuman.slimefun_essentials.client.RecipeCategory;
import me.justahuman.slimefun_essentials.client.RecipeDisplayComponent;
import me.justahuman.slimefun_essentials.client.SlimefunRecipe;
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
            EmiIntegration.wrap(this, widgets, component, recipe, inputs, outputs);
        }
    }
}
