package prob11

import utils.*
import java.io.File

data class Squid(var energy: Int, var hasFlashed: Boolean)

fun main() {
    val text = File("src/prob11/prob11Input.txt").readText()
    val grid = text.asCharGrid { Squid(it.digitToInt(), false) }
    part1(grid)
    //part2(lines)
}

var totalFlashes = 0
private fun part1(grid: Grid<Squid>) {
    repeat(100) {
        println("After $it:")
        println(grid.toString { it.energy.toString() })
        grid.next()
    }
    println("After 100:")
    println(grid.toString { it.energy.toString() })
    println("Total flashes: $totalFlashes")
}

private fun Grid<Squid>.next() {
    forEach { it.energy++ }
    val flashers = mutableSetOf<Vector>()
    positions.forEach { pos ->
        asGraph(Neighborhoods.moore).depthFirstSpread(
            SpreadInstructions(
                startingNode = pos,
                canSpread = { !get(it.from.node).hasFlashed && get(it.from.node).energy > 9 },
                onSpread = {
                    flashers.add(it.from.node)
                    get(it.from.node).hasFlashed = true
                    get(it.to.node).energy++
                },
            )
        )
    }
    forEach {
        if (it.hasFlashed) {
            it.energy = 0
            it.hasFlashed = false
        }
    }
    totalFlashes += flashers.size
}


/*
var synchronized = false
private fun part2(lines: List<String>) {
    val grid = Grid(Vector(lines[0].length, lines.size)) { pos ->
        val char = lines[pos.intY][pos.intX]
        Squid(char.digitToInt(), false)
    }
    repeat(10000000) {
        if (synchronized) {
            println("Synchronized after $it")
            return
        }
        grid.next()
    }
}


 */

val neighborDeltas = (-1..1).flatMap { y -> (-1..1).map { x -> Vector(x, y) } }.filter { it != Vector(0, 0) }


