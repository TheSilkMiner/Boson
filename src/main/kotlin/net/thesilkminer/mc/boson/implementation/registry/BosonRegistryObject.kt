package net.thesilkminer.mc.boson.implementation.registry

import net.minecraft.util.ResourceLocation
import net.minecraftforge.common.MinecraftForge
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent
import net.minecraftforge.registries.IForgeRegistry
import net.minecraftforge.registries.IForgeRegistryEntry
import net.thesilkminer.kotlin.commons.lang.reloadableLazy
import net.thesilkminer.kotlin.commons.lang.uncheckedCast
import net.thesilkminer.mc.boson.MOD_NAME
import net.thesilkminer.mc.boson.api.event.ObjectHoldersAppliedEvent
import net.thesilkminer.mc.boson.api.id.NameSpacedString
import net.thesilkminer.mc.boson.api.log.L
import net.thesilkminer.mc.boson.api.registry.RegistryObject

internal class BosonRegistryObject<T : IForgeRegistryEntry<in T>> private constructor(override val name: NameSpacedString,
                                                                                      private val targetRegistryName: NameSpacedString?,
                                                                                      private val objectGetter: (NameSpacedString) -> T?) : RegistryObject<T> {
    companion object {
        private val allRegistryObjects = mutableListOf<BosonRegistryObject<*>>()
        private val l = L(MOD_NAME, "Registry Object Reloading")

        init { MinecraftForge.EVENT_BUS.register(this) }

        internal fun <T : IForgeRegistryEntry<T>, U : T> build(name: NameSpacedString, registry: IForgeRegistry<T>): RegistryObject<U> {
            return BosonRegistryObject(name, registry.name?.let { NameSpacedString(it.namespace, it.path) }) { registry.findTarget(it) }
        }

        internal operator fun <T : IForgeRegistryEntry<T>, U : T> invoke(name: NameSpacedString, registry: IForgeRegistry<T>): RegistryObject<U> = build(name, registry)

        @SubscribeEvent
        fun onObjectHoldersApplication(event: ObjectHoldersAppliedEvent) = this.allRegistryObjects.forEach(BosonRegistryObject<*>::reload)

        private fun <T : IForgeRegistryEntry<T>, U : T> IForgeRegistry<T>.findTarget(name: NameSpacedString): U? {
            // It could be cleaner yeah, but it works
            val entryName = ResourceLocation(name.nameSpace, name.path)
            return if (this.containsKey(entryName)) this.getValue(entryName)?.uncheckedCast<U>() else null
        }
    }

    init { allRegistryObjects += this }

    private val reloadableLazy = reloadableLazy { this.objectGetter(this.name) }
    override val value: T? by reloadableLazy

    private fun reload() {
        l.debug("Reloading entry '${this.name}' from registry '${this.targetRegistryName ?: "ERROR TYPE"}'")
        this.reloadableLazy.reload()
    }

    internal fun hotReload() { this.reloadableLazy.reload() }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other == null || this::class != other::class) return false
        if ((other as? BosonRegistryObject<*>) == null) return false
        return this.name == other.name && this.targetRegistryName == other.targetRegistryName
    }

    override fun hashCode() = 31 * this.name.hashCode() + (this.targetRegistryName?.hashCode() ?: 0)

    override fun toString() = "RegistryObject{name='${this.name}',targetRegistryName='${this.targetRegistryName}'}"
}
