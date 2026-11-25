package com.odtheking.odinaddon.features.render

import com.odtheking.odin.OdinMod.mc
import com.odtheking.odin.clickgui.settings.AlwaysActive
import com.odtheking.odin.clickgui.settings.impl.MapSetting
import com.odtheking.odin.features.Module
import com.odtheking.odin.features.impl.render.ClickGUIModule.PanelData
import com.odtheking.odinaddon.OdinAddon

/**
 * Holds the addon's own Click GUI state.
 *
 * Odin only shows its own modules in its Click GUI, so every addon creates its own instance (see [OdinAddon.clickGUI]).
 * Panel positions are stored here, so they are saved to the addon's config instead of Odin's.
 */
@AlwaysActive
object AddonClickGUI : Module(
    name = "OdinAddon GUI", // module names have to be unique across Odin and all addons
    description = "Opens the OdinAddon Click GUI when the keybind is pressed."
) {
    val panelSetting by MapSetting("Panel Data", mutableMapOf<String, PanelData>())

    override fun onKeybind() {
        mc.setScreenAndShow(OdinAddon.clickGUI)
    }
}