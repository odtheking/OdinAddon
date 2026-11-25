package com.odtheking.odinaddon.commands

import com.github.stivais.commodore.Commodore
import com.github.stivais.commodore.utils.GreedyString
import com.odtheking.odin.OdinMod.mc
import com.odtheking.odin.utils.modMessage
import com.odtheking.odinaddon.OdinAddon

// Commands are handled via https://github.com/Stivais/Commodore
val odinAddonCommand = Commodore("oa", "odinaddon") {

    // opens this addon's Click GUI
    runs {
        mc.schedule { mc.setScreenAndShow(OdinAddon.clickGUI) }
    }

    runs { greedy: GreedyString ->
        modMessage("Command with parameter executed: ${greedy.string}")
    }
}