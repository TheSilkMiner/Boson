package net.thesilkminer.mc.boson.implementation.naming

import net.minecraft.util.ResourceLocation
import net.thesilkminer.mc.boson.api.id.NameSpacedString

class ResourceLocationBackedNameSpacedString(domain: String?, path: String) : NameSpacedString {
    private val backend = ResourceLocation(domain ?: "", path)

    override val nameSpace: String = this.backend.namespace
    override val path: String = this.backend.path
    override fun compareTo(other: NameSpacedString) = this.backend.compareTo(ResourceLocation(other.nameSpace, other.path))
    override fun equals(other: Any?): Boolean = when {
        this === other -> true
        other is ResourceLocationBackedNameSpacedString -> this.backend == other.backend
        other is NameSpacedString -> this == ResourceLocationBackedNameSpacedString(other.nameSpace, other.path)
        else -> this.backend == other
    }
    override fun hashCode() = this.backend.hashCode()
    override fun toString() = this.backend.toString()
}
