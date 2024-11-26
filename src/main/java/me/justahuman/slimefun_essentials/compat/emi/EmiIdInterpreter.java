package me.justahuman.slimefun_essentials.compat.emi;

import dev.emi.emi.api.stack.EmiIngredient;
import dev.emi.emi.api.stack.EmiStack;
import me.justahuman.slimefun_essentials.SlimefunEssentials;
import me.justahuman.slimefun_essentials.api.IdInterpreter;
import me.justahuman.slimefun_essentials.client.SlimefunRecipe;
import me.justahuman.slimefun_essentials.client.RecipeComponent;
import net.fabricmc.fabric.api.transfer.v1.fluid.FluidVariant;
import net.minecraft.entity.EntityType;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.registry.tag.TagKey;

import java.util.ArrayList;
import java.util.List;

public class EmiIdInterpreter implements IdInterpreter<EmiIngredient> {
    public List<EmiIngredient> getInputIngredients(SlimefunRecipe recipe) {
        return recipe.inputs() != null && !recipe.inputs().isEmpty()
                ? recipe.inputs().stream().map(this::emiIngredientFromComponent).toList()
                : new ArrayList<>();
    }

    public List<EmiStack> getOutputStacks(SlimefunRecipe recipe) {
        return recipe.outputs() != null && !recipe.outputs().isEmpty()
                ? recipe.outputs().stream().map(this::emiStackFromComponent).toList()
                : new ArrayList<>();
    }

    public EmiStack emiStackFromComponent(RecipeComponent component) {
        final EmiIngredient ingredient = emiIngredientFromComponent(component);
        if (ingredient instanceof EmiStack emiStack) {
            return emiStack;
        }

        SlimefunEssentials.LOGGER.error("Failed to interpret EmiStack from component: {}", component);
        return EmiStack.EMPTY;
    }

    public EmiIngredient emiIngredientFromComponent(RecipeComponent component) {
        final List<String> multiId = component.getMultiId();
        if (multiId != null) {
            final List<EmiIngredient> multiStack = new ArrayList<>();
            for (String id : multiId) {
                multiStack.add(interpretId(component, id, EmiStack.EMPTY));
            }
            return EmiIngredient.of(multiStack, multiStack.isEmpty() ? 1 : multiStack.get(0).getAmount());
        }

        return interpretId(component, component.getId(), EmiStack.EMPTY);
    }

    @Override
    public EmiIngredient fromTag(float chance, TagKey<Item> tagKey, int amount, EmiIngredient def) {
        return EmiIngredient.of(tagKey, amount).setChance(chance);
    }

    @Override
    public EmiIngredient fromItemStack(float chance, ItemStack itemStack, int amount, EmiIngredient def) {
        return EmiStack.of(itemStack, amount).setChance(chance);
    }

    @Override
    public EmiIngredient fromFluid(float chance, FluidVariant fluid, int amount, EmiIngredient def) {
        return EmiStack.of(fluid.getFluid()).setChance(chance);
    }

    @Override
    public EmiIngredient fromEntityType(float chance, EntityType<?> entityType, boolean baby, int amount, EmiIngredient def) {
        return new EntityEmiStack(entityType, baby).setAmount(amount).setChance(chance);
    }
}
