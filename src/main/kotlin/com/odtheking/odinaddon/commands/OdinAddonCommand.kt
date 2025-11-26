package com.odtheking.odinaddon.commands

import com.github.stivais.commodore.Commodore
import com.github.stivais.commodore.utils.GreedyString
import com.odtheking.odin.utils.modMessage

// Commands are handled via https://github.com/Stivais/Commodore
val odinAddonCommand = Commodore("oa", "odinaddon") {

    runs {
        modMessage("Odin Addon command executed")
    }

    runs { greedy: GreedyString ->
        modMessage("Command with parameter executed: ${greedy.string}")
    }
}