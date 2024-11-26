package me.justahuman.slimefun_essentials.compat.rei;

import me.justahuman.slimefun_essentials.api.IdInterpreter;
import me.justahuman.slimefun_essentials.client.SlimefunRecipe;
import me.justahuman.slimefun_essentials.client.RecipeComponent;
import me.shedaniel.rei.api.common.entry.EntryIngredient;
import me.shedaniel.rei.api.common.util.EntryIngredients;
import net.fabricmc.fabric.api.transfer.v1.fluid.FluidVariant;
import net.minecraft.entity.EntityType;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.registry.tag.TagKey;

import java.util.ArrayList;
import java.util.List;

public class ReiRecipeInterpreter implements IdInterpreter<EntryIngredient> {
    public List<EntryIngredient> getInputEntries(SlimefunRecipe slimefunRecipe) {
        final List<EntryIngredient> ingredients = new ArrayList<>();
        for (RecipeComponent component : slimefunRecipe.inputs()) {
            ingredients.add(ReiIntegration.RECIPE_INTERPRETER.entryIngredientFromComponent(component));
        }
        return ingredients;
    }

    public List<EntryIngredient> getOutputEntries(SlimefunRecipe slimefunRecipe) {
        final List<EntryIngredient> ingredients = new ArrayList<>();
        for (RecipeComponent component : slimefunRecipe.outputs()) {
            ingredients.add(ReiIntegration.RECIPE_INTERPRETER.entryIngredientFromComponent(component));
        }
        return ingredients;
    }

    public EntryIngredient entryIngredientFromComponent(RecipeComponent component) {
        if (component.getMultiId() != null) {
            EntryIngredient.Builder builder = EntryIngredient.builder();
            for (String id : component.getMultiId()) {
                builder.addAll(interpretId(component, id, EntryIngredient.empty()));
            }
            return builder.build();
        } else {
            return interpretId(component, component.getId(), EntryIngredient.empty());
        }
    }

    @Override
    public EntryIngredient fromTag(float chance, TagKey<Item> tagKey, int amount, EntryIngredient def) {
        return EntryIngredients.ofItemTag(tagKey);
    }

    @Override
    public EntryIngredient fromItemStack(float chance, ItemStack itemStack, int amount, EntryIngredient def) {
        itemStack.setCount(amount);
        return EntryIngredients.of(itemStack);
    }

    @Override
    public EntryIngredient fromFluid(float chance, FluidVariant fluid, int amount, EntryIngredient def) {
        return EntryIngredients.of(fluid.getFluid(), amount);
    }

    @Override
    public EntryIngredient fromEntityType(float chance, EntityType<?> entityType, boolean baby, int amount, EntryIngredient def) {
        // TODO: add entity support
        return def;
    }
}
