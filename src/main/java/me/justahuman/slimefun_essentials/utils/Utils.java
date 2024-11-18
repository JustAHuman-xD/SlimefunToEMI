package me.justahuman.slimefun_essentials.utils;

import me.justahuman.slimefun_essentials.SlimefunEssentials;
import me.justahuman.slimefun_essentials.client.SlimefunRegistry;
import net.minecraft.component.ComponentChanges;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.component.type.NbtComponent;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.util.Identifier;
import org.jetbrains.annotations.Nullable;

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
}
