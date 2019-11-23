package net.thesilkminer.mc.boson.prefab.loader.preprocessor

import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.google.gson.JsonObject
import com.google.gson.JsonSyntaxException
import net.thesilkminer.kotlin.commons.lang.extractMessage
import net.thesilkminer.mc.boson.api.id.NameSpacedString
import net.thesilkminer.mc.boson.api.loader.Context
import net.thesilkminer.mc.boson.api.loader.Preprocessor

class JsonConverterPreprocessor : Preprocessor<String, JsonObject> {
    companion object {
        private val jsonReader: Gson = GsonBuilder().setPrettyPrinting().serializeNulls().disableHtmlEscaping().create()
    }

    override fun preProcessData(content: String, identifier: NameSpacedString, globalContext: Context?, phaseContext: Context?): JsonObject? =
            try {
                jsonReader.fromJson(content, JsonObject::class.java)
            } catch (e: JsonSyntaxException) {
                throw JsonSyntaxException(
                        "The file identified by '$identifier' is not a valid JSON file. Please check your syntax.", e
                )
            }
}
