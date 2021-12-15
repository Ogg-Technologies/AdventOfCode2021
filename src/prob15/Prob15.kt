package prob15

import utils.*
import java.io.File
import kotlin.time.ExperimentalTime
import kotlin.time.measureTime

@OptIn(ExperimentalTime::class)
fun main() {
    measureTime {
        var grid = File("src/prob15/prob15InputSmall.txt").readText().asCharGrid { it.digitToInt() }
        //var grid = File("src/prob15/prob15Input.txt").readText().asCharGrid { it.digitToInt() }
        //grid = grid.enlarged()
        val start = grid.minPos
        val end = grid.maxPos
        val path = grid.asGraph(Neighborhoods.vonNeumann).aStarShortestPath(start, end,
            calculateDistance = { _, to -> grid[to].toDouble() },
            estimateDistanceToEnd = { (end - it).dist }
        ) ?: error("No path found")
        println(grid.toStringIndexed { pos, e -> if (pos in path) "■" else e.toString() })
        println("Risk level: ${path.sumOf { grid[it] }}")
    }.also { println("Time: $it") }
}

private fun Grid<Int>.enlarged() = Grid(size * 5) {
    val originalValue = get(Vector(it.intX % size.intX, it.intY % size.intY))
    val increases = it.intX / size.intX + it.intY / size.intY
    (originalValue + increases - 1) % 9 + 1
}