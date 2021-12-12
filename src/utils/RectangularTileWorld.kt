package utils

abstract class RectangularTileWorld<T> : TileWorld<T> {
    abstract val minPos: Vector
    abstract val maxPos: Vector

    val xPositions = (minPos.intX..maxPos.intX)
    val yPositions = (minPos.intY..maxPos.intY)
    val positions = xPositions.flatMap { x -> yPositions.map { y -> Vector(x, y) } }

    fun forEach(action: (T) -> Unit) = positions.forEach { pos -> action(get(pos)) }
    fun forEachIndexed(action: (pos: Vector, T) -> Unit) = positions.forEach { pos -> action(pos, get(pos)) }

    fun mapInPlace(mutator: (T) -> T) = positions.forEach { pos -> set(pos, mutator(get(pos))) }
    fun mapInPlaceIndexed(mutator: (pos: Vector, T) -> T) =
        positions.forEach { pos -> set(pos, mutator(pos, get(pos))) }

    operator fun iterator(): Iterator<T> = positions.map { get(it) }.iterator()

    override fun toString(): String = toString { it.toString() }
    fun toString(elementTransformer: (T) -> String): String {
        val elementSize = positions.map { get(it).toString().length }.maxOrNull() ?: 0
        return yPositions.fold("") { acc, y ->
            acc + xPositions.fold("") { rowAcc, x ->
                rowAcc + get(Vector(x, y)).toString().padEnd(elementSize + 1)
            } + "\n"
        }
    }
}

infix fun <T> Vector.isWithin(world: RectangularTileWorld<T>): Boolean =
    intY in world.minPos.intY..world.maxPos.intY && intX in world.minPos.intX..world.maxPos.intX

infix fun <T> Vector.isNotWithin(world: RectangularTileWorld<T>): Boolean = !isWithin(world)
