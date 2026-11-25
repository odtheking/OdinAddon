package com.odtheking.odinaddon

import com.odtheking.odin.clickgui.ClickGUI
import com.odtheking.odin.clickgui.ui.Panel
import com.odtheking.odin.config.ModuleConfig
import com.odtheking.odin.events.core.EventBus
import com.odtheking.odin.features.Module
import com.odtheking.odin.features.ModuleManager
import com.odtheking.odin.features.impl.render.ClickGUIModule.PanelData
import com.odtheking.odinaddon.commands.odinAddonCommand
import com.odtheking.odinaddon.features.render.AddonClickGUI
import com.odtheking.odinaddon.features.skyblock.TestModule
import net.fabricmc.api.ClientModInitializer
import net.fabricmc.fabric.api.client.command.v2.ClientCommandRegistrationCallback

object OdinAddon : ClientModInitializer {

    // Register modules by adding to the list
    private val modules: List<Module> = listOf(AddonClickGUI, TestModule)

    /**
     * This addon's own Click GUI, containing only this addon's modules.
     * Opened with /oa or the keybind in [AddonClickGUI].
     */
    val clickGUI: ClickGUI by lazy {
        ClickGUI(modules.groupBy { it.category }.map { (category, modules) ->
            val data = AddonClickGUI.panelSetting.getOrPut(category.name) { PanelData(category.x, category.y) }
            Panel(category, modules, data)
        })
    }

    override fun onInitializeClient() {
        println("Odin Addon initialized!")

        // registers commands
        ClientCommandRegistrationCallback.EVENT.register { dispatcher, _ ->
            arrayOf(odinAddonCommand).forEach { commodore -> commodore.register(dispatcher) }
        }

        // Register objects to event bus by adding to the list
        listOf(this).forEach { EventBus.subscribe(it) }

        ModuleManager.registerModules(ModuleConfig("OdinAddon.json"), *modules.toTypedArray())
    }
}