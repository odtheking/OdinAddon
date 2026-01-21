package com.odtheking.odinaddon.mixin;

import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import com.odtheking.odinaddon.features.impl.render.Animations;
import net.minecraft.world.entity.LivingEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(LivingEntity.class)
public abstract class LivingEntityMixin {

    @ModifyReturnValue(method = "getCurrentSwingDuration", at = @At("RETURN"))
    private int odinaddon$overrideSwingDuration(int original) {
        if (Animations.isActive() && Animations.getIgnoreHaste()) return Math.max(Animations.getSpeed(), 1);
        return original;
    }
}
