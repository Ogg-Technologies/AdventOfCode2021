package utils

fun getCenterIntersectedPositions(startTile: Vector, endTile: Vector): List<Vector> {
    if (startTile == endTile) return listOf(startTile)
    if (startTile.intX == endTile.intX) return (startTile.intY..endTile.intY).map { Vector(startTile.intX, it) }
    if (startTile.intY == endTile.intY) return (startTile.intX..endTile.intX).map { Vector(it, startTile.intY) }

    val diff = endTile - startTile
    val step = gcd(diff.intX, diff.intY)
    val normalizedDiff = diff / step

    val positions = mutableListOf(startTile)
    var current = startTile
    while (current.intX != endTile.intX) {
        current += normalizedDiff
        positions += current
    }
    return positions
}

private fun gcd(a: Int, b: Int): Int {
    return if (b == 0) a else gcd(b, a % b)
}