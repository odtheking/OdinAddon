package com.odtheking.odinaddon

import com.odtheking.odin.clickgui.settings.impl.HUDSetting
import com.odtheking.odin.clickgui.settings.impl.KeybindSetting
import com.odtheking.odin.events.core.EventBus
import com.odtheking.odin.features.ModuleManager
import com.odtheking.odin.features.Module
import com.odtheking.odinaddon.features.impl.render.Animations
import net.fabricmc.api.ClientModInitializer

object OdinAnimations : ClientModInitializer {

    override fun onInitializeClient() {
        listOf(this).forEach { EventBus.subscribe(it) }

        addModules(Animations)
    }

    @JvmStatic
    fun addModules(vararg modules: Module) {
        modules.forEach { module ->
            ModuleManager.modules.add(module)

            module.key?.let {
                module.register(KeybindSetting("Keybind", it, "Toggles the module").apply {
                    onPress = { module.onKeybind() }
                })
            }

            for (setting in module.settings) {
                if (setting is KeybindSetting) ModuleManager.keybindSettingsCache.add(setting)
                if (setting is HUDSetting) ModuleManager.hudSettingsCache.add(setting)
            }
        }
    }
}
