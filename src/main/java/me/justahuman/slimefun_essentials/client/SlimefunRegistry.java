package me.justahuman.slimefun_essentials.client;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;
import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;
import me.justahuman.slimefun_essentials.SlimefunEssentials;
import me.justahuman.slimefun_essentials.utils.Utils;
import net.minecraft.client.MinecraftClient;
import net.minecraft.item.ItemStack;
import net.minecraft.resource.Resource;
import net.minecraft.resource.ResourceManager;
import net.minecraft.resource.VanillaResourcePackProvider;
import net.minecraft.util.Identifier;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.Collections;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

public class SlimefunRegistry {
    private static final Gson GSON = new Gson().newBuilder().setPrettyPrinting().create();
    private static final Map<String, SlimefunItemStack> SLIMEFUN_ITEMS = new LinkedHashMap<>();
    private static final Set<String> VANILLA_ITEMS = new HashSet<>();
    @Setter @Getter private static int ticksPerSecond = 2;

    public static void reset() {
        SLIMEFUN_ITEMS.clear();
        VANILLA_ITEMS.clear();
        ticksPerSecond = 2;
    }

    public static JsonObject jsonObjectFromResource(Resource resource) {
        try {
            final InputStream inputStream = resource.getInputStream();
            return GSON.fromJson(new InputStreamReader(inputStream, StandardCharsets.UTF_8), JsonObject.class);
        } catch(IOException e) {
            SlimefunEssentials.LOGGER.error("Failed to load resource", e);
            return new JsonObject();
        }
    }

    public static void addItems(Map<String, ItemStack> items) {
        for (Map.Entry<String, ItemStack> entry : items.entrySet()) {
            SLIMEFUN_ITEMS.put(entry.getKey(), new SlimefunItemStack(entry.getKey(), entry.getValue()));
            VANILLA_ITEMS.add(entry.getValue().getItem().toString());
        }
    }

    public static void loadItemModels() {
        ResourceManager manager = MinecraftClient.getInstance().getResourceManager();
        loadCustomModels(manager, "item");
        loadCustomModels(manager, "block");
    }

    public static void loadCustomModels(ResourceManager manager, String directory) {
        for (Map.Entry<Identifier, Resource> entry : manager.findResources("models/" + directory, Utils::filterVanillaItems).entrySet()) {
            final Resource resource = entry.getValue();
            if (VanillaResourcePackProvider.VANILLA_KEY.equals(resource.getPackId())) {
                continue;
            }

            final JsonObject model = jsonObjectFromResource(resource);
            if (model != null && model.get("overrides") instanceof JsonArray overrides) {
                for (JsonElement element : overrides) {
                    if (element instanceof JsonObject override) {
                        loadCustomModel(override);
                    }
                }
            }
        }
    }

    public static void loadCustomModel(JsonObject override) {
        if (!(override.get("predicate") instanceof JsonObject predicate)
                || !(predicate.get("custom_model_data") instanceof JsonPrimitive modelData)
                || !modelData.isNumber()
                || !(override.get("model") instanceof JsonPrimitive model)
                || !model.isString()) {
            return;
        }

        final int customModelData = modelData.getAsInt();
        final String modelId = model.getAsString();
        final int idStart = modelId.lastIndexOf("/");
        final int idEnd = modelId.lastIndexOf(".");
        final String id = modelId.substring(idStart == -1 ? 0 : idStart + 1,
                idEnd == -1 ? modelId.length() : idEnd).toUpperCase(Locale.ROOT);

        if (SLIMEFUN_ITEMS.containsKey(id)) {
            SLIMEFUN_ITEMS.get(id.toUpperCase()).setCustomModelData(customModelData);
        }
    }

    public static SlimefunItemStack getSlimefunItem(String id) {
        return SLIMEFUN_ITEMS.get(id);
    }

    @NonNull
    public static Map<String, SlimefunItemStack> getSlimefunItems() {
        return Collections.unmodifiableMap(SLIMEFUN_ITEMS);
    }

    @NonNull
    public static Set<String> getVanillaItems() {
        return Collections.unmodifiableSet(VANILLA_ITEMS);
    }
}
