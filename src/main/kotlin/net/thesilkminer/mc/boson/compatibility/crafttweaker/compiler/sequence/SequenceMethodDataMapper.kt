/*
 * Copyright (C) 2020  TheSilkMiner
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

@file:JvmName("SequenceMethodDataMapper")

package net.thesilkminer.mc.boson.compatibility.crafttweaker.compiler.sequence

import net.thesilkminer.mc.boson.MOD_NAME
import net.thesilkminer.mc.boson.api.log.L
import net.thesilkminer.mc.boson.compatibility.crafttweaker.zenscriptx.function.BiFunction
import net.thesilkminer.mc.boson.compatibility.crafttweaker.zenscriptx.function.BiPredicate
import net.thesilkminer.mc.boson.compatibility.crafttweaker.zenscriptx.function.Consumer
import net.thesilkminer.mc.boson.compatibility.crafttweaker.zenscriptx.function.Function
import net.thesilkminer.mc.boson.compatibility.crafttweaker.zenscriptx.function.IntFunction
import net.thesilkminer.mc.boson.compatibility.crafttweaker.zenscriptx.function.ObjIntConsumer
import net.thesilkminer.mc.boson.compatibility.crafttweaker.zenscriptx.function.Predicate
import net.thesilkminer.mc.boson.compatibility.crafttweaker.zenscriptx.function.ToDoubleFunction
import net.thesilkminer.mc.boson.compatibility.crafttweaker.zenscriptx.function.ToIntBiFunction
import net.thesilkminer.mc.boson.compatibility.crafttweaker.zenscriptx.function.ToIntFunction
import net.thesilkminer.mc.boson.compatibility.crafttweaker.zenscriptx.sequence.ZenSequence
import stanhebben.zenscript.compiler.IEnvironmentGlobal
import stanhebben.zenscript.definitions.ParsedFunctionArgument
import stanhebben.zenscript.expression.Expression
import stanhebben.zenscript.expression.ExpressionJavaLambdaSimpleGeneric
import stanhebben.zenscript.expression.partial.IPartialExpression
import stanhebben.zenscript.type.ZenType
import stanhebben.zenscript.type.ZenTypeArrayBasic
import stanhebben.zenscript.util.ZenPosition
import kotlin.reflect.KClass
import kotlin.reflect.KVisibility
import kotlin.reflect.full.declaredFunctions

private typealias ZenTypeCreator = (environment: IEnvironmentGlobal?, sequenceType: SequenceZenType) -> ZenType
private typealias ExpressionCreator = (position: ZenPosition?, callee: IPartialExpression?, arguments: List<Expression>, sequenceType: SequenceZenType) -> Expression

private class SequenceMethodDataBuilder {
    val arguments = mutableListOf<ZenTypeCreator>()

    lateinit var expressionCreator: ExpressionCreator

    fun argument(supplier: () -> ZenTypeCreator) {
        this.arguments += supplier()
    }

    fun build() = SequenceMethodData(this.arguments.count(), this.arguments.toList(), this.expressionCreator)
}

internal data class SequenceMethodData(val argumentsCount: Int, val arguments: List<ZenTypeCreator>, val expressionCreator: ExpressionCreator)
internal data class ExpressionInvocationData(val name: String, val argumentClass: KClass<*>, val sequenceTargetPositions: List<Int>)
internal data class ArgumentData(val index: Int, val argumentClass: KClass<*>, val argumentZenType: ZenType, val expression: Expression)
internal data class ReturnTypeData(val targetClass: KClass<*>, val correspondingZenType: ZenType)

private val missingLog = L("$MOD_NAME - CT Integration", "Sequence Compiler")
internal val methodDataMap by lazy { mutableMapOf<String, List<SequenceMethodData>>().apply { this.populateMapWithData() }.toMap().apply { this.logMissing() } }

private fun MutableMap<String, List<SequenceMethodData>>.populateMapWithData() {
    this.create(methodName = "contains") {
        data {
            argument { getNativeFun(ObjectZenType) }
            expressionCreator = { position, callee, arguments, _ ->
                SingleGenericArgumentKnownReturnTypeSequenceFunctionExpression(
                        position = position,
                        callee = callee,
                        argumentExpression = arguments.extractArgument(position = 0),
                        returnType = ReturnTypeData(targetClass = java.lang.Boolean.TYPE.kotlin, correspondingZenType = ZenType.BOOL),
                        methodName = "contains"
                )
            }
        }
    }
    this.create(methodName = "elementAt") {
        data {
            argument { getNativeFun(ZenType.INT) }
            expressionCreator = { position, callee, arguments, sequenceType ->
                SingleNonFunctionArgumentGenericReturnTypeSequenceFunctionExpression(
                        position = position,
                        callee = callee,
                        sequence = sequenceType,
                        argumentData = ArgumentData(index = 0, argumentClass = Integer.TYPE.kotlin, argumentZenType = ZenType.INT, expression = arguments.extractArgument(position = 0)),
                        methodName = "elementAt"
                )
            }
        }
    }
    this.create(methodName = "elementAtOrElse") {
        data {
            argument { getNativeFun(ZenType.INT) }
            argument { { _, sequenceType -> FunctionSequenceZenType(sequenceType, IntFunction::class) } }
            expressionCreator = { position, callee, arguments, sequenceType ->
                DoubleNonFunctionFunctionArgumentsGenericReturnTypeSequenceFunctionExpression(
                        position = position,
                        callee = callee,
                        sequence = sequenceType,
                        firstArgument = ArgumentData(index = 0, argumentClass = Integer.TYPE.kotlin, argumentZenType = ZenType.INT, expression = arguments.extractArgument(position = 0)),
                        secondArgument = ArgumentData(index = 1, argumentClass = IntFunction::class, argumentZenType = ZenType.ANY, expression = arguments.extractFunction(position = 1)),
                        methodName = "elementAtOrElse",
                        sequenceTargetPositions = listOf()
                        // void apply(value: Int): R
                )
            }
        }
    }
    this.create(methodName = "elementAtOrNull") {
        data {
            argument { getNativeFun(ZenType.INT) }
            expressionCreator = { position, callee, arguments, sequenceType ->
                SingleNonFunctionArgumentGenericReturnTypeSequenceFunctionExpression(
                        position = position,
                        callee = callee,
                        sequence = sequenceType,
                        argumentData = ArgumentData(index = 0, argumentClass = Integer.TYPE.kotlin, argumentZenType = ZenType.INT, expression = arguments.extractArgument(position = 0)),
                        methodName = "elementAtOrNull"
                )
            }
        }
    }
    this.create(methodName = "find") {
        data {
            argument { getTypeFun(Predicate::class) }
            expressionCreator = { position, callee, arguments, sequenceType ->
                SingleArgumentGenericReturnTypeSequenceFunctionExpression(
                        position = position,
                        callee = callee,
                        sequenceType = sequenceType,
                        function = arguments.convertToGeneric(sequenceType, javaLambdaPosition = 0, sequenceTargetPositions = listOf(0)).extractFunction(position = 0),
                        expressionInvocationData = ExpressionInvocationData(name = "find", argumentClass = Predicate::class, sequenceTargetPositions = listOf(0)) // test(t: T): Boolean
                )
            }
        }
    }
    this.create(methodName = "findLast") {
        data {
            argument { getTypeFun(Predicate::class) }
            expressionCreator = { position, callee, arguments, sequenceType ->
                SingleArgumentGenericReturnTypeSequenceFunctionExpression(
                        position = position,
                        callee = callee,
                        sequenceType = sequenceType,
                        function = arguments.convertToGeneric(sequenceType, javaLambdaPosition = 0, sequenceTargetPositions = listOf(0)).extractFunction(position = 0),
                        expressionInvocationData = ExpressionInvocationData(name = "findLast", argumentClass = Predicate::class, sequenceTargetPositions = listOf(0)) // test(t: T): Boolean
                )
            }
        }
    }
    this.create(methodName = "first") {
        data {
            expressionCreator = { position, callee, _, sequenceType ->
                NoArgumentGenericReturnTypeSequenceFunctionExpression(
                        position = position,
                        callee = callee,
                        sequenceType = sequenceType,
                        targetMethodName = "first"
                )
            }
        }
        data {
            argument { getTypeFun(Predicate::class) }
            expressionCreator = { position, callee, arguments, sequenceType ->
                SingleArgumentGenericReturnTypeSequenceFunctionExpression(
                        position = position,
                        callee = callee,
                        sequenceType = sequenceType,
                        function = arguments.convertToGeneric(sequenceType, javaLambdaPosition = 0, sequenceTargetPositions = listOf(0)).extractFunction(position = 0),
                        expressionInvocationData = ExpressionInvocationData(name = "first", argumentClass = Predicate::class, sequenceTargetPositions = listOf(0)) // test(t: T): Boolean
                )
            }
        }
    }
    this.create(methodName = "firstOrNull") {
        data {
            expressionCreator = { position, callee, _, sequenceType ->
                NoArgumentGenericReturnTypeSequenceFunctionExpression(
                        position = position,
                        callee = callee,
                        sequenceType = sequenceType,
                        targetMethodName = "firstOrNull"
                )
            }
        }
        data {
            argument { getTypeFun(Predicate::class) }
            expressionCreator = { position, callee, arguments, sequenceType ->
                SingleArgumentGenericReturnTypeSequenceFunctionExpression(
                        position = position,
                        callee = callee,
                        sequenceType = sequenceType,
                        function = arguments.convertToGeneric(sequenceType, javaLambdaPosition = 0, sequenceTargetPositions = listOf(0)).extractFunction(position = 0),
                        expressionInvocationData = ExpressionInvocationData(name = "firstOrNull", argumentClass = Predicate::class, sequenceTargetPositions = listOf(0)) // test(t: T): Boolean
                )
            }
        }
    }
    this.create(methodName = "indexOf") {
        data {
            argument { getNativeFun(ObjectZenType) }
            expressionCreator = { position, callee, arguments, _ ->
                SingleGenericArgumentKnownReturnTypeSequenceFunctionExpression(
                        position = position,
                        callee = callee,
                        argumentExpression = arguments.extractArgument(position = 0),
                        returnType = ReturnTypeData(targetClass = Integer::class, correspondingZenType = ZenType.INT),
                        methodName = "indexOf"
                )
            }
        }
    }
    this.create(methodName = "indexOfFirst") {
        data {
            argument { getTypeFun(Predicate::class) }
            expressionCreator = { position, callee, arguments, sequenceType ->
                SingleArgumentKnownReturnTypeSequenceFunctionExpression(
                        position = position,
                        callee = callee,
                        returnType = ReturnTypeData(targetClass = Integer.TYPE.kotlin, correspondingZenType = ZenType.INT),
                        function = arguments.convertToGeneric(sequenceType, javaLambdaPosition = 0, sequenceTargetPositions = listOf(0)).extractFunction(position = 0),
                        expressionInvocationData = ExpressionInvocationData(name = "indexOfFirst", argumentClass = Predicate::class, sequenceTargetPositions = listOf(0)) // test(t: T): Boolean
                )
            }
        }
    }
    this.create(methodName = "indexOfLast") {
        data {
            argument { getTypeFun(Predicate::class) }
            expressionCreator = { position, callee, arguments, sequenceType ->
                SingleArgumentKnownReturnTypeSequenceFunctionExpression(
                        position = position,
                        callee = callee,
                        returnType = ReturnTypeData(targetClass = Integer.TYPE.kotlin, correspondingZenType = ZenType.INT),
                        function = arguments.convertToGeneric(sequenceType, javaLambdaPosition = 0, sequenceTargetPositions = listOf(0)).extractFunction(position = 0),
                        expressionInvocationData = ExpressionInvocationData(name = "indexOfLast", argumentClass = Predicate::class, sequenceTargetPositions = listOf(0)) // test(t: T): Boolean
                )
            }
        }
    }
    this.create(methodName = "last") {
        data {
            expressionCreator = { position, callee, _, sequenceType ->
                NoArgumentGenericReturnTypeSequenceFunctionExpression(
                        position = position,
                        callee = callee,
                        sequenceType = sequenceType,
                        targetMethodName = "last"
                )
            }
        }
        data {
            argument { getTypeFun(Predicate::class) }
            expressionCreator = { position, callee, arguments, sequenceType ->
                SingleArgumentGenericReturnTypeSequenceFunctionExpression(
                        position = position,
                        callee = callee,
                        sequenceType = sequenceType,
                        function = arguments.convertToGeneric(sequenceType, javaLambdaPosition = 0, sequenceTargetPositions = listOf(0)).extractFunction(position = 0),
                        expressionInvocationData = ExpressionInvocationData(name = "last", argumentClass = Predicate::class, sequenceTargetPositions = listOf(0)) // test(t: T): Boolean
                )
            }
        }
    }
    this.create(methodName = "lastIndexOf") {
        data {
            argument { getNativeFun(ObjectZenType) }
            expressionCreator = { position, callee, arguments, _ ->
                SingleGenericArgumentKnownReturnTypeSequenceFunctionExpression(
                        position = position,
                        callee = callee,
                        argumentExpression = arguments.extractArgument(position = 0),
                        returnType = ReturnTypeData(targetClass = Integer::class, correspondingZenType = ZenType.INT),
                        methodName = "lastIndexOf"
                )
            }
        }
    }
    this.create(methodName = "lastOrNull") {
        data {
            expressionCreator = { position, callee, _, sequenceType ->
                NoArgumentGenericReturnTypeSequenceFunctionExpression(
                        position = position,
                        callee = callee,
                        sequenceType = sequenceType,
                        targetMethodName = "lastOrNull"
                )
            }
        }
        data {
            argument { getTypeFun(Predicate::class) }
            expressionCreator = { position, callee, arguments, sequenceType ->
                SingleArgumentGenericReturnTypeSequenceFunctionExpression(
                        position = position,
                        callee = callee,
                        sequenceType = sequenceType,
                        function = arguments.convertToGeneric(sequenceType, javaLambdaPosition = 0, sequenceTargetPositions = listOf(0)).extractFunction(position = 0),
                        expressionInvocationData = ExpressionInvocationData(name = "lastOrNull", argumentClass = Predicate::class, sequenceTargetPositions = listOf(0)) // test(t: T): Boolean
                )
            }
        }
    }
    this.create(methodName = "single") {
        data {
            expressionCreator = { position, callee, _, sequenceType ->
                NoArgumentGenericReturnTypeSequenceFunctionExpression(
                        position = position,
                        callee = callee,
                        sequenceType = sequenceType,
                        targetMethodName = "single"
                )
            }
        }
        data {
            argument { getTypeFun(Predicate::class) }
            expressionCreator = { position, callee, arguments, sequenceType ->
                SingleArgumentGenericReturnTypeSequenceFunctionExpression(
                        position = position,
                        callee = callee,
                        sequenceType = sequenceType,
                        function = arguments.convertToGeneric(sequenceType, javaLambdaPosition = 0, sequenceTargetPositions = listOf(0)).extractFunction(position = 0),
                        expressionInvocationData = ExpressionInvocationData(name = "single", argumentClass = Predicate::class, sequenceTargetPositions = listOf(0)) // test(t: T): Boolean
                )
            }
        }
    }
    this.create(methodName = "singleOrNull") {
        data {
            expressionCreator = { position, callee, _, sequenceType ->
                NoArgumentGenericReturnTypeSequenceFunctionExpression(
                        position = position,
                        callee = callee,
                        sequenceType = sequenceType,
                        targetMethodName = "singleOrNull"
                )
            }
        }
        data {
            argument { getTypeFun(Predicate::class) }
            expressionCreator = { position, callee, arguments, sequenceType ->
                SingleArgumentGenericReturnTypeSequenceFunctionExpression(
                        position = position,
                        callee = callee,
                        sequenceType = sequenceType,
                        function = arguments.convertToGeneric(sequenceType, javaLambdaPosition = 0, sequenceTargetPositions = listOf(0)).extractFunction(position = 0),
                        expressionInvocationData = ExpressionInvocationData(name = "singleOrNull", argumentClass = Predicate::class, sequenceTargetPositions = listOf(0)) // test(t: T): Boolean
                )
            }
        }
    }
    this.create(methodName = "drop") {
        data {
            argument { getNativeFun(ZenType.INT) }
            expressionCreator = { position, callee, arguments, sequenceType ->
                SingleNonFunctionArgumentSameSequenceReturnTypeSequenceFunctionExpression(
                        position = position,
                        callee = callee,
                        sequence = sequenceType,
                        argumentData = ArgumentData(index = 0, argumentClass = Integer.TYPE.kotlin, argumentZenType = ZenType.INT, expression = arguments.extractArgument(position = 0)),
                        methodName = "drop"
                )
            }
        }
    }
    this.create(methodName = "dropWhile") {
        data {
            argument { getTypeFun(Predicate::class) }
            expressionCreator = { position, callee, arguments, sequenceType ->
                SingleArgumentSameSequenceReturnTypeSequenceFunctionExpression(
                        position = position,
                        callee = callee,
                        sequence = sequenceType,
                        function = arguments.convertToGeneric(sequenceType, javaLambdaPosition = 0, sequenceTargetPositions = listOf(0)).extractFunction(position = 0),
                        expressionInvocationData = ExpressionInvocationData(name = "dropWhile", argumentClass = Predicate::class, sequenceTargetPositions = listOf(0)) // test(t: T): Boolean
                )
            }
        }
    }
    this.create(methodName = "filter") {
        data {
            argument { getTypeFun(Predicate::class) }
            expressionCreator = { position, callee, arguments, sequenceType ->
                SingleArgumentSameSequenceReturnTypeSequenceFunctionExpression(
                        position = position,
                        callee = callee,
                        sequence = sequenceType,
                        function = arguments.convertToGeneric(sequenceType, javaLambdaPosition = 0, sequenceTargetPositions = listOf(0)).extractFunction(position = 0),
                        expressionInvocationData = ExpressionInvocationData(name = "filter", argumentClass = Predicate::class, sequenceTargetPositions = listOf(0)) // test(t: T): Boolean
                )
            }
        }
    }
    this.create(methodName = "filterIndexed") {
        data {
            argument { getTypeFun(BiPredicate::class) }
            expressionCreator = { position, callee, arguments, sequenceType ->
                SingleArgumentSameSequenceReturnTypeSequenceFunctionExpression(
                        position = position,
                        callee = callee,
                        sequence = sequenceType,
                        function = arguments
                                .convertToGeneric(SequenceZenType(ZenType.INT), javaLambdaPosition = 0, sequenceTargetPositions = listOf(0)) // TODO("Does this make sense?")
                                .convertToGeneric(sequenceType, javaLambdaPosition = 0, sequenceTargetPositions = listOf(1))
                                .extractFunction(position = 0),
                        expressionInvocationData = ExpressionInvocationData(name = "filterIndexed", argumentClass = BiPredicate::class, sequenceTargetPositions = listOf(1))
                        // test(t: T, u: U): Boolean (but, T is Int)
                )
            }
        }
    }
    this.create(methodName = "filterNot") {
        data {
            argument { getTypeFun(Predicate::class) }
            expressionCreator = { position, callee, arguments, sequenceType ->
                SingleArgumentSameSequenceReturnTypeSequenceFunctionExpression(
                        position = position,
                        callee = callee,
                        sequence = sequenceType,
                        function = arguments.convertToGeneric(sequenceType, javaLambdaPosition = 0, sequenceTargetPositions = listOf(0)).extractFunction(position = 0),
                        expressionInvocationData = ExpressionInvocationData(name = "filterNot", argumentClass = Predicate::class, sequenceTargetPositions = listOf(0)) // test(t: T): Boolean
                )
            }
        }
    }
    this.create(methodName = "take") {
        data {
            argument { getNativeFun(ZenType.INT) }
            expressionCreator = { position, callee, arguments, sequenceType ->
                SingleNonFunctionArgumentSameSequenceReturnTypeSequenceFunctionExpression(
                        position = position,
                        callee = callee,
                        sequence = sequenceType,
                        argumentData = ArgumentData(index = 0, argumentClass = Integer.TYPE.kotlin, argumentZenType = ZenType.INT, expression = arguments.extractArgument(position = 0)),
                        methodName = "take"
                )
            }
        }
    }
    this.create(methodName = "takeWhile") {
        data {
            argument { getTypeFun(Predicate::class) }
            expressionCreator = { position, callee, arguments, sequenceType ->
                SingleArgumentSameSequenceReturnTypeSequenceFunctionExpression(
                        position = position,
                        callee = callee,
                        sequence = sequenceType,
                        function = arguments.convertToGeneric(sequenceType, javaLambdaPosition = 0, sequenceTargetPositions = listOf(0)).extractFunction(position = 0),
                        expressionInvocationData = ExpressionInvocationData(name = "takeWhile", argumentClass = Predicate::class, sequenceTargetPositions = listOf(0)) // test(t: T): Boolean
                )
            }
        }
    }
    this.create(methodName = "sortedWith") {
        data {
            argument { getTypeFun(ToIntBiFunction::class) }
            expressionCreator = { position, callee, arguments, sequenceType ->
                SingleArgumentSameSequenceReturnTypeSequenceFunctionExpression(
                        position = position,
                        callee = callee,
                        sequence = sequenceType,
                        function = arguments.convertToGeneric(sequenceType, javaLambdaPosition = 0, sequenceTargetPositions = listOf(0, 1)).extractFunction(position = 0),
                        expressionInvocationData = ExpressionInvocationData(name = "sortedWith", argumentClass = ToIntBiFunction::class, sequenceTargetPositions = listOf(0, 1))
                        // apply(t: T, u: U): Int (but, T and U are the same in "sortedWith")
                )
            }
        }
    }
    this.create(methodName = "toList") {
        data {
            expressionCreator = { position, callee, _, _ ->
                NoArgumentKnownReturnTypeSequenceFunctionExpression(
                        position = position,
                        callee = callee,
                        returnType = ReturnTypeData(targetClass = List::class, correspondingZenType = ZenType.ANYARRAY),
                        targetMethodName = "toList" // TODO("Check if this works")
                )
            }
        }
    }
    this.create(methodName = "map") {
        data {
            argument { { _, sequenceType -> FunctionSequenceZenType(sequenceType, Function::class) } }
            expressionCreator = { position, callee, arguments, sequenceType ->
                SingleArgumentDifferentSequenceReturnTypeSequenceFunctionExpression(
                        position = position,
                        callee = callee,
                        sequence = sequenceType,
                        function = arguments.convertToGeneric(sequenceType, javaLambdaPosition = 0, sequenceTargetPositions = listOf(0)).extractFunction(position = 0),
                        expressionInvocationData = ExpressionInvocationData(name = "map", argumentClass = Function::class, sequenceTargetPositions = listOf(0)) // apply(t: T): U
                )
            }
        }
    }
    this.create(methodName = "mapIndexed") {
        data {
            argument { { _, sequenceType -> FunctionSequenceZenType(sequenceType, BiFunction::class) } }
            expressionCreator = { position, callee, arguments, sequenceType ->
                SingleArgumentDifferentSequenceReturnTypeSequenceFunctionExpression(
                        position = position,
                        callee = callee,
                        sequence = sequenceType,
                        function = arguments
                                .convertToGeneric(SequenceZenType(ZenType.INT), javaLambdaPosition = 0, sequenceTargetPositions = listOf(0))
                                .convertToGeneric(sequenceType, javaLambdaPosition = 0, sequenceTargetPositions = listOf(1))
                                .extractFunction(position = 0),
                        expressionInvocationData = ExpressionInvocationData(name = "mapIndexed", argumentClass = BiFunction::class, sequenceTargetPositions = listOf(1))
                        // apply(t: T, u: U): R (but T is Int)
                )
            }
        }
    }
    this.create(methodName = "distinct") {
        data {
            expressionCreator = { position, callee, _, sequenceType ->
                NoArgumentSameSequenceReturnTypeSequenceFunctionExpression(
                        position = position,
                        callee = callee,
                        sequence = sequenceType,
                        targetMethodName = "distinct"
                )
            }
        }
    }
    this.create(methodName = "distinctBy") {
        data {
            argument { getTypeFun(Function::class) }
            expressionCreator = { position, callee, arguments, sequenceType ->
                SingleArgumentSameSequenceReturnTypeSequenceFunctionExpression(
                        position = position,
                        callee = callee,
                        sequence = sequenceType,
                        function = arguments.convertToGeneric(sequenceType, javaLambdaPosition = 0, sequenceTargetPositions = listOf(0)).extractFunction(position = 0),
                        expressionInvocationData = ExpressionInvocationData(name = "distinctBy", argumentClass = Function::class, sequenceTargetPositions = listOf(0))
                        // apply(t: T): K (but K can be Object in this case)
                )
            }
        }
    }
    this.create(methodName = "all") {
        data {
            argument { getTypeFun(Predicate::class) }
            expressionCreator = { position, callee, arguments, sequenceType ->
                SingleArgumentKnownReturnTypeSequenceFunctionExpression(
                        position = position,
                        callee = callee,
                        returnType = ReturnTypeData(targetClass = java.lang.Boolean.TYPE.kotlin, correspondingZenType = ZenType.BOOL),
                        function = arguments.convertToGeneric(sequenceType, javaLambdaPosition = 0, sequenceTargetPositions = listOf(0)).extractFunction(position = 0),
                        expressionInvocationData = ExpressionInvocationData(name = "all", argumentClass = Predicate::class, sequenceTargetPositions = listOf(0)) // test(t: T): Boolean
                )
            }
        }
    }
    this.create(methodName = "any") {
        data {
            expressionCreator = { position, callee, _, _ ->
                NoArgumentKnownReturnTypeSequenceFunctionExpression(
                        position = position,
                        callee = callee,
                        returnType = ReturnTypeData(targetClass = java.lang.Boolean.TYPE.kotlin, correspondingZenType = ZenType.BOOL),
                        targetMethodName = "any"
                )
            }
        }
        data {
            argument { getTypeFun(Predicate::class) }
            expressionCreator = { position, callee, arguments, sequenceType ->
                SingleArgumentKnownReturnTypeSequenceFunctionExpression(
                        position = position,
                        callee = callee,
                        returnType = ReturnTypeData(targetClass = java.lang.Boolean.TYPE.kotlin, correspondingZenType = ZenType.BOOL),
                        function = arguments.convertToGeneric(sequenceType, javaLambdaPosition = 0, sequenceTargetPositions = listOf(0)).extractFunction(position = 0),
                        expressionInvocationData = ExpressionInvocationData(name = "any", argumentClass = Predicate::class, sequenceTargetPositions = listOf(0)) // test(t: T): Boolean
                )
            }
        }
    }
    this.create(methodName = "count") {
        data {
            expressionCreator = { position, callee, _, _ ->
                NoArgumentKnownReturnTypeSequenceFunctionExpression(
                        position = position,
                        callee = callee,
                        returnType = ReturnTypeData(targetClass = Integer.TYPE.kotlin, correspondingZenType = ZenType.INT),
                        targetMethodName = "count"
                )
            }
        }
        data {
            argument { getTypeFun(Predicate::class) }
            expressionCreator = { position, callee, arguments, sequenceType ->
                SingleArgumentKnownReturnTypeSequenceFunctionExpression(
                        position = position,
                        callee = callee,
                        returnType = ReturnTypeData(targetClass = Integer.TYPE.kotlin, correspondingZenType = ZenType.INT),
                        function = arguments.convertToGeneric(sequenceType, javaLambdaPosition = 0, sequenceTargetPositions = listOf(0)).extractFunction(position = 0),
                        expressionInvocationData = ExpressionInvocationData(name = "count", argumentClass = Predicate::class, sequenceTargetPositions = listOf(0)) // test(t: T): Boolean
                )
            }
        }
    }
    this.create(methodName = "fold") {
        data {
            argument { getNativeFun(ObjectZenType) }
            argument { getTypeFun(BiFunction::class) } // TODO("Infer?")
            expressionCreator = { position, callee, arguments, sequenceType ->
                FoldSequenceFunctionExpression(
                        position = position,
                        callee = callee,
                        sequence = sequenceType,
                        firstArgumentExpression = arguments.extractArgument(position = 0),
                        secondArgumentFunction = arguments.convertToGeneric(sequenceType, javaLambdaPosition = 1, sequenceTargetPositions = listOf(1)).extractFunction(position = 1),
                        expressionInvocationData = ExpressionInvocationData(name = "fold", argumentClass = BiFunction::class, sequenceTargetPositions = listOf(1))
                        // apply(r: R, t: T): R (but I have no idea)
                )
            }
        }
    }
    this.create(methodName = "forEach") {
        data {
            argument { getTypeFun(Consumer::class) }
            expressionCreator = { position, callee, arguments, sequenceType ->
                SingleArgumentKnownReturnTypeSequenceFunctionExpression(
                        position = position,
                        callee = callee,
                        returnType = ReturnTypeData(targetClass = Void.TYPE.kotlin, correspondingZenType = ZenType.VOID),
                        function = arguments.convertToGeneric(sequenceType, javaLambdaPosition = 0, sequenceTargetPositions = listOf(0)).extractFunction(position = 0),
                        expressionInvocationData = ExpressionInvocationData(name = "forEach", argumentClass = Consumer::class, sequenceTargetPositions = listOf(0)) // accept(t: T): Unit
                )
            }
        }
    }
    this.create(methodName = "forEachIndexed") {
        data {
            argument { getTypeFun(ObjIntConsumer::class) }
            expressionCreator = { position, callee, arguments, sequenceType ->
                SingleArgumentKnownReturnTypeSequenceFunctionExpression(
                        position = position,
                        callee = callee,
                        returnType = ReturnTypeData(targetClass = Void.TYPE.kotlin, correspondingZenType = ZenType.VOID),
                        function = arguments.convertToGeneric(sequenceType, javaLambdaPosition = 0, sequenceTargetPositions = listOf(0)).extractFunction(position = 0),
                        expressionInvocationData = ExpressionInvocationData(name = "forEachIndexed", argumentClass = ObjIntConsumer::class, sequenceTargetPositions = listOf(0))
                        // accept(t: T, value: Int): Unit
                )
            }
        }
    }
    this.create(methodName = "maxWith") {
        data {
            argument { getTypeFun(ToIntBiFunction::class) }
            expressionCreator = { position, callee, arguments, sequenceType ->
                SingleArgumentGenericReturnTypeSequenceFunctionExpression(
                        position = position,
                        callee = callee,
                        sequenceType = sequenceType,
                        function = arguments.convertToGeneric(sequenceType, javaLambdaPosition = 0, sequenceTargetPositions = listOf(0, 1)).extractFunction(position = 0),
                        expressionInvocationData = ExpressionInvocationData(name = "maxWith", argumentClass = ToIntBiFunction::class, sequenceTargetPositions = listOf(0, 1))
                        // apply(t: T, u: U): Int
                )
            }
        }
    }
    this.create(methodName = "minWith") {
        data {
            argument { getTypeFun(ToIntBiFunction::class) }
            expressionCreator = { position, callee, arguments, sequenceType ->
                SingleArgumentGenericReturnTypeSequenceFunctionExpression(
                        position = position,
                        callee = callee,
                        sequenceType = sequenceType,
                        function = arguments.convertToGeneric(sequenceType, javaLambdaPosition = 0, sequenceTargetPositions = listOf(0, 1)).extractFunction(position = 0),
                        expressionInvocationData = ExpressionInvocationData(name = "minWith", argumentClass = ToIntBiFunction::class, sequenceTargetPositions = listOf(0, 1))
                        // apply(t: T, u: U): Int
                )
            }
        }
    }
    this.create(methodName = "none") {
        data {
            expressionCreator = { position, callee, _, _ ->
                NoArgumentKnownReturnTypeSequenceFunctionExpression(
                        position = position,
                        callee = callee,
                        returnType = ReturnTypeData(targetClass = java.lang.Boolean.TYPE.kotlin, correspondingZenType = ZenType.BOOL),
                        targetMethodName = "none"
                )
            }
        }
        data {
            argument { getTypeFun(Predicate::class) }
            expressionCreator = { position, callee, arguments, sequenceType ->
                SingleArgumentKnownReturnTypeSequenceFunctionExpression(
                        position = position,
                        callee = callee,
                        returnType = ReturnTypeData(targetClass = java.lang.Boolean.TYPE.kotlin, correspondingZenType = ZenType.BOOL),
                        function = arguments.convertToGeneric(sequenceType, javaLambdaPosition = 0, sequenceTargetPositions = listOf(0)).extractFunction(position = 0),
                        expressionInvocationData = ExpressionInvocationData(name = "none", argumentClass = Predicate::class, sequenceTargetPositions = listOf(0))
                )
            }
        }
    }
    this.create(methodName = "onEach") {
        data {
            argument { getTypeFun(Consumer::class) }
            expressionCreator = { position, callee, arguments, sequenceType ->
                SingleArgumentSameSequenceReturnTypeSequenceFunctionExpression(
                        position = position,
                        callee = callee,
                        sequence = sequenceType,
                        function = arguments.convertToGeneric(sequenceType, javaLambdaPosition = 0, sequenceTargetPositions = listOf(0)).extractFunction(position = 0),
                        expressionInvocationData = ExpressionInvocationData(name = "onEach", argumentClass = Consumer::class, sequenceTargetPositions = listOf(0)) // accept(t: T): Unit
                )
            }
        }
    }
    this.create(methodName = "sumBy") {
        data {
            argument { getTypeFun(ToIntFunction::class) }
            expressionCreator = { position, callee, arguments, sequenceType ->
                SingleArgumentKnownReturnTypeSequenceFunctionExpression(
                        position = position,
                        callee = callee,
                        returnType = ReturnTypeData(targetClass = Integer.TYPE.kotlin, correspondingZenType = ZenType.INT),
                        function = arguments.convertToGeneric(sequenceType, javaLambdaPosition = 0, sequenceTargetPositions = listOf(0)).extractFunction(position = 0),
                        expressionInvocationData = ExpressionInvocationData(name = "sumBy", argumentClass = ToIntFunction::class, sequenceTargetPositions = listOf(0)) // apply(t: T): Int
                )
            }
        }
    }
    this.create(methodName = "sumByDouble") {
        data {
            argument { getTypeFun(ToDoubleFunction::class) }
            expressionCreator = { position, callee, arguments, sequenceType ->
                SingleArgumentKnownReturnTypeSequenceFunctionExpression(
                        position = position,
                        callee = callee,
                        returnType = ReturnTypeData(targetClass = java.lang.Double.TYPE.kotlin, correspondingZenType = ZenType.DOUBLE),
                        function = arguments.convertToGeneric(sequenceType, javaLambdaPosition = 0, sequenceTargetPositions = listOf(0)).extractFunction(position = 0),
                        expressionInvocationData = ExpressionInvocationData(name = "sumByDouble", argumentClass = ToDoubleFunction::class, sequenceTargetPositions = listOf(0))
                        // apply(t: T): Double
                )
            }
        }
    }
    this.create(methodName = "minus") {
        data {
            argument { { _, sequenceType -> ZenTypeArrayBasic(sequenceType.genericType) } }
            expressionCreator = { position, callee, arguments, sequenceType ->
                SingleGenericArgumentSameSequenceReturnTypeSequenceFunctionExpression(
                        position = position,
                        callee = callee,
                        sequence = sequenceType,
                        isArray = true,
                        argumentExpression = arguments.extractArgument(position = 0),
                        methodName = "minus"
                )
            }
        }
    }
    this.create(methodName = "minusElement") {
        data {
            argument { getNativeFun(ObjectZenType) }
            expressionCreator = { position, callee, arguments, sequenceType ->
                SingleGenericArgumentSameSequenceReturnTypeSequenceFunctionExpression(
                        position = position,
                        callee = callee,
                        sequence = sequenceType,
                        isArray = false,
                        argumentExpression = arguments.extractArgument(position = 0),
                        methodName = "minusElement"
                )
            }
        }
    }
    this.create(methodName = "plus") {
        data {
            argument { { _, sequenceType -> ZenTypeArrayBasic(sequenceType.genericType) } }
            expressionCreator = { position, callee, arguments, sequenceType ->
                SingleGenericArgumentSameSequenceReturnTypeSequenceFunctionExpression(
                        position = position,
                        callee = callee,
                        sequence = sequenceType,
                        isArray = true,
                        argumentExpression = arguments.extractArgument(position = 0),
                        methodName = "plus"
                )
            }
        }
    }
    this.create(methodName = "plusElement") {
        data {
            argument { getNativeFun(ObjectZenType) }
            expressionCreator = { position, callee, arguments, sequenceType ->
                SingleGenericArgumentSameSequenceReturnTypeSequenceFunctionExpression(
                        position = position,
                        callee = callee,
                        sequence = sequenceType,
                        isArray = false,
                        argumentExpression = arguments.extractArgument(position = 0),
                        methodName = "plusElement"
                )
            }
        }
    }
    this.create(methodName = "zipWithNext") {
        data {
            argument { { _, sequenceType -> FunctionSequenceZenType(sequenceType, BiFunction::class) } }
            expressionCreator = { position, callee, arguments, sequenceType ->
                SingleArgumentDifferentSequenceReturnTypeSequenceFunctionExpression(
                        position = position,
                        callee = callee,
                        sequence = sequenceType,
                        function = arguments.convertToGeneric(sequenceType, javaLambdaPosition = 0, sequenceTargetPositions = listOf(0, 1)).extractFunction(position = 0), // TODO("?")
                        expressionInvocationData = ExpressionInvocationData(name = "zipWithNext", argumentClass = BiFunction::class, sequenceTargetPositions = listOf(0, 1))
                        // apply(t: T, u: U): R
                )
            }
        }
    }
    this.create(methodName = "joinToString") {
        data {
            expressionCreator = { position, callee, _, _ ->
                JoinToStringSequenceFunctionExpression(
                        position = position,
                        callee = callee,
                        separatorExpression = null,
                        prefixExpression = null,
                        postfixExpression = null,
                        limitExpression = null,
                        truncatedExpression = null,
                        transformFunction = null,
                        sequenceTargetPositions = listOf(0)
                )
            }
        }
        data {
            argument { getNativeFun(ZenType.STRING) }
            expressionCreator = { position, callee, arguments, _ ->
                JoinToStringSequenceFunctionExpression(
                        position = position,
                        callee = callee,
                        separatorExpression = arguments.extractArgument(position = 0),
                        prefixExpression = null,
                        postfixExpression = null,
                        limitExpression = null,
                        truncatedExpression = null,
                        transformFunction = null,
                        sequenceTargetPositions = listOf(0)
                )
            }
        }
        data {
            argument { getNativeFun(ZenType.STRING) }
            argument { getNativeFun(ZenType.STRING) }
            expressionCreator = { position, callee, arguments, _ ->
                JoinToStringSequenceFunctionExpression(
                        position = position,
                        callee = callee,
                        separatorExpression = arguments.extractArgument(position = 0),
                        prefixExpression = arguments.extractArgument(position = 1),
                        postfixExpression = null,
                        limitExpression = null,
                        truncatedExpression = null,
                        transformFunction = null,
                        sequenceTargetPositions = listOf(0)
                )
            }
        }
        data {
            argument { getNativeFun(ZenType.STRING) }
            argument { getNativeFun(ZenType.STRING) }
            argument { getNativeFun(ZenType.STRING) }
            expressionCreator = { position, callee, arguments, _ ->
                JoinToStringSequenceFunctionExpression(
                        position = position,
                        callee = callee,
                        separatorExpression = arguments.extractArgument(position = 0),
                        prefixExpression = arguments.extractArgument(position = 1),
                        postfixExpression = arguments.extractArgument(position = 2),
                        limitExpression = null,
                        truncatedExpression = null,
                        transformFunction = null,
                        sequenceTargetPositions = listOf(0)
                )
            }
        }
        data {
            argument { getNativeFun(ZenType.STRING) }
            argument { getNativeFun(ZenType.STRING) }
            argument { getNativeFun(ZenType.STRING) }
            argument { getNativeFun(ZenType.INT) }
            expressionCreator = { position, callee, arguments, _ ->
                JoinToStringSequenceFunctionExpression(
                        position = position,
                        callee = callee,
                        separatorExpression = arguments.extractArgument(position = 0),
                        prefixExpression = arguments.extractArgument(position = 1),
                        postfixExpression = arguments.extractArgument(position = 2),
                        limitExpression = arguments.extractArgument(position = 3),
                        truncatedExpression = null,
                        transformFunction = null,
                        sequenceTargetPositions = listOf(0)
                )
            }
        }
        data {
            argument { getNativeFun(ZenType.STRING) }
            argument { getNativeFun(ZenType.STRING) }
            argument { getNativeFun(ZenType.STRING) }
            argument { getNativeFun(ZenType.INT) }
            argument { getNativeFun(ZenType.STRING) }
            expressionCreator = { position, callee, arguments, _ ->
                JoinToStringSequenceFunctionExpression(
                        position = position,
                        callee = callee,
                        separatorExpression = arguments.extractArgument(position = 0),
                        prefixExpression = arguments.extractArgument(position = 1),
                        postfixExpression = arguments.extractArgument(position = 2),
                        limitExpression = arguments.extractArgument(position = 3),
                        truncatedExpression = arguments.extractArgument(position = 4),
                        transformFunction = null,
                        sequenceTargetPositions = listOf(0)
                )
            }
        }
        data {
            argument { getNativeFun(ZenType.STRING) }
            argument { getNativeFun(ZenType.STRING) }
            argument { getNativeFun(ZenType.STRING) }
            argument { getNativeFun(ZenType.INT) }
            argument { getNativeFun(ZenType.STRING) }
            argument { { _, sequenceType -> FunctionSequenceZenType(sequenceType, Function::class) } }
            expressionCreator = { position, callee, arguments, sequenceType ->
                JoinToStringSequenceFunctionExpression(
                        position = position,
                        callee = callee,
                        separatorExpression = arguments.extractArgument(position = 0),
                        prefixExpression = arguments.extractArgument(position = 1),
                        postfixExpression = arguments.extractArgument(position = 2),
                        limitExpression = arguments.extractArgument(position = 3),
                        truncatedExpression = arguments.extractArgument(position = 4),
                        transformFunction = arguments.convertToGeneric(sequenceType, javaLambdaPosition = 5, sequenceTargetPositions = listOf(0)).extractFunction(position = 5),
                        sequenceTargetPositions = listOf(0)
                )
            }
        }
    }
    this.create(methodName = "asSequence") {
        data {
            expressionCreator = { position, callee, _, sequenceType ->
                NoArgumentSameSequenceReturnTypeSequenceFunctionExpression(
                        position = position,
                        callee = callee,
                        sequence = sequenceType,
                        targetMethodName = "asSequence"
                )
            }
        }
    }
}

private fun MutableMap<String, List<SequenceMethodData>>.create(methodName: String, builder: MutableList<SequenceMethodData>.() -> Unit) {
    val targetList = (this[methodName] ?: listOf()).toMutableList()
    targetList.builder()
    this[methodName] = targetList
}

private fun MutableList<SequenceMethodData>.data(creator: SequenceMethodDataBuilder.() -> Unit) {
    val builder = SequenceMethodDataBuilder()
    builder.creator()
    this += builder.build()
}

private fun Map<String, List<SequenceMethodData>>.logMissing() =
        ZenSequence::class.declaredFunctions
                .asSequence()
                .filter { it.visibility == KVisibility.PUBLIC }
                .onEach { missingLog.debug("Checking presence of mapping for function '${it.name}(${it.parameters.asSequence().drop(1).map { p -> p.type }.joinToString()})'") }
                .filterNot { this[it.name] != null && this.getValue(it.name).any { data -> data.argumentsCount == it.parameters.count() - 1 }}
                .map { "Missing registry data entry for method '${it.name}' with ${it.parameters.count() - 1} arguments! This is a serious error!" }
                .forEach { missingLog.error(it) }

private fun getNativeFun(type: ZenType): ZenTypeCreator = { _, _ -> type }
private fun <T : Any> getTypeFun(type: KClass<T>): ZenTypeCreator = { environment, _ -> environment[type] }
private operator fun <T : Any> IEnvironmentGlobal?.get(clazz: KClass<T>): ZenType = this!!.getType(clazz.java)

private fun List<Expression>.convertToGeneric(sequenceType: SequenceZenType, javaLambdaPosition: Int, sequenceTargetPositions: List<Int>): List<Expression> {
    val returningList = mutableListOf<Expression>()
    this.forEachIndexed { index, expression ->
        if (index != javaLambdaPosition) {
            returningList += expression
            return@forEachIndexed
        }
        if (expression !is ExpressionJavaLambdaSimpleGeneric) throw IllegalStateException("Expected a lambda at position '$javaLambdaPosition' but instead found '$expression'")
        returningList += expression.convert(sequenceType, sequenceTargetPositions)
    }
    return returningList
}

private fun List<Expression>.extractFunction(position: Int) = this.extractArgument(position)
private fun List<Expression>.extractArgument(position: Int) = this[position]

internal fun ExpressionJavaLambdaSimpleGeneric.convert(sequenceType: SequenceZenType, sequenceTargetPositions: List<Int>): ExpressionJavaLambdaSimpleGeneric {
    return ExpressionJavaLambdaSimpleGeneric(
            this.position, this.interfaceClass, this.arguments.toList().toMutableList().convert(sequenceType, sequenceTargetPositions), this.statements, this.type)
}

internal tailrec fun MutableList<ParsedFunctionArgument>.convert(sequenceType: SequenceZenType, sequenceTargetPositions: List<Int>): List<ParsedFunctionArgument> {
    if (sequenceTargetPositions.count() <= 0) return this.toList()
    val position = sequenceTargetPositions[0]
    val subList = sequenceTargetPositions.drop(1)
    return this.convert(sequenceType, position).convert(sequenceType, subList)
}

private fun MutableList<ParsedFunctionArgument>.convert(sequenceType: SequenceZenType, sequenceTargetPosition: Int): MutableList<ParsedFunctionArgument> {
    if (this.count() <= sequenceTargetPosition) throw IllegalStateException("Expected lambda generic argument at position '$sequenceTargetPosition' but expression wasn't long enough")
    val targetArgument = this[sequenceTargetPosition]
    if (targetArgument.type == ZenType.ANY) {
        this[sequenceTargetPosition] = SequenceParsedFunctionArgument(targetArgument, sequenceType)
    }
    return this
}
