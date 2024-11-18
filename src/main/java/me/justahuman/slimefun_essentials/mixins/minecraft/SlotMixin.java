package me.justahuman.slimefun_essentials.mixins.minecraft;

import me.justahuman.slimefun_essentials.config.ModConfig;
import me.justahuman.slimefun_essentials.utils.Utils;
import net.minecraft.item.ItemStack;
import net.minecraft.screen.slot.Slot;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(Slot.class)
public abstract class SlotMixin {
    @Shadow public abstract ItemStack getStack();

    @Inject(at = @At("HEAD"), method = "canBeHighlighted", cancellable = true)
    public void canBeHighlighted(CallbackInfoReturnable<Boolean> cir) {
        if (!ModConfig.hideBackgroundTooltips()) {
            return;
        }

        final ItemStack itemStack = getStack();
        final String id = Utils.getSlimefunId(itemStack);
        if (id != null && Utils.HIDDEN_SF_IDS.contains(id)) {
            cir.setReturnValue(false);
        }
    }
}
