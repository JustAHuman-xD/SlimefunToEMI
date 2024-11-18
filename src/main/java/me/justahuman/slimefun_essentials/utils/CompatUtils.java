package me.justahuman.slimefun_essentials.utils;

import net.fabricmc.loader.api.FabricLoader;

public class CompatUtils {
    public static boolean isModLoaded(String id) {
        return FabricLoader.getInstance().isModLoaded(id);
    }

    public static boolean isClothConfigLoaded() {
        return isModLoaded("cloth-config2");
    }

    public static boolean isRecipeModLoaded() {
        return isEmiLoaded() || isReiLoaded() || isJeiLoaded();
    }

    public static boolean isEmiLoaded() {
        return isModLoaded("emi");
    }

    public static boolean isReiLoaded() {
        return isModLoaded("roughlyenoughitems");
    }

    public static boolean isJeiLoaded() {
        return isModLoaded("jei");
    }

    public static boolean isJadeLoaded() {
        return isModLoaded("jade");
    }

    public static boolean isBlockFeatureModLoaded() {
        return isJadeLoaded();
    }
}
