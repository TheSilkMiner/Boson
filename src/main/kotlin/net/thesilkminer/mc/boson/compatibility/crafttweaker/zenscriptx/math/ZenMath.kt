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

package net.thesilkminer.mc.boson.compatibility.crafttweaker.zenscriptx.math

import crafttweaker.annotations.ZenRegister
import stanhebben.zenscript.annotations.ZenClass
import stanhebben.zenscript.annotations.ZenMethod
import kotlin.math.E
import kotlin.math.IEEErem
import kotlin.math.PI
import kotlin.math.pow
import kotlin.math.roundToInt
import kotlin.math.roundToLong
import kotlin.math.sign

@Suppress("SpellCheckingInspection")
@ZenClass("zenscriptx.math.Math")
@ZenRegister
object ZenMath {
    // Constants
    @ZenMethod("pi") @JvmStatic fun pi() = PI
    @ZenMethod("e") @JvmStatic fun e() = E

    // Double-based math - Kotlin Standard Lib
    @ZenMethod("sin") @JvmStatic fun sin(x: Double) = kotlin.math.sin(x)
    @ZenMethod("cos") @JvmStatic fun cos(x: Double) = kotlin.math.cos(x)
    @ZenMethod("tan") @JvmStatic fun tan(x: Double) = kotlin.math.tan(x)
    @ZenMethod("asin") @JvmStatic fun asin(x: Double) = kotlin.math.asin(x)
    @ZenMethod("acos") @JvmStatic fun acos(x: Double) = kotlin.math.acos(x)
    @ZenMethod("atan") @JvmStatic fun atan(x: Double) = kotlin.math.atan(x)
    @ZenMethod("atan2") @JvmStatic fun atan2(y: Double, x: Double) = kotlin.math.atan2(y, x)
    @ZenMethod("sinh") @JvmStatic fun sinh(x: Double) = kotlin.math.sinh(x)
    @ZenMethod("cosh") @JvmStatic fun cosh(x: Double) = kotlin.math.cosh(x)
    @ZenMethod("tanh") @JvmStatic fun tanh(x: Double) = kotlin.math.tanh(x)
    @ZenMethod("asinh") @JvmStatic fun asinh(x: Double) = kotlin.math.asinh(x)
    @ZenMethod("acosh") @JvmStatic fun acosh(x: Double) = kotlin.math.acosh(x)
    @ZenMethod("atanh") @JvmStatic fun atanh(x: Double) = kotlin.math.atanh(x)
    @ZenMethod("hypot") @JvmStatic fun hypot(x: Double, y: Double) = kotlin.math.hypot(x, y)
    @ZenMethod("sqrt") @JvmStatic fun sqrt(x: Double) = kotlin.math.sqrt(x)
    @ZenMethod("exp") @JvmStatic fun exp(x: Double) = kotlin.math.exp(x)
    @ZenMethod("expm1") @JvmStatic fun expm1(x: Double) = kotlin.math.expm1(x)
    @ZenMethod("logn") @JvmStatic fun logn(x: Double, base: Double) = kotlin.math.log(x, base)
    @ZenMethod("ln") @JvmStatic fun ln(x: Double) = kotlin.math.ln(x)
    @ZenMethod("log10") @JvmStatic fun log10(x: Double) = kotlin.math.log10(x)
    @ZenMethod("log2") @JvmStatic fun log2(x: Double) = kotlin.math.log2(x)
    @ZenMethod("ln1p") @JvmStatic fun ln1p(x: Double) = kotlin.math.ln1p(x)
    @ZenMethod("ceil") @JvmStatic fun ceil(x: Double) = kotlin.math.ceil(x)
    @ZenMethod("floor") @JvmStatic fun floor(x: Double) = kotlin.math.floor(x)
    @ZenMethod("truncate") @JvmStatic fun truncate(x: Double) = kotlin.math.truncate(x)
    @ZenMethod("round") @JvmStatic fun round(x: Double) = kotlin.math.round(x)
    @ZenMethod("abs") @JvmStatic fun abs(x: Double) = kotlin.math.abs(x)
    @ZenMethod("sign") @JvmStatic fun sign(x: Double) = kotlin.math.sign(x)
    @ZenMethod("min") @JvmStatic fun min(a: Double, b: Double) = kotlin.math.min(a, b)
    @ZenMethod("max") @JvmStatic fun max(a: Double, b: Double) = kotlin.math.max(a, b)
    @ZenMethod("pow") @JvmStatic fun pow(base: Double, exp: Double) = base.pow(exp)
    @ZenMethod("pow") @JvmStatic fun pow(base: Double, exp: Int) = base.pow(exp)
    @ZenMethod("rem") @JvmStatic fun remIEEE(value: Double, div: Double) = value.IEEErem(div)

