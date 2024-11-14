package me.justahuman.slimefun_essentials.utils;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;
import me.justahuman.slimefun_essentials.SlimefunEssentials;
import net.minecraft.component.ComponentChanges;
import net.minecraft.component.ComponentMapImpl;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtElement;
import net.minecraft.nbt.NbtOps;
import net.minecraft.nbt.StringNbtReader;
import net.minecraft.registry.BuiltinRegistries;
import net.minecraft.registry.Registries;
import net.minecraft.registry.RegistryOps;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.util.Identifier;
import net.minecraft.util.JsonHelper;

public class JsonUtils {
    private static final RegistryWrapper.WrapperLookup LOOKUP = BuiltinRegistries.createWrapperLookup();
    private static final RegistryOps<NbtElement> NBT_OPS = LOOKUP.getOps(NbtOps.INSTANCE);
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

    public static String serializeItem(ItemStack itemStack) {
        final JsonObject json = new JsonObject();
        final ComponentChanges changes = itemStack.getComponentChanges();
        json.addProperty("item", Registries.ITEM.getId(itemStack.getItem()).toString());
        json.addProperty("amount", itemStack.getCount());
        if (changes != ComponentChanges.EMPTY) {
            json.addProperty("components", ComponentChanges.CODEC.encodeStart(NBT_OPS, changes).getOrThrow().asString());
        }
        return json.toString();
    }

    public static ItemStack deserializeItem(String string) {
        return deserializeItem(GSON.fromJson(string, JsonObject.class));
    }
    
    public static ItemStack deserializeItem(JsonObject json) {
        if (json == null || json.isEmpty() || !json.has("item")) {
            return ItemStack.EMPTY;
        }

        final ItemStack itemStack = new ItemStack(Registries.ITEM.get(Identifier.tryParse(json.get("item").getAsString())));
        itemStack.setCount(JsonHelper.getInt(json, "amount", 1));

        try {
            if (itemStack.getComponents() instanceof ComponentMapImpl components && json.get("components") instanceof JsonPrimitive primitive && primitive.isString()) {
                components.setChanges(ComponentChanges.CODEC.decode(NBT_OPS, StringNbtReader.parse(primitive.getAsString())).getOrThrow().getFirst());
            }
        } catch (Exception e) {
            SlimefunEssentials.LOGGER.error("Failed to deserialize item components", e);
        }
        
        return itemStack;
    }
}
