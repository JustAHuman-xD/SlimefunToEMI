package me.justahuman.slimefun_essentials.compat.jei;

import me.justahuman.slimefun_essentials.SlimefunEssentials;
import me.justahuman.slimefun_essentials.client.DrawMode;
import me.justahuman.slimefun_essentials.client.RecipeCategory;
import me.justahuman.slimefun_essentials.client.RecipeDisplayComponent;
import me.justahuman.slimefun_essentials.client.SlimefunRecipe;
import me.justahuman.slimefun_essentials.utils.TextureUtils;
import mezz.jei.api.constants.VanillaTypes;
import mezz.jei.api.gui.builder.IRecipeLayoutBuilder;
import mezz.jei.api.gui.drawable.IDrawable;
import mezz.jei.api.gui.drawable.IDrawableBuilder;
import mezz.jei.api.gui.ingredient.IRecipeSlotsView;
import mezz.jei.api.helpers.IGuiHelper;
import mezz.jei.api.recipe.IFocusGroup;
import mezz.jei.api.recipe.RecipeType;
import mezz.jei.api.recipe.category.IRecipeCategory;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.text.Text;
import org.jetbrains.annotations.NotNull;

public class SlimefunJeiCategory implements IRecipeCategory<SlimefunRecipe> {
    protected final IGuiHelper guiHelper;
    protected final RecipeCategory recipeCategory;
    protected IDrawable icon;
    protected final IDrawable background;

    public SlimefunJeiCategory(IGuiHelper guiHelper, RecipeCategory recipeCategory) {
        this.guiHelper = guiHelper;
        this.recipeCategory = recipeCategory;
        this.icon = guiHelper.createDrawableIngredient(VanillaTypes.ITEM_STACK, this.recipeCategory.itemStack());
        IDrawableBuilder builder = guiHelper.drawableBuilder(TextureUtils.WIDGETS, 0, 0, 0, 0);
        int width = 0;
        int height = 0;
        for (SlimefunRecipe recipe : recipeCategory.childRecipes()) {
            width = Math.max(width, recipeCategory.display().width(recipe));
            height = Math.max(height, recipeCategory.display().height(recipe));
        }
        this.background = builder.addPadding(height / 2, height / 2, width / 2, width / 2).build();
    }

    @Override
    public @NotNull RecipeType<SlimefunRecipe> getRecipeType() {
        return RecipeType.create(SlimefunEssentials.MOD_ID, this.recipeCategory.id().toLowerCase(), SlimefunRecipe.class);
    }

    @Override
    public @NotNull Text getTitle() {
        return this.recipeCategory.itemStack().getName();
    }

    @Override
    public @NotNull IDrawable getBackground() {
        return this.background;
    }

    @Override
    public @NotNull IDrawable getIcon() {
        return this.icon;
    }

    public void updateIcon() {
        this.icon = this.guiHelper.createDrawableIngredient(VanillaTypes.ITEM_STACK, this.recipeCategory.itemStack());;
    }

    @Override
    public void setRecipe(IRecipeLayoutBuilder builder, SlimefunRecipe recipe, IFocusGroup focuses) {
        int xOffset = (this.background.getWidth() - recipeCategory.display().width(recipe)) / 2;
        int yOffset = (this.background.getHeight() - recipeCategory.display().height(recipe)) / 2;

        for (RecipeDisplayComponent component : recipeCategory.display().components(recipe)) {
            if (component.type().equals("slot") || component.type().equals("large_slot")) {
                int index = component.index();
                int offset = component.type().equals("large_slot") ? 5 : 1;
                int x = component.x() + offset + xOffset;
                int y = component.y() + offset + yOffset;

                if (index <= -1) {
                    JeiIntegration.RECIPE_INTERPRETER.addIngredient(builder.addInputSlot(x, y), this.recipeCategory.itemStack());
                } else if (!component.output() && index > 0 && index <= recipe.inputs().size()) {
                    JeiIntegration.RECIPE_INTERPRETER.addIngredients(builder.addInputSlot(x, y), recipe.inputs().get(--index));
                } else if (component.output() && index > 0 && index <= recipe.outputs().size()) {
                    JeiIntegration.RECIPE_INTERPRETER.addIngredients(builder.addOutputSlot(x, y), recipe.outputs().get(--index));
                }
            }
        }
    }

    @Override
    public void draw(SlimefunRecipe recipe, IRecipeSlotsView recipeSlotsView, DrawContext graphics, double mouseX, double mouseY) {
        int xOffset = (this.background.getWidth() - recipeCategory.display().width(recipe)) / 2;
        int yOffset = (this.background.getHeight() - recipeCategory.display().height(recipe)) / 2;

        for (RecipeDisplayComponent component : recipeCategory.display().components(recipe)) {
            component.draw(recipe, DrawMode.LIGHT, graphics, component.x() + xOffset, component.y() + yOffset);
        }

        for (RecipeDisplayComponent component : recipeCategory.display().components(recipe)) {
            int x = component.x() + xOffset;
            int y = component.y() + yOffset;
            int width = component.width();
            int height = component.height();
            if (x >= mouseX && mouseX < x + width && y >= mouseY && mouseY < y + height) {
                component.drawTooltip(graphics, component.tooltip(DrawMode.LIGHT, recipe), x, y, (int) mouseX, (int) mouseY, width, height);
            }
        }
    }
}
