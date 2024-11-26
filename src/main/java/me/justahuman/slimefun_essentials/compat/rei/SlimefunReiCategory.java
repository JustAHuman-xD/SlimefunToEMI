package me.justahuman.slimefun_essentials.compat.rei;

import me.justahuman.slimefun_essentials.client.RecipeCategory;
import me.justahuman.slimefun_essentials.client.RecipeDisplayComponent;
import me.justahuman.slimefun_essentials.client.SlimefunRecipe;
import me.justahuman.slimefun_essentials.utils.TextureUtils;
import me.justahuman.slimefun_essentials.utils.Utils;
import me.shedaniel.math.Rectangle;
import me.shedaniel.rei.api.client.gui.Renderer;
import me.shedaniel.rei.api.client.gui.widgets.Widget;
import me.shedaniel.rei.api.client.gui.widgets.Widgets;
import me.shedaniel.rei.api.client.registry.display.DisplayCategory;
import me.shedaniel.rei.api.common.category.CategoryIdentifier;
import me.shedaniel.rei.api.common.util.EntryStacks;
import net.minecraft.item.ItemStack;
import net.minecraft.text.Text;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

public class SlimefunReiCategory implements DisplayCategory<SlimefunDisplay> {
    protected final RecipeCategory category;
    protected final ItemStack icon;
    protected final int displayHeight;

    public SlimefunReiCategory(RecipeCategory category, ItemStack icon) {
        this.category = category;
        this.icon = icon;
        int height = 0;
        for (SlimefunRecipe recipe : category.childRecipes()) {
            height = Math.max(height, category.display().height(recipe));
        }
        this.displayHeight = height + TextureUtils.REI_PADDING;
    }

    @Override
    public @NotNull CategoryIdentifier<SlimefunDisplay> getCategoryIdentifier() {
        return CategoryIdentifier.of(Utils.id(this.category.id().toLowerCase()));
    }

    @NotNull
    public Text getTitle() {
        return this.icon.getName();
    }

    @NotNull
    public Renderer getIcon() {
        return EntryStacks.of(this.icon);
    }

    @Override
    public int getDisplayHeight() {
        return this.displayHeight;
    }

    @Override
    public int getDisplayWidth(SlimefunDisplay display) {
        return this.category.display().width(display.slimefunRecipe()) + TextureUtils.REI_PADDING;
    }

    @Override
    public List<Widget> setupDisplay(SlimefunDisplay display, Rectangle bounds) {
        final int xOffset = bounds.getMinX();
        final int yOffset = bounds.getMinY() + ((getDisplayHeight() - this.category.display().height(display.slimefunRecipe)) / 2);
        List<Widget> widgets = new ArrayList<>();
        widgets.add(Widgets.createCategoryBase(bounds));
        for (RecipeDisplayComponent component : this.category.display().components(display.slimefunRecipe())) {
            ReiIntegration.wrap(widgets, component, display.slimefunRecipe(), display.inputs, display.outputs, xOffset, yOffset);
        }
        return widgets;
    }
}
