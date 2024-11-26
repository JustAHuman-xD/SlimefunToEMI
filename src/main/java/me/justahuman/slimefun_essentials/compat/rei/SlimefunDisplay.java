package me.justahuman.slimefun_essentials.compat.rei;

import me.justahuman.slimefun_essentials.client.RecipeCategory;
import me.justahuman.slimefun_essentials.client.SlimefunRecipe;
import me.justahuman.slimefun_essentials.utils.Utils;
import me.shedaniel.rei.api.common.category.CategoryIdentifier;
import me.shedaniel.rei.api.common.display.Display;
import me.shedaniel.rei.api.common.entry.EntryIngredient;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

public class SlimefunDisplay implements Display {
    protected final RecipeCategory recipeCategory;
    protected final SlimefunRecipe slimefunRecipe;
    protected final List<EntryIngredient> inputs = new ArrayList<>();
    protected final List<EntryIngredient> outputs = new ArrayList<>();

    public SlimefunDisplay(RecipeCategory recipeCategory, SlimefunRecipe slimefunRecipe) {
        this.recipeCategory = recipeCategory;
        this.slimefunRecipe = slimefunRecipe;
        this.inputs.addAll(ReiIntegration.RECIPE_INTERPRETER.getInputEntries(slimefunRecipe));
        this.outputs.addAll(ReiIntegration.RECIPE_INTERPRETER.getOutputEntries(slimefunRecipe));
    }

    public SlimefunRecipe slimefunRecipe() {
        return this.slimefunRecipe;
    }

    @Override
    public List<EntryIngredient> getInputEntries() {
        return this.inputs;
    }

    @Override
    public List<EntryIngredient> getOutputEntries() {
        return this.outputs;
    }

    @Override
    public @NotNull CategoryIdentifier<SlimefunDisplay> getCategoryIdentifier() {
        return CategoryIdentifier.of(Utils.id(this.recipeCategory.id().toLowerCase()));
    }
}
