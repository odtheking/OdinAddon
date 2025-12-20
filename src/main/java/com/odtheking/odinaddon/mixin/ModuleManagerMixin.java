package com.odtheking.odinaddon.mixin;

import com.odtheking.odin.features.ModuleManager;
import com.odtheking.odinaddon.OdinAnimations;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ModuleManager.class)
public abstract class ModuleManagerMixin {

    @Inject(method = "<clinit>", at = @At("TAIL"))
    private static void onModuleManagerFinishInit(CallbackInfo ci) {
        OdinAnimations.addModules();
    }
}