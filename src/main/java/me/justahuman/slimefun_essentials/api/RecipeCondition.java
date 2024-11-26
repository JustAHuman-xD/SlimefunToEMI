package me.justahuman.slimefun_essentials.api;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;
import me.justahuman.slimefun_essentials.client.SlimefunRecipe;
import me.justahuman.slimefun_essentials.utils.JsonUtils;

import java.util.function.BiFunction;
import java.util.function.Function;

@FunctionalInterface
public interface RecipeCondition {
    RecipeCondition TRUE = recipe -> true;
    RecipeCondition FALSE = recipe -> false;

    boolean passes(SlimefunRecipe recipe);

    static RecipeCondition deserialize(JsonElement condition) {
        if (condition instanceof JsonObject object) {
            Function<SlimefunRecipe, Number> property = getMapper(JsonUtils.get(object, "property", "0.0"));
            BiFunction<Number, Number, Boolean> comparison = switch (JsonUtils.get(object , "condition", "")) {
                case ">" -> (a, b) -> a.doubleValue() > b.doubleValue();
                case ">=" -> (a, b) -> a.doubleValue() >= b.doubleValue();
                case "<" -> (a, b) -> a.doubleValue() < b.doubleValue();
                case "<=" -> (a, b) -> a.doubleValue() <= b.doubleValue();
                case "=" -> (a, b) -> a.doubleValue() == b.doubleValue();
                case "!=" -> (a, b) -> a.doubleValue() != b.doubleValue();
                default -> (a, b) -> false;
            };
            Function<SlimefunRecipe, Number> value = getMapper(JsonUtils.get(object, "value", "0.0"));
            return recipe -> comparison.apply(property.apply(recipe), value.apply(recipe));
        } else {
            return !(condition instanceof JsonPrimitive primitive) || !primitive.isBoolean() || primitive.getAsBoolean() ? TRUE : FALSE;
        }
    }

    private static Function<SlimefunRecipe, Number> getMapper(String serializedValue) {
        Function<SlimefunRecipe, Number> mapper = switch (serializedValue) {
            case "%sf_ticks%" -> SlimefunRecipe::sfTicks;
            case "%time_seconds%" -> SlimefunRecipe::seconds;
            case "%time_ticks%" -> SlimefunRecipe::ticks;
            case "%time_millis%" -> SlimefunRecipe::millis;
            case "%energy%" -> SlimefunRecipe::energy;
            case "%total_energy%" -> SlimefunRecipe::totalEnergy;
            case "%inputs%" -> recipe -> recipe.inputs().size();
            case "%outputs%" -> recipe -> recipe.outputs().size();
            default -> {
                double value = 0.0;
                try {
                    value = Double.parseDouble(serializedValue);
                } catch (NumberFormatException ignored) {}
                double finalValue = value;
                yield recipe -> finalValue;
            }
        };
        return mapper.andThen(number -> number == null ? 0 : number);
    }
}
