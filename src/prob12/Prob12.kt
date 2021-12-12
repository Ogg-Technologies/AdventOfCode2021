package prob12

import utils.Graph
import utils.SpreadInstructions
import utils.asEdgesToUndirectedGraph
import utils.depthFirstSpread
import java.io.File

fun main() {
    val lines = File("src/prob12/prob12Input.txt").readLines()
    //val lines = File("src/prob12/prob12InputTiny.txt").readLines()
    val graph = lines.map { it.substringBefore("-") to it.substringAfter("-") }.asEdgesToUndirectedGraph()
    part2(graph)
}

fun String.isSmallCave() = this.all { it.isLowerCase() }

private fun part1(graph: Graph<String>) {
    val start = "start"
    val end = "end"

    val paths = mutableListOf<List<String>>()
    graph.depthFirstSpread(SpreadInstructions(
        startingNode = start,
        canSpread = {
            val spreadingFromEndNode = it.from.node == end
            val repeatsSmallCave = it.to.node.isSmallCave() && it.to.node in it.from.fullPath
            !spreadingFromEndNode && !repeatsSmallCave
        },
        preSpread = {
            if (it.node == end) {
                paths.add(it.fullPath)
            }
        }
    ))
    paths.forEach(::println)
    println(paths.size)
}

private fun part2(graph: Graph<String>) {
    val start = "start"
    val end = "end"

    val paths = mutableListOf<List<String>>()
    graph.depthFirstSpread(SpreadInstructions(
        startingNode = start,
        canSpread = { status ->
            if (status.from.node == end) return@SpreadInstructions false
            if (status.to.node == start) return@SpreadInstructions false

            val repeatedSmallCaves =
                status.to.fullPath
                    .filter { it.isSmallCave() }
                    .groupingBy { it }
                    .eachCount()
                    .filter { it.value > 1 }
            if (repeatedSmallCaves.isEmpty()) return@SpreadInstructions true
            if (repeatedSmallCaves.size == 1 && repeatedSmallCaves.values.first() == 2) return@SpreadInstructions true
            return@SpreadInstructions false
        },
        preSpread = {
            if (it.node == end) {
                paths.add(it.fullPath)
            }
        }
    ))
    //paths.forEach(::println)
    println(paths.size)
}