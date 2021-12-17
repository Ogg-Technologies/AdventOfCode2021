package utils

abstract class RectangularTileWorld<T> : TileWorld<T> {
    abstract val minPos: Vector
    abstract val maxPos: Vector

    val xPositions get() = (minPos.intX..maxPos.intX)
    val yPositions get() = (minPos.intY..maxPos.intY)
    val positions get() = (xPositions product yPositions).map { it.toVector() }

    fun forEach(action: (T) -> Unit) = positions.forEach { pos -> action(get(pos)) }
    fun forEachIndexed(action: (pos: Vector, T) -> Unit) = positions.forEach { pos -> action(pos, get(pos)) }

    fun mapInPlace(mutator: (T) -> T) = positions.forEach { pos -> set(pos, mutator(get(pos))) }
    fun mapInPlaceIndexed(mutator: (pos: Vector, T) -> T) =
        positions.forEach { pos -> set(pos, mutator(pos, get(pos))) }

    operator fun iterator(): Iterator<T> = positions.map { get(it) }.iterator()

    override fun toString(): String = toString { it.toString() }
    fun toString(elementTransformer: (T) -> String): String =
        toStringIndexed { pos, element -> "$pos: ${elementTransformer(element)}" }

    fun toStringIndexed(elementTransformer: (pos: Vector, T) -> String): String {
        val elementSize = positions.maxOfOrNull { elementTransformer(it, get(it)).length } ?: 0
        return yPositions.fold("") { acc, y ->
            acc + xPositions.fold("") { rowAcc, x ->
                rowAcc + elementTransformer(Vector(x, y), get(Vector(x, y))).padEnd(elementSize + 1)
            } + "\n"
        }
    }
}

infix fun <T> Vector.isWithin(world: RectangularTileWorld<T>): Boolean =
    intY in world.minPos.intY..world.maxPos.intY && intX in world.minPos.intX..world.maxPos.intX

infix fun <T> Vector.isNotWithin(world: RectangularTileWorld<T>): Boolean = !isWithin(world)

fun <T> RectangularTileWorld<T>.asGraph(getNeighbors: (pos: Vector) -> Collection<Vector>): Graph<Vector> =
    object : Graph<Vector> {
        override val nodes: Collection<Vector> = positions
        override fun getNeighbors(pos: Vector): Collection<Vector> = getNeighbors(pos)
    }

fun <T> RectangularTileWorld<T>.asGraph(neighborDeltas: Collection<Vector>): Graph<Vector> =
    this.asGraph { pos -> neighborDeltas.map { pos + it }.filter { it isWithin this } }