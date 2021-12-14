package prob13

import utils.Grid
import utils.Vector
import java.io.File

fun main() {
    //val input = File("src/prob13/prob13InputSmall.txt").readLines()
    val input = File("src/prob13/prob13Input.txt").readLines()
    val points = input
        .filter { it.isNotEmpty() && it.first().isDigit() }
        .map { Vector(it.substringBefore(",").toInt(), it.substringAfter(",").toInt()) }

    val grid = Grid(
        Vector(points.maxOf { it.x } + 1, points.maxOf { it.y } + 1)
    ) {
        if (it in points) '#' else '.'
    }

    val folds = input.mapNotNull { Regex("fold along ([xy])=(\\d*)").find(it) }
        .map { it.groupValues[1] to it.groupValues[2].toInt() }

    //part1(folds, grid)
    part2(folds, grid)


}

fun part1(folds: List<Pair<String, Int>>, inputGrid: Grid<Char>) {
    val grid = inputGrid.foldUsing(folds.first())
    var dots = 0
    grid.forEach { if (it == '#') dots++ }
    println("Part 1 dots= $dots")
}

private fun part2(
    folds: List<Pair<String, Int>>,
    inputGrid: Grid<Char>
) {
    folds
        .also(::println)
        .fold(inputGrid) { grid, fold ->
            grid.foldUsing(fold)
        }.also(::println)
}

private fun Grid<Char>.foldUsing(foldInstruction: Pair<String, Int>): Grid<Char> {
    return if (foldInstruction.first == "x") {
        verticalFold(foldInstruction.second)
    } else {
        horizontalFold(foldInstruction.second)
    }
}

fun Grid<Char>.verticalFold(xPos: Int): Grid<Char> {
    check(size.intX % 2 == 1) { "Vertical fold requires odd width" }
    return Grid(Vector((size.intX - 1) / 2, size.intY)) {
        if (listOf(this[it], this[it.copy(x = size.intX - 1 - it.x)]).any { it == '#' }) '#' else '.'
    }
}

fun Grid<Char>.horizontalFold(yPos: Int): Grid<Char> {
    check(size.intY % 2 == 1) { "Horizontal fold requires odd height" }
    return Grid(Vector(size.intX, (size.intY - 1) / 2)) {
        if (listOf(this[it], this[it.copy(y = size.intY - 1 - it.y)]).any { it == '#' }) '#' else '.'
    }
}