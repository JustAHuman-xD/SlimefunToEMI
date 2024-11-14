package me.justahuman.slimefun_essentials.mixins.patchouli;

import me.justahuman.slimefun_essentials.client.SlimefunItemGroup;
import me.justahuman.slimefun_essentials.client.SlimefunRecipeCategory;
import me.justahuman.slimefun_essentials.compat.patchouli.PatchouliIntegration;
import me.justahuman.slimefun_essentials.utils.Utils;
import net.minecraft.util.Identifier;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import vazkii.patchouli.client.book.BookCategory;
import vazkii.patchouli.client.book.BookContents;
import vazkii.patchouli.client.book.BookContentsBuilder;
import vazkii.patchouli.client.book.BookEntry;
import vazkii.patchouli.common.book.Book;

import java.util.Map;

@Mixin(BookContentsBuilder.class)
public abstract class ContentsBuilderMixin {
    @Shadow(remap = false)
    @Final private Book book;

    @Shadow(remap = false)
    @Final private Map<Identifier, BookCategory> categories;

    @Shadow(remap = false)
    @Final private Map<Identifier, BookEntry> entries;

    @Inject(method = "build", at = @At("HEAD"), remap = false)
    private void buildSlimefun(World level, CallbackInfoReturnable<BookContents> cir) {
        if (!PatchouliIntegration.BOOK_IDENTIFIER.equals(this.book.id)) {
            return;
        }

        int i = 0;
        final Map<String, SlimefunRecipeCategory> recipeCategories = SlimefunRecipeCategory.getAllCategories();
        for (SlimefunItemGroup itemGroup : SlimefunItemGroup.getItemGroups().values()) {
            final Identifier identifier = Utils.id(itemGroup.identifier().toString().replace(":", "_"));
            final BookCategory category = new BookCategory(PatchouliIntegration.getItemGroupCategory(itemGroup, i), identifier, this.book);
            this.categories.put(identifier, category);

            int c = 0;
            for (String content : itemGroup.content()) {
                final SlimefunRecipeCategory recipeCategory = recipeCategories.get(content);
                if (recipeCategory != null) {
                    final Identifier recipe = Utils.id(content);
                    final BookEntry recipeEntry = new BookEntry(PatchouliIntegration.getRecipeEntry(category, recipeCategory, c), recipe, book, null);
                    recipeEntry.initCategory(recipe, ignored -> category);
                    this.entries.put(recipe, recipeEntry);
                }
                c++;
            }
            i++;
        }
    }
}
