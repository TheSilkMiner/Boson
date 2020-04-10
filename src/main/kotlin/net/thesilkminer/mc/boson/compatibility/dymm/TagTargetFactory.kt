package net.thesilkminer.mc.boson.compatibility.dymm

import com.aaronhowser1.dymm.JsonUtilities
import com.aaronhowser1.dymm.api.documentation.Target
import com.aaronhowser1.dymm.api.loading.GlobalLoadingState
import com.aaronhowser1.dymm.api.loading.factory.TargetFactory
import com.aaronhowser1.dymm.module.base.BasicItemTarget
import com.google.gson.JsonObject
import net.thesilkminer.mc.boson.api.bosonApi
import net.thesilkminer.mc.boson.prefab.naming.toNameSpacedString
import net.thesilkminer.mc.boson.prefab.tag.itemTagType

@Suppress("unused")
class TagTargetFactory : TargetFactory {
    override fun fromJson(state: GlobalLoadingState, `object`: JsonObject): List<Target> {
        val tagName = JsonUtilities.getString(`object`, "tag").toNameSpacedString()
        val tag = bosonApi.tagRegistry[itemTagType, tagName]
        return tag.elements.map { BasicItemTarget(it) }.toList()
    }
}
