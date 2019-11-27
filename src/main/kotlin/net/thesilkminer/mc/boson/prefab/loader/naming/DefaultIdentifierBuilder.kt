package net.thesilkminer.mc.boson.prefab.loader.naming

import net.minecraftforge.fml.common.Loader
import net.thesilkminer.mc.boson.api.bosonApi
import net.thesilkminer.mc.boson.api.loader.Context
import net.thesilkminer.mc.boson.api.loader.IdentifierBuilder
import net.thesilkminer.mc.boson.api.loader.Location
import net.thesilkminer.mc.boson.prefab.loader.modIdContextKey
import org.apache.commons.io.FilenameUtils

class DefaultIdentifierBuilder(private val removeExtension: Boolean = false) : IdentifierBuilder {
    override fun makeIdentifier(location: Location, globalContext: Context?, phaseContext: Context?) =
            bosonApi.constructNameSpacedString(
                    nameSpace = location.additionalContext?.get(modIdContextKey) ?: Loader.instance().activeModContainer()?.modId,
                    path = (if (this.removeExtension) FilenameUtils.removeExtension(location.path.toString()) else location.path.toString()).replace(Regex("\\\\"), "/")
            )
}
