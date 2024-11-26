package me.justahuman.slimefun_essentials.compat.jei;

import me.justahuman.slimefun_essentials.api.IdInterpreter;
import me.justahuman.slimefun_essentials.client.SlimefunItemStack;
import me.justahuman.slimefun_essentials.client.RecipeComponent;
import mezz.jei.api.gui.builder.IRecipeSlotBuilder;
import mezz.jei.fabric.ingredients.fluid.JeiFluidIngredient;
import net.fabricmc.fabric.api.transfer.v1.fluid.FluidVariant;
import net.minecraft.entity.EntityType;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.registry.Registries;
import net.minecraft.registry.entry.RegistryEntryList;
import net.minecraft.registry.tag.TagKey;

import java.util.List;
import java.util.Optional;

public class JeiRecipeInterpreter implements IdInterpreter<Object> {
    public void addIngredient(IRecipeSlotBuilder slotBuilder, ItemStack itemStack) {
        slotBuilder.addItemStack(itemStack);
    }

    public void addIngredients(IRecipeSlotBuilder slotBuilder, RecipeComponent component) {
        for (String id : component.getMultiId() != null ? component.getMultiId() : List.of(component.getId())) {
            addIngredientObject(slotBuilder, interpretId(component, id, ItemStack.EMPTY));
        }
    }

    public void addIngredientObject(IRecipeSlotBuilder slotBuilder, Object ingredient) {
        if (ingredient instanceof List<?> list) {
            for (Object object : list) {
                addIngredientObject(slotBuilder, object);
            }
        } else if (ingredient instanceof ItemStack itemStack) {
            slotBuilder.addItemStack(itemStack);
        } else if (ingredient instanceof SlimefunItemStack slimefunItemStack) {
            slotBuilder.addItemStack(slimefunItemStack.itemStack());
        } else if (ingredient instanceof JeiFluidIngredient fluidStack) {
            slotBuilder.addFluidStack(fluidStack.getFluidVariant().getFluid(), fluidStack.getAmount());
        }
    }

    @Override
    public Object fromTag(float chance, TagKey<Item> tagKey, int amount, Object def) {
        Optional<RegistryEntryList.Named<Item>> optional = Registries.ITEM.getEntryList(tagKey);
        if (optional.isEmpty()) {
            return def;
        }

        return optional.get().stream().map(item -> new ItemStack(item, amount)).toList();
    }

    @Override
    public Object fromItemStack(float chance, ItemStack itemStack, int amount, Object def) {
        itemStack.setCount(amount);
        return itemStack;
    }

    @Override
    public Object fromFluid(float chance, FluidVariant fluid, int amount, Object def) {
        return new JeiFluidIngredient(fluid, amount);
    }

    @Override
    public Object fromEntityType(float chance, EntityType<?> entityType, boolean baby, int amount, Object def) {
        // TODO: add support for entities
        return def;
    }
}
