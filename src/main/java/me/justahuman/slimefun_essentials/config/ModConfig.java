package me.justahuman.slimefun_essentials.config;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import lombok.Setter;
import me.justahuman.slimefun_essentials.SlimefunEssentials;
import me.justahuman.slimefun_essentials.utils.JsonUtils;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.client.MinecraftClient;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

public class ModConfig {
    private static final Gson gson = new Gson().newBuilder().setPrettyPrinting().create();

    private static @Setter boolean blockFeatures = true;
    private static @Setter boolean recipeFeatures = true;
    private static @Setter boolean replaceItemIdentifiers = true;
    private static @Setter boolean hideBackgroundTooltips = true;
    
    public static void loadConfig() {
        final JsonObject root = new JsonObject();
        try (final FileReader reader = new FileReader(getConfigFile())) {
            if (JsonParser.parseReader(reader) instanceof JsonObject jsonObject) {
                jsonObject.entrySet().forEach(entry -> root.add(entry.getKey(), entry.getValue()));
            }
        } catch (Exception e) {
            SlimefunEssentials.LOGGER.error("Error occurred while reading Config!", e);
        }

        loadConfigOption(() -> blockFeatures = JsonUtils.get(root, "block_features", true, true));
        loadConfigOption(() -> recipeFeatures = JsonUtils.get(root, "recipe_features", true, true));
        loadConfigOption(() -> replaceItemIdentifiers = JsonUtils.get(root, "replace_item_identifiers", true, true));
        loadConfigOption(() -> hideBackgroundTooltips = JsonUtils.get(root, "hide_background_tooltips", true, true));
    }

    private static void loadConfigOption(Runnable runnable) {
        try {
            runnable.run();
        } catch (Exception e) {
            SlimefunEssentials.LOGGER.error("Error occurred while loading Config!", e);
        }
    }
    
    public static void saveConfig() {
        MinecraftClient.getInstance().reloadResources();

        final JsonObject root = new JsonObject();
        root.addProperty("block_features", blockFeatures);
        root.addProperty("recipe_features", recipeFeatures);
        root.addProperty("replace_item_identifiers", replaceItemIdentifiers);
        root.addProperty("hide_background_tooltips", hideBackgroundTooltips);

        try (final FileWriter fileWriter = new FileWriter(getConfigFile())) {
            gson.toJson(root, fileWriter);
            fileWriter.flush();
        } catch (IOException e) {
            SlimefunEssentials.LOGGER.error("Error occurred while saving Config!", e);
        }
    }
    
    public static boolean blockFeatures() {
        return blockFeatures;
    }
    public static boolean recipeFeatures() {
        return recipeFeatures;
    }
    public static boolean replaceItemIdentifiers() {
        return replaceItemIdentifiers;
    }
    public static boolean hideBackgroundTooltips() {
        return hideBackgroundTooltips;
    }

    public static File getConfigFile() {
        final File configFile = FabricLoader.getInstance().getConfigDir().resolve("slimefun_essentials.json").toFile();
        if (!configFile.exists()) {
            try {
                configFile.getParentFile().mkdirs();
                if (!configFile.createNewFile()) {
                    throw new IOException();
                }
            } catch(IOException | SecurityException e) {
                SlimefunEssentials.LOGGER.error("Error occurred creating Config file!", e);
            }
        }
        return configFile;
    }
}