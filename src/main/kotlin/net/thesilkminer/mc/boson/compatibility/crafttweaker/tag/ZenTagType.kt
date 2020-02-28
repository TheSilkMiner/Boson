package net.thesilkminer.mc.boson.compatibility.crafttweaker.tag

import crafttweaker.annotations.ZenRegister
import net.thesilkminer.mc.boson.api.tag.TagType
import net.thesilkminer.mc.boson.compatibility.crafttweaker.toZen
import stanhebben.zenscript.annotations.ZenClass
import stanhebben.zenscript.annotations.ZenGetter

@ZenClass("net.thesilkminer.mc.boson.zen.tag.TagType")
@ZenRegister
class ZenTagType<T : Any>(val tagType: TagType<T>) {
    companion object {
        fun <T : Any> findZenTagType(name: String) =
                (TagType.find<T>(name) ?: throw IllegalStateException("Unable to obtain tag type for unknown type '$name'")).toZen()
    }

    val directoryName: String @ZenGetter get() = this.tagType.directoryName
    val name: String @ZenGetter get() = this.tagType.name

    /*
    val type: KClass<out T>
    val toElement: (NameSpacedString) -> T
     */
}
