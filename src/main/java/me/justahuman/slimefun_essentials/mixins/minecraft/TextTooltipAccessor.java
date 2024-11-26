package me.justahuman.slimefun_essentials.mixins.minecraft;

import net.minecraft.client.gui.tooltip.OrderedTextTooltipComponent;
import net.minecraft.text.OrderedText;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(OrderedTextTooltipComponent.class)
public interface TextTooltipAccessor {
    @Accessor OrderedText getText();
}
