package com.odtheking.odinaddon.mixin;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.odtheking.odinaddon.features.impl.render.Animations;
import net.minecraft.client.player.FirstPersonHandsAndItems;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(FirstPersonHandsAndItems.class)
public abstract class FirstPersonHandsAndItemsMixin {

    @Shadow
    private float mainHandHeight;

    @Shadow
    private float offHandHeight;

    @Shadow
    private float oMainHandHeight;

    @Shadow
    private float oOffHandHeight;

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
                    target = "Lnet/minecraft/client/player/LocalPlayer;getItemSwapScale(F)F"
            )
    )
    private float overrideAttackStrengthScale(float originalValue) {
        if (Animations.getShouldNoEquipReset() || Animations.getShouldStopSwing()) return 1f;
        return originalValue;
    }

    @Inject(method = "shouldInstantlyReplaceVisibleItem", at = @At("HEAD"), cancellable = true)
    private void forceInstantItemSwap(ItemStack oldItem, ItemStack newItem, LocalPlayer player, CallbackInfoReturnable<Boolean> cir) {
        if (Animations.getShouldNoEquipReset()) cir.setReturnValue(true);
    }
}