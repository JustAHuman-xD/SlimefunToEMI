package me.justahuman.slimefun_essentials.compat.emi;

import dev.emi.emi.api.recipe.EmiRecipeCategory;
import dev.emi.emi.api.stack.EmiStack;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;

public class SlimefunEmiCategory extends EmiRecipeCategory {
    private final Text displayName;
    
    public SlimefunEmiCategory(Identifier id, EmiStack workstation) {
        super(id, workstation);

        this.displayName = workstation.getItemStack().getName();
    }

    @Override
    public Text getName() {
        return Text.translatable("slimefun_essentials.recipes.category.slimefun", this.displayName);
    }
}
