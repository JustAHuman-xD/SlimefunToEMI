package me.justahuman.slimefun_essentials;

import me.justahuman.slimefun_essentials.client.SlimefunRegistry;
import me.justahuman.slimefun_essentials.client.payloads.ComponentTypePayload;
import me.justahuman.slimefun_essentials.client.payloads.ItemGroupsPayload;
import me.justahuman.slimefun_essentials.client.payloads.ItemsPayload;
import me.justahuman.slimefun_essentials.client.payloads.RecipeCategoryPayload;
import me.justahuman.slimefun_essentials.client.payloads.RecipeDisplayPayload;
import me.justahuman.slimefun_essentials.compat.cloth_config.ConfigScreen;
import me.justahuman.slimefun_essentials.config.ModConfig;
import me.justahuman.slimefun_essentials.utils.Payloads;
import me.justahuman.slimefun_essentials.utils.CompatUtils;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.networking.v1.PayloadTypeRegistry;
import net.minecraft.client.option.KeyBinding;
import org.lwjgl.glfw.GLFW;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SlimefunEssentials implements ClientModInitializer {
    public static final String MOD_ID = "slimefun_essentials";
    public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

    @Override
    public void onInitializeClient() {
        PayloadTypeRegistry.playS2C().register(Payloads.COMPONENT_TYPE_CHANNEL, ComponentTypePayload.CODEC);
        PayloadTypeRegistry.playS2C().register(Payloads.ITEM_CHANNEL, ItemsPayload.CODEC);
        PayloadTypeRegistry.playS2C().register(Payloads.ITEM_GROUPS_CHANNEL, ItemGroupsPayload.CODEC);
        PayloadTypeRegistry.playS2C().register(Payloads.RECIPE_CATEGORY_CHANNEL, RecipeCategoryPayload.CODEC);
        PayloadTypeRegistry.playS2C().register(Payloads.RECIPE_DISPLAY_CHANNEL, RecipeDisplayPayload.CODEC);
        ModConfig.loadConfig();

        ClientPlayNetworking.registerGlobalReceiver(Payloads.ITEM_CHANNEL, (payload, context) -> {
            if (ModConfig.recipeFeatures() && payload != ItemsPayload.EMPTY) {
                payload.load();
                SlimefunRegistry.loadItemModels();
            }
        });

        ClientPlayNetworking.registerGlobalReceiver(Payloads.ITEM_CHANNEL, (payload, context) -> {
            if (ModConfig.recipeFeatures() && payload != ItemsPayload.EMPTY) {
                payload.load();
            }
        });

        ClientPlayNetworking.registerGlobalReceiver(Payloads.ITEM_CHANNEL, (payload, context) -> {
            if (ModConfig.recipeFeatures() && payload != ItemsPayload.EMPTY) {
                payload.load();
            }
        });

        ClientPlayNetworking.registerGlobalReceiver(Payloads.ITEM_CHANNEL, (payload, context) -> {
            if (ModConfig.recipeFeatures() && payload != ItemsPayload.EMPTY) {
                payload.load();
            }
        });

        if (CompatUtils.isClothConfigLoaded()) {
            final KeyBinding keyBinding = KeyBindingHelper.registerKeyBinding(new KeyBinding("slimefun_essentials.key_bind.open_config", GLFW.GLFW_KEY_F6, "slimefun_essentials.title"));
            ClientTickEvents.END_CLIENT_TICK.register(client -> {
                if (keyBinding.isPressed()) {
                    client.setScreen(ConfigScreen.buildScreen(client.currentScreen));
                }
            });
        }
    }
}