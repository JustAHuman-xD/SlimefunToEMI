package me.justahuman.slimefun_essentials.compat.jei;

import me.justahuman.slimefun_essentials.utils.Utils;
import mezz.jei.api.ingredients.subtypes.ISubtypeInterpreter;
import mezz.jei.api.ingredients.subtypes.UidContext;
import net.minecraft.item.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class SlimefunIdInterpreter implements ISubtypeInterpreter<ItemStack> {
    private final ISubtypeInterpreter<ItemStack> defaultInterpreter;

    public SlimefunIdInterpreter(ISubtypeInterpreter<ItemStack> defaultInterpreter) {
        this.defaultInterpreter = defaultInterpreter;
    }

    @Override
    public @Nullable Object getSubtypeData(ItemStack ingredient, UidContext context) {
        final String sfId = Utils.getSlimefunId(ingredient);
        if (sfId == null) {
            return this.defaultInterpreter == null ? null : this.defaultInterpreter.getSubtypeData(ingredient, context);
        }
        return sfId;
    }

    @Override
    public @NotNull String getLegacyStringSubtypeInfo(ItemStack ingredient, UidContext context) {
        final String sfId = Utils.getSlimefunId(ingredient);
        if (sfId == null) {
            return this.defaultInterpreter == null ? "" : this.defaultInterpreter.getLegacyStringSubtypeInfo(ingredient, context);
        }
        return sfId;
    }
}
