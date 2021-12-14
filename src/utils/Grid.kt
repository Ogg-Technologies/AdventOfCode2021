package utils

class Grid<T>(val size: Vector, private val grid: Array<Array<T>>) : RectangularTileWorld<T>() {
    companion object {
        inline operator fun <reified T> invoke(): Grid<T> {
            return Grid(Vector.zero, emptyArray())
        }

        inline operator fun <reified T> invoke(size: Vector, init: (pos: Vector) -> T): Grid<T> {
            val grid: Array<Array<T>> =
                Array(size.intY) { y -> Array(size.intX) { x -> init(Vector(x, y)) } }
            return Grid(size, grid)
        }
    }

    override val minPos: Vector
        get() = Vector(0, 0)
    override val maxPos: Vector
        get() = Vector(size.intX - 1, size.intY - 1)

    override operator fun get(pos: Vector): T = grid[pos.intY][pos.intX]
    override operator fun set(pos: Vector, value: T) {
        grid[pos.intY][pos.intX] = value
    }
}

inline fun <reified T> gridOf(size: Vector, value: T): Grid<T> = Grid(size) { value }

inline fun <reified T> String.asCharGrid(transform: (Char) -> T) = this.lines().map { it.toList() }.toGrid(transform)

inline fun <reified T> String.asGridWithSeparators(
    rowSeparator: String = "\n",
    tileSeparator: String = " ",
    transform: (String) -> T
) = this.split(rowSeparator).map { it.split(tileSeparator) }.toGrid(transform)

inline fun <I, reified T> List<List<I>>.toGrid(transform: (I) -> T): Grid<T> {
    if (isEmpty()) return Grid()
    val size = Vector(this.size, this[0].size)
    val grid = Array(size.intY) { y ->
        require(this[y].size == size.intX) { "Inconsistent grid size: ${this[y].size} != $size" }
        Array(size.intX) { x -> transform(this[y][x]) }
    }
    return Grid(size, grid)
}
