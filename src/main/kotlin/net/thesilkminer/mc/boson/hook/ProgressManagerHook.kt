package net.thesilkminer.mc.boson.hook

@Suppress("unused")
object ProgressManagerHook {

    @JvmStatic
    fun hookFmlProgressBarCreation(title: String, steps: Int): Int {
        val stack = Throwable().fillInStackTrace().stackTrace
        return if (stack[3].className == "net.minecraftforge.fml.common.Loader" && title == "Loading") {
            // TODO("Logger")
            println("Found the ProgressBar creation for Loader: replacing steps")
            steps + 4 // Registry creation, registry firing (happens twice), post post initialization
        } else {
            steps
        }
    }
}
