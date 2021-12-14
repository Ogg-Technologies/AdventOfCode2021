package prob14

import java.io.File

fun main() {
    //val input = File("src/prob14/prob14InputSmall.txt").readLines()
    val input = File("src/prob14/prob14Input.txt").readLines()
    var str = input[0]

    val transitions = input.mapNotNull { Regex("(\\w\\w) -> (\\w)").find(it)?.groupValues }
        .associate { (_, pattern, addition) -> pattern to addition }

    println(str)
    repeat(40) {
        str = str.next(transitions)
        println("step ${it+1}")
    }
    val grouping = str.groupingBy { it }
    println(grouping.eachCount())
    println("diff = ${grouping.eachCount().values.maxOrNull()!! - grouping.eachCount().values.minOrNull()!!}")
}

fun String.next(transitions: Map<String, String>) : String {
    val insertions = transitions.flatMap { (pattern, addition) ->
        Regex("(?=$pattern)").findAll(this).map { it.range.first+1 to addition }
    }.sortedBy { it.first }
    val sb = StringBuilder(this)
    insertions.forEachIndexed { index, (indexToInsert, addition) ->
        sb.insert(indexToInsert+index, addition)
    }
    return sb.toString()
}