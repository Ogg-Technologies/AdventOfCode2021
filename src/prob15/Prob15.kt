package prob15

import utils.*
import utils.Vector
import java.io.File
import java.util.*
import java.util.Comparator.comparing
import kotlin.time.ExperimentalTime
import kotlin.time.measureTime

@OptIn(ExperimentalTime::class)
fun main() {
    measureTime {
        //var grid = File("src/prob15/prob15InputSmall.txt").readText().asCharGrid { it.digitToInt() }
        var grid = File("src/prob15/prob15Input.txt").readText().asCharGrid { it.digitToInt() }
        //grid = grid.enlarged()
        val path = grid.asGraph(Neighborhoods.vonNeumann)
            .aStarShortestPath(grid.minPos, grid.maxPos) { _, to -> grid[to] }
        println(grid.toStringIndexed { pos, e -> if (pos in path) "■" else e.toString() })
        println("Risk level: ${path.sumOf { grid[it] }}")
    }.also { println("Time: $it") }
}

private fun Grid<Int>.enlarged() = Grid(size * 5) {
    val originalValue = get(Vector(it.intX % size.intX, it.intY % size.intY))
    val increases = it.intX / size.intX + it.intY / size.intY
    (originalValue + increases - 1) % 9 + 1
}


fun Graph<Vector>.aStarShortestPath(
    start: Vector,
    end: Vector,
    distance: (from: Vector, to: Vector) -> Int
): List<Vector> {
    val gScore = HashMap<Vector, Int>().withDefault { Int.MAX_VALUE }
    fun hScore(v: Vector) = (v - end).dist;
    fun fScore(v: Vector) = gScore.getValue(v) + hScore(v)

    val open = PriorityQueue<Vector>(comparing { fScore(it) })
    val cameFrom = HashMap<Vector, Vector>()

    gScore[start] = 0

    open.add(start)
    while (open.isNotEmpty()) {
        val current = open.remove()
        if (current == end) {
            return reconstructPath(cameFrom, current)
        }

        for (neighbor in getNeighbors(current)) {
            val tentativeGScore = gScore.getValue(current) + distance(current, neighbor)

            if (tentativeGScore < gScore.getValue(neighbor)) {
                cameFrom[neighbor] = current
                gScore[neighbor] = tentativeGScore
                if (!open.contains(neighbor)) {
                    open.add(neighbor)
                }
            }
        }
    }
    return emptyList()
}

fun reconstructPath(cameFrom: HashMap<Vector, Vector>, end: Vector): List<Vector> {
    val path = mutableListOf<Vector>()
    var current = end
    while (cameFrom.containsKey(current)) {
        path.add(current)
        current = cameFrom.getValue(current)
    }
    return path.reversed()
}
