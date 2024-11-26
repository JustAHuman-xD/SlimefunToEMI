package me.justahuman.slimefun_essentials.client;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import lombok.NonNull;
import me.justahuman.slimefun_essentials.utils.JsonUtils;
import net.minecraft.item.ItemStack;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class RecipeCategory {
    private static final Map<String, RecipeCategory> RECIPE_CATEGORIES = new LinkedHashMap<>();
    private static final Map<String, RecipeCategory> EMPTY_CATEGORIES = new HashMap<>();
    private static final Map<String, String> TO_COPY = new HashMap<>();

    private final String id;
    private final ItemStack itemStack;
    private final String display;
    private final Integer speed;
    private final Integer energy;
    private final List<SlimefunRecipe> childRecipes;
    private SlimefunRecipe recipe = null;

    public RecipeCategory(String id, ItemStack itemStack, String display, Integer speed, Integer energy, List<SlimefunRecipe> childRecipes) {
        this.id = id;
        this.itemStack = itemStack;
        this.display = display;
        this.speed = speed;
        this.energy = energy;
        this.childRecipes = childRecipes;
    }

    public String id() {
        return this.id;
    }

    public RecipeDisplay display() {
        return RecipeDisplay.get(this.display);
    }

    public ItemStack itemStack() {
        return !this.itemStack.isEmpty() ? this.itemStack : SlimefunRegistry.getItemStack(this.id);
    }

    public Integer speed() {
        return this.speed == null ? 1 : this.speed;
    }

    public Integer energy() {
        return this.energy;
    }

    public SlimefunRecipe recipe() {
        return this.recipe;
    }

    public List<SlimefunRecipe> childRecipes() {
        return this.childRecipes;
    }
    
    public static void deserialize(String id, JsonObject categoryObject) {
        final String display = JsonUtils.get(categoryObject, "display", "dynamic");
        final ItemStack itemStack = JsonUtils.deserializeItem(JsonUtils.get(categoryObject, "item", new JsonObject()));
        final Integer speed = JsonUtils.get(categoryObject, "speed", (Integer) null);
        final Integer energy = JsonUtils.get(categoryObject, "energy", (Integer) null);
        final List<SlimefunRecipe> recipes = new ArrayList<>();

        final RecipeCategory category = new RecipeCategory(id, itemStack, display, speed, energy, recipes);
        for (JsonElement recipeElement : JsonUtils.get(categoryObject, "recipes", new JsonArray())) {
            if (recipeElement instanceof JsonObject recipeObject) {
                recipes.add(SlimefunRecipe.deserialize(category, recipeObject, energy));
            }
        }

        TO_COPY.put(id, JsonUtils.get(categoryObject, "copy", ""));
        RECIPE_CATEGORIES.put(id, category);
    }

    public static void finalizeCategories() {
        for (Map.Entry<String, String> copyMap : TO_COPY.entrySet()) {
            final RecipeCategory target = RECIPE_CATEGORIES.get(copyMap.getKey());
            final RecipeCategory parent = RECIPE_CATEGORIES.get(copyMap.getValue());
            if (target != null && parent != null) {
                for (SlimefunRecipe slimefunRecipe : parent.childRecipes()) {
                    target.childRecipes().add(slimefunRecipe.copy(target));
                }
            }
        }
        TO_COPY.clear();

        for (RecipeCategory category : RECIPE_CATEGORIES.values()) {
            for (SlimefunRecipe recipe : category.childRecipes()) {
                final int weight = weight(recipe);
                for (RecipeComponent output : recipe.outputs()) {
                    final List<String> multiId = output.getMultiId();
                    if (multiId != null) {
                        for (String id : multiId) {
                            final RecipeCategory forCategory = fromId(id);
                            if (forCategory != null && weight >= weight(forCategory.recipe)) {
                                forCategory.recipe = recipe;
                            }
                        }
                    } else {
                        final RecipeCategory forCategory = fromId(output.getId());
                        if (forCategory != null && weight >= weight(forCategory.recipe)) {
                            forCategory.recipe = recipe;
                        }
                    }
                }
            }
        }
    }

    public static int weight(SlimefunRecipe recipe) {
        if (recipe == null) {
            return 0;
        }

        final String type = recipe.parent().display().id;
        if (type.contains("grid")) {
            return 10;
        } else if (type.equals("ancient_altar")) {
            return 9;
        } else if (type.equals("smeltery")) {
            return 8;
        } else if (type.equals("reactor")) {
            return 7;
        } else {
            return 0;
        }
    }

    public static RecipeCategory fromId(String component) {
        if (!component.contains(":")) {
            return null;
        }

        String id = component.split(":")[0];
        if (id.contains("%")) {
            id = id.substring(0, id.indexOf("%"));
        }

        if (RECIPE_CATEGORIES.containsKey(id)) {
            return RECIPE_CATEGORIES.get(id);
        } else if (EMPTY_CATEGORIES.containsKey(id)) {
            return EMPTY_CATEGORIES.get(id);
        } else if (SlimefunRegistry.getSlimefunItem(id) != null) {
            final SlimefunItemStack itemStack = SlimefunRegistry.getSlimefunItem(id);
            final RecipeCategory category = new RecipeCategory(id, itemStack.itemStack(), "none", null, null, new ArrayList<>());
            EMPTY_CATEGORIES.put(id, category);
        }
        return null;
    }
    
    /**
     * Returns an unmodifiable version of {@link RecipeCategory#RECIPE_CATEGORIES}
     *
     * @return {@link Map}
     */
    @NonNull
    public static Map<String, RecipeCategory> getRecipeCategories() {
        return Collections.unmodifiableMap(RECIPE_CATEGORIES);
    }

    public static void clear() {
        RECIPE_CATEGORIES.clear();
    }

    public static Map<String, RecipeCategory> getAllCategories() {
        final Map<String, RecipeCategory> categories = new HashMap<>(RECIPE_CATEGORIES);
        categories.putAll(EMPTY_CATEGORIES);
        return categories;
    }
}
