package me.justahuman.slimefun_essentials.compat.jei;

import me.justahuman.slimefun_essentials.SlimefunEssentials;
import me.justahuman.slimefun_essentials.client.ResourceLoader;
import me.justahuman.slimefun_essentials.client.SlimefunItemGroup;
import me.justahuman.slimefun_essentials.client.SlimefunItemStack;
import me.justahuman.slimefun_essentials.client.SlimefunRecipe;
import me.justahuman.slimefun_essentials.client.SlimefunRecipeCategory;
import me.justahuman.slimefun_essentials.compat.jei.categories.AncientAltarCategory;
import me.justahuman.slimefun_essentials.compat.jei.categories.GridCategory;
import me.justahuman.slimefun_essentials.compat.jei.categories.ProcessCategory;
import me.justahuman.slimefun_essentials.compat.jei.categories.ReactorCategory;
import me.justahuman.slimefun_essentials.compat.jei.categories.SmelteryCategory;
import me.justahuman.slimefun_essentials.mixins.jei.InterpretersAccessor;
import me.justahuman.slimefun_essentials.utils.TextureUtils;
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
import mezz.jei.api.registration.IRecipeTransferRegistration;
import mezz.jei.api.registration.IRuntimeRegistration;
import mezz.jei.api.registration.ISubtypeRegistration;
import mezz.jei.library.ingredients.subtypes.SubtypeInterpreters;
import mezz.jei.library.load.registration.SubtypeRegistration;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.screen.Generic3x3ContainerScreenHandler;
import net.minecraft.screen.ScreenHandlerType;
import net.minecraft.util.Identifier;
import org.jetbrains.annotations.NotNull;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

@JeiPlugin
public class JeiIntegration implements IModPlugin {
    private static final Set<ProcessCategory> CATEGORIES = new HashSet<>();
    public static final JeiRecipeInterpreter RECIPE_INTERPRETER = new JeiRecipeInterpreter();

    @Override
    @NotNull
    public Identifier getPluginUid() {
        return Utils.id("jei_integration");
    }

    @Override
    public void registerRuntime(IRuntimeRegistration registration) {
        if (!Utils.shouldFunction()) {
            return;
        }

        for (ProcessCategory category : CATEGORIES) {
            category.updateIcon();
        }

        registration.getIngredientManager().addIngredientsAtRuntime(VanillaTypes.ITEM_STACK,
                SlimefunItemGroup.sort(List.copyOf(ResourceLoader.getSlimefunItems().values())).stream().map(SlimefunItemStack::itemStack).toList());
    }

    @Override
    public void registerItemSubtypes(ISubtypeRegistration iregistration) {
        if (!Utils.shouldFunction() || !(iregistration instanceof SubtypeRegistration registration)) {
            return;
        }

        SubtypeInterpreters interpreters = registration.getInterpreters();
        Set<Item> wrappedItems = new HashSet<>();
        for (SlimefunItemStack slimefunItemStack : ResourceLoader.getSlimefunItems().values()) {
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
        if (!Utils.shouldFunction()) {
            return;
        }

        IJeiHelpers jeiHelpers = registration.getJeiHelpers();
        IGuiHelper guiHelper = jeiHelpers.getGuiHelper();
        for (SlimefunRecipeCategory recipeCategory : SlimefunRecipeCategory.getRecipeCategories().values()) {
            final ProcessCategory category = getJeiCategory(guiHelper, recipeCategory);
            registration.addRecipeCategories(category);
            CATEGORIES.add(category);
        }
    }

    @Override
    public void registerRecipes(IRecipeRegistration registration) {
        if (!Utils.shouldFunction()) {
            return;
        }

        for (SlimefunRecipeCategory recipeCategory : SlimefunRecipeCategory.getRecipeCategories().values()) {
            registration.addRecipes(RecipeType.create(SlimefunEssentials.MOD_ID, recipeCategory.id().toLowerCase(), SlimefunRecipe.class), recipeCategory.childRecipes());
        }
    }

    @Override
    public void registerRecipeCatalysts(IRecipeCatalystRegistration registration) {
        if (!Utils.shouldFunction()) {
            return;
        }

        for (SlimefunRecipeCategory recipeCategory : SlimefunRecipeCategory.getRecipeCategories().values()) {
            registration.addRecipeCatalyst(recipeCategory.itemStack(), RecipeType.create(SlimefunEssentials.MOD_ID, recipeCategory.id().toLowerCase(), SlimefunRecipe.class));
        }
    }

    @Override
    public void registerRecipeTransferHandlers(IRecipeTransferRegistration registration) {
        if (!Utils.shouldFunction()) {
            return;
        }

        for (SlimefunRecipeCategory recipeCategory : SlimefunRecipeCategory.getRecipeCategories().values()) {
            if (recipeCategory.type().contains("ancient_altar")) {
                return;
            }

            final RecipeType<SlimefunRecipe> recipeType = RecipeType.create(SlimefunEssentials.MOD_ID, recipeCategory.id().toLowerCase(), SlimefunRecipe.class);
            if (recipeCategory.type().contains("grid")) {
                registration.addRecipeTransferHandler(Generic3x3ContainerScreenHandler.class, ScreenHandlerType.GENERIC_3X3, recipeType, 0, 9, 9, 36);
            }
        }
    }

    public static ProcessCategory getJeiCategory(IGuiHelper guiHelper, SlimefunRecipeCategory recipeCategory) {
        final String type = recipeCategory.type();
        if (type.equals("ancient_altar")) {
            return new AncientAltarCategory(guiHelper, recipeCategory);
        } else if (type.equals("smeltery")) {
            return new SmelteryCategory(guiHelper, recipeCategory);
        } else if (type.equals("reactor")) {
            return new ReactorCategory(guiHelper, recipeCategory);
        } else if (type.contains("grid")) {
            return new GridCategory(guiHelper, recipeCategory, TextureUtils.getSideSafe(type));
        } else {
            return new ProcessCategory(guiHelper, recipeCategory);
        }
    }
}