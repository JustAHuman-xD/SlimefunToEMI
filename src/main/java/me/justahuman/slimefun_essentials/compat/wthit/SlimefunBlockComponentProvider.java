package me.justahuman.slimefun_essentials.compat.wthit;

import mcp.mobius.waila.api.IBlockAccessor;
import mcp.mobius.waila.api.IBlockComponentProvider;
import mcp.mobius.waila.api.IDataWriter;
import mcp.mobius.waila.api.IPluginConfig;
import me.justahuman.slimefun_essentials.client.ResourceLoader;
import me.justahuman.slimefun_essentials.client.SlimefunItemStack;
import me.justahuman.slimefun_essentials.utils.Utils;
import net.minecraft.text.Text;
import net.minecraft.util.math.BlockPos;

public class SlimefunBlockComponentProvider implements IBlockComponentProvider {
    @Override
    public void appendDataContext(IDataWriter ctx, IBlockAccessor accessor, IPluginConfig config) {
        if (!Utils.shouldFunction()) return;
        final BlockPos blockPos = accessor.getPosition();
        if (!ResourceLoader.isSlimefunItem(blockPos)) return;
        final String id = ResourceLoader.getPlacedId(blockPos).toUpperCase();
        final SlimefunItemStack slimefunItem = ResourceLoader.getSlimefunItem(id);
        if (slimefunItem == null) return;
        final Text itemName = slimefunItem.itemStack().getName();
        ctx.raw().putString("customName", itemName.getString());
    }
}
