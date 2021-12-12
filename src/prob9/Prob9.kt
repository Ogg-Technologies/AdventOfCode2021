package prob9

import java.io.File
import kotlin.math.*


private val neighborDeltas = listOf(Vector(1, 0), Vector(-1, 0), Vector(0, 1), Vector(0, -1))

fun main() {
    val lines = File("src/prob9Input.txt").readLines()
    val grid = Grid(Vector(lines[0].length, lines.size)) { pos ->
        val char = lines[pos.intY][pos.intX]
        char.toString().toInt()
    }

    val lowestPoints = mutableListOf<Vector>()
    grid.forEachIndexed { pos, value ->
        if (neighborDeltas.map { pos + it }.all { if (it.isWithin(grid)) value < grid[it] else true }) {
            lowestPoints.add(pos)
        }
    }

    println("Risk level ${lowestPoints.sumBy { grid[it] + 1 }}")

    lowestPoints
        .map { pos ->
            grid.calculateSpreadPositions(pos, neighborDeltas) { from, to ->
                from.value < to.value && to.value != 9
            }
        }
        .map { it.size }
        .sortedDescending()
        .take(3)
        .reduce { acc, size -> acc * size }
        .also { println(it) }
}

private class Grid<T>(val size: Vector, private val grid: Array<Array<T>>) {
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

    operator fun get(pos: Vector): T = grid[pos.intY][pos.intX]
    operator fun set(pos: Vector, value: T) {
        grid[pos.intY][pos.intX] = value
    }

    operator fun get(y: Int): Array<T> = grid[y]

    operator fun iterator(): Iterator<T> {
        return grid.flatMap { it.asList() }.iterator()
    }

    fun forEach(action: (T) -> Unit) {
        grid.forEach { arr -> arr.forEach(action) }
    }

    fun forEachIndexed(action: (pos: Vector, T) -> Unit) {
        grid.forEachIndexed { y, arr ->
            arr.forEachIndexed { x, value ->
                action(
                    Vector(x, y),
                    value
                )
            }
        }
    }

    data class GridElement<T>(val pos: Vector, val value: T)

    fun calculateSpreadPositions(
        startPositions: Vector,
        neighborDeltas: List<Vector>,
        spreadAllowed: (from: GridElement<T>, to: GridElement<T>) -> Boolean,
    ): Set<Vector> {
        fun neighborsTo(pos: Vector) = neighborDeltas.map { pos + it }
        val spreads = mutableSetOf(startPositions)
        fun spread(pos: Vector) {
            neighborsTo(pos)
                .filter { neighbor ->
                    neighbor isWithin this && neighbor !in spreads && spreadAllowed(
                        GridElement(pos, this[pos]),
                        GridElement(neighbor, this[neighbor])
                    )
                }
                .forEach {
                    spreads.add(it)
                    spread(it)
                }
        }
        spread(startPositions)
        return spreads
    }

    fun forEachInSection(startPos: Vector, size: Vector, action: (T) -> Unit) {
        forEachInSectionIndexed(startPos, size) { _, t -> action(t) }
    }

    fun forEachInSectionIndexed(startPos: Vector, size: Vector, action: (pos: Vector, T) -> Unit) {
        for (yd in 0 until size.intY) {
            for (xd in 0 until size.intX) {
                val newPos = startPos + Vector(xd, yd)
                action(newPos, this[startPos])
            }
        }
    }

    override fun toString(): String {
        return grid.fold("") { acc, row ->
            acc + row.fold("") { rowAcc, value ->
                rowAcc + value
            } + "\n"
        }
    }


}

private inline fun <reified T> gridOf(size: Vector, value: T): Grid<T> = Grid(size) { value }

private infix fun <T> Vector.isWithin(grid: Grid<T>) =
    intY in 0 until grid.size.intY && intX in 0 until grid.size.intX

private infix fun <T> Vector.isNotWithin(grid: Grid<T>) = !(this isWithin grid)

private data class Vector(
    val x: Double,
    val y: Double
) {
    constructor(x: Number, y: Number) : this(x.toDouble(), y.toDouble())

    companion object {
        fun fromPolar(angle: Number, dist: Number): Vector =
            Vector(cos(angle.toDouble()), sin(angle.toDouble())) * dist

        val zero = Vector(0, 0)
    }

    val dist: Double by lazy { hypot(x, y) }
    val squaredDist: Double by lazy { x * x + y * y }
    val angle: Double by lazy { atan2(y, x) }

    operator fun plus(v: Vector): Vector = Vector(x + v.x, y + v.y)
    operator fun minus(v: Vector): Vector = Vector(x - v.x, y - v.y)
    operator fun unaryMinus(): Vector = Vector(-x, -y)
    operator fun times(k: Number): Vector = Vector(x * k.toDouble(), y * k.toDouble())
    operator fun div(k: Number): Vector = Vector(x / k.toDouble(), y / k.toDouble())

    fun withDistance(newDistance: Number): Vector {
        check(dist != 0.0) { "Zero vector: $this cannot be projected to another distance" }
        return this * (newDistance.toDouble() / dist)
    }

    fun clampDistance(maxDistance: Number): Vector {
        return if (dist <= maxDistance.toDouble()) {
            this
        } else {
            withDistance(maxDistance)
        }
    }

    fun withAngle(newAngle: Number): Vector = fromPolar(newAngle.toDouble(), dist)
    fun rotated(deltaAngle: Number): Vector =
        if (deltaAngle == 0) this else withAngle(angle + deltaAngle.toDouble())

    fun withX(newX: Number): Vector = Vector(newX, y)
    fun withY(newY: Number): Vector = Vector(x, newY)

    fun asUnitVector() = withDistance(1.0)

    fun rounded(): Vector = Vector(intX, intY)

    val intX: Int get() = x.roundToInt()
    val intY: Int get() = y.roundToInt()

    val floatX: Float get() = x.toFloat()
    val floatY: Float get() = y.toFloat()
}