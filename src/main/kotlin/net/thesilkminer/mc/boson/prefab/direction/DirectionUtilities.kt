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

@file:JvmName("DU")

package net.thesilkminer.mc.boson.prefab.direction

import net.minecraft.util.EnumFacing
import net.minecraft.util.math.BlockPos
import net.thesilkminer.mc.boson.api.direction.Direction

fun Direction.toFacing() = when (this) {
    Direction.NORTH -> EnumFacing.NORTH
    Direction.EAST -> EnumFacing.EAST
    Direction.SOUTH -> EnumFacing.SOUTH
    Direction.WEST -> EnumFacing.WEST
    Direction.UP -> EnumFacing.UP
    Direction.DOWN -> EnumFacing.DOWN
}

fun EnumFacing.toDirection() = when (this) {
    EnumFacing.DOWN -> Direction.DOWN
    EnumFacing.UP -> Direction.UP
    EnumFacing.NORTH -> Direction.NORTH
    EnumFacing.SOUTH -> Direction.SOUTH
    EnumFacing.WEST -> Direction.WEST
    EnumFacing.EAST -> Direction.EAST
}

// TODO("API BlockPos out")
fun BlockPos.offset(direction: Direction, amount: Int = 1): BlockPos = this.offset(direction.toFacing(), amount)
