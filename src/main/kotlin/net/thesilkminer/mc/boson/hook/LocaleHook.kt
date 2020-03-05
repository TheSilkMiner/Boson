package net.thesilkminer.mc.boson.hook

import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.google.gson.JsonElement
import com.google.gson.JsonObject
import com.google.gson.JsonParseException
import net.minecraft.client.resources.IResource
import net.minecraft.client.resources.IResourceManager
import net.minecraft.util.JsonUtils
import net.minecraft.util.ResourceLocation
import net.thesilkminer.kotlin.commons.lang.extractMessage
import net.thesilkminer.mc.boson.MOD_NAME
import net.thesilkminer.mc.boson.api.log.L
import java.io.BufferedReader
import java.io.IOException
import java.io.InputStreamReader
import java.io.PrintWriter
import java.io.StringWriter

@Suppress("unused")
object LocaleHook {
    private val l = L(MOD_NAME, "Locale Hook")
    private val jsonReader: Gson = GsonBuilder().setPrettyPrinting().serializeNulls().disableHtmlEscaping().create()

    @JvmStatic
    fun hookJsonLocale(resourceDomain: String, language: String, resourceManager: IResourceManager, properties: MutableMap<String, String>) =
            runHook(resourceDomain, language, resourceManager, properties)

    private fun runHook(resourceDomain: String, language: String, resourceManager: IResourceManager, properties: MutableMap<String, String>) =
            this.loadAllJsonFilesForLanguage(ResourceLocation(resourceDomain, "lang/$language.json"), resourceManager, properties)

    private fun loadAllJsonFilesForLanguage(resourceName: ResourceLocation, resourceManager: IResourceManager, properties: MutableMap<String, String>) =
            this.loadJsonLocaleFiles(resourceName, resourceManager.safelyGetAllResources(resourceName), properties)

    private fun loadJsonLocaleFiles(name: ResourceLocation, jsonList: List<IResource>, properties: MutableMap<String, String>) =
            jsonList.forEach { this.loadJsonLocale(name, it, properties) }

    private fun loadJsonLocale(name: ResourceLocation, resource: IResource, properties: MutableMap<String, String>) =
            properties.putAll(this.tryParseFile(name, this.readJsonFile(resource)))

    private fun readJsonFile(resource: IResource): String = try {
        BufferedReader(InputStreamReader(resource.inputStream)).lineSequence().joinToString(separator = "\n")
    } catch (e: IOException) {
        "" // Maybe the file doesn't exist, but this isn't an error
    }

    private fun tryParseFile(name: ResourceLocation, content: String): Map<String, String> = try {
        if (content.isEmpty()) {
            mapOf()
        } else {
            val jsonObject = jsonReader.fromJson(content, JsonObject::class.java)
            jsonObject.entrySet()
                    .asSequence()
                    .map { Pair(it.key, it.value.attemptAsString(it.key)) }
                    .toMap()
        }
    } catch (e: JsonParseException) {
        val errorMessage = e.extractMessage()
        val exceptionType = e::class.simpleName
        val stringWriter = StringWriter()
        e.printStackTrace(PrintWriter(stringWriter))
        val msgFixedPart = """
                    An error has occurred while attempting to load the language file '$name'.
                    Loading will now be skipped and the file will be considered effectively empty!
                    
                    Error message: $errorMessage
                    Exception type: $exceptionType
                    Name of the file that caused the error: $name
                """.trimIndent()
        l.bigError("$msgFixedPart\n\nThe full stacktrace is in the text that follows:\n$stringWriter", dumpStack = L.DumpStackBehavior.DO_NOT_DUMP)
        mapOf()
    }

    private fun JsonElement.attemptAsString(name: String) = JsonUtils.getString(this, name)
    private fun IResourceManager.safelyGetAllResources(location: ResourceLocation) = try { this.getAllResources(location).toList() } catch (e: IOException) { listOf<IResource>() }
}
