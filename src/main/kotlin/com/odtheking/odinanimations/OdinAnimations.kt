package com.odtheking.odinanimations

import com.odtheking.odin.config.ModuleConfig
import com.odtheking.odin.features.ModuleManager
import com.odtheking.odinanimations.features.impl.render.Animations
import net.fabricmc.api.ClientModInitializer

object OdinAnimations : ClientModInitializer {

    override fun onInitializeClient() {
        ModuleManager.registerModules(ModuleConfig("OdinAnimations.json"), Animations)
    }
}
