package me.justahuman.slimefun_essentials.compat.emi.recipes;

import dev.emi.emi.api.widget.WidgetHolder;
import me.justahuman.slimefun_essentials.api.OffsetBuilder;
import me.justahuman.slimefun_essentials.client.SlimefunRecipeCategory;
import me.justahuman.slimefun_essentials.client.SlimefunRecipe;
import me.justahuman.slimefun_essentials.compat.emi.EmiUtils;
import me.justahuman.slimefun_essentials.compat.emi.SlimefunEmiCategory;
import me.justahuman.slimefun_essentials.utils.TextureUtils;

public class ReactorRecipe extends ProcessRecipe {
    public ReactorRecipe(SlimefunRecipeCategory slimefunRecipeCategory, SlimefunRecipe slimefunRecipe, SlimefunEmiCategory emiRecipeCategory) {
        super(Type.REACTOR, slimefunRecipeCategory, slimefunRecipe, emiRecipeCategory);

        EmiUtils.fillInputs(this.inputs, 4);
    }
    
    @Override
    public void addWidgets(WidgetHolder widgets) {
        final OffsetBuilder offsets = new OffsetBuilder(this, this.slimefunRecipe, TextureUtils.PADDING, TextureUtils.PADDING);

        widgets.addSlot(this.inputs.get(0), offsets.getX(), offsets.getY());
        offsets.y().addSlot(false);
        widgets.addSlot(offsets.getX(), offsets.getY());
        offsets.y().addSlot(false);
        widgets.addSlot(offsets.getX(), offsets.getY());
        offsets.x().addSlot();

        addArrowWithCheck(widgets, offsets.getX(), offsets.getY(), false);
        offsets.x().addArrow();

        if (this.slimefunRecipe.hasOutputs()) {
            widgets.addSlot(this.outputs.get(0), offsets.getX(), offsets.getY()).large(true);
        }

        if (this.slimefunRecipe.hasEnergy()) {
            addEnergy(widgets, offsets.getX() + (this.slimefunRecipe.hasOutputs() ? (TextureUtils.LARGE_SLOT.size() - TextureUtils.ENERGY.width()) / 2 : 0), offsets.getY() + (this.slimefunRecipe.hasOutputs() ? - TextureUtils.ENERGY.height() - TextureUtils.PADDING : TextureUtils.PADDING));
            offsets.x().add(this.slimefunRecipe.hasOutputs() ? TextureUtils.LARGE_SLOT.size() : TextureUtils.ENERGY.width()).addPadding();
        }

        addArrowWithCheck(widgets, offsets.getX(), offsets.getY(), true);
        offsets.x().addArrow();
        offsets.y().subtract(TextureUtils.SLOT.size() * 2);
        widgets.addSlot(inputs.get(1), offsets.getX(), offsets.getY());
        offsets.y().addSlot(false);
        widgets.addSlot(inputs.get(2), offsets.getX(), offsets.getY());
        offsets.y().addSlot(false);
        widgets.addSlot(inputs.get(3), offsets.getX(), offsets.getY());
    }
}
