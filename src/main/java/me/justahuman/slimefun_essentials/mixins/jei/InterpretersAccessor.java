package me.justahuman.slimefun_essentials.mixins.jei;

import mezz.jei.api.ingredients.subtypes.ISubtypeInterpreter;
import mezz.jei.library.ingredients.subtypes.SubtypeInterpreters;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

import java.util.Map;

@Mixin(SubtypeInterpreters.class)
public interface InterpretersAccessor {
    @Accessor(remap = false) Map<Object, ISubtypeInterpreter<?>> getMap();
}
