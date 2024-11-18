package me.justahuman.slimefun_essentials.compat.emi.recipes;

import dev.emi.emi.api.widget.WidgetHolder;
import me.justahuman.slimefun_essentials.api.OffsetBuilder;
import me.justahuman.slimefun_essentials.client.SlimefunRecipeCategory;
import me.justahuman.slimefun_essentials.client.SlimefunRecipe;
import me.justahuman.slimefun_essentials.compat.emi.EmiUtils;
import me.justahuman.slimefun_essentials.compat.emi.SlimefunEmiCategory;
import me.justahuman.slimefun_essentials.utils.TextureUtils;

public class GridRecipe extends ProcessRecipe {
    protected final int side;
    public GridRecipe(SlimefunRecipeCategory slimefunRecipeCategory, SlimefunRecipe slimefunRecipe, SlimefunEmiCategory emiRecipeCategory, int side) {
        super(Type.grid(side), slimefunRecipeCategory, slimefunRecipe, emiRecipeCategory);

        this.side = side;
        EmiUtils.fillInputs(this.inputs, this.side * this.side);
    }
    
    @Override
    public void addWidgets(WidgetHolder widgets) {
        final OffsetBuilder offsets = new OffsetBuilder(this, this.slimefunRecipe, calculateXOffset(this.slimefunRecipe), TextureUtils.PADDING);
    
        // Display Energy
        addEnergyWithCheck(widgets, offsets);

        int i = 0;
        for (int y = 1; y <= this.side; y++) {
            for (int x = 1; x <= this.side; x++) {
                widgets.addSlot(this.inputs.get(i), offsets.getX(), offsets.getY());
                offsets.x().addSlot(false);
                i++;
            }
            offsets.x().subtract(TextureUtils.SLOT.size(getDrawMode()) * this.side);
            offsets.y().addSlot(false);
        }
        offsets.x().add(TextureUtils.SLOT.size(getDrawMode()) * this.side).addPadding();
    
        // Display Arrow
        addArrowWithCheck(widgets, offsets);
    
        // Display Outputs
        addOutputsOrEnergy(widgets, offsets);
    }

    public int getSideLength() {
        return this.side;
    }
}
