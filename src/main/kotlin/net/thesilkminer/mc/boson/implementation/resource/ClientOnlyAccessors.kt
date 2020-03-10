@file:JvmName("COA")

package net.thesilkminer.mc.boson.implementation.resource

import net.minecraft.client.Minecraft
import net.minecraft.client.resources.IResourcePack
import net.minecraftforge.fml.common.ObfuscationReflectionHelper
import net.thesilkminer.kotlin.commons.lang.uncheckedCast

private val defaultResourcePacksField by lazy { ObfuscationReflectionHelper.findField(Minecraft::class.java, "field_110449_ao").apply { this.isAccessible = true } }

internal val Minecraft.defaultResourcePacks get() = defaultResourcePacksField[this].uncheckedCast<MutableList<IResourcePack>>()
