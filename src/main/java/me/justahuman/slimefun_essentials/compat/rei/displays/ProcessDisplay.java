package me.justahuman.slimefun_essentials.compat.rei.displays;

import me.justahuman.slimefun_essentials.api.OffsetBuilder;
import me.justahuman.slimefun_essentials.client.DrawMode;
import me.justahuman.slimefun_essentials.client.SlimefunRecipeCategory;
import me.justahuman.slimefun_essentials.client.SlimefunLabel;
import me.justahuman.slimefun_essentials.client.SlimefunRecipe;
import me.justahuman.slimefun_essentials.compat.rei.ReiIntegration;
import me.justahuman.slimefun_essentials.utils.TextureUtils;
import me.justahuman.slimefun_essentials.utils.Utils;
import me.shedaniel.math.Point;
import me.shedaniel.math.Rectangle;
import me.shedaniel.rei.api.client.REIRuntime;
import me.shedaniel.rei.api.client.gui.widgets.Slot;
import me.shedaniel.rei.api.client.gui.widgets.Widget;
import me.shedaniel.rei.api.client.gui.widgets.Widgets;
import me.shedaniel.rei.api.common.category.CategoryIdentifier;
import me.shedaniel.rei.api.common.entry.EntryIngredient;
import me.shedaniel.rei.api.common.entry.EntryStack;
import me.shedaniel.rei.api.common.util.EntryStacks;
import net.minecraft.item.ItemStack;
import net.minecraft.text.Text;

import java.util.List;

public class ProcessDisplay extends SlimefunDisplay {
    public ProcessDisplay(SlimefunRecipeCategory slimefunRecipeCategory, SlimefunRecipe slimefunRecipe) {
        super(slimefunRecipeCategory, slimefunRecipe);
    }

    @Override
    public List<EntryIngredient> getInputEntries() {
        return this.inputs;
    }
    
    @Override
    public List<EntryIngredient> getOutputEntries() {
        return this.outputs;
    }
    
    @Override
    public CategoryIdentifier<?> getCategoryIdentifier() {
        return CategoryIdentifier.of(Utils.id(this.slimefunRecipeCategory.id().toLowerCase()));
    }

    @Override
    public List<Widget> setupDisplay(OffsetBuilder offsets, List<Widget> widgets, Rectangle bounds, ItemStack icon) {
        // Display Labels
        if (this.slimefunRecipe.hasLabels()) {
            for (SlimefunLabel slimefunLabel : this.slimefunRecipe.labels()) {
                widgets.add(ReiIntegration.widgetFromSlimefunLabel(slimefunLabel, offsets.getX(), offsets.label()));
                widgets.add(ReiIntegration.toolTipForSlimefunLabel(slimefunLabel, offsets.getX(), offsets.label()));
                offsets.x().addLabel();
            }
        }

        // Display Energy
        addEnergyWithCheck(widgets, offsets);

        // Display Inputs
        if (this.slimefunRecipe.hasInputs()) {
            getInputEntries().forEach(entryIngredient -> addSlot(widgets, offsets, entryIngredient));
        } else {
            addSlot(widgets, offsets, EntryStacks.of(icon));
        }
        offsets.x().addPadding();

        if (this.slimefunRecipe.hasEnergy() || this.slimefunRecipe.hasOutputs()) {
            // Display Arrow
            addArrow(widgets, offsets);

            // Display Outputs
            addOutputsOrEnergy(widgets, offsets);
        }

        return widgets;
    }

    protected void addEnergyWithCheck(List<Widget> widgets, OffsetBuilder offsets) {
        if (this.slimefunRecipe.hasEnergy() && this.slimefunRecipe.hasOutputs()) {
            addEnergy(widgets, offsets);
        }
    }

    protected void addEnergy(List<Widget> widgets, OffsetBuilder offsets) {
        addEnergy(widgets, offsets.getX(), offsets.energy());
        offsets.x().addEnergy();
    }

