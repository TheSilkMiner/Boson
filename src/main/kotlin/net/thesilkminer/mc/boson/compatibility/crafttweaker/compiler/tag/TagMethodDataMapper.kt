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

@file:JvmName("TagMethodDataMapper")

package net.thesilkminer.mc.boson.compatibility.crafttweaker.compiler.tag

import net.thesilkminer.mc.boson.compatibility.crafttweaker.naming.ZenNameSpacedString
import net.thesilkminer.mc.boson.compatibility.crafttweaker.tag.ZenTag
import net.thesilkminer.mc.boson.compatibility.crafttweaker.tag.ZenTagType
import stanhebben.zenscript.compiler.IEnvironmentGlobal
import stanhebben.zenscript.expression.Expression
import stanhebben.zenscript.expression.partial.IPartialExpression
import stanhebben.zenscript.type.ZenType
import stanhebben.zenscript.type.ZenTypeArray
import stanhebben.zenscript.type.ZenTypeArrayBasic
import stanhebben.zenscript.type.ZenTypeArrayList
import stanhebben.zenscript.util.ZenPosition
import kotlin.reflect.KClass
import kotlin.reflect.full.isSubclassOf

private typealias ZenTypeCreator = (tag: TagZenType, env: IEnvironmentGlobal?) -> ZenType
private typealias ExpressionCreator = (position: ZenPosition?, callee: IPartialExpression?, arguments: List<Expression>, tagType: TagZenType, environment: IEnvironmentGlobal?) -> Expression
private typealias ArgumentPredicate = (arguments: List<Expression>) -> Boolean

private class TagMethodDataBuilder {
    private val arguments = mutableListOf<ZenTypeCreator>()
    private var hasEndingVararg = false

    lateinit var expressionCreator: ExpressionCreator

    fun argument(supplier: () -> ZenTypeCreator) {
        if (this.hasEndingVararg) throw IllegalStateException("Vararg already added")
        this.arguments += supplier()
    }

    fun vararg(supplier: () -> ZenTypeCreator) {
        this.argument(supplier)
        this.hasEndingVararg = true
    }

    fun build() = TagMethodData(this.arguments.count(), this.arguments.toList(), this.expressionCreator, this.hasEndingVararg)
}

internal data class TagMethodData(val argumentsCount: Int, val arguments: List<ZenTypeCreator>, val expressionCreator: ExpressionCreator, val hasEndingVararg: Boolean)
internal data class ReturnTypeData(val returnTypeClass: KClass<*>, val returnType: ZenType)
internal data class ArgumentData(val index: Int, val argumentTypeClass: KClass<*>, val argumentType: ZenType, val expression: Expression?)
internal data class OverloadPathExpressionData(val priority: Int, val predicate: ArgumentPredicate, val validExpression: Expression)

internal val methodDataMap by lazy { mutableMapOf<String, List<TagMethodData>>().apply { this.populateMapWithData() }.toMap() }

