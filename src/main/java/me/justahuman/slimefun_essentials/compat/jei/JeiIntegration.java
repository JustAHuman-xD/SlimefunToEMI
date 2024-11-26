package me.justahuman.slimefun_essentials.compat.jei;

import me.justahuman.slimefun_essentials.SlimefunEssentials;
import me.justahuman.slimefun_essentials.client.SlimefunRegistry;
import me.justahuman.slimefun_essentials.client.SlimefunItemGroup;
import me.justahuman.slimefun_essentials.client.SlimefunItemStack;
import me.justahuman.slimefun_essentials.client.SlimefunRecipe;
import me.justahuman.slimefun_essentials.client.RecipeCategory;
import me.justahuman.slimefun_essentials.mixins.jei.InterpretersAccessor;
import me.justahuman.slimefun_essentials.utils.Utils;
import mezz.jei.api.IModPlugin;
import mezz.jei.api.JeiPlugin;
import mezz.jei.api.constants.VanillaTypes;
import mezz.jei.api.helpers.IGuiHelper;
import mezz.jei.api.helpers.IJeiHelpers;
import mezz.jei.api.ingredients.subtypes.ISubtypeInterpreter;
import mezz.jei.api.recipe.RecipeType;
import mezz.jei.api.registration.IRecipeCatalystRegistration;
import mezz.jei.api.registration.IRecipeCategoryRegistration;
import mezz.jei.api.registration.IRecipeRegistration;
import mezz.jei.api.registration.IRuntimeRegistration;
import mezz.jei.api.registration.ISubtypeRegistration;
import mezz.jei.library.ingredients.subtypes.SubtypeInterpreters;
import mezz.jei.library.load.registration.SubtypeRegistration;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Identifier;
import org.jetbrains.annotations.NotNull;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

@JeiPlugin
public class JeiIntegration implements IModPlugin {
    private static final Set<SlimefunJeiCategory> CATEGORIES = new HashSet<>();
    public static final JeiRecipeInterpreter RECIPE_INTERPRETER = new JeiRecipeInterpreter();

    @Override
    @NotNull
    public Identifier getPluginUid() {
        return Utils.id("jei_integration");
    }

    @Override
    public void registerRuntime(IRuntimeRegistration registration) {
        for (SlimefunJeiCategory category : CATEGORIES) {
            category.updateIcon();
        }

        registration.getIngredientManager().addIngredientsAtRuntime(VanillaTypes.ITEM_STACK,
                SlimefunItemGroup.sort(List.copyOf(SlimefunRegistry.getSLIMEFUN_ITEMS().values())).stream().map(SlimefunItemStack::itemStack).toList());
    }

    @Override
    public void registerItemSubtypes(ISubtypeRegistration iregistration) {
        if (!(iregistration instanceof SubtypeRegistration registration)) {
            return;
        }

        SubtypeInterpreters interpreters = registration.getInterpreters();
        Set<Item> wrappedItems = new HashSet<>();
        for (SlimefunItemStack slimefunItemStack : SlimefunRegistry.getSLIMEFUN_ITEMS().values()) {
            ItemStack itemStack = slimefunItemStack.itemStack();
            Item item = itemStack.getItem();
            if (!wrappedItems.add(item)) {
                continue;
            }

            ISubtypeInterpreter<ItemStack> oldInterpreter = interpreters.get(VanillaTypes.ITEM_STACK, itemStack);
            ISubtypeInterpreter<ItemStack> newInterpreter = new SlimefunIdInterpreter(oldInterpreter);
            ((InterpretersAccessor) interpreters).getMap().put(item, newInterpreter);
        }
    }

    @Override
    public void registerCategories(IRecipeCategoryRegistration registration) {
        IJeiHelpers jeiHelpers = registration.getJeiHelpers();
        IGuiHelper guiHelper = jeiHelpers.getGuiHelper();
        for (RecipeCategory recipeCategory : RecipeCategory.getRecipeCategories().values()) {
            SlimefunJeiCategory category = new SlimefunJeiCategory(guiHelper, recipeCategory);
            registration.addRecipeCategories(category);
            CATEGORIES.add(category);
        }
    }

    @Override
    public void registerRecipes(IRecipeRegistration registration) {
        for (RecipeCategory recipeCategory : RecipeCategory.getRecipeCategories().values()) {
            registration.addRecipes(RecipeType.create(SlimefunEssentials.MOD_ID, recipeCategory.id().toLowerCase(), SlimefunRecipe.class), recipeCategory.childRecipes());
        }
    }

    @Override
    public void registerRecipeCatalysts(IRecipeCatalystRegistration registration) {
        for (RecipeCategory recipeCategory : RecipeCategory.getRecipeCategories().values()) {
            registration.addRecipeCatalyst(recipeCategory.itemStack(), RecipeType.create(SlimefunEssentials.MOD_ID, recipeCategory.id().toLowerCase(), SlimefunRecipe.class));
        }
    }
}