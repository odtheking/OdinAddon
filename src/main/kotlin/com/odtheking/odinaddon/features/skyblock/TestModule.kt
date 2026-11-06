package com.odtheking.odinaddon.features.skyblock

import com.mojang.blaze3d.platform.InputConstants
import com.odtheking.odin.clickgui.settings.RenderableSetting.Companion.withDependency
import com.odtheking.odin.clickgui.settings.impl.*
import com.odtheking.odin.events.TickEvent
import com.odtheking.odin.events.core.on
import com.odtheking.odin.events.core.onReceive
import com.odtheking.odin.features.Category
import com.odtheking.odin.features.Module
import com.odtheking.odin.utils.Colors
import com.odtheking.odin.utils.modMessage
import com.odtheking.odin.utils.render.textDim
import net.minecraft.network.protocol.game.ClientboundSoundPacket

object TestModule : Module(
    name = "Test Module",
    description = "This is a test module's description."
) {
    private val boolean by BooleanSetting("Example Boolean Setting", true, desc = "Description.")
    private val number by NumberSetting("Example Number Setting", 50, 0..100, desc = "Description.")
    private val select by SelectorSetting("Example Select", SelectEnum.OPTION1, "Description.")

    private enum class SelectEnum {
        OPTION1,
        OPTION2,
        OPTION3
    }

    private val string by StringSetting("Example String Setting", "Hello, Odin!", desc = "Description.", placeholder = "Placeholder text")
    private val action by ActionSetting("Example Action Setting", "Description") {
        modMessage("You clicked the action setting!")
    }
    private val color by ColorSetting("Example Color Setting", Colors.MINECRAFT_RED, true, desc = "Description.") // true allows for alpha channel

    private val dropdown by DropdownSetting("Example Dropdown Setting", false, "")
    private val keybind by KeybindSetting("Example Keybind Setting", InputConstants.UNKNOWN.value, desc = "Description.").withDependency { dropdown }

    // These are not visible settings, but are used to store data under the module
    private val list by ListSetting("Example List Setting", mutableListOf("Item 1", "Item 2"))
    private val map by MapSetting("Example Map Setting", mutableMapOf("Key1" to "Value1", "Key2" to "Value2"))

    // This is how you can register a HUD under the module
    private val hud by HUD("Example HUD", desc = "This is a hud.") {
        textDim("Hello, Odin HUD!", 0, 0) // This function automatically returns the width and height of the text rendered
    }

    // This is where you register events to the eventbus
    // Use on<> for Odin's custom events
    // or use onReceive<> / onSend<> for vanilla packets
    init {
        on<TickEvent.End> {
            modMessage("Tick End Event fired from Test Module!")
        }

        onReceive<ClientboundSoundPacket> {
            modMessage("Sound Packet received by Test Module!")
        }
    }
}