private fun MutableMap<String, List<TagMethodData>>.populateMapWithData() {

    // Getters
    this.create(methodName = "getName") {
        data {
            expressionCreator = { position, callee, _, _, environment ->
                NoArgumentKnownReturnTypeTagFunctionExpression(
                        position = position,
                        callee = callee,
                        returnTypeData = ZenNameSpacedString::class.toReturnTypeData(environment),
                        targetMethodName = "getName"
                )
            }
        }
    }
    this.create(methodName = "getType") {
        data {
            expressionCreator = { position, callee, _, _, environment ->
                NoArgumentKnownReturnTypeTagFunctionExpression(
                        position = position,
                        callee = callee,
                        returnTypeData = ZenTagType::class.toReturnTypeData(environment),
                        targetMethodName = "getType"
                )
            }
        }
    }
    this.create(methodName = "getElements") {
        data {
            expressionCreator = { position, callee, _, tagType, _ ->
                NoArgumentKnownReturnTypeTagFunctionExpression(
                        position = position,
                        callee = callee,
                        returnTypeData = ReturnTypeData(returnTypeClass = List::class, returnType = ZenTypeArrayList(tagType.genericType)),
                        targetMethodName = "getElements"
                )
            }
        }
    }

    // Add
    this.create(methodName = "add") {
        data {
            vararg { { _, env -> env!!.getType(ZenNameSpacedString::class.java) } }
            expressionCreator = { position, callee, arguments, _, environment ->
                SingleVarargArgumentKnownReturnTypeTagFunctionExpression(
                        position = position,
                        callee = callee,
                        targetMethodName = "add",
                        returnTypeData = unit(),
                        varargArgumentData = ArgumentData(index = -1, argumentTypeClass = ZenNameSpacedString::class,
                                argumentType = environment!!.getType(ZenNameSpacedString::class.java), expression = null),
                        actualArguments = arguments,
                        actualArgumentClass = Array<ZenNameSpacedString>::class
                )
            }
        }
    }
    this.create(methodName = "addAll") {
        data {
            argument { { tag, _ -> ZenTypeArrayBasic(tag.genericType) } }
            expressionCreator = { position, callee, arguments, tagType, _ ->
                SingleArgumentKnownReturnTypeTagFunctionExpression(
                        position = position,
                        callee = callee,
                        targetMethodName = "addAll",
                        returnTypeData = unit(),
                        argumentData = ArgumentData(index = 0, argumentTypeClass = Array<Any>::class, argumentType = ZenTypeArrayBasic(tagType.genericType),
                                expression = arguments.findArgument(position = 0))
                )
            }
        }
    }
    this.create(methodName = "addFrom") {
        data {
            argument { getIdentityFun() }
            expressionCreator = { position, callee, arguments, tagType, _ ->
                SingleArgumentKnownReturnTypeTagFunctionExpression(
                        position = position,
                        callee = callee,
                        targetMethodName = "addFrom",
                        returnTypeData = unit(),
                        argumentData = ArgumentData(index = 0, argumentTypeClass = ZenTag::class, argumentType = tagType, expression = arguments.findArgument(position = 0))
                )
            }
        }
    }

    // Replace
    this.create(methodName = "replace") {
        data {
            vararg { { _, env -> env!!.getType(ZenNameSpacedString::class.java) } }
            expressionCreator = { position, callee, arguments, _, environment ->
                SingleVarargArgumentKnownReturnTypeTagFunctionExpression(
                        position = position,
                        callee = callee,
                        targetMethodName = "replace",
                        returnTypeData = unit(),
                        varargArgumentData = ArgumentData(index = -1, argumentTypeClass = ZenNameSpacedString::class,
                                argumentType = environment!!.getType(ZenNameSpacedString::class.java), expression = null),
                        actualArguments = arguments,
                        actualArgumentClass = Array<ZenNameSpacedString>::class
                )
            }
        }
    }
    this.create(methodName = "replaceAll") {
        data {
            argument { { tag, _ -> ZenTypeArrayBasic(tag.genericType) } }
            expressionCreator = { position, callee, arguments, tagType, _ ->
                SingleArgumentKnownReturnTypeTagFunctionExpression(
                        position = position,
                        callee = callee,
                        targetMethodName = "replaceAll",
                        returnTypeData = unit(),
                        argumentData = ArgumentData(index = 0, argumentTypeClass = Array<Any>::class, argumentType = ZenTypeArrayBasic(tagType.genericType),
                                expression = arguments.findArgument(position = 0))
                )
            }
        }
    }
    this.create(methodName = "replaceWith") {
        data {
            argument { getIdentityFun() }
            expressionCreator = { position, callee, arguments, tagType, _ ->
                SingleArgumentKnownReturnTypeTagFunctionExpression(
                        position = position,
                        callee = callee,
                        targetMethodName = "replaceWith",
                        returnTypeData = unit(),
                        argumentData = ArgumentData(index = 0, argumentTypeClass = ZenTag::class, argumentType = tagType, expression = arguments.findArgument(position = 0))
                )
            }
        }
    }

    // Remove
    this.create(methodName = "remove") {
        data {
            vararg { { _, env -> env!!.getType(ZenNameSpacedString::class.java) } }
            expressionCreator = { position, callee, arguments, _, environment ->
                SingleVarargArgumentKnownReturnTypeTagFunctionExpression(
                        position = position,
                        callee = callee,
                        targetMethodName = "remove",
                        returnTypeData = unit(),
                        varargArgumentData = ArgumentData(index = -1, argumentTypeClass = ZenNameSpacedString::class,
                                argumentType = environment!!.getType(ZenNameSpacedString::class.java), expression = null),
                        actualArguments = arguments,
                        actualArgumentClass = Array<ZenNameSpacedString>::class
                )
            }
        }
    }
    this.create(methodName = "removeAll") {
        data {
            argument { { tag, _ -> ZenTypeArrayBasic(tag.genericType) } }
            expressionCreator = { position, callee, arguments, tagType, _ ->
                SingleArgumentKnownReturnTypeTagFunctionExpression(
                        position = position,
                        callee = callee,
                        targetMethodName = "removeAll",
                        returnTypeData = unit(),
                        argumentData = ArgumentData(index = 0, argumentTypeClass = Array<Any>::class, argumentType = ZenTypeArrayBasic(tagType.genericType),
                                expression = arguments.findArgument(position = 0))
                )
            }
        }
    }
    this.create(methodName = "removeFrom") {
        data {
            argument { getIdentityFun() }
            expressionCreator = { position, callee, arguments, tagType, _ ->
                SingleArgumentKnownReturnTypeTagFunctionExpression(
                        position = position,
                        callee = callee,
                        targetMethodName = "removeFrom",
                        returnTypeData = unit(),
                        argumentData = ArgumentData(index = 0, argumentTypeClass = ZenTag::class, argumentType = tagType, expression = arguments.findArgument(position = 0))
                )
            }
        }
    }

    // Other
    this.create(methodName = "clear") {
        data {
            expressionCreator = { position, callee, _, _, _ ->
                NoArgumentKnownReturnTypeTagFunctionExpression(
                        position = position,
                        callee = callee,
                        targetMethodName = "clear",
                        returnTypeData = unit()
                )
            }
        }
    }

    // Operators
    this.create(methodName = "contains") {
        data {
            argument { { _, env -> env!!.getType(Object::class.java) } }
            expressionCreator = { position, callee, arguments, tagType, _ ->
                SingleArgumentKnownReturnTypeTagFunctionExpression(
                        position = position,
                        callee = callee,
                        targetMethodName = "contains",
                        returnTypeData = ReturnTypeData(returnTypeClass = java.lang.Boolean.TYPE.kotlin, returnType = ZenType.BOOL),
                        argumentData = ArgumentData(index = 0, argumentTypeClass = Object::class, argumentType = tagType.genericType, expression = arguments.findArgument(position = 0))
                )
            }
        }
    }
    this.create(methodName = "plusAssign") {
        data {
            argument { dummy() }
            expressionCreator = { position, callee, arguments, tagType, environment ->
                OverloadedSingleArgumentTagFunctionExpression(
                        position = position,
                        arguments = arguments,
                        backupEnvironment = environment,
                        // (ZenTag) -> (Array<*>) -> (ZenNameSpacedString) -> (Any)
                        overloads = listOf(
                                OverloadPathExpressionData(
                                        priority = 400,
                                        predicate = ensureArgumentType(tagType),
                                        validExpression = SingleArgumentKnownReturnTypeTagFunctionExpression(
                                                position = position,
                                                callee = callee,
                                                targetMethodName = "plusAssign",
                                                returnTypeData = unit(),
                                                argumentData = ArgumentData(index = 0, argumentTypeClass = ZenTag::class, argumentType = tagType,
                                                        expression = arguments.findArgument(position = 0))
                                        )
                                ),
                                OverloadPathExpressionData(
                                        priority = 300,
                                        predicate = multiple(ensureArgumentType(ZenType.ANYARRAY), ensureInnerArgumentType(tagType.genericType)),
                                        validExpression = SingleArgumentKnownReturnTypeTagFunctionExpression(
                                                position = position,
                                                callee = callee,
                                                targetMethodName = "plusAssign",
                                                returnTypeData = unit(),
                                                argumentData = ArgumentData(index = 0, argumentTypeClass = Array<Any>::class, argumentType = ZenType.ANYARRAY,
                                                        expression = arguments.findArgument(position = 0))
                                        )
                                ),
                                OverloadPathExpressionData(
                                        priority = 200,
                                        predicate = ensureArgumentType(environment!!.getType(ZenNameSpacedString::class.java)),
                                        validExpression = SingleArgumentKnownReturnTypeTagFunctionExpression(
                                                position = position,
                                                callee = callee,
                                                targetMethodName = "plusAssign",
                                                returnTypeData = unit(),
                                                argumentData = ArgumentData(index = 0, argumentTypeClass = ZenNameSpacedString::class,
                                                        argumentType = environment.getType(ZenNameSpacedString::class.java), expression = arguments.findArgument(position = 0))
                                        )
                                ),
                                OverloadPathExpressionData(
                                        priority = 100,
                                        predicate = { true },
                                        validExpression = SingleArgumentKnownReturnTypeTagFunctionExpression(
                                                position = position,
                                                callee = callee,
                                                targetMethodName = "plusAssign",
                                                returnTypeData = unit(),
                                                argumentData = ArgumentData(index = 0, argumentTypeClass = Object::class, argumentType = environment.getType(Object::class.java),
                                                        expression = arguments.findArgument(position = 0))
                                        )
                                )
                        )
                )
            }
        }
    }
    this.create(methodName = "minusAssign") {
        data {
            argument { dummy() }
            expressionCreator = { position, callee, arguments, tagType, environment ->
                OverloadedSingleArgumentTagFunctionExpression(
                        position = position,
                        arguments = arguments,
                        backupEnvironment = environment,
                        // (ZenTag) -> (Array<*>) -> (ZenNameSpacedString) -> (Any)
                        overloads = listOf(
                                OverloadPathExpressionData(
                                        priority = 400,
                                        predicate = ensureArgumentType(tagType),
                                        validExpression = SingleArgumentKnownReturnTypeTagFunctionExpression(
                                                position = position,
                                                callee = callee,
                                                targetMethodName = "minusAssign",
                                                returnTypeData = unit(),
                                                argumentData = ArgumentData(index = 0, argumentTypeClass = ZenTag::class, argumentType = tagType,
                                                        expression = arguments.findArgument(position = 0))
                                        )
                                ),
                                OverloadPathExpressionData(
                                        priority = 300,
                                        predicate = multiple(ensureArgumentType(ZenType.ANYARRAY), ensureInnerArgumentType(tagType.genericType)),
                                        validExpression = SingleArgumentKnownReturnTypeTagFunctionExpression(
                                                position = position,
                                                callee = callee,
                                                targetMethodName = "minusAssign",
                                                returnTypeData = unit(),
                                                argumentData = ArgumentData(index = 0, argumentTypeClass = Array<Any>::class, argumentType = ZenType.ANYARRAY,
                                                        expression = arguments.findArgument(position = 0))
                                        )
                                ),
                                OverloadPathExpressionData(
                                        priority = 200,
                                        predicate = ensureArgumentType(environment!!.getType(ZenNameSpacedString::class.java)),
                                        validExpression = SingleArgumentKnownReturnTypeTagFunctionExpression(
                                                position = position,
                                                callee = callee,
                                                targetMethodName = "minusAssign",
                                                returnTypeData = unit(),
                                                argumentData = ArgumentData(index = 0, argumentTypeClass = ZenNameSpacedString::class,
                                                        argumentType = environment.getType(ZenNameSpacedString::class.java), expression = arguments.findArgument(position = 0))
                                        )
                                ),
                                OverloadPathExpressionData(
                                        priority = 100,
                                        predicate = { true },
                                        validExpression = SingleArgumentKnownReturnTypeTagFunctionExpression(
                                                position = position,
                                                callee = callee,
                                                targetMethodName = "minusAssign",
                                                returnTypeData = unit(),
                                                argumentData = ArgumentData(index = 0, argumentTypeClass = Object::class, argumentType = environment.getType(Object::class.java),
                                                        expression = arguments.findArgument(position = 0))
                                        )
                                )
                        )
                )
            }
        }
    }
    this.create(methodName = "unaryMinus") {
        data {
            expressionCreator = { position, callee, _, _, _ ->
                NoArgumentKnownReturnTypeTagFunctionExpression(
                        position = position,
                        callee = callee,
                        targetMethodName = "unaryMinus",
                        returnTypeData = unit()
                )
            }
        }
    }
    this.create(methodName = "invoke") {
        data {
            expressionCreator = { position, callee, _, tagType, _ ->
                NoArgumentKnownReturnTypeTagFunctionExpression(
                        position = position,
                        callee = callee,
                        targetMethodName = "invoke",
                        returnTypeData = ReturnTypeData(returnTypeClass = List::class, returnType = ZenTypeArrayList(tagType.genericType))
                )
            }
        }
    }
}

