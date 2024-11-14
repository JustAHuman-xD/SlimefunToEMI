package me.justahuman.slimefun_essentials;

import me.justahuman.slimefun_essentials.client.ResourceLoader;
import me.justahuman.slimefun_essentials.client.payloads.DisabledItemPayload;
import me.justahuman.slimefun_essentials.client.payloads.SlimefunAddonPayload;
import me.justahuman.slimefun_essentials.client.payloads.SlimefunBlockPayload;
import me.justahuman.slimefun_essentials.compat.cloth_config.ConfigScreen;
import me.justahuman.slimefun_essentials.config.ModConfig;
import me.justahuman.slimefun_essentials.utils.Payloads;
import me.justahuman.slimefun_essentials.utils.CompatUtils;
import me.justahuman.slimefun_essentials.utils.Utils;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientChunkEvents;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayConnectionEvents;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.fabricmc.fabric.api.networking.v1.PayloadTypeRegistry;
import net.fabricmc.fabric.api.resource.ResourceManagerHelper;
import net.fabricmc.fabric.api.resource.SimpleSynchronousResourceReloadListener;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.resource.ResourceManager;
import net.minecraft.resource.ResourceType;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.ChunkPos;
import org.lwjgl.glfw.GLFW;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;

public class SlimefunEssentials implements ClientModInitializer {
    public static final String MOD_ID = "slimefun_essentials";
    public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

    @Override
    public void onInitializeClient() {
        PayloadTypeRegistry.playS2C().register(Payloads.ADDON_CHANNEL, SlimefunAddonPayload.CODEC);
        PayloadTypeRegistry.playS2C().register(Payloads.BLOCK_CHANNEL, SlimefunBlockPayload.CODEC);
        PayloadTypeRegistry.playS2C().register(Payloads.ITEM_CHANNEL, DisabledItemPayload.CODEC);
        ModConfig.loadConfig();

        /*if (CompatUtils.isPatchouliLoaded()) {
            PatchouliIntegration.init();
        }*/

        if (CompatUtils.isClothConfigLoaded()) {
            final KeyBinding keyBinding = KeyBindingHelper.registerKeyBinding(new KeyBinding("slimefun_essentials.key_bind.open_config", GLFW.GLFW_KEY_F6, "slimefun_essentials.title"));
            ClientTickEvents.END_CLIENT_TICK.register(client -> {
                if (keyBinding.isPressed()) {
                    client.setScreen(ConfigScreen.buildScreen(client.currentScreen));
                }
            });
        }
    
        ResourceManagerHelper.get(ResourceType.CLIENT_RESOURCES).registerReloadListener(new SimpleSynchronousResourceReloadListener() {
            @Override
            public Identifier getFabricId() {
                return Utils.id("reload_listener");
            }
    
            @Override
            public void reload(ResourceManager manager) {
                ResourceLoader.clear();
                ResourceLoader.loadResources(manager);
            }
        });
        
        if (ModConfig.blockFeatures()) {
            ClientChunkEvents.CHUNK_LOAD.register(((world, chunk) -> {
                final PacketByteBuf packetByteBuf = PacketByteBufs.create();
                final ChunkPos chunkPos = chunk.getPos();
                packetByteBuf.writeInt(chunkPos.x);
                packetByteBuf.writeInt(chunkPos.z);

            }));

            ClientChunkEvents.CHUNK_UNLOAD.register((world, chunk) -> ResourceLoader.removePlacedChunk(chunk.getPos()));

            ClientPlayNetworking.registerGlobalReceiver(Payloads.BLOCK_CHANNEL, (payload, context) -> {
                // If the id is a space that means it's no longer a slimefun block
                if (payload.id().equals(" ")) {
                    ResourceLoader.removePlacedBlock(payload.pos());
                    return;
                }

                ResourceLoader.addPlacedBlock(payload.pos(), payload.id().toLowerCase());
            });

            ClientPlayConnectionEvents.DISCONNECT.register((handler, minecraftClient) -> ResourceLoader.clearPlacedBlocks());
        }

        if (ModConfig.autoToggleAddons()) {
            final List<String> normalAddons = new ArrayList<>();
            ClientPlayNetworking.registerGlobalReceiver(Payloads.ADDON_CHANNEL, (payload, context) -> {
                if (payload.addon().equals("clear")) {
                    normalAddons.addAll(ModConfig.getAddons());
                    ModConfig.getAddons().clear();
                    return;
                }

                ModConfig.getAddons().add(payload.addon());
            });

            ClientPlayConnectionEvents.DISCONNECT.register(((handler, client) -> {
                if (!normalAddons.isEmpty()) {
                    ModConfig.getAddons().clear();
                    ModConfig.getAddons().addAll(normalAddons);
                    normalAddons.clear();
                }
            }));
        }

        if (ModConfig.autoManageItems()) {
            ClientPlayNetworking.registerGlobalReceiver(Payloads.ITEM_CHANNEL, (payload, context) -> {
                ResourceLoader.blacklistItem(payload.id());
            });

            ClientPlayConnectionEvents.DISCONNECT.register(((handler, client) -> ResourceLoader.clearItemBlacklist()));
        }
    }
}