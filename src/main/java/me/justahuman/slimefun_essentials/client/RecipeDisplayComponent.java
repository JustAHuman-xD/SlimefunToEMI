package me.justahuman.slimefun_essentials.client;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import me.justahuman.slimefun_essentials.api.CustomRenderable;
import me.justahuman.slimefun_essentials.api.DisplayComponentType;
import me.justahuman.slimefun_essentials.utils.JsonUtils;
import me.justahuman.slimefun_essentials.utils.Utils;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.tooltip.TooltipComponent;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public record RecipeDisplayComponent(String type, int x, int y, int index, boolean output, CustomRenderable renderable, List<TooltipComponent> tooltipOverride, Map<SlimefunRecipe, List<TooltipComponent>> tooltipCache) implements DisplayComponentType {
    public static final List<TooltipComponent> EMPTY_TOOLTIP = List.of();

    public RecipeDisplayComponent(String type, int x, int y) {
        this(type, x, y, -1, false, null, EMPTY_TOOLTIP, new HashMap<>());
    }

    public RecipeDisplayComponent(String type, int x, int y, int index) {
        this(type, x, y, index, false, null, EMPTY_TOOLTIP, new HashMap<>());
    }

    public RecipeDisplayComponent(String type, int x, int y, int index, boolean output) {
        this(type, x, y, index, output, null, EMPTY_TOOLTIP, new HashMap<>());
    }

    public RecipeDisplayComponent(String type, int x, int y, int index, boolean output, CustomRenderable renderable) {
        this(type, x, y, index, output, renderable, EMPTY_TOOLTIP, new HashMap<>());
    }

    public RecipeDisplayComponent(String type, int x, int y, List<TooltipComponent> tooltipOverride) {
        this(type, x, y, -1, false, null, tooltipOverride, new HashMap<>());
    }

    @Override
    public int width() {
        return this.renderable != null ? this.renderable.width() : getType().width();
    }

    @Override
    public int height() {
        return this.renderable != null ? this.renderable.height() : getType().height();
    }

    @Override
    public void draw(SlimefunRecipe recipe, DrawMode mode, DrawContext context, int x, int y) {
        if (this.renderable != null) {
            getType().draw(recipe, this.renderable, context, x, y);
        } else {
            getType().draw(recipe, mode, context, x, y);
        }
    }

    @Override
    public List<TooltipComponent> tooltip(DrawMode drawMode, SlimefunRecipe recipe) {
        return tooltipCache.computeIfAbsent(recipe, key -> {
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
        CustomRenderable renderable = null;
        if (jsonObject.has("renderable")) {
            renderable = CustomRenderable.deserialize(jsonObject.getAsJsonObject("renderable"));
        }
        if (!jsonObject.has("tooltip")) {
            return new RecipeDisplayComponent(type, x, y, index, output, renderable);
        }
        JsonArray tooltipArray = jsonObject.getAsJsonArray("tooltip");
        List<String> tooltip = new ArrayList<>();
        tooltipArray.forEach(element -> tooltip.add(element.getAsString()));
        return new RecipeDisplayComponent(type, x, y, index, output, renderable, tooltip.stream()
                .map(Text::literal)
                .map(MutableText::asOrderedText)
                .map(TooltipComponent::of).toList(), new HashMap<>());
    }
}