    // Double-based math - ZenScriptX additions
    @ZenMethod("sind") @JvmStatic fun sind(x: Double) = sin(d2r(x))
    @ZenMethod("cosd") @JvmStatic fun cosd(x: Double) = cos(d2r(x))
    @ZenMethod("tand") @JvmStatic fun tand(x: Double) = tan(d2r(x))
    @ZenMethod("asind") @JvmStatic fun asind(x: Double) = r2d(asin(x))
    @ZenMethod("acosd") @JvmStatic fun acosd(x: Double) = r2d(acos(x))
    @ZenMethod("atand") @JvmStatic fun atand(x: Double) = r2d(atan(x))
    @ZenMethod("nsqrt") @JvmStatic fun nsqrt(x: Double) = -sqrt(x)
    @ZenMethod("ceili") @JvmStatic fun ceili(x: Double) = ceil(x).roundToInt()
    @ZenMethod("floori") @JvmStatic fun floori(x: Double) = floor(x).roundToInt()
    @ZenMethod("roundi") @JvmStatic fun roundi(x: Double) = round(x).roundToInt()
    @ZenMethod("ceill") @JvmStatic fun ceill(x: Double) = ceil(x).roundToLong()
    @ZenMethod("floorl") @JvmStatic fun floorl(x: Double) = floor(x).roundToLong()
    @ZenMethod("roundl") @JvmStatic fun roundl(x: Double) = round(x).roundToLong()
    @ZenMethod("sq") @JvmStatic fun sq(x: Double) = pow(x, 2)
    @ZenMethod("clamp") @JvmStatic fun clamp(x: Double, min: Double, max: Double) = if (x <= min) min else if (x >= max) max else x

    // Float-based math - Kotlin Standard Lib
    @ZenMethod("sin") @JvmStatic fun sin(x: Float) = kotlin.math.sin(x)
    @ZenMethod("cos") @JvmStatic fun cos(x: Float) = kotlin.math.cos(x)
    @ZenMethod("tan") @JvmStatic fun tan(x: Float) = kotlin.math.tan(x)
    @ZenMethod("asin") @JvmStatic fun asin(x: Float) = kotlin.math.asin(x)
    @ZenMethod("acos") @JvmStatic fun acos(x: Float) = kotlin.math.acos(x)
    @ZenMethod("atan") @JvmStatic fun atan(x: Float) = kotlin.math.atan(x)
    @ZenMethod("atan2") @JvmStatic fun atan2(y: Float, x: Float) = kotlin.math.atan2(y, x)
    @ZenMethod("sinh") @JvmStatic fun sinh(x: Float) = kotlin.math.sinh(x)
    @ZenMethod("cosh") @JvmStatic fun cosh(x: Float) = kotlin.math.cosh(x)
    @ZenMethod("tanh") @JvmStatic fun tanh(x: Float) = kotlin.math.tanh(x)
    @ZenMethod("asinh") @JvmStatic fun asinh(x: Float) = kotlin.math.asinh(x)
    @ZenMethod("acosh") @JvmStatic fun acosh(x: Float) = kotlin.math.acosh(x)
    @ZenMethod("atanh") @JvmStatic fun atanh(x: Float) = kotlin.math.atanh(x)
    @ZenMethod("hypot") @JvmStatic fun hypot(x: Float, y: Float) = kotlin.math.hypot(x, y)
    @ZenMethod("sqrt") @JvmStatic fun sqrt(x: Float) = kotlin.math.sqrt(x)
    @ZenMethod("exp") @JvmStatic fun exp(x: Float) = kotlin.math.exp(x)
    @ZenMethod("expm1") @JvmStatic fun expm1(x: Float) = kotlin.math.expm1(x)
    @ZenMethod("logn") @JvmStatic fun logn(x: Float, base: Float) = kotlin.math.log(x, base)
    @ZenMethod("ln") @JvmStatic fun ln(x: Float) = kotlin.math.ln(x)
    @ZenMethod("log10") @JvmStatic fun log10(x: Float) = kotlin.math.log10(x)
    @ZenMethod("log2") @JvmStatic fun log2(x: Float) = kotlin.math.log2(x)
    @ZenMethod("ln1p") @JvmStatic fun ln1p(x: Float) = kotlin.math.ln1p(x)
    @ZenMethod("ceil") @JvmStatic fun ceil(x: Float) = kotlin.math.ceil(x)
    @ZenMethod("floor") @JvmStatic fun floor(x: Float) = kotlin.math.floor(x)
    @ZenMethod("truncate") @JvmStatic fun truncate(x: Float) = kotlin.math.truncate(x)
    @ZenMethod("round") @JvmStatic fun round(x: Float) = kotlin.math.round(x)
    @ZenMethod("abs") @JvmStatic fun abs(x: Float) = kotlin.math.abs(x)
    @ZenMethod("sign") @JvmStatic fun sign(x: Float) = kotlin.math.sign(x)
    @ZenMethod("min") @JvmStatic fun min(a: Float, b: Float) = kotlin.math.min(a, b)
    @ZenMethod("max") @JvmStatic fun max(a: Float, b: Float) = kotlin.math.max(a, b)
    @ZenMethod("pow") @JvmStatic fun pow(base: Float, exp: Float) = base.pow(exp)
    @ZenMethod("pow") @JvmStatic fun pow(base: Float, exp: Int) = base.pow(exp)
    @ZenMethod("rem") @JvmStatic fun remIEEE(value: Float, div: Float) = value.IEEErem(div)

