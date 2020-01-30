@file:JvmName("NSSU")

package net.thesilkminer.mc.boson.prefab.naming

import net.minecraft.util.ResourceLocation
import net.thesilkminer.mc.boson.api.id.NameSpacedString

fun NameSpacedString.toResourceLocation() = ResourceLocation(this.nameSpace, this.path)
fun ResourceLocation.toNameSpacedString() = NameSpacedString(this.path, this.namespace)

fun String.toNameSpacedString(defaultNamespace: String? = null) =
        if (this.indexOf(':') != -1) {
            val (namespace, path) = this.split(':', limit = 2)
            NameSpacedString(namespace, path)
        } else {
            NameSpacedString(defaultNamespace, this)
        }
