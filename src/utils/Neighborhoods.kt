package utils

import kotlin.math.abs

object Neighborhoods {

    // The 4 closest neighbors
    val vonNeumann = listOf(
        Vector(1, 0),
        Vector(0, 1),
        Vector(-1, 0),
        Vector(0, -1),
    )

    // The 8 closest neighbors
    val moore = (-1..1).square().map { it.toVector() }.filter { it != Vector.zero }

    // Neighbors that a knight could jump to in chess. 2 steps in one axis, 1 step in the other.
    val knightJumps =
        listOf(-2, -1, 1, 2).square().mapNotNull { (x, y) -> if (abs(x) != abs(y)) Vector(x, y) else null }
}