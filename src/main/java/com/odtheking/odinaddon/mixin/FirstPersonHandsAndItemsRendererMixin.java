package com.odtheking.odinaddon.mixin;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.llamalad7.mixinextras.sugar.Local;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import com.odtheking.odinaddon.features.impl.render.Animations;
import net.minecraft.client.renderer.FirstPersonHandsAndItemsRenderer;
import net.minecraft.core.component.DataComponents;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.HumanoidArm;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(FirstPersonHandsAndItemsRenderer.class)
public abstract class FirstPersonHandsAndItemsRendererMixin {

    @Shadow
    protected abstract void applyItemArmAttackTransform(PoseStack poseStack, HumanoidArm arm, float swingProgress);

    @Unique
    private static boolean shouldTransform(ItemStack itemStack) {
        return Animations.isActive() && !itemStack.isEmpty() && !itemStack.has(DataComponents.MAP_ID);
    }

    @WrapOperation(
            method = "submitArmWithItem",
            at = @At(
                    value = "INVOKE",
                    target = "Lcom/mojang/blaze3d/vertex/PoseStack;pushPose()V",
                    ordinal = 0
            )
    )
    private void applyCustomHandAnimation(PoseStack instance, Operation<Void> original, @Local(argsOnly = true) InteractionHand hand, @Local(argsOnly = true) ItemStack itemStack) {
        original.call(instance);

        if (!shouldTransform(itemStack)) return;

        float xOffset = Animations.getX();
        instance.translate(hand == InteractionHand.MAIN_HAND ? xOffset : -xOffset, Animations.getY(), Animations.getZ());

        instance.rotateDegrees(Axis.XP, Animations.getPitch());
        instance.rotateDegrees(Axis.YP, Animations.getYaw());
        instance.rotateDegrees(Axis.ZP, Animations.getRoll());
    }

    @Inject(method = "swingArm", at = @At("HEAD"), cancellable = true)
    private void handleCustomSwingAnimation(float swingProgress, PoseStack poseStack, int invert, HumanoidArm arm, CallbackInfo ci) {
        if (!Animations.getShouldStopSwing()) return;
        ci.cancel();

        this.applyItemArmAttackTransform(poseStack, arm, swingProgress);
    }

    @Inject(
            method = "submitArmWithItem",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/client/renderer/item/ItemStackRenderState;submit(Lcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/client/renderer/SubmitNodeCollector;III)V"
            )
    )
    private void applySizeTransform(CallbackInfo ci, @Local(argsOnly = true) ItemStack itemStack, @Local(argsOnly = true) PoseStack poseStack) {
        if (!shouldTransform(itemStack)) return;

        float size = Animations.getSize();
        poseStack.scale(size, size, size);
    }
}