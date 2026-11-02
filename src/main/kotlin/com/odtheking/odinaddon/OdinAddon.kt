package com.odtheking.odinaddon

import com.odtheking.odin.config.ModuleConfig
import com.odtheking.odin.features.ModuleManager
import com.odtheking.odinaddon.commands.odinAddonCommand
import net.fabricmc.api.ClientModInitializer
import net.fabricmc.fabric.api.client.command.v2.ClientCommandRegistrationCallback
import com.odtheking.odinaddon.features.TestModule

object OdinAddon : ClientModInitializer {

    override fun onInitializeClient() {
        println("Odin Addon initialized!")
        
        // Register modules by adding to this list
        val modules=listOf(
            TestModule,
        )
        
        // Register commands by adding to this list
        val commands=listOf(
            odinAddonCommand,
        )
        
        // registers commands
        ClientCommandRegistrationCallback.EVENT.register { dispatcher, _ ->
            commands.forEach { commodore -> commodore.register(dispatcher) }
        }
        
        // registers modules
        ModuleManager.registerModules(ModuleConfig("OdinAddon.json"),*modules.toTypedArray())
    }
}
