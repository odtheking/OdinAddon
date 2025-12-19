package com.odtheking.odinaddon.mixin;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.sugar.Local;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import com.odtheking.odinaddon.features.impl.render.Animations;
import net.minecraft.client.player.AbstractClientPlayer;
import net.minecraft.client.renderer.ItemInHandRenderer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.HumanoidArm;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;

@Mixin(ItemInHandRenderer.class)
public abstract class ItemInHandRendererMixin {

    @Shadow
    private float mainHandHeight;

    @Shadow
    private float offHandHeight;

    @Shadow
    private float oMainHandHeight;

    @Shadow
    private float oOffHandHeight;

    @Shadow
    protected abstract void applyItemArmTransform(PoseStack poseStack, HumanoidArm arm, float partialTicks);

    @Shadow
    protected abstract void applyItemArmAttackTransform(PoseStack poseStack, HumanoidArm arm, float swingProgress);

    @WrapOperation(
            method = "renderArmWithItem",
            at = @At(value = "INVOKE", target = "Lcom/mojang/blaze3d/vertex/PoseStack;pushPose()V", ordinal = 0)
    )
    private void applyCustomHandAnimation(PoseStack instance, Operation<Void> original, @Local(argsOnly = true) InteractionHand hand) {
        original.call(instance);
        if (!Animations.isActive()) return;

        float xOffset = Animations.getX();
        float yOffset = Animations.getY();
        float zOffset = Animations.getZ();
        instance.translate(hand == InteractionHand.MAIN_HAND ? xOffset : -xOffset, yOffset, zOffset);
        instance.mulPose(Axis.XP.rotationDegrees(Animations.getPitch()));
        instance.mulPose(Axis.YP.rotationDegrees(Animations.getYaw()));
        instance.mulPose(Axis.ZP.rotationDegrees(Animations.getRoll()));
    }

    @Inject(method = "swingArm", at = @At("HEAD"), cancellable = true)
    private void handleCustomSwingAnimation(float swingProgress, float equipProgress, PoseStack poseStack, int swingTicks, HumanoidArm arm, CallbackInfo ci) {
        if (!Animations.getShouldStopSwing()) return;
        ci.cancel();

        this.applyItemArmTransform(poseStack, arm, equipProgress);
        this.applyItemArmAttackTransform(poseStack, arm, swingProgress);
    }

    @Inject(
            method = "renderArmWithItem",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/client/renderer/ItemInHandRenderer;renderItem(Lnet/minecraft/world/entity/LivingEntity;Lnet/minecraft/world/item/ItemStack;Lnet/minecraft/world/item/ItemDisplayContext;Lcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/client/renderer/MultiBufferSource;I)V"
            )
    )
    private void applySizeTransform(AbstractClientPlayer abstractClientPlayer, float f, float g, InteractionHand interactionHand, float h, ItemStack itemStack, float i, PoseStack poseStack, MultiBufferSource multiBufferSource, int j, CallbackInfo ci, @Local(argsOnly = true) InteractionHand hand) {
        if (Animations.isActive()) poseStack.scale(Animations.getSize(), Animations.getSize(), Animations.getSize());
    }

    @Inject(method = "tick", at = @At("TAIL"))
    private void maintainCustomEquipHeights(CallbackInfo ci) {
        if (Animations.getShouldNoEquipReset()) {
            this.oMainHandHeight = 1.0f;
            this.mainHandHeight = 1.0f;
            this.oOffHandHeight = 1.0f;
            this.offHandHeight = 1.0f;
        }
    }

    @ModifyExpressionValue(
            method = "tick",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/client/player/LocalPlayer;getAttackStrengthScale(F)F"
            )
    )
    private float overrideAttackStrengthScale(float originalValue) {
        if (Animations.getShouldNoEquipReset() || Animations.getShouldStopSwing()) return 1f;
        return originalValue;
    }

    @Inject(method = "shouldInstantlyReplaceVisibleItem", at = @At("HEAD"), cancellable = true)
    private void forceInstantItemSwap(ItemStack oldItem, ItemStack newItem, CallbackInfoReturnable<Boolean> cir) {
        if (Animations.getShouldNoEquipReset()) cir.setReturnValue(true);
    }
}