package com.odtheking.odinaddon

import com.odtheking.odin.clickgui.settings.impl.HUDSetting
import com.odtheking.odin.clickgui.settings.impl.KeybindSetting
import com.odtheking.odin.events.core.EventBus
import com.odtheking.odin.features.ModuleManager
import com.odtheking.odin.features.Module
import com.odtheking.odinaddon.commands.odinAddonCommand
import com.odtheking.odinaddon.features.impl.skyblock.TestModule
import net.fabricmc.api.ClientModInitializer
import net.fabricmc.fabric.api.client.command.v2.ClientCommandRegistrationCallback

object OdinAddon : ClientModInitializer {

    override fun onInitializeClient() {
        println("Odin Addon initialized!")

        // Register commands by adding to the array
        ClientCommandRegistrationCallback.EVENT.register { dispatcher, _ ->
            arrayOf(odinAddonCommand).forEach { commodore -> commodore.register(dispatcher) }
        }

        // Register objects to event bus by adding to the list
        listOf(this).forEach { EventBus.subscribe(it) }

        // Register modules by adding to the function
        addModules(TestModule)
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
