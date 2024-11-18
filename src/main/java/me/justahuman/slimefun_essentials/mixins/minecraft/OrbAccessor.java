package me.justahuman.slimefun_essentials.mixins.minecraft;

import net.minecraft.entity.ExperienceOrbEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(ExperienceOrbEntity.class)
public interface OrbAccessor {
    @Accessor void setAmount(int pickingCount);
}
