package com.odtheking.odinaddon

import com.odtheking.odin.config.ModuleConfig
import com.odtheking.odin.events.core.EventBus
import com.odtheking.odin.features.ModuleManager
import com.odtheking.odinaddon.features.impl.render.Animations
import net.fabricmc.api.ClientModInitializer
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientLifecycleEvents
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayConnectionEvents

object OdinAnimations : ClientModInitializer {
    // Shared addon config file. Multiple addons can point at the same file safely (see ModuleConfigMixin).
    private val config = ModuleConfig("OdinAddon.json")

    override fun onInitializeClient() {
        listOf(this).forEach { EventBus.subscribe(it) }

        ModuleManager.registerModules(config, Animations)

        ClientPlayConnectionEvents.DISCONNECT.register { _, _ ->
            ModuleManager.saveConfigurations()
        }

        ClientLifecycleEvents.CLIENT_STOPPING.register {
            ModuleManager.saveConfigurations()
        }
    }
}
