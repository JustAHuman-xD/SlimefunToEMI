package me.justahuman.slimefun_essentials.utils;

import me.justahuman.slimefun_essentials.SlimefunEssentials;
import me.justahuman.slimefun_essentials.client.SlimefunRecipe;
import me.justahuman.slimefun_essentials.client.SlimefunRegistry;
import me.justahuman.slimefun_essentials.mixins.minecraft.TextTooltipAccessor;
import net.minecraft.client.gui.tooltip.TooltipComponent;
import net.minecraft.component.ComponentChanges;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.component.type.NbtComponent;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.text.OrderedText;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Optional;
import java.util.Set;

public class Utils {
    public static final Set<String> HIDDEN_SF_IDS = Set.of("_UI_BACKGROUND", "_UI_INPUT_SLOT", "_UI_OUTPUT_SLOT");

    public static Identifier id(String path) {
        return Identifier.of(SlimefunEssentials.MOD_ID, path.toLowerCase(Locale.ROOT));
    }

    public static boolean filterResources(Identifier identifier) {
        return identifier.getPath().endsWith(".json");
    }

    public static boolean filterVanillaItems(Identifier identifier) {
        if (!filterResources(identifier)) {
            return false;
        }

        final String path = identifier.getPath();
        final String item = getFileName(path);
        return SlimefunRegistry.getVanillaItems().contains(item);
    }

    public static String getFileName(String path) {
        return path.substring(path.lastIndexOf("/") + 1, path.indexOf(".json"));
    }

    public static NbtCompound getPluginNbt(@Nullable ItemStack itemStack) {
        return itemStack == null ? null : getPluginNbt(itemStack.getComponentChanges());
    }

    public static NbtCompound getPluginNbt(@Nullable ComponentChanges components) {
        if (components == null || components.isEmpty()) {
            return null;
        }
        Optional<? extends NbtComponent> customData = components.get(DataComponentTypes.CUSTOM_DATA);
        return customData != null ? customData.map(NbtComponent::getNbt).map(compound -> compound.getCompound("PublicBukkitValues")).orElse(null) : null;
    }

    public static String getSlimefunId(@Nullable ItemStack itemStack) {
        return itemStack == null ? null : getSlimefunId(itemStack.getComponentChanges());
    }

    public static String getSlimefunId(@Nullable ComponentChanges components) {
        final NbtCompound pluginNbt = getPluginNbt(components);
        if (pluginNbt == null || !pluginNbt.contains("slimefun:slimefun_item")) {
            return null;
        }
        return pluginNbt.getString("slimefun:slimefun_item");
    }

    public static String getGuideMode(@Nullable ItemStack itemStack) {
        return itemStack == null ? null : getGuideMode(itemStack.getComponentChanges());
    }

    public static String getGuideMode(@Nullable ComponentChanges components) {
        final NbtCompound pluginNbt = getPluginNbt(components);
        if (pluginNbt == null || !pluginNbt.contains("slimefun:slimefun_guide_mode")) {
            return null;
        }
        return pluginNbt.getString("slimefun:slimefun_guide_mode");
    }

    public static String fillPlaceholders(String string, SlimefunRecipe recipe) {
        return string.replace("%sf_ticks%", "%s".formatted(recipe.sfTicks()))
                .replace("%time_seconds%", "%s".formatted(recipe.seconds()))
                .replace("%time_ticks%", "%s".formatted(recipe.ticks()))
                .replace("%time_millis%", "%s".formatted(recipe.millis()))
                .replace("%energy%", "%s".formatted(recipe.energy()))
                .replace("%total_energy%", "%s".formatted(recipe.totalEnergy()))
                .replace("%abs_total_energy%", "%s".formatted(Math.abs(recipe.totalEnergy())))
                .replace("%inputs%", "%s".formatted(recipe.inputs().size()))
                .replace("%outputs%", "%s".formatted(recipe.outputs().size()));
    }

    public static List<TooltipComponent> updateTooltip(List<TooltipComponent> tooltip, SlimefunRecipe recipe) {
        if (tooltip.isEmpty()) {
            return tooltip;
        }

        List<String> strings = tooltip.stream().map(component -> from(((TextTooltipAccessor) component).getText())).map(string -> fillPlaceholders(string, recipe)).toList();
        List<TooltipComponent> newTooltip = new ArrayList<>();
        strings.forEach(string -> newTooltip.add(TooltipComponent.of(Text.literal(string).asOrderedText())));
        return newTooltip;
    }

    private static String from(OrderedText text) {
        final StringBuilder builder = new StringBuilder();
        text.accept((index, style, c) -> {
            builder.appendCodePoint(c);
            return true;
        });
        return builder.toString();
    }
}
