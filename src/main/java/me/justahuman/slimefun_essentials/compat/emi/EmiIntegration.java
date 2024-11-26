package me.justahuman.slimefun_essentials.compat.emi;

import dev.emi.emi.api.EmiPlugin;
import dev.emi.emi.api.EmiRegistry;
import dev.emi.emi.api.recipe.EmiRecipe;
import dev.emi.emi.api.stack.Comparison;
import dev.emi.emi.api.stack.EmiIngredient;
import dev.emi.emi.api.stack.EmiStack;
import dev.emi.emi.api.widget.Bounds;
import dev.emi.emi.api.widget.SlotWidget;
import dev.emi.emi.api.widget.Widget;
import dev.emi.emi.api.widget.WidgetHolder;
import me.justahuman.slimefun_essentials.api.DisplayComponentType;
import me.justahuman.slimefun_essentials.client.DrawMode;
import me.justahuman.slimefun_essentials.client.RecipeDisplayComponent;
import me.justahuman.slimefun_essentials.client.SlimefunRegistry;
import me.justahuman.slimefun_essentials.client.SlimefunItemGroup;
import me.justahuman.slimefun_essentials.client.SlimefunItemStack;
import me.justahuman.slimefun_essentials.client.RecipeCategory;
import me.justahuman.slimefun_essentials.client.SlimefunRecipe;
import me.justahuman.slimefun_essentials.utils.Payloads;
import me.justahuman.slimefun_essentials.utils.Utils;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.tooltip.TooltipComponent;
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
        if (!Payloads.metExpected()) {
            return;
        }

        for (SlimefunItemStack slimefunItemStack : SlimefunRegistry.getSlimefunItems().values()) {
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

        for (SlimefunItemStack slimefunItemStack : SlimefunItemGroup.sort(List.copyOf(SlimefunRegistry.getSlimefunItems().values()))) {
            emiRegistry.addEmiStack(EmiStack.of(slimefunItemStack.itemStack()));
        }
    }

    public static void wrap(EmiRecipe recipe, WidgetHolder holder, RecipeDisplayComponent component, SlimefunRecipe slimefunRecipe, List<EmiIngredient> inputs, List<EmiStack> outputs) {
        int x = component.x();
        int y = component.y();

        holder.add(new Widget() {
            private final SlimefunRecipe recipe = slimefunRecipe;
            private final DisplayComponentType type = component.getType();
            private final Bounds bounds = new Bounds(x, y, type.width(), type.height());
            private final List<TooltipComponent> tooltip = component.tooltip(DrawMode.LIGHT, recipe);

            @Override
            public Bounds getBounds() {
                return bounds;
            }

            @Override
            public void render(DrawContext draw, int mouseX, int mouseY, float delta) {
                type.draw(recipe, DrawMode.LIGHT, draw, bounds.x(), bounds.y(), mouseX, mouseY, tooltip);
            }
        });

        if (component.type().equals("slot") || component.type().equals("large_slot")) {
            int index = component.index();
            boolean large = component.type().equals("large_slot");
            if (index <= -1) {
                holder.addSlot(EmiStack.of(slimefunRecipe.parent().itemStack()), x, y).large(large).drawBack(false);
            } else if (!component.output() && index > 0 && index <= inputs.size()) {
                holder.addSlot(inputs.get(--index), x, y).large(large).drawBack(false);
            } else if (component.output() && index > 0 && index <= outputs.size()) {
                holder.addSlot(outputs.get(--index), x, y).recipeContext(recipe).large(large).drawBack(false);
            }
        }
    }
}
