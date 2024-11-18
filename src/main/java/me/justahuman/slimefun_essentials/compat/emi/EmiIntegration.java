package me.justahuman.slimefun_essentials.compat.emi;

import dev.emi.emi.api.EmiPlugin;
import dev.emi.emi.api.EmiRegistry;
import dev.emi.emi.api.recipe.EmiRecipe;
import dev.emi.emi.api.stack.Comparison;
import dev.emi.emi.api.stack.EmiStack;
import me.justahuman.slimefun_essentials.client.SlimefunRegistry;
import me.justahuman.slimefun_essentials.client.SlimefunItemGroup;
import me.justahuman.slimefun_essentials.client.SlimefunItemStack;
import me.justahuman.slimefun_essentials.client.SlimefunRecipeCategory;
import me.justahuman.slimefun_essentials.client.SlimefunRecipe;
import me.justahuman.slimefun_essentials.compat.emi.handler.GridHandler;
import me.justahuman.slimefun_essentials.compat.emi.recipes.AncientAltarRecipe;
import me.justahuman.slimefun_essentials.compat.emi.recipes.GridRecipe;
import me.justahuman.slimefun_essentials.compat.emi.recipes.ProcessRecipe;
import me.justahuman.slimefun_essentials.compat.emi.recipes.ReactorRecipe;
import me.justahuman.slimefun_essentials.compat.emi.recipes.SmelteryRecipe;
import me.justahuman.slimefun_essentials.utils.TextureUtils;
import me.justahuman.slimefun_essentials.utils.Utils;
import net.minecraft.screen.ScreenHandlerType;
import net.minecraft.util.Identifier;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class EmiIntegration implements EmiPlugin {
    public static final EmiIdInterpreter RECIPE_INTERPRETER = new EmiIdInterpreter();
    private static final Comparison SLIMEFUN_ID = Comparison.compareData(stack -> Utils.getSlimefunId(stack.getComponentChanges()));
    private static final Map<String, SlimefunEmiCategory> slimefunCategories = new HashMap<>();
    
    @Override
    public void register(EmiRegistry emiRegistry) {
        for (SlimefunItemStack slimefunItemStack : SlimefunRegistry.getSlimefunItems().values()) {
            emiRegistry.setDefaultComparison(EmiStack.of(slimefunItemStack.itemStack()), SLIMEFUN_ID);
        }
        slimefunCategories.clear();

        for (SlimefunRecipeCategory slimefunRecipeCategory : SlimefunRecipeCategory.getRecipeCategories().values()) {
            final String workstationId = slimefunRecipeCategory.id();
            final Identifier categoryIdentifier = Utils.id(workstationId);
            final EmiStack workStation = EmiStack.of(slimefunRecipeCategory.itemStack());
            final SlimefunEmiCategory slimefunEmiCategory;
            if (slimefunCategories.containsKey(workstationId)) {
                slimefunEmiCategory = slimefunCategories.get(workstationId);
            } else {
                slimefunEmiCategory = new SlimefunEmiCategory(categoryIdentifier, workStation);
                slimefunCategories.put(workstationId, slimefunEmiCategory);
                emiRegistry.addCategory(slimefunEmiCategory);
                emiRegistry.addWorkstation(slimefunEmiCategory, workStation);
            }
            
            for (SlimefunRecipe slimefunRecipe : slimefunRecipeCategory.childRecipes()) {
                emiRegistry.addRecipe(getEmiRecipe(slimefunRecipeCategory, slimefunRecipe, slimefunEmiCategory));
            }
        }

        for (SlimefunItemStack slimefunItemStack : SlimefunItemGroup.sort(List.copyOf(SlimefunRegistry.getSlimefunItems().values()))) {
            emiRegistry.addEmiStack(EmiStack.of(slimefunItemStack.itemStack()));
        }

        emiRegistry.addRecipeHandler(ScreenHandlerType.GENERIC_3X3, new GridHandler());
        // TODO: Support machine categories for all mods
        // emiRegistry.addRecipeHandler(ScreenHandlerType.GENERIC_9X1, new MachineHandler());
        // emiRegistry.addRecipeHandler(ScreenHandlerType.GENERIC_9X2, new MachineHandler());
        // emiRegistry.addRecipeHandler(ScreenHandlerType.GENERIC_9X3, new MachineHandler());
        // emiRegistry.addRecipeHandler(ScreenHandlerType.GENERIC_9X4, new MachineHandler());
        // emiRegistry.addRecipeHandler(ScreenHandlerType.GENERIC_9X5, new MachineHandler());
        // emiRegistry.addRecipeHandler(ScreenHandlerType.GENERIC_9X6, new MachineHandler());
    }

    public static EmiRecipe getEmiRecipe(SlimefunRecipeCategory slimefunRecipeCategory, SlimefunRecipe slimefunRecipe, SlimefunEmiCategory category) {
        final String type = slimefunRecipeCategory.type();
        if (type.equals("ancient_altar")) {
            return new AncientAltarRecipe(slimefunRecipeCategory, slimefunRecipe, category);
        } else if (type.equals("smeltery")) {
            return new SmelteryRecipe(slimefunRecipeCategory, slimefunRecipe, category);
        } else if (type.equals("reactor")) {
            return new ReactorRecipe(slimefunRecipeCategory, slimefunRecipe, category);
        } else if (type.contains("grid")) {
            return new GridRecipe(slimefunRecipeCategory, slimefunRecipe, category, TextureUtils.getSideSafe(type));
        } else {
            return new ProcessRecipe(slimefunRecipeCategory, slimefunRecipe, category);
        }
    }
}
