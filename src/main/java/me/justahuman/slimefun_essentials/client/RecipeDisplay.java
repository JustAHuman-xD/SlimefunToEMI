package me.justahuman.slimefun_essentials.client;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import me.justahuman.slimefun_essentials.api.DisplayComponentType;
import me.justahuman.slimefun_essentials.utils.TextureUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class RecipeDisplay {
    private static final Map<String, RecipeDisplay> RECIPE_DISPLAYS = new HashMap<>();
    public static final RecipeDisplay DYNAMIC = new DynamicDisplay();
    public static final RecipeDisplay NONE = new RecipeDisplay("none", new ArrayList<>(), 0, 0);

    protected final String id;
    protected final List<RecipeDisplayComponent> components;
    protected final int width;
    protected final int height;

    public RecipeDisplay(String id, List<RecipeDisplayComponent> components, int width, int height) {
        this.id = id;
        this.components = components;
        this.width = width;
        this.height = height;
    }

    public int width(SlimefunRecipe recipe) {
        return width;
    }

    public int height(SlimefunRecipe recipe) {
        return height;
    }

    public List<RecipeDisplayComponent> components(SlimefunRecipe recipe) {
        return components;
    }

    public static void deserialize(String id, JsonObject displayObject) {
        JsonArray serializedComponents = displayObject.getAsJsonArray("components");
        List<RecipeDisplayComponent> components = new ArrayList<>();
        for (int i = 0; i < serializedComponents.size(); i++) {
            JsonObject componentObject = serializedComponents.get(i).getAsJsonObject();
            components.add(RecipeDisplayComponent.deserialize(componentObject));
        }

        int width = displayObject.get("width").getAsInt();
        int height = displayObject.get("height").getAsInt();
        RECIPE_DISPLAYS.put(id, new RecipeDisplay(id, components, width, height));
    }

    public static RecipeDisplay get(String id) {
        return id.equals("dynamic") ? DYNAMIC : RECIPE_DISPLAYS.getOrDefault(id, NONE);
    }

    public static void clear() {
        RECIPE_DISPLAYS.clear();
        ((DynamicDisplay) DYNAMIC).SNAPSHOTS.clear();
    }

    private static class DynamicDisplay extends RecipeDisplay {
        protected final Map<SlimefunRecipe, Snapshot> SNAPSHOTS = new HashMap<>();
        public DynamicDisplay() {
            super("dynamic", new ArrayList<>(), 0, 0);
        }

        @Override
        public int width(SlimefunRecipe recipe) {
            return SNAPSHOTS.computeIfAbsent(recipe, Snapshot::of).width;
        }

        @Override
        public int height(SlimefunRecipe recipe) {
            return SNAPSHOTS.computeIfAbsent(recipe, Snapshot::of).height;
        }

        @Override
        public List<RecipeDisplayComponent> components(SlimefunRecipe recipe) {
            return SNAPSHOTS.computeIfAbsent(recipe, Snapshot::of).components;
        }

        record Snapshot(List<RecipeDisplayComponent> components, int width, int height) {
            static Snapshot of(SlimefunRecipe recipe) {
                List<RecipeDisplayComponent> components = new ArrayList<>();
                int width = TextureUtils.PADDING * 2;
                int height = TextureUtils.PADDING * 2;

                if (recipe.hasLabels()) {
                    for (RecipeDisplayComponent component : recipe.labels()) {
                        width += component.width() + TextureUtils.PADDING;
                        height = Math.max(height, component.height() + TextureUtils.PADDING * 2);
                    }
                }

                if (recipe.hasEnergy()) {
                    DisplayComponentType type = DisplayComponentType.get("energy");
                    width += type.width() + TextureUtils.PADDING;
                    height = Math.max(height, type.height() + TextureUtils.PADDING * 2);
                }

                if (recipe.hasInputs()) {
                    for (RecipeComponent component : recipe.inputs()) {
                        DisplayComponentType type = DisplayComponentType.get(component.isLarge() ? "large_slot" : "slot");
                        width += type.width();
                        height = Math.max(height, type.height() + TextureUtils.PADDING * 2);
                    }
                    width += TextureUtils.PADDING;
                } else {
                    DisplayComponentType type = DisplayComponentType.get("slot");
                    width += type.width() + TextureUtils.PADDING;
                    height = Math.max(height, type.height() + TextureUtils.PADDING * 2);
                }

                if (recipe.hasEnergy() || recipe.hasOutputs()) {
                    DisplayComponentType type = DisplayComponentType.get("arrow_right");
                    width += type.width() + TextureUtils.PADDING;
                    height = Math.max(height, type.height() + TextureUtils.PADDING * 2);
                }

                if (recipe.hasOutputs()) {
                    for (RecipeComponent component : recipe.outputs()) {
                        DisplayComponentType type = DisplayComponentType.get(component.isLarge() ? "large_slot" : "slot");
                        width += type.width();
                        height = Math.max(height, type.height() + TextureUtils.PADDING * 2);
                    }
                    width += TextureUtils.PADDING;
                }

                int x = TextureUtils.PADDING;

                if (recipe.hasLabels()) {
                    for (RecipeDisplayComponent component : recipe.labels()) {
                        components.add(new RecipeDisplayComponent(component.type(), x, centered(component, height), component.tooltipOverride()));
                        x += component.width() + TextureUtils.PADDING;
                    }
                }

                if (recipe.hasEnergy() && recipe.hasOutputs()) {
                    DisplayComponentType type = DisplayComponentType.get("energy");
                    components.add(new RecipeDisplayComponent("energy", x, centered(type, height)));
                    x += type.width() + TextureUtils.PADDING;
                }

                if (recipe.hasInputs()) {
                    int i = 1;
                    for (RecipeComponent component : recipe.inputs()) {
                        DisplayComponentType type = DisplayComponentType.get(component.isLarge() ? "large_slot" : "slot");
                        components.add(new RecipeDisplayComponent(component.isLarge() ? "large_slot" : "slot", x, centered(type, height), i));
                        x += type.width();
                        i++;
                    }
                    x += TextureUtils.PADDING;
                } else {
                    DisplayComponentType type = DisplayComponentType.get("slot");
                    components.add(new RecipeDisplayComponent("slot", x, centered(type, height)));
                    x += type.width() + TextureUtils.PADDING;
                }

                if (recipe.hasEnergy() || recipe.hasOutputs()) {
                    DisplayComponentType type = DisplayComponentType.get("filling_arrow_right");
                    components.add(new RecipeDisplayComponent("filling_arrow_right", x, centered(type, height)));
                    x += type.width() + TextureUtils.PADDING;
                }

                if (recipe.hasOutputs()) {
                    int i = 1;
                    for (RecipeComponent component : recipe.outputs()) {
                        DisplayComponentType type = DisplayComponentType.get(component.isLarge() ? "large_slot" : "slot");
                        components.add(new RecipeDisplayComponent(component.isLarge() ? "large_slot" : "slot", x, centered(type, height), i, true));
                        x += type.width();
                        i++;
                    }
                } else if (recipe.hasEnergy()) {
                    DisplayComponentType type = DisplayComponentType.get("energy");
                    components.add(new RecipeDisplayComponent("energy", x, centered(type, height)));
                }

                return new Snapshot(components, width, height);
            }

            private static int centered(DisplayComponentType type, int height) {
                return (height - type.height()) / 2;
            }
            
            private static int centered(RecipeDisplayComponent component, int height) {
                return (height - component.height()) / 2;
            }
        }
    }
}
