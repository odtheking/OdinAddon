package com.odtheking.odinaddon.mixin;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.odtheking.odinaddon.features.impl.render.Animations;
import net.minecraft.world.entity.LivingEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(LivingEntity.class)
public abstract class LivingEntityMixin {

    @ModifyExpressionValue(method = "updateSwingTime", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/LivingEntity;getCurrentSwingDuration()I"))
    private int modifySwingDuration(int original) {
        if (Animations.isActive() && Animations.getIgnoreHaste()) return Animations.getSpeed();
        return original;
    }
}