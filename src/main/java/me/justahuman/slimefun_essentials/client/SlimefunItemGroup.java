package me.justahuman.slimefun_essentials.client;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;
import lombok.NonNull;
import me.justahuman.slimefun_essentials.SlimefunEssentials;
import me.justahuman.slimefun_essentials.utils.JsonUtils;
import me.justahuman.slimefun_essentials.utils.Utils;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Identifier;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public record SlimefunItemGroup(Identifier identifier, ItemStack itemStack, List<String> content, List<String> requirements) {
    private static final Map<String, SlimefunItemGroup> ITEM_GROUPS = new LinkedHashMap<>();
    private static final Map<String, SlimefunItemGroup> BY_CONTENT = new HashMap<>();
    private static final SlimefunItemGroup EMPTY = new SlimefunItemGroup(Utils.id("empty"), ItemStack.EMPTY, List.of(), List.of());

    public static void deserialize(String id, JsonObject groupObject) {
        final Identifier identifier = Identifier.tryParse(id);
        final ItemStack itemStack = JsonUtils.deserializeItem(JsonUtils.get(groupObject, "item", (JsonObject) null));
        final List<String> content = new ArrayList<>();
        final List<String> requirements = new ArrayList<>();

        for (JsonElement element : JsonUtils.get(groupObject, "items", new JsonArray())) {
            if (element instanceof JsonPrimitive primitive && primitive.isString()) {
                content.add(primitive.getAsString());
            }
        }

        for (JsonElement element : JsonUtils.get(groupObject, "nested", new JsonArray())) {
            if (element instanceof JsonPrimitive primitive && primitive.isString()) {
                content.add(primitive.getAsString());
            }
        }

        for (JsonElement element : JsonUtils.get(groupObject, "locked", new JsonArray())) {
            if (element instanceof JsonPrimitive primitive && primitive.isString()) {
                requirements.add(primitive.getAsString());
            }
        }

        SlimefunItemGroup itemGroup = new SlimefunItemGroup(identifier, itemStack, content, requirements);
        ITEM_GROUPS.put(identifier.toString(), itemGroup);
        content.forEach(contentId -> BY_CONTENT.put(contentId, itemGroup));
    }

    @NonNull
    public static Map<String, SlimefunItemGroup> getItemGroups() {
        return ITEM_GROUPS;
    }

    public static void clear() {
        ITEM_GROUPS.clear();
    }

    public static void addParents() {
        for (SlimefunItemGroup itemGroup : ITEM_GROUPS.values()) {
            for (String content : itemGroup.content()) {
                final SlimefunItemGroup child = ITEM_GROUPS.get(content);
                if (child != null) {
                    child.requirements().add(SlimefunEssentials.MOD_ID + ":" + itemGroup.identifier().toString().replace(":", "_"));
                }
            }
        }
    }

    public static List<SlimefunItemStack> sort(List<SlimefunItemStack> itemStacks) {
        final List<SlimefunItemGroup> groups = new ArrayList<>(ITEM_GROUPS.values());
        itemStacks = new ArrayList<>(itemStacks);
        groups.add(EMPTY);

        itemStacks.sort(Comparator.comparingInt(stack -> BY_CONTENT.getOrDefault(stack.id(), EMPTY).content().indexOf(stack.id())));
        itemStacks.sort(Comparator.comparingInt(stack -> groups.indexOf(BY_CONTENT.getOrDefault(stack.id(), EMPTY))));
        return itemStacks;
    }
}