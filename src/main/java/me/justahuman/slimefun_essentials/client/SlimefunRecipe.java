package me.justahuman.slimefun_essentials.client;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;
import me.justahuman.slimefun_essentials.utils.JsonUtils;

import java.util.ArrayList;
import java.util.List;

public class SlimefunRecipe {
    protected SlimefunRecipeCategory parent;
    protected Integer time;
    protected Integer energy;
    protected List<SlimefunRecipeComponent> inputs;
    protected List<SlimefunRecipeComponent> outputs;
    protected List<SlimefunLabel> labels;

    public SlimefunRecipe(SlimefunRecipeCategory parent, Integer time, Integer energy, List<SlimefunRecipeComponent> inputs, List<SlimefunRecipeComponent> outputs, List<SlimefunLabel> labels) {
        this.parent = parent;
        this.time = time;
        this.energy = energy;
        this.inputs = inputs;
        this.outputs = outputs;
        this.labels = labels;
    }

    public static SlimefunRecipe deserialize(SlimefunRecipeCategory parent, JsonObject recipeObject, Integer workstationEnergy) {
        final Integer time = JsonUtils.getInt(recipeObject, "time", null);
        final Integer energy = JsonUtils.getInt(recipeObject, "energy", workstationEnergy);
        final List<SlimefunRecipeComponent> inputs = new ArrayList<>();
        final List<SlimefunRecipeComponent> outputs = new ArrayList<>();
        final List<SlimefunLabel> labels = new ArrayList<>();
        final JsonArray complex = JsonUtils.getArray(recipeObject, "complex", new JsonArray());

        for (JsonElement inputElement : JsonUtils.getArray(recipeObject, "inputs", new JsonArray())) {
            final SlimefunRecipeComponent inputRecipeElement = SlimefunRecipeComponent.deserialize(complex, inputElement);
            if (inputRecipeElement != null) {
                inputs.add(inputRecipeElement);
            }
        }

        for (JsonElement outputElement : JsonUtils.getArray(recipeObject, "outputs", new JsonArray())) {
            final SlimefunRecipeComponent outputRecipeElement = SlimefunRecipeComponent.deserialize(complex, outputElement);
            if (outputRecipeElement != null) {
                outputs.add(outputRecipeElement);
            }
        }

        for (JsonElement labelElement : JsonUtils.getArray(recipeObject, "labels", new JsonArray())) {
            if (! (labelElement instanceof JsonPrimitive jsonPrimitive) || ! jsonPrimitive.isString()) {
                continue;
            }
            
            final SlimefunLabel slimefunLabel = SlimefunLabel.getSlimefunLabels().get(jsonPrimitive.getAsString());
            if (slimefunLabel != null) {
                labels.add(slimefunLabel);
            }
        }

        return new SlimefunRecipe(parent, time, energy, inputs, outputs, labels);
    }

    public void fillInputs(int size) {
        if (this.inputs == null) {
            this.inputs = new ArrayList<>(size);
            return;
        }

        if (this.inputs.size() >= size) {
            return;
        }

        for (int i = this.inputs.size(); i < size; i++) {
            inputs.add(SlimefunRecipeComponent.EMPTY);
        }
    }

    public void fillOutputs(int size) {
        if (this.outputs == null) {
            this.outputs = new ArrayList<>(size);
            return;
        }

        if (this.outputs.size() >= size) {
            return;
        }

        for (int i = this.inputs.size(); i < size; i++) {
            outputs.add(SlimefunRecipeComponent.EMPTY);
        }
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
        return this.time != null;
    }

    public boolean hasOutputs() {
        return this.outputs != null && !this.outputs.isEmpty();
    }

    public SlimefunRecipeCategory parent() {
        return this.parent;
    }

    public Integer time() {
        return this.time;
    }

    public Integer sfTicks() {
        return hasTime() ? Math.max(1, time() / 10 / this.parent.speed()) : 1;
    }

    public Integer energy() {
        return this.energy;
    }

    public Integer totalEnergy() {
        return this.hasEnergy() ? sfTicks() * this.energy : null;
    }

    public List<SlimefunRecipeComponent> inputs() {
        return this.inputs;
    }

    public List<SlimefunRecipeComponent> outputs() {
        return this.outputs;
    }

    public List<SlimefunLabel> labels() {
        return this.labels;
    }

    public SlimefunRecipe copy(SlimefunRecipeCategory newParent) {
        return new SlimefunRecipe(newParent, this.time, this.energy, new ArrayList<>(this.inputs), new ArrayList<>(this.outputs), new ArrayList<>(this.labels));
    }
}
