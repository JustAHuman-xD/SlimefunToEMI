package me.justahuman.slimefun_essentials.utils;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;
import com.mojang.serialization.DynamicOps;
import me.justahuman.slimefun_essentials.SlimefunEssentials;
import net.minecraft.client.MinecraftClient;
import net.minecraft.component.ComponentChanges;
import net.minecraft.component.ComponentMapImpl;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtOps;
import net.minecraft.nbt.StringNbtReader;
import net.minecraft.registry.Registries;
import net.minecraft.util.Identifier;
import net.minecraft.util.JsonHelper;

public class JsonUtils {
    private static final Gson GSON = new Gson().newBuilder().setPrettyPrinting().create();

    public static JsonObject getObject(JsonObject parent, String key, JsonObject def) {
        return parent.get(key) instanceof JsonObject json ? json : def;
    }
    
    public static JsonArray getArray(JsonObject parent, String key, JsonArray def, boolean set) {
        final JsonArray result = getArray(parent, key, def);
        if (set) {
            parent.add(key, result);
        }
        return result;
    }
    
    public static JsonArray getArray(JsonObject parent, String key, JsonArray def) {
        final JsonElement value = parent.get(key);
        if (value instanceof JsonArray array) {
            return array;
        } else if (value == null) {
            return def;
        }

        final JsonArray array = new JsonArray();
        array.add(value);
        return array;
    }
    
    public static String getString(JsonObject parent, String key, String def) {
        return parent.get(key) instanceof JsonPrimitive primitive && primitive.isString() ? primitive.getAsString() : def;
    }
    
    public static Boolean getBool(JsonObject parent, String key, Boolean def, boolean set) {
        final Boolean result = getBool(parent, key, def);
        if (set) {
            parent.addProperty(key, result);
        }
        return result;
    }
    
    public static Boolean getBool(JsonObject parent, String key, Boolean def) {
        return parent.get(key) instanceof JsonPrimitive primitive && primitive.isBoolean() ? primitive.getAsBoolean() : def;
    }
    
    public static Long getLong(JsonObject parent, String key, Long def) {
        return parent.get(key) instanceof JsonPrimitive primitive && primitive.isNumber() ? primitive.getAsLong() : def;
    }
    
    public static Integer getInt(JsonObject parent, String key, Integer def) {
        return parent.get(key) instanceof JsonPrimitive primitive && primitive.isNumber() ? primitive.getAsInt() : def;
    }

    public static JsonObject toJson(String string) {
        return GSON.fromJson(string, JsonObject.class);
    }

    public static String serializeItem(ItemStack itemStack) {
        final JsonObject json = new JsonObject();
        final ComponentChanges changes = itemStack.getComponentChanges();
        json.addProperty("item", Registries.ITEM.getId(itemStack.getItem()).toString());
        json.addProperty("amount", itemStack.getCount());
        if (changes != ComponentChanges.EMPTY) {
            json.addProperty("components", ComponentChanges.CODEC.encodeStart(withRegistryAccess(NbtOps.INSTANCE), changes).getOrThrow().asString());
        }
        return json.toString();
    }

    public static ItemStack deserializeItem(String string) {
        return deserializeItem(toJson(string));
    }
    
    public static ItemStack deserializeItem(JsonObject json) {
        if (json == null || json.isEmpty() || !json.has("item")) {
            return ItemStack.EMPTY;
        }

        final ItemStack itemStack = new ItemStack(Registries.ITEM.get(Identifier.tryParse(json.get("item").getAsString())));
        itemStack.setCount(JsonHelper.getInt(json, "amount", 1));

        try {
            if (itemStack.getComponents() instanceof ComponentMapImpl components && json.get("components") instanceof JsonPrimitive primitive && primitive.isString()) {
                components.setChanges(ComponentChanges.CODEC.decode(withRegistryAccess(NbtOps.INSTANCE), StringNbtReader.parse(primitive.getAsString())).getOrThrow().getFirst());
            }
        } catch (Exception e) {
            SlimefunEssentials.LOGGER.error("Failed to deserialize item components", e);
        }

        return itemStack;
    }

    private static <T> DynamicOps<T> withRegistryAccess(DynamicOps<T> ops) {
        MinecraftClient instance = MinecraftClient.getInstance();
        if (instance == null || instance.world == null) {
            return ops;
        }
        return instance.world.getRegistryManager().getOps(ops);
    }
}