    // Float-based math - ZenScriptX additions
    @ZenMethod("sind") @JvmStatic fun sind(x: Float) = sin(d2r(x))
    @ZenMethod("cosd") @JvmStatic fun cosd(x: Float) = cos(d2r(x))
    @ZenMethod("tand") @JvmStatic fun tand(x: Float) = tan(d2r(x))
    @ZenMethod("asind") @JvmStatic fun asind(x: Float) = r2d(asin(x))
    @ZenMethod("acosd") @JvmStatic fun acosd(x: Float) = r2d(acos(x))
    @ZenMethod("atand") @JvmStatic fun atand(x: Float) = r2d(atan(x))
    @ZenMethod("nsqrt") @JvmStatic fun nsqrt(x: Float) = -sqrt(x)
    @ZenMethod("ceili") @JvmStatic fun ceili(x: Float) = ceil(x).roundToInt()
    @ZenMethod("floori") @JvmStatic fun floori(x: Float) = floor(x).roundToInt()
    @ZenMethod("roundi") @JvmStatic fun roundi(x: Float) = round(x).roundToInt()
    @ZenMethod("ceill") @JvmStatic fun ceill(x: Float) = ceil(x).roundToLong()
    @ZenMethod("floorl") @JvmStatic fun floorl(x: Float) = floor(x).roundToLong()
    @ZenMethod("roundl") @JvmStatic fun roundl(x: Float) = round(x).roundToLong()
    @ZenMethod("sq") @JvmStatic fun sq(x: Float) = pow(x, 2)
    @ZenMethod("clamp") @JvmStatic fun clamp(x: Float, min: Float, max: Float) = if (x <= min) min else if (x >= max) max else x

    // Integer-based math - Kotlin Standard Lib
    @ZenMethod("abs") @JvmStatic fun abs(x: Int) = kotlin.math.abs(x)
    @ZenMethod("min") @JvmStatic fun min(a: Int, b: Int) = kotlin.math.min(a, b)
    @ZenMethod("max") @JvmStatic fun max(a: Int, b: Int) = kotlin.math.max(a, b)
    @ZenMethod("sign") @JvmStatic fun sign(a: Int) = a.sign

    // Integer-based math - ZenScriptX additions
    @ZenMethod("clamp") @JvmStatic fun clamp(x: Int, min: Int, max: Int) = if (x <= min) min else if (x >= max) max else x

    // Long-based math - Kotlin Standard Lib
    @ZenMethod("abs") @JvmStatic fun abs(x: Long) = kotlin.math.abs(x)
    @ZenMethod("min") @JvmStatic fun min(a: Long, b: Long) = kotlin.math.min(a, b)
    @ZenMethod("max") @JvmStatic fun max(a: Long, b: Long) = kotlin.math.max(a, b)
    @ZenMethod("sign") @JvmStatic fun sign(a: Long) = a.sign

    // Long-based math - ZenScriptX additions
    @ZenMethod("clamp") @JvmStatic fun clamp(x: Long, min: Long, max: Long) = if (x <= min) min else if (x >= max) max else x

    // Unexposed converter functions
    private fun d2r(x: Double) = x * PI / 180.0
    private fun d2r(x: Float) = x * PI.toFloat() / 180.0F
    private fun r2d(x: Double) = x * 180.0 / PI
    private fun r2d(x: Float) = x * 180.0F / PI.toFloat()
}