private fun MutableMap<String, List<TagMethodData>>.create(methodName: String, builder: MutableList<TagMethodData>.() -> Unit) {
    val targetList = (this[methodName] ?: listOf()).toMutableList()
    targetList.builder()
    this[methodName] = targetList
}

private fun MutableList<TagMethodData>.data(creator: TagMethodDataBuilder.() -> Unit) {
    val builder = TagMethodDataBuilder()
    builder.creator()
    this += builder.build()
}

private fun KClass<*>.toReturnTypeData(environment: IEnvironmentGlobal?) = ReturnTypeData(this, environment!!.getType(this.java))
private fun unit() = ReturnTypeData(returnTypeClass = Void.TYPE.kotlin, returnType = ZenType.VOID)

private fun List<Expression>.findArgument(position: Int) = this[position]

private fun multiple(vararg predicates: ArgumentPredicate): ArgumentPredicate = { predicates.all { predicate -> predicate(it) } }
private fun ensureArgumentType(target: ZenType): ArgumentPredicate = { ensureCompatibleTypes(target, it.singleOrNull()?.type) == true }
private fun ensureInnerArgumentType(innerTarget: ZenType): ArgumentPredicate = { ensureCompatibleTypes(innerTarget, (it.singleOrNull()?.type as? ZenTypeArray)?.baseType) == true }

private fun ensureCompatibleTypes(expected: ZenType, actual: ZenType?) = actual?.toJavaClass()?.kotlin?.isSubclassOf(expected.toJavaClass().kotlin)

private fun getIdentityFun(): ZenTypeCreator = { tag, _ -> tag }
private fun dummy(): ZenTypeCreator = { _, env -> env!!.getType(Object::class.java) }
