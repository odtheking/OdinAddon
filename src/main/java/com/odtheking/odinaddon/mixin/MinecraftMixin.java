package com.odtheking.odinaddon.mixin;

import net.minecraft.client.Minecraft;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

// example mixin
@Mixin(Minecraft.class)
public abstract class MinecraftMixin {

    @Inject(method = "onGameLoadFinished", at = @At("TAIL"))
    private void onGameLoadFinished(CallbackInfo ci) {
        System.out.println("Hello from Odin Addon mixin!");
    }
}