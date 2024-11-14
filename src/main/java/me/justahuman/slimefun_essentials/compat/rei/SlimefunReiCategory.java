package me.justahuman.slimefun_essentials.compat.rei;

import me.justahuman.slimefun_essentials.api.OffsetBuilder;
import me.justahuman.slimefun_essentials.api.SimpleRecipeRenderer;
import me.justahuman.slimefun_essentials.client.DrawMode;
import me.justahuman.slimefun_essentials.client.SlimefunRecipeCategory;
import me.justahuman.slimefun_essentials.compat.rei.displays.SlimefunDisplay;
import me.justahuman.slimefun_essentials.utils.TextureUtils;
import me.justahuman.slimefun_essentials.utils.Utils;
import me.shedaniel.math.Rectangle;
import me.shedaniel.rei.api.client.REIRuntime;
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

public class SlimefunReiCategory<T extends SlimefunDisplay> extends SimpleRecipeRenderer implements DisplayCategory<T> {
    protected final SlimefunRecipeCategory slimefunRecipeCategory;
    protected final ItemStack icon;

    public SlimefunReiCategory(SlimefunRecipeCategory slimefunRecipeCategory, ItemStack icon) {
        super(Type.from(slimefunRecipeCategory.type()));

        this.slimefunRecipeCategory = slimefunRecipeCategory;
        this.icon = icon;
    }

    @Override
    public DrawMode getDrawMode() {
        return REIRuntime.getInstance().isDarkThemeEnabled() ? DrawMode.DARK : DrawMode.LIGHT;
    }

    @Override
    @NotNull
    public CategoryIdentifier<T> getCategoryIdentifier() {
        return CategoryIdentifier.of(Utils.id(this.slimefunRecipeCategory.id().toLowerCase()));
    }

    @NotNull
    public Text getTitle() {
        return Text.translatable("slimefun_essentials.recipes.category.slimefun", this.icon.getName());
    }

    @NotNull
    public Renderer getIcon() {
        return EntryStacks.of(this.icon);
    }

    @Override
    public int getDisplayHeight() {
        return getDisplayHeight(this.slimefunRecipeCategory) + TextureUtils.REI_PADDING;
    }

    @Override
    public int getDisplayWidth(T display) {
        return getDisplayWidth(display.slimefunRecipe()) + TextureUtils.REI_PADDING;
    }

    @Override
    public List<Widget> setupDisplay(T display, Rectangle bounds) {
        final int x = bounds.getMinX() + ((getDisplayWidth(display) - getContentsWidth(display.slimefunRecipe())) / 2);
        final int y = bounds.getMinY() + ((getDisplayHeight() - getDisplayHeight(display.slimefunRecipe())) / 2);
        return display.setupDisplay(new OffsetBuilder(display, display.slimefunRecipe(), x, y, y), new ArrayList<>(List.of(Widgets.createCategoryBase(bounds))), bounds, this.icon);
    }
}
