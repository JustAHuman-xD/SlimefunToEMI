package me.justahuman.slimefun_essentials.compat.emi;

import dev.emi.emi.api.EmiPlugin;
import dev.emi.emi.api.EmiRegistry;
import dev.emi.emi.api.stack.Comparison;
import dev.emi.emi.api.stack.EmiIngredient;
import dev.emi.emi.api.stack.EmiStack;
import dev.emi.emi.api.widget.DrawableWidget;
import dev.emi.emi.api.widget.WidgetHolder;
import me.justahuman.slimefun_essentials.client.DrawMode;
import me.justahuman.slimefun_essentials.client.RecipeDisplayComponent;
import me.justahuman.slimefun_essentials.client.SlimefunRegistry;
import me.justahuman.slimefun_essentials.client.SlimefunItemGroup;
import me.justahuman.slimefun_essentials.client.SlimefunItemStack;
import me.justahuman.slimefun_essentials.client.RecipeCategory;
import me.justahuman.slimefun_essentials.client.SlimefunRecipe;
import me.justahuman.slimefun_essentials.utils.Utils;
import net.minecraft.util.Identifier;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class EmiIntegration implements EmiPlugin {
    public static final EmiIdInterpreter RECIPE_INTERPRETER = new EmiIdInterpreter();
    private static final Comparison SLIMEFUN_ID = Comparison.compareData(stack -> Utils.getSlimefunId(stack.getComponentChanges()));
    private static final Map<String, SlimefunEmiCategory> CATEGORIES = new HashMap<>();
    
    @Override
    public void register(EmiRegistry emiRegistry) {
        for (SlimefunItemStack slimefunItemStack : SlimefunRegistry.getSLIMEFUN_ITEMS().values()) {
            emiRegistry.setDefaultComparison(EmiStack.of(slimefunItemStack.itemStack()), SLIMEFUN_ID);
        }
        CATEGORIES.clear();

        for (RecipeCategory recipeCategory : RecipeCategory.getRecipeCategories().values()) {
            final String workstationId = recipeCategory.id();
            final Identifier categoryIdentifier = Utils.id(workstationId);
            final EmiStack workStation = EmiStack.of(recipeCategory.itemStack());
            final SlimefunEmiCategory slimefunEmiCategory;
            if (CATEGORIES.containsKey(workstationId)) {
                slimefunEmiCategory = CATEGORIES.get(workstationId);
            } else {
                slimefunEmiCategory = new SlimefunEmiCategory(categoryIdentifier, workStation);
                CATEGORIES.put(workstationId, slimefunEmiCategory);
                emiRegistry.addCategory(slimefunEmiCategory);
                emiRegistry.addWorkstation(slimefunEmiCategory, workStation);
            }
            
            for (SlimefunRecipe slimefunRecipe : recipeCategory.childRecipes()) {
                emiRegistry.addRecipe(new SlimefunEmiRecipe(recipeCategory, slimefunRecipe, slimefunEmiCategory));
            }
        }

        for (SlimefunItemStack slimefunItemStack : SlimefunItemGroup.sort(List.copyOf(SlimefunRegistry.getSLIMEFUN_ITEMS().values()))) {
            emiRegistry.addEmiStack(EmiStack.of(slimefunItemStack.itemStack()));
        }
    }

    public static void wrap(WidgetHolder holder, RecipeDisplayComponent component, SlimefunRecipe recipe, List<EmiIngredient> inputs, List<EmiStack> outputs) {
        if (component.type().equals("slot") || component.type().equals("large_slot")) {
            boolean large = component.type().equals("large_slot");
            List<? extends EmiIngredient> ingredients = component.output() ? outputs : inputs;
            EmiIngredient ingredient = EmiStack.EMPTY;
            if (component.index() <= -1) {
                ingredient = EmiStack.of(recipe.parent().itemStack());
            } else if (component.index() > 0 && component.index() <= ingredients.size()) {
                ingredient = ingredients.get(component.index() - 1);
            }
            holder.addSlot(ingredient, component.x(), component.y()).large(large).drawBack(false);
        }
        holder.add(new DrawableWidget(component.x(), component.y(), component.width(), component.height(), (context, mouseX, mouseY, delta) ->
                component.getType().draw(recipe, DrawMode.LIGHT, context, component.x(), component.y(), mouseX, mouseY, component.tooltip(DrawMode.LIGHT, recipe))));
    }
}
