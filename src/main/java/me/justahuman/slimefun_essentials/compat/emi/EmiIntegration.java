package me.justahuman.slimefun_essentials.compat.emi;

import dev.emi.emi.api.EmiPlugin;
import dev.emi.emi.api.EmiRegistry;
import dev.emi.emi.api.stack.Comparison;
import dev.emi.emi.api.stack.EmiStack;
import dev.emi.emi.runtime.EmiReloadManager;
import me.justahuman.slimefun_essentials.client.SlimefunRegistry;
import me.justahuman.slimefun_essentials.client.SlimefunItemStack;
import me.justahuman.slimefun_essentials.client.RecipeCategory;
import me.justahuman.slimefun_essentials.client.SlimefunRecipe;
import me.justahuman.slimefun_essentials.utils.Payloads;
import me.justahuman.slimefun_essentials.utils.Utils;
import net.minecraft.util.Identifier;

import java.util.HashMap;
import java.util.Map;

public class EmiIntegration implements EmiPlugin {
    public static final EmiIdInterpreter RECIPE_INTERPRETER = new EmiIdInterpreter();
    private static final Comparison SLIMEFUN_ID = Comparison.compareData(stack -> Utils.getSlimefunId(stack.getComponentChanges()));
    private static final Map<String, SlimefunEmiCategory> CATEGORIES = new HashMap<>();
    
    @Override
    public void register(EmiRegistry emiRegistry) {
        if (!Payloads.metExpected()) {
            Payloads.onMeetExpected(EmiReloadManager::reload);
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

        for (SlimefunItemStack slimefunItemStack : SlimefunRegistry.getSlimefunItems().values()) {
            emiRegistry.addEmiStack(EmiStack.of(slimefunItemStack.itemStack()));
        }
    }
}
