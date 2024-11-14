package me.justahuman.slimefun_essentials.config;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.JsonPrimitive;
import lombok.Getter;
import lombok.Setter;
import me.justahuman.slimefun_essentials.SlimefunEssentials;
import me.justahuman.slimefun_essentials.utils.JsonUtils;
import me.justahuman.slimefun_essentials.utils.Utils;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ServerInfo;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class ModConfig {
    private static final Gson gson = new Gson().newBuilder().setPrettyPrinting().create();
    private static final JsonArray defaultAddons = new JsonArray();
    static {
        defaultAddons.add("Slimefun");
        defaultAddons.add("InfinityExpansion");
    }

    private static @Setter boolean blockFeatures = true;
    private static @Setter boolean customGuide = true;
    private static @Setter boolean recipeFeatures = true;
    private static @Setter @Getter List<String> addons = new ArrayList<>();

    private static @Setter boolean requireServerConnection = false;
    private static @Setter boolean enableServerWhitelist = false;
    private static @Setter @Getter List<String> serverWhitelist = new ArrayList<>();

    private static @Setter boolean autoToggleAddons = true;
    private static @Setter boolean autoManageItems = true;

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

        loadConfigOption(() -> blockFeatures = JsonUtils.getBool(root, "block_features", true, true));
        //loadConfigOption(() -> customGuide = JsonUtils.getBool(root, "custom_guide", true, true));
        loadConfigOption(() -> recipeFeatures = JsonUtils.getBool(root, "recipe_features", true, true));
        loadConfigOption(() -> {
            for (JsonElement addon : JsonUtils.getArray(root, "addons", defaultAddons, true)) {
                if (addon instanceof JsonPrimitive jsonPrimitive && jsonPrimitive.isString()) {
                    addons.add(jsonPrimitive.getAsString());
                }
            }
        });

        loadConfigOption(() -> requireServerConnection = JsonUtils.getBool(root, "require_server_connection", true, true));
        loadConfigOption(() -> enableServerWhitelist = JsonUtils.getBool(root, "enable_server_whitelist", false, true));
        loadConfigOption(() -> {
            for (JsonElement server : JsonUtils.getArray(root, "enabled_servers", new JsonArray(), true)) {
                if (server instanceof JsonPrimitive jsonPrimitive && jsonPrimitive.isString()) {
                    serverWhitelist.add(jsonPrimitive.getAsString());
                }
            }
        });

        loadConfigOption(() -> autoToggleAddons = JsonUtils.getBool(root, "auto_toggle_addons", true, true));
        loadConfigOption(() -> autoManageItems = JsonUtils.getBool(root, "auto_manage_items", true, true));

        loadConfigOption(() -> replaceItemIdentifiers = JsonUtils.getBool(root, "replace_item_identifiers", true, true));
        loadConfigOption(() -> hideBackgroundTooltips = JsonUtils.getBool(root, "hide_background_tooltips", true, true));
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
        final JsonArray addonArray = new JsonArray();
        for (String addon : addons) {
            addonArray.add(addon);
        }

        final JsonArray serverArray = new JsonArray();
        for (String server : serverWhitelist) {
            serverArray.add(server);
        }

        root.addProperty("block_features", blockFeatures);
        //root.addProperty("custom_guide", customGuide);
        root.addProperty("recipe_features", recipeFeatures);
        root.add("addons", addonArray);

        root.addProperty("require_server_connection", requireServerConnection);
        root.addProperty("enable_server_whitelist", enableServerWhitelist);
        root.add("enabled_servers", serverArray);

        root.addProperty("auto_toggle_addons", autoToggleAddons);
        root.addProperty("auto_manage_items", autoManageItems);

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

    public static boolean customGuide() {
        return customGuide;
    }

    public static boolean recipeFeatures() {
        return recipeFeatures;
    }

    public static boolean requireServerConnection() {
        return requireServerConnection;
    }

    public static boolean enableServerWhitelist() {
        return enableServerWhitelist;
    }

    public static boolean autoToggleAddons() {
        return autoToggleAddons;
    }

    public static boolean autoManageItems() {
        return autoManageItems;
    }

    public static boolean replaceItemIdentifiers() {
        return replaceItemIdentifiers;
    }

    public static boolean hideBackgroundTooltips() {
        return hideBackgroundTooltips;
    }

    public static boolean isCurrentServerEnabled() {
        final MinecraftClient client = MinecraftClient.getInstance();
        if (client == null) {
            return false;
        }

        final ServerInfo serverInfo = client.getCurrentServerEntry();
        return serverInfo != null && serverWhitelist.contains(serverInfo.name);
    }

    public static boolean isServerEnabled(String server) {
        return serverWhitelist.contains(server);
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