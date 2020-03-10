package net.thesilkminer.mc.boson.implementation.resource

import net.minecraft.client.resources.FileResourcePack
import net.minecraft.client.resources.FolderResourcePack
import net.minecraft.client.resources.IResourcePack
import net.minecraftforge.fml.common.Loader
import net.thesilkminer.mc.boson.MOD_NAME
import net.thesilkminer.mc.boson.api.log.L
import java.awt.image.BufferedImage
import java.io.ByteArrayInputStream
import java.io.File
import java.io.IOException
import java.io.InputStream
import java.nio.file.Path
import javax.imageio.ImageIO

private interface InjectedBosonResourcePack {
    val owner: String
    val name: String
    val description: String
}

internal class InjectedBosonDirectoryResourcePack(override val owner: String, override val name: String, override val description: String, fileRoot: File)
    : FolderResourcePack(fileRoot), InjectedBosonResourcePack {

    override fun getPackName(): String = this.name

    override fun getInputStreamByName(name: String): InputStream =
            try { super.getInputStreamByName(name) } catch (e: IOException) { if ("pack.mcmeta" == name) this.packMeta() else throw e }

    override fun getPackImage(): BufferedImage {
        val modMetadata = Loader.instance().modList.find { it.modId == owner }?.metadata ?: throw IOException("Owner doesn't exist!")
        return ImageIO.read(this.getInputStreamByName(modMetadata.logoFile))
    }
}

internal class InjectedBosonFileResourcePack(override val owner: String, override val name: String, override val description: String, fileRoot: File)
    : FileResourcePack(fileRoot), InjectedBosonResourcePack {

    override fun getPackName(): String = this.name

    override fun getInputStreamByName(name: String): InputStream =
            try { super.getInputStreamByName(name) } catch (e: IOException) { if ("pack.mcmeta" == name) this.packMeta() else throw e }

    override fun getPackImage(): BufferedImage {
        val modMetadata = Loader.instance().modList.find { it.modId == owner }?.metadata ?: throw IOException("Owner doesn't exist!")
        return ImageIO.read(this.getInputStreamByName(modMetadata.logoFile))
    }
}

private val l = L(MOD_NAME, "Resource Pack Creator")

internal fun createResourcePackFrom(owner: String, name: String, description: String, root: Path, file: Boolean): IResourcePack? =
        root.toFileOrNull()?.let {
            @Suppress("USELESS_CAST") // Kotlin at it again with type inference that the compiler doesn't know. I bet there is something weird here.
            (if (file) ::InjectedBosonFileResourcePack else ::InjectedBosonDirectoryResourcePack)(owner, name, description, it) as IResourcePack
        }

private fun Path.toFileOrNull() = try {
    this.toFile()
} catch (e: UnsupportedOperationException) {
    l.bigError("Unable to convert '$this' to a file! This is currently not supported!")
    //TODO("Support this")
    null
}

private fun InjectedBosonResourcePack.packMeta(): InputStream {
    l.error("Pack '${this.name}' owned  by '${this.owner}' does not specify a 'pack.mcmeta' file! We will replace it with a fake one! Note that this is NOT supported!")
    return ByteArrayInputStream("""
        {
            "pack": {
                "description": "${this.description.safeDesc()}",
                "pack_format": 3
            }
        }
    """.trimIndent().toByteArray(Charsets.UTF_8))
}

private fun String.safeDesc() = this
        .replace('"', '\'')
        .replace("\n", "\\n")
        .replace("\r", "")
        .replace("\b", "")
