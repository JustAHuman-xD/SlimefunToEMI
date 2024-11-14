package me.justahuman.slimefun_essentials.compat.patchouli;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import me.justahuman.slimefun_essentials.client.SlimefunItemGroup;
import me.justahuman.slimefun_essentials.client.SlimefunRecipe;
import me.justahuman.slimefun_essentials.client.SlimefunRecipeCategory;
import me.justahuman.slimefun_essentials.compat.patchouli.pages.AncientAltarPage;
import me.justahuman.slimefun_essentials.compat.patchouli.pages.GridPage;
import me.justahuman.slimefun_essentials.compat.patchouli.pages.ProcessPage;
import me.justahuman.slimefun_essentials.compat.patchouli.pages.ReactorPage;
import me.justahuman.slimefun_essentials.compat.patchouli.pages.SmelteryPage;
import me.justahuman.slimefun_essentials.utils.JsonUtils;
import me.justahuman.slimefun_essentials.utils.TextureUtils;
import me.justahuman.slimefun_essentials.utils.Utils;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Identifier;
import vazkii.patchouli.api.PatchouliAPI;
import vazkii.patchouli.client.book.BookCategory;
import vazkii.patchouli.client.book.ClientBookRegistry;

public class PatchouliIntegration {
    public static final Identifier BOOK_IDENTIFIER = Utils.id("slimefun");
    public static final PatchouliIdInterpreter INTERPRETER = new PatchouliIdInterpreter();

    public static void init() {
        ClientBookRegistry.INSTANCE.pageTypes.put(Utils.id("ancient_altar"), AncientAltarPage.class);
        ClientBookRegistry.INSTANCE.pageTypes.put(Utils.id("grid"), GridPage.class);
        ClientBookRegistry.INSTANCE.pageTypes.put(Utils.id("process"), ProcessPage.class);
        ClientBookRegistry.INSTANCE.pageTypes.put(Utils.id("reactor"), ReactorPage.class);
        ClientBookRegistry.INSTANCE.pageTypes.put(Utils.id("smeltery"), SmelteryPage.class);
    }

    public static JsonObject getItemGroupCategory(SlimefunItemGroup itemGroup, int sortnum) {
        final JsonObject category = new JsonObject();
        final String itemName = itemGroup.itemStack().getName().getString();
        category.addProperty("name", itemName);
        category.addProperty("description", "The " + itemName + " Item Group!");
        category.addProperty("icon", JsonUtils.serializeItem(itemGroup.itemStack()));
        category.addProperty("sortnum", sortnum);

        for (String requirement : itemGroup.requirements()) {
            if (requirement.startsWith("slimefun_essentials:")) {
                category.addProperty("parent", requirement);
                break;
            }
        }

        if (!category.has("parent")) {
            category.addProperty("parent", "slimefun_essentials:item_groups");
        }

        return category;
    }

    public static JsonObject getRecipeEntry(BookCategory category, SlimefunRecipeCategory recipeCategory, int sortnum) {
        final JsonObject entry = new JsonObject();
        final ItemStack itemStack = recipeCategory.itemStack();
        entry.addProperty("category", category.getId().toString());
        entry.addProperty("name", itemStack.getName().getString());
        entry.addProperty("icon", JsonUtils.serializeItem(itemStack));
        entry.addProperty("sortnum", sortnum);

        final JsonArray pages = new JsonArray();
        if (recipeCategory.recipe() != null) {
            pages.add(getPage(recipeCategory, recipeCategory.recipe()));
        }
        entry.add("pages", pages);

        return entry;
    }

    public static JsonObject getPage(SlimefunRecipeCategory category, SlimefunRecipe recipe) {
        final String type = recipe.parent().type();
        final JsonObject page = new JsonObject();

        page.addProperty("id", category.id());

        if (type.contains("grid")) {
            page.addProperty("type", "slimefun_essentials:grid");
            page.addProperty("side", TextureUtils.getSideSafe(type));
        } else {
            page.addProperty("type", "slimefun_essentials:" + type);
        }

        return page;
    }

    public static void openGuide() {
        PatchouliAPI.get().openBookGUI(BOOK_IDENTIFIER);
    }
}
