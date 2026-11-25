package com.odtheking.odinanimations

import com.odtheking.odin.features.ModuleManager
import com.odtheking.odinanimations.features.impl.render.Animations
import net.fabricmc.api.ClientModInitializer

object OdinAnimations : ClientModInitializer {

    override fun onInitializeClient() {
        // Registered into Odin's own config so the modules show up in Odin's Click GUI (it is built from odinConfig).
        // Must happen during init, Odin's Click GUI is only built once on first open.
        ModuleManager.registerModules(ModuleManager.odinConfig, Animations)
    }
}
