package me.justahuman.slimefun_essentials.compat.cloth_config;

import me.justahuman.slimefun_essentials.config.ModConfig;
import me.justahuman.slimefun_essentials.utils.CompatUtils;
import me.shedaniel.clothconfig2.api.ConfigBuilder;
import me.shedaniel.clothconfig2.api.ConfigCategory;
import me.shedaniel.clothconfig2.api.ConfigEntryBuilder;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.text.Text;

import java.util.ArrayList;
import java.util.List;

public class ConfigScreen {
    public static Screen buildScreen(Screen parent) {
        final ConfigBuilder builder = ConfigBuilder.create()
                .setParentScreen(parent)
                .setTitle(Text.translatable("slimefun_essentials.title"));

        final ConfigEntryBuilder entryBuilder = builder.entryBuilder();
        final ConfigCategory generalCategory = builder.getOrCreateCategory(Text.translatable("slimefun_essentials.config.category.general"));
        final ConfigCategory visualCategory = builder.getOrCreateCategory(Text.translatable("slimefun_essentials.config.category.visual"));
        final ConfigCategory serverCategory = builder.getOrCreateCategory(Text.translatable("slimefun_essentials.config.category.server"));

        /* General Config Options */

        generalCategory.addEntry(entryBuilder.startBooleanToggle(Text.translatable("slimefun_essentials.config.option.block_features"), ModConfig.blockFeatures())
                .setDefaultValue(true)
                .setTooltip(Text.translatable("slimefun_essentials.config.option.block_features.tooltip"))
                .setRequirement(CompatUtils::isBlockFeatureModLoaded)
                .setSaveConsumer(ModConfig::setBlockFeatures)
                .build());

        /*generalCategory.addEntry(entryBuilder.startBooleanToggle(Text.translatable("slimefun_essentials.config.option.custom_guide"), ModConfig.customGuide())
                .setDefaultValue(true)
                .setTooltip(Text.translatable("slimefun_essentials.config.option.custom_guide.tooltip"))
                .setRequirement(CompatUtils::isPatchouliLoaded)
                .setSaveConsumer(ModConfig::setCustomGuide)
                .build());*/

        generalCategory.addEntry(entryBuilder.startBooleanToggle(Text.translatable("slimefun_essentials.config.option.recipe_features"), ModConfig.recipeFeatures())
                .setDefaultValue(true)
                .setTooltip(Text.translatable("slimefun_essentials.config.option.recipe_features.tooltip"))
                .setRequirement(CompatUtils::isRecipeModLoaded)
                .setSaveConsumer(ModConfig::setRecipeFeatures)
                .build());

        generalCategory.addEntry(entryBuilder.startStrList(Text.translatable("slimefun_essentials.config.option.addons"), ModConfig.getAddons())
                .setDefaultValue(new ArrayList<>(List.of("Slimefun", "InfinityExpansion")))
                .setTooltip(Text.translatable("slimefun_essentials.config.option.addons.tooltip"))
                .setSaveConsumer(ModConfig::setAddons)
                .build());

        /* Visual */

        visualCategory.addEntry(entryBuilder.startBooleanToggle(Text.translatable("slimefun_essentials.config.option.replace_item_identifiers"), ModConfig.replaceItemIdentifiers())
                .setDefaultValue(true)
                .setTooltip(Text.translatable("slimefun_essentials.config.option.replace_item_identifiers.tooltip"))
                .setSaveConsumer(ModConfig::setReplaceItemIdentifiers)
                .build());

        visualCategory.addEntry(entryBuilder.startBooleanToggle(Text.translatable("slimefun_essentials.config.option.hide_background_tooltips"), ModConfig.hideBackgroundTooltips())
                .setDefaultValue(true)
                .setTooltip(Text.translatable("slimefun_essentials.config.option.hide_background_tooltips.tooltip"))
                .setSaveConsumer(ModConfig::setHideBackgroundTooltips)
                .build());

        /* Server Config Options */

        serverCategory.addEntry(entryBuilder.startBooleanToggle(Text.translatable("slimefun_essentials.config.option.require_server_connection"), ModConfig.requireServerConnection())
                .setDefaultValue(true)
                .setTooltip(Text.translatable("slimefun_essentials.config.option.require_server_connection.tooltip"))
                .setSaveConsumer(ModConfig::setRequireServerConnection)
                .build());

        serverCategory.addEntry(entryBuilder.startBooleanToggle(Text.translatable("slimefun_essentials.config.option.enable_server_whitelist"), ModConfig.enableServerWhitelist())
                .setDefaultValue(false)
                .setTooltip(Text.translatable("slimefun_essentials.config.option.enable_server_whitelist.tooltip"))
                .setSaveConsumer(ModConfig::setEnableServerWhitelist)
                .build());

        serverCategory.addEntry(entryBuilder.startStrList(Text.translatable("slimefun_essentials.config.option.server_whitelist"), ModConfig.getServerWhitelist())
                .setDefaultValue(new ArrayList<>())
                .setTooltip(Text.translatable("slimefun_essentials.config.option.server_whitelist.tooltip"))
                .setSaveConsumer(ModConfig::setServerWhitelist)
                .build());

        serverCategory.addEntry(entryBuilder.startBooleanToggle(Text.translatable("slimefun_essentials.config.option.auto_toggle_addons"), ModConfig.autoToggleAddons())
                .setDefaultValue(true)
                .setTooltip(Text.translatable("slimefun_essentials.config.option.auto_toggle_addons.tooltip"))
                .setSaveConsumer(ModConfig::setAutoToggleAddons)
                .build());

        serverCategory.addEntry(entryBuilder.startBooleanToggle(Text.translatable("slimefun_essentials.config.option.auto_manage_items"), ModConfig.autoManageItems())
                .setDefaultValue(true)
                .setTooltip(Text.translatable("slimefun_essentials.config.option.auto_manage_items.tooltip"))
                .setSaveConsumer(ModConfig::setAutoManageItems)
                .build());

        builder.setSavingRunnable(ModConfig::saveConfig);

        return builder.build();
    }
}