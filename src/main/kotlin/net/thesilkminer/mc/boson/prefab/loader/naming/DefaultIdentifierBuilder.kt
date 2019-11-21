package net.thesilkminer.mc.boson.prefab.loader.naming

import net.minecraftforge.fml.common.Loader
import net.thesilkminer.mc.boson.api.bosonApi
import net.thesilkminer.mc.boson.api.loader.Context
import net.thesilkminer.mc.boson.api.loader.IdentifierBuilder
import net.thesilkminer.mc.boson.api.loader.Location
import net.thesilkminer.mc.boson.prefab.loader.modIdContextKey

class DefaultIdentifierBuilder : IdentifierBuilder {
    override fun makeIdentifier(location: Location, globalContext: Context?, phaseContext: Context?) =
            bosonApi.constructNameSpacedString(
                    nameSpace = location.additionalContext?.get(modIdContextKey) ?: Loader.instance().activeModContainer()?.modId,
                    path = location.path.toString().replace(Regex("\\\\"), "/")
            )
}
