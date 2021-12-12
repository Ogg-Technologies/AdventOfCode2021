package utils

object Neighborhoods {
    val vonNeumann = listOf<Vector>(
        Vector(1, 0),
        Vector(0, 1),
        Vector(-1, 0),
        Vector(0, -1),
    )

    val moore = (-1..1).flatMap { y -> (-1..1).map { x -> Vector(x, y) } }
        .filter { it != Vector.zero }
}