    protected void addEnergy(List<Widget> widgets, int x, int y) {
        widgets.add(ReiIntegration.widgetFromSlimefunLabel(TextureUtils.ENERGY, x, y));
        widgets.add(ReiIntegration.widgetFromSlimefunLabel((this.slimefunRecipe.totalEnergy() >= 0 ? TextureUtils.ENERGY_POSITIVE : TextureUtils.ENERGY_NEGATIVE), x, y, 1000, false, this.slimefunRecipe.totalEnergy() < 0, this.slimefunRecipe.totalEnergy() < 0));
        widgets.add(Widgets.createTooltip(new Rectangle(x, y, TextureUtils.ENERGY.width(getDrawMode()), TextureUtils.ENERGY.height(getDrawMode())), Text.translatable("slimefun_essentials.recipes.energy." + (this.slimefunRecipe.totalEnergy() >= 0 ? "generate" : "use"), TextureUtils.numberFormat.format(Math.abs(this.slimefunRecipe.totalEnergy())))));
    }

    protected void addSlot(List<Widget> widgets, OffsetBuilder offsets, EntryIngredient entryIngredient) {
        addSlot(widgets, entryIngredient, offsets.getX() + 1, offsets.slot() + 1);
        offsets.x().addSlot(false);
    }

    protected void addSlot(List<Widget> widgets, EntryIngredient entryIngredient, int x, int y) {
        addSlot(widgets, entryIngredient, x, y, true);
    }

    protected void addSlot(List<Widget> widgets, EntryIngredient entryIngredient, int x, int y, boolean background) {
        final Slot slot = Widgets.createSlot(new Point(x, y)).entries(entryIngredient).markInput();
        slot.setBackgroundEnabled(background);
        widgets.add(slot);
    }

    protected void addSlot(List<Widget> widgets, OffsetBuilder offsets, EntryStack<?> entryStack) {
        addSlot(widgets, entryStack, offsets.getX() + 1, offsets.slot() + 1);
        offsets.x().addSlot();
    }

    protected void addSlot(List<Widget> widgets, EntryStack<?> entryStack, int x, int y) {
        addSlot(widgets, entryStack, x, y, true);
    }

    protected void addSlot(List<Widget> widgets, EntryStack<?> entryStack, int x, int y, boolean background) {
        final Slot slot = Widgets.createSlot(new Point(x, y)).entry(entryStack).markInput();
        slot.setBackgroundEnabled(background);
        widgets.add(slot);
    }

    protected void addArrow(List<Widget> widgets, OffsetBuilder offsets) {
        addArrow(widgets, offsets.getX(), offsets.arrow(), false);
        offsets.x().addArrow();
    }

    protected void addArrow(List<Widget> widgets, int x, int y, boolean backwards) {
        widgets.add(ReiIntegration.widgetFromSlimefunLabel((backwards ? TextureUtils.BACKWARDS_ARROW : TextureUtils.ARROW), x, y));

        if (this.slimefunRecipe.hasTime()) {
            final int sfTicks = this.slimefunRecipe.sfTicks();
            final int millis =  sfTicks * 500;
            widgets.add(ReiIntegration.widgetFromSlimefunLabel((backwards ? TextureUtils.FILLED_BACKWARDS_ARROW : TextureUtils.FILLED_ARROW), x, y, millis, true, backwards, false));
            widgets.add(Widgets.createTooltip(new Rectangle(x, y, TextureUtils.ARROW.width(getDrawMode()), TextureUtils.ARROW.height(getDrawMode())), Text.translatable("slimefun_essentials.recipes.time", TextureUtils.numberFormat.format(sfTicks / 2), TextureUtils.numberFormat.format(sfTicks * 10L))));
        }
    }

    protected void addOutputsOrEnergy(List<Widget> widgets, OffsetBuilder offsets) {
        if (this.slimefunRecipe.hasOutputs()) {
            addOutputs(widgets, offsets);
        } else if (this.slimefunRecipe.hasEnergy()) {
            addEnergy(widgets, offsets);
        }
    }

    protected void addOutputs(List<Widget> widgets, OffsetBuilder offsets) {
        for (EntryIngredient entryIngredient : getOutputEntries()) {
            widgets.add(Widgets.createResultSlotBackground(new Point(offsets.getX() + 5, offsets.largeSlot() + 5)));
            widgets.add(Widgets.createSlot(new Point(offsets.getX() + 5, offsets.largeSlot() + 5)).entries(entryIngredient).disableBackground().markOutput());
            offsets.x().addLargeSlot(false);
        }
        offsets.x().addPadding();
    }

    @Override
    public DrawMode getDrawMode() {
        return REIRuntime.getInstance().isDarkThemeEnabled() ? DrawMode.DARK : DrawMode.LIGHT;
    }
}
