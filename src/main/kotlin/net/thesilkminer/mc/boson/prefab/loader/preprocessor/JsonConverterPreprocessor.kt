package net.thesilkminer.mc.boson.prefab.loader.preprocessor

import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.google.gson.JsonObject
import net.thesilkminer.mc.boson.api.id.NameSpacedString
import net.thesilkminer.mc.boson.api.loader.Context
import net.thesilkminer.mc.boson.api.loader.Preprocessor

class JsonConverterPreprocessor : Preprocessor<String, JsonObject> {
    companion object {
        private val jsonReader: Gson = GsonBuilder().setPrettyPrinting().serializeNulls().disableHtmlEscaping().create()
    }

    override fun preProcessData(content: String, identifier: NameSpacedString, globalContext: Context?, phaseContext: Context?): JsonObject =
            jsonReader.fromJson(content, JsonObject::class.java)
}
