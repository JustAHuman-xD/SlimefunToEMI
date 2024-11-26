package me.justahuman.slimefun_essentials.compat.rei;

import me.justahuman.slimefun_essentials.client.DrawMode;
import me.justahuman.slimefun_essentials.client.RecipeCategory;
import me.justahuman.slimefun_essentials.client.RecipeDisplayComponent;
import me.justahuman.slimefun_essentials.client.SlimefunRecipe;
import me.justahuman.slimefun_essentials.utils.TextureUtils;
import me.justahuman.slimefun_essentials.utils.Utils;
import me.shedaniel.math.Point;
import me.shedaniel.math.Rectangle;
import me.shedaniel.rei.api.client.REIRuntime;
import me.shedaniel.rei.api.client.gui.Renderer;
import me.shedaniel.rei.api.client.gui.widgets.Slot;
import me.shedaniel.rei.api.client.gui.widgets.Widget;
import me.shedaniel.rei.api.client.gui.widgets.Widgets;
import me.shedaniel.rei.api.client.registry.display.DisplayCategory;
import me.shedaniel.rei.api.common.category.CategoryIdentifier;
import me.shedaniel.rei.api.common.util.EntryStacks;
import net.minecraft.client.gui.tooltip.TooltipComponent;
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
        final DrawMode mode = REIRuntime.getInstance().isDarkThemeEnabled() ? DrawMode.DARK : DrawMode.LIGHT;
        final SlimefunRecipe recipe = display.slimefunRecipe();
        List<Widget> widgets = new ArrayList<>();
        widgets.add(Widgets.createCategoryBase(bounds));

        for (RecipeDisplayComponent component : this.category.display().components(recipe)) {
            widgets.add(Widgets.createDrawableWidget((context, mouseX, mouseY, delta) ->
                    component.draw(recipe, mode, context, component.x() + xOffset, component.y() + yOffset)));

            if (component.type().equals("slot") || component.type().equals("large_slot")) {
                int index = component.index();
                int offset = component.type().equals("large_slot") ? 5 : 1;
                Slot slot = Widgets.createSlot(new Point(
                        component.x() + xOffset + offset,
                        component.y() + yOffset + offset
                )).disableBackground();

                if (index <= -1) {
                    slot.entry(EntryStacks.of(recipe.parent().itemStack())).markInput();
                } else if (!component.output() && index > 0 && index <= display.inputs.size()) {
                    slot.entries(display.inputs.get(--index)).markInput();
                } else if (component.output() && index > 0 && index <= display.outputs.size()) {
                    slot.entries(display.outputs.get(--index)).markOutput();
                }
                widgets.add(slot);
            }
        }

        for (RecipeDisplayComponent component : this.category.display().components(recipe)) {
            int x = component.x() + xOffset;
            int y = component.y() + yOffset;
            int width = component.width();
            int height = component.height();
            List<TooltipComponent> tooltip = component.tooltip(mode, recipe);
            widgets.add(Widgets.createDrawableWidget((context, mouseX, mouseY, delta) ->
                    component.drawTooltip(context, tooltip, x, y, mouseX, mouseY, width, height)));
        }

        return widgets;
    }
}
