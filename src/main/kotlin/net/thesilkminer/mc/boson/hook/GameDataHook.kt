package net.thesilkminer.mc.boson.hook

import net.minecraftforge.fml.common.Loader
import net.minecraftforge.fml.common.ProgressManager
import net.minecraftforge.fml.common.event.FMLEvent
import net.thesilkminer.mc.boson.api.event.BosonPreAvailableEvent

@Suppress("unused")
object GameDataHook {
    private const val PRE_INIT_MARKER = "\$Boson\$marker\$UsePreInit"

    private fun reflectProgressBarFromLoader() = with(Loader::class.java.getDeclaredField("progressBar")) {
        this.isAccessible = true
        this.get(Loader.instance()) as ProgressManager.ProgressBar
    }

    @JvmStatic
    fun showPreInitializationCreationBar() {
        this.reflectProgressBarFromLoader().step(PRE_INIT_MARKER)
    }

    @JvmStatic
    fun stepPopulatingRegistryBar() {
        this.reflectProgressBarFromLoader().step("Populating Registries")
    }

    @JvmStatic
    fun stepBosonAvailableBar() {
        this.reflectProgressBarFromLoader().step("Propagating Loading Completion")
    }

    @JvmStatic
    fun getDescription(event: FMLEvent) = if (event is BosonPreAvailableEvent) "BosonPreAvailable" else event.description()!!

    @JvmStatic
    fun getSortingComparator() = Comparator<Any> { o1, o2 -> o1.toString().compareTo(o2.toString(), ignoreCase = true) }
}
