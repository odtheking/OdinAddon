package com.odtheking.odinanimations.mixin;

import com.odtheking.odinanimations.features.impl.render.Animations;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.component.SwingAnimation;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(LivingEntity.class)
public abstract class LivingEntityMixin {

    @Shadow
    public abstract boolean isSwinging();

    @Inject(method = "getModifiedSwingDuration", at = @At("HEAD"), cancellable = true)
    private void modifySwingDuration(SwingAnimation swingAnimation, CallbackInfoReturnable<Integer> cir) {
        if (Animations.isActive() && Animations.getIgnoreHaste()) cir.setReturnValue(Animations.getSpeed());
    }

    @Inject(method = "swing(Lnet/minecraft/world/InteractionHand;Lnet/minecraft/world/item/component/SwingAnimation;Z)Z", at = @At("HEAD"), cancellable = true)
    private void preventReSwing(InteractionHand hand, SwingAnimation swingAnimation, boolean sendToSwingingEntity, CallbackInfoReturnable<Boolean> cir) {
        if (Animations.getShouldNotSwing() && this.isSwinging() && ((LivingEntity) (Object) this).level().isClientSide()) cir.setReturnValue(false);
    }
}