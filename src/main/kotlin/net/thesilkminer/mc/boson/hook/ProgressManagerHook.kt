package net.thesilkminer.mc.boson.hook

import net.thesilkminer.mc.boson.api.log.L

@Suppress("unused")
object ProgressManagerHook {
    private const val PRE_INIT_MARKER = "\$Boson\$marker\$UsePreInit"
    private const val PRE_INIT_MESSAGE = "Initializing mods Phase 1"
    private val l = L("Boson ASM Hooks", "Progress Manager")

    @JvmStatic
    fun hookFmlProgressBarCreation(title: String, steps: Int): Int {
        val stack = Throwable().fillInStackTrace().stackTrace
        return if (stack[3].className == "net.minecraftforge.fml.common.Loader" && title == "Loading") {
            this.l.info("Found the ProgressBar creation for Loader: replacing steps")
            steps + 4 // Registry creation, registry firing (happens twice), post post initialization
        } else {
            steps
        }
    }

    @JvmStatic
    @Suppress("CascadeIf")
    fun checkForRegistryCreationMessage(message: String): String {
        return if (PRE_INIT_MESSAGE == message) "Creating Registries" else if (PRE_INIT_MARKER == message) PRE_INIT_MESSAGE else message
    }
}
