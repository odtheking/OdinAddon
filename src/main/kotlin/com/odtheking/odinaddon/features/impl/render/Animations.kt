package com.odtheking.odinaddon.features.impl.render

import com.odtheking.odin.clickgui.settings.Setting.Companion.withDependency
import com.odtheking.odin.clickgui.settings.impl.BooleanSetting
import com.odtheking.odin.clickgui.settings.impl.NumberSetting
import com.odtheking.odin.features.Module

object Animations : Module(
    name = "Animations",
    description = "Changes the appearance of the first-person view model."
) {
    @JvmStatic val size by NumberSetting("Size", 1f, 0.1, 3, 0.05, desc = "Scales the held item. Default: 1")
    @JvmStatic val x by NumberSetting("X", 0f, -2.5, 1.5, 0.05, desc = "Moves the held item. Default: 0")
    @JvmStatic val y by NumberSetting("Y", 0f, -1.5, 1.5, 0.05, desc = "Moves the held item. Default: 0")
    @JvmStatic val z by NumberSetting("Z", 0f, -1.5, 3.0, 0.05, desc = "Moves the held item. Default: 0")
    @JvmStatic val yaw by NumberSetting("Yaw", 0f, -180, 180, 1, desc = "Rotates your held item. Default: 0")
    @JvmStatic val pitch by NumberSetting("Pitch", 0f, -180, 180, 1, desc = "Rotates your held item. Default: 0")
    @JvmStatic val roll by NumberSetting("Roll", 0f, -180, 180, 1, desc = "Rotates your held item. Default: 0")
    @JvmStatic val ignoreHaste by BooleanSetting("Ignore Effects", false, desc = "Makes the chosen speed override haste modifiers.")
    @JvmStatic val speed by NumberSetting("Speed", 6, 0, 16, 1, desc = "Speed of the swing animation.").withDependency { ignoreHaste }

    val noEquipReset by BooleanSetting("No Equip Reset", false, desc = "Disables the equipping animation when switching items.")
    val noSwing by BooleanSetting("No Swing", false, desc = "Prevents your item from visually swinging forward.")

    @JvmStatic
    val shouldNoEquipReset get() = enabled && noEquipReset

    @JvmStatic
    val shouldStopSwing get() = enabled && noSwing

    @JvmStatic
    val isActive get() = enabled
}