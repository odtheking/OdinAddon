package com.odtheking.odinaddon.features.impl.render

import com.odtheking.odin.clickgui.settings.impl.ActionSetting
import com.odtheking.odin.clickgui.settings.impl.BooleanSetting
import com.odtheking.odin.clickgui.settings.impl.NumberSetting
import com.odtheking.odin.features.Module

object Animations : Module(
    name = "Animations",
    description = "Changes the appearance of the first-person view model."
) {
    @JvmStatic val x by NumberSetting("X", 0.0f, -2.5, 1.5, 0.05, desc = "Moves the held item. Default: 0")
    @JvmStatic val y by NumberSetting("Y", 0.0f, -1.5, 1.5, 0.05, desc = "Moves the held item. Default: 0")
    @JvmStatic val z by NumberSetting("Z", 0.0f, -1.5, 3.0, 0.05, desc = "Moves the held item. Default: 0")
    @JvmStatic val yaw by NumberSetting("Yaw", 0.0f, -180.0, 180.0, 1.0, desc = "Rotates your held item. Default: 0")
    @JvmStatic val pitch by NumberSetting("Pitch", 0.0f, -180.0, 180.0, 1.0, desc = "Rotates your held item. Default: 0")
    @JvmStatic val roll by NumberSetting("Roll", 0.0f, -180.0, 180.0, 1.0, desc = "Rotates your held item. Default: 0")

    val noEquipReset by BooleanSetting("No Equip Reset", false, desc = "Disables the equipping animation when switching items.")
    val noSwing by BooleanSetting("No Swing", false, desc = "Prevents your item from visually swinging forward.")

    private val reset by ActionSetting("Reset", desc = "Resets the settings to their default values.") {
        settings.forEach { it.reset() }
    }

    @JvmStatic
    val shouldNoEquipReset get() = enabled && noEquipReset

    @JvmStatic
    val shouldStopSwing get() = enabled && noSwing

    @JvmStatic
    val isActive get() = enabled
}