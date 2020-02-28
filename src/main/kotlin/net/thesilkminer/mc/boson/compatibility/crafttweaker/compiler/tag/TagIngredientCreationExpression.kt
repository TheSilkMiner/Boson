package net.thesilkminer.mc.boson.compatibility.crafttweaker.compiler.tag

import net.thesilkminer.mc.boson.api.tag.Tag
import net.thesilkminer.mc.boson.compatibility.crafttweaker.tag.ZenTagIngredient
import net.thesilkminer.mc.boson.mod.common.recipe.TagIngredient
import stanhebben.zenscript.compiler.IEnvironmentGlobal
import stanhebben.zenscript.compiler.IEnvironmentMethod
import stanhebben.zenscript.expression.Expression
import stanhebben.zenscript.type.ZenType
import stanhebben.zenscript.util.ZenPosition

class TagIngredientCreationExpression(position: ZenPosition?, private val tagTypeRepresentation: String, private val tagNameRepresentation: String,
                                      private val backupEnvironment: IEnvironmentGlobal?) : Expression(position) {
    override fun compile(result: Boolean, environment: IEnvironmentMethod?) {
        // Constructor invocation, so we need to prepare all of that
        environment?.output?.let {
            it.newObject(TagIngredient::class.java)
            it.dup()
        }

        // To create a tag ingredient, we first need to create the tag
        val tagCreationExpression = TagCreationExpression(this.position, this.tagTypeRepresentation, this.tagNameRepresentation, this.backupEnvironment)
        tagCreationExpression.compile(true, environment)

        // And now onto the rest
        environment?.output?.let {
            it.invokeStatic("net/thesilkminer/mc/boson/compatibility/crafttweaker/WrapUnwrap", "toNative",
                    "(Lnet/thesilkminer/mc/boson/compatibility/crafttweaker/tag/ZenTag;)Lnet/thesilkminer/mc/boson/api/tag/Tag;")
            it.construct(TagIngredient::class.java, Tag::class.java)
            it.invokeStatic("net/thesilkminer/mc/boson/compatibility/crafttweaker/WrapUnwrap", "toZen",
                    "(Lnet/thesilkminer/mc/boson/mod/common/recipe/TagIngredient;)Lnet/thesilkminer/mc/boson/compatibility/crafttweaker/tag/ZenTagIngredient;")

            if (!result) it.pop(this.type.isLarge)
        }
    }

    override fun getType(): ZenType = this.backupEnvironment?.getType(ZenTagIngredient::class.java) ?: throw IllegalStateException("ZenTagIngredient wasn't registered")
}
