package me.justahuman.slimefun_essentials.client;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import me.justahuman.slimefun_essentials.api.DisplayComponentType;
import me.justahuman.slimefun_essentials.utils.JsonUtils;
import me.justahuman.slimefun_essentials.utils.Utils;
import net.minecraft.client.gui.tooltip.TooltipComponent;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public record RecipeDisplayComponent(String type, int x, int y, int index, boolean output, List<TooltipComponent> tooltipOverride) {
    public static final List<TooltipComponent> EMPTY_TOOLTIP = List.of();
    public static final Map<SlimefunRecipe, List<TooltipComponent>> TOOLTIP_CACHE = new HashMap<>();

    public RecipeDisplayComponent(String type, int x, int y) {
        this(type, x, y, -1, false, EMPTY_TOOLTIP);
    }

    public RecipeDisplayComponent(String type, int x, int y, int index) {
        this(type, x, y, index, false, EMPTY_TOOLTIP);
    }

    public RecipeDisplayComponent(String type, int x, int y, int index, boolean output) {
        this(type, x, y, index, output, EMPTY_TOOLTIP);
    }

    public RecipeDisplayComponent(String type, int x, int y, List<TooltipComponent> tooltipOverride) {
        this(type, x, y, -1, false, tooltipOverride);
    }

    public int width() {
        return getType().width();
    }

    public int height() {
        return getType().height();
    }

    public List<TooltipComponent> tooltip(DrawMode drawMode, SlimefunRecipe recipe) {
        return TOOLTIP_CACHE.computeIfAbsent(recipe, key -> {
            if (this.tooltipOverride.isEmpty()) {
                return getType().tooltip(drawMode, recipe);
            }
            return Utils.updateTooltip(this.tooltipOverride, recipe);
        });
    }

    public DisplayComponentType getType() {
        return DisplayComponentType.get(this.type);
    }

    public static RecipeDisplayComponent deserialize(JsonObject jsonObject) {
        String type = jsonObject.get("type").getAsString();
        int x = jsonObject.get("x").getAsInt();
        int y = jsonObject.get("y").getAsInt();
        int index = JsonUtils.get(jsonObject, "index", -1);
        boolean output = JsonUtils.get(jsonObject, "output", false);
        if (!jsonObject.has("tooltip")) {
            return new RecipeDisplayComponent(type, x, y, index, output);
        }

        JsonArray tooltipArray = jsonObject.getAsJsonArray("tooltip");
        List<String> tooltip = new ArrayList<>();
        tooltipArray.forEach(element -> tooltip.add(element.getAsString()));
        return new RecipeDisplayComponent(type, x, y, index, output, tooltip.stream()
                .map(Text::literal)
                .map(MutableText::asOrderedText)
                .map(TooltipComponent::of).toList());
    }
}
