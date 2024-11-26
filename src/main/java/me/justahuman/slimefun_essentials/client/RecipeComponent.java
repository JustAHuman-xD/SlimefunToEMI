package me.justahuman.slimefun_essentials.client;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;
import lombok.Getter;
import me.justahuman.slimefun_essentials.utils.JsonUtils;
import net.minecraft.item.ItemStack;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Getter
public class RecipeComponent {
    public static final RecipeComponent EMPTY = new RecipeComponent(new JsonArray(), "");
    private final List<ItemStack> complexStacks = new ArrayList<>();
    private final String id;
    private final List<String> multiId;
    
    public RecipeComponent(JsonArray complex, String id) {
        this(complex, id, null);
    }
    
    public RecipeComponent(JsonArray complex, List<String> multiId) {
        this(complex, null, multiId);
    }

    private RecipeComponent(JsonArray complex, String id, List<String> multiId) {
        this.id = id;
        this.multiId = multiId;
        for (JsonElement element : complex) {
            if (element instanceof JsonObject object) {
                this.complexStacks.add(JsonUtils.deserializeItem(object));
            }
        }
    }

    public boolean isLarge() {
        if (this.id != null) {
            return id.startsWith("@") && !id.startsWith("baby_", 1);
        } else if (this.multiId != null) {
            for (String id : multiId) {
                if (id.startsWith("@") && !id.startsWith("baby_", 1)) {
                    return true;
                }
            }
        }
        return false;
    }
    
    public static RecipeComponent deserialize(JsonArray complex, JsonElement element) {
        if (element instanceof JsonPrimitive primitive && primitive.isString()) {
            final String[] elements = primitive.getAsString().split(",");
            if (elements.length == 1) {
                return new RecipeComponent(complex, primitive.getAsString());
            }
            return new RecipeComponent(complex, Arrays.asList(elements));
        }
        return null;
    }
}
