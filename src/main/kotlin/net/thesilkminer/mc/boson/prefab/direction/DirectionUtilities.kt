@file:JvmName("DU")

package net.thesilkminer.mc.boson.prefab.direction

import net.minecraft.util.EnumFacing
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
