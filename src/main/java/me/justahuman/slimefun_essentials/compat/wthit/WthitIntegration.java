package me.justahuman.slimefun_essentials.compat.wthit;

import mcp.mobius.waila.api.IClientRegistrar;
import mcp.mobius.waila.api.IWailaClientPlugin;
import me.justahuman.slimefun_essentials.config.ModConfig;

@SuppressWarnings("unused")
class WthitIntegration implements IWailaClientPlugin {
    @Override
    public void register(IClientRegistrar registration) {
        if (!ModConfig.blockFeatures()) {
            return;
        }
        registration.dataContext(new SlimefunBlockComponentProvider(), SlimefunBlockComponentProvider.class);
    }
}
