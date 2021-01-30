/*
 * Copyright (C) 2021  TheSilkMiner
 *
 * This file is part of Boson.
 *
 * Boson is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published
 * by the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * Boson is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with Boson.  If not, see <https://www.gnu.org/licenses/>.
 *
 * Contact information:
 * E-mail: thesilkminer <at> outlook <dot> com
 */

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
