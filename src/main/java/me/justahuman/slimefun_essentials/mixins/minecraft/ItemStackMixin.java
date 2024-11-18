package me.justahuman.slimefun_essentials.mixins.minecraft;

import me.justahuman.slimefun_essentials.config.ModConfig;
import me.justahuman.slimefun_essentials.utils.Utils;
import net.minecraft.component.ComponentChanges;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.tooltip.TooltipType;
import net.minecraft.registry.Registries;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.Identifier;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.List;
import java.util.Locale;

@Mixin(value = ItemStack.class, priority = 100000)
public abstract class ItemStackMixin {
    @Shadow public abstract Item getItem();
    @Shadow public abstract ComponentChanges getComponentChanges();

    @Inject(method = "getTooltip", at = @At(value = "RETURN"))
    public void changeTooltip(Item.TooltipContext context, PlayerEntity player, TooltipType type, CallbackInfoReturnable<List<Text>> cir) {
        final String guideMode = Utils.getGuideMode(getComponentChanges());
        final String id = guideMode == null ? Utils.getSlimefunId(getComponentChanges()) : guideMode + "_guide";
        if (id == null) {
            return;
        }

        final List<Text> lore = cir.getReturnValue();
        if (ModConfig.hideBackgroundTooltips() && Utils.HIDDEN_SF_IDS.contains(id)) {
            lore.clear();
            return;
        }

        if (!ModConfig.replaceItemIdentifiers()) {
            return;
        }

        final Identifier identifier = Registries.ITEM.getId(getItem());
        final String idLine = identifier.toString();
        for (int i = 0; i < lore.size(); i++) {
            String line = lore.get(i).getString();
            if (line.equals(idLine)) {
                lore.set(i, Text.literal("slimefun:" + id.toLowerCase(Locale.ROOT)).formatted(Formatting.DARK_GRAY));
            } else if (line.equals("Minecraft")) {
                lore.set(i, Text.literal("Slimefun").formatted(Formatting.BLUE).formatted(Formatting.ITALIC));
            }
        }
    }
}
