package com.odtheking.odinaddon.features.impl.skyblock

import com.odtheking.odin.clickgui.settings.Setting.Companion.withDependency
import com.odtheking.odin.clickgui.settings.impl.ActionSetting
import com.odtheking.odin.clickgui.settings.impl.BooleanSetting
import com.odtheking.odin.clickgui.settings.impl.ColorSetting
import com.odtheking.odin.clickgui.settings.impl.DropdownSetting
import com.odtheking.odin.clickgui.settings.impl.KeybindSetting
import com.odtheking.odin.clickgui.settings.impl.ListSetting
import com.odtheking.odin.clickgui.settings.impl.MapSetting
import com.odtheking.odin.clickgui.settings.impl.NumberSetting
import com.odtheking.odin.clickgui.settings.impl.SelectorSetting
import com.odtheking.odin.clickgui.settings.impl.StringSetting
import com.odtheking.odin.events.TickEvent
import com.odtheking.odin.events.core.on
import com.odtheking.odin.features.Module
import com.odtheking.odin.utils.Colors
import com.odtheking.odin.utils.modMessage
import com.odtheking.odin.utils.render.textDim
import org.lwjgl.glfw.GLFW

object TestModule : Module(
    name = "Test Module",
    description = "This is a test module's description."
) {
    // These are visible settings that will render under this module in the GUI
    private val boolean by BooleanSetting("Example Boolean Setting", true, desc = "Description.")
    private val number by NumberSetting("Example Number Setting", 50, 0, 100, desc = "Description.")
    private val select by SelectorSetting("Example Select", "Option 1", listOf("Option 1", "Option 2", "Option 3"), desc = "Description.")
    private val string by StringSetting("Example String Setting", "Hello, Odin!", desc = "Description.")
    private val action by ActionSetting("Example Action Setting", "Description") {
        modMessage("You clicked the action setting!")
    }
    private val color by ColorSetting("Example Color Setting", Colors.MINECRAFT_RED, true, desc = "Description.")

    private val dropdown by DropdownSetting("Example Dropdown Setting")
    private val keybind by KeybindSetting("Example Keybind Setting", GLFW.GLFW_KEY_UNKNOWN, desc = "Description.").withDependency { dropdown }

    // These are not visible settings, but are used to store data under the module
    private val list by ListSetting("Example List Setting", mutableListOf("Item 1", "Item 2"))
    private val map by MapSetting("Example Map Setting", mutableMapOf("Key1" to "Value1", "Key2" to "Value2"))

    // This is how you can register a HUD under the module
    private val hud by HUD("Example HUD", desc = "This is a hud.") {
        textDim("Hello, Odin HUD!", 0, 0) // This function automatically returns the width and height of the text rendered
    }

    init {
        on<TickEvent.End> {
            modMessage("Tick End Event fired from Test Module!")
        }
    }
}