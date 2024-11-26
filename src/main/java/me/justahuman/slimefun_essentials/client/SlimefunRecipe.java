package me.justahuman.slimefun_essentials.client;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;
import me.justahuman.slimefun_essentials.utils.JsonUtils;

import java.util.ArrayList;
import java.util.List;

public class SlimefunRecipe {
    protected RecipeCategory parent;
    protected Integer sfTicks;
    protected Integer energy;
    protected List<RecipeComponent> inputs;
    protected List<RecipeComponent> outputs;
    protected List<RecipeDisplayComponent> labels;

    public SlimefunRecipe(RecipeCategory parent, Integer sfTicks, Integer energy, List<RecipeComponent> inputs, List<RecipeComponent> outputs, List<RecipeDisplayComponent> labels) {
        this.parent = parent;
        this.sfTicks = sfTicks;
        this.energy = energy;
        this.inputs = inputs;
        this.outputs = outputs;
        this.labels = labels;
    }

    public static SlimefunRecipe deserialize(RecipeCategory parent, JsonObject recipeObject, Integer workstationEnergy) {
        final Integer sfTicks = JsonUtils.get(recipeObject, "sf_ticks", (Integer) null);
        final Integer energy = JsonUtils.get(recipeObject, "energy", workstationEnergy);
        final List<RecipeComponent> inputs = new ArrayList<>();
        final List<RecipeComponent> outputs = new ArrayList<>();
        final List<RecipeDisplayComponent> labels = new ArrayList<>();
        final JsonArray complex = JsonUtils.get(recipeObject, "complex", new JsonArray());

        for (JsonElement inputElement : JsonUtils.get(recipeObject, "inputs", new JsonArray())) {
            final RecipeComponent inputRecipeElement = RecipeComponent.deserialize(complex, inputElement);
            if (inputRecipeElement != null) {
                inputs.add(inputRecipeElement);
            }
        }

        for (JsonElement outputElement : JsonUtils.get(recipeObject, "outputs", new JsonArray())) {
            final RecipeComponent outputRecipeElement = RecipeComponent.deserialize(complex, outputElement);
            if (outputRecipeElement != null) {
                outputs.add(outputRecipeElement);
            }
        }

        for (JsonElement labelElement : JsonUtils.get(recipeObject, "labels", new JsonArray())) {
            if (! (labelElement instanceof JsonPrimitive jsonPrimitive) || ! jsonPrimitive.isString()) {
                continue;
            }
            
            final RecipeDisplayComponent recipeDisplayComponent = null;
            if (recipeDisplayComponent != null) {
                labels.add(recipeDisplayComponent);
            }
        }

        return new SlimefunRecipe(parent, sfTicks, energy, inputs, outputs, labels);
    }

    public boolean hasLabels() {
        return this.labels != null && !this.labels.isEmpty();
    }

    public boolean hasEnergy() {
        return this.energy != null && this.energy != 0;
    }

    public boolean hasInputs() {
        return this.inputs != null && !this.inputs.isEmpty();
    }

    public boolean hasTime() {
        return this.sfTicks != null;
    }

    public boolean hasOutputs() {
        return this.outputs != null && !this.outputs.isEmpty();
    }

    public RecipeCategory parent() {
        return this.parent;
    }

    public int sfTicks() {
        return hasTime() ? Math.max(1, this.sfTicks / this.parent.speed()) : 0;
    }

    public int ticks() {
        return seconds() * 20;
    }

    public int seconds() {
        return sfTicks() / SlimefunRegistry.getTicksPerSecond();
    }

    public int millis() {
        return seconds() * 1000;
    }

    public Integer energy() {
        return this.energy;
    }

    public Integer totalEnergy() {
        if (!this.hasEnergy()) {
            return null;
        }
        return hasTime() ? this.energy * sfTicks() : this.energy;
    }

    public List<RecipeComponent> inputs() {
        return this.inputs;
    }

    public List<RecipeComponent> outputs() {
        return this.outputs;
    }

    public List<RecipeDisplayComponent> labels() {
        return this.labels;
    }

    public SlimefunRecipe copy(RecipeCategory newParent) {
        return new SlimefunRecipe(newParent, this.sfTicks, this.energy, new ArrayList<>(this.inputs), new ArrayList<>(this.outputs), new ArrayList<>(this.labels));
    }
}
