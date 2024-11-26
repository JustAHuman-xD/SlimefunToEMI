package me.justahuman.slimefun_essentials.compat.rei;

import me.justahuman.slimefun_essentials.client.DrawMode;
import me.justahuman.slimefun_essentials.client.RecipeDisplayComponent;
import me.justahuman.slimefun_essentials.client.SlimefunRegistry;
import me.justahuman.slimefun_essentials.client.SlimefunItemGroup;
import me.justahuman.slimefun_essentials.client.RecipeCategory;
import me.justahuman.slimefun_essentials.client.SlimefunItemStack;
import me.justahuman.slimefun_essentials.client.SlimefunRecipe;
import me.shedaniel.math.Point;
import me.shedaniel.rei.api.client.REIRuntime;
import me.shedaniel.rei.api.client.gui.widgets.Slot;
import me.shedaniel.rei.api.client.gui.widgets.Widget;
import me.shedaniel.rei.api.client.gui.widgets.Widgets;
import me.shedaniel.rei.api.client.plugins.REIClientPlugin;
import me.shedaniel.rei.api.client.registry.category.CategoryRegistry;
import me.shedaniel.rei.api.client.registry.display.DisplayCategory;
import me.shedaniel.rei.api.client.registry.display.DisplayRegistry;
import me.shedaniel.rei.api.client.registry.entry.EntryRegistry;
import me.shedaniel.rei.api.common.entry.EntryIngredient;
import me.shedaniel.rei.api.common.entry.comparison.ItemComparatorRegistry;
import me.shedaniel.rei.api.common.util.EntryStacks;
import net.minecraft.item.ItemStack;

import java.util.List;

public class ReiIntegration implements REIClientPlugin {
    public static final ReiRecipeInterpreter RECIPE_INTERPRETER = new ReiRecipeInterpreter();

    @Override
    public double getPriority() {
        return 10;
    }

    @Override
    public void registerItemComparators(ItemComparatorRegistry registry) {
        registry.registerGlobal(new SlimefunIdComparator());
    }

    @Override
    public void registerEntries(EntryRegistry registry) {
        for (SlimefunItemStack slimefunItemStack : SlimefunItemGroup.sort(List.copyOf(SlimefunRegistry.getSlimefunItems().values()))) {
            registry.addEntry(EntryStacks.of(slimefunItemStack.itemStack()));
        }
    }
    
    @Override
    public void registerCategories(CategoryRegistry registry) {
        for (RecipeCategory recipeCategory : RecipeCategory.getRecipeCategories().values()) {
            final ItemStack icon = recipeCategory.itemStack();
            final DisplayCategory<?> displayCategory = new SlimefunReiCategory(recipeCategory, icon);
            registry.add(displayCategory);
            registry.addWorkstations(displayCategory.getCategoryIdentifier(), EntryStacks.of(icon));
        }
    }
    
    @Override
    public void registerDisplays(DisplayRegistry registry) {
        for (RecipeCategory recipeCategory : RecipeCategory.getRecipeCategories().values()) {
            for (SlimefunRecipe slimefunRecipe : recipeCategory.childRecipes()) {
                registry.add(new SlimefunDisplay(recipeCategory, slimefunRecipe));
            }
        }
    }

    public static void wrap(List<Widget> widgets, RecipeDisplayComponent component, SlimefunRecipe recipe, List<EntryIngredient> inputs, List<EntryIngredient> outputs, int xOffset, int yOffset) {
        DrawMode mode = REIRuntime.getInstance().isDarkThemeEnabled() ? DrawMode.DARK : DrawMode.LIGHT;
        if (component.type().equals("slot") || component.type().equals("large_slot")) {
            int offset = component.type().equals("large_slot") ? 5 : 1;
            Slot slot = Widgets.createSlot(new Point(
                    component.x() + xOffset + offset,
                    component.y() + yOffset + offset
            )).disableBackground();

            if (component.index() <= -1) {
                slot.entry(EntryStacks.of(recipe.parent().itemStack())).markInput();
            } else if (component.index() > 0 && component.output() && component.index() < outputs.size()) {
                slot.entries(outputs.get(component.index() - 1)).markOutput();
            } else if (component.index() > 0 && !component.output() && component.index() < inputs.size()) {
                slot.entries(inputs.get(component.index() - 1)).markInput();
            }
        }
        widgets.add(Widgets.createDrawableWidget((context, mouseX, mouseY, delta) ->
                component.getType().draw(recipe, mode, context, component.x() + xOffset, component.y() + yOffset, mouseX, mouseY, component.tooltip(mode, recipe))));
    }
}
