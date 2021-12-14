package prob14

import java.io.File

lateinit var additions: Map<String, String>

fun main() {
    //val input = File("src/prob14/prob14InputSmall.txt").readLines()
    val input = File("src/prob14/prob14Input.txt").readLines()
    var str = input[0]
    //val str = "NNNN"

    additions = input.mapNotNull { Regex("(\\w\\w) -> (\\w)").find(it)?.groupValues }
        .associate { (_, pattern, addition) -> pattern to addition }

    freqMap = additions.map { (str, _) -> Pair(str, 0) to str.groupingBy { it }.eachCount().mapValues { it.value.toLong() } }.toMap().toMutableMap()

    println(str)
    //val grouping = str.zipWithNext().fold(emptyMap<Char, Int>()) { acc, sPair -> freq(sPair.toList().joinToString(""), 1) }
    val grouping = freq(str, 40)
    println(grouping)
    println("diff = ${grouping.values.maxOrNull()!! - grouping.values.minOrNull()!!}")

}

lateinit var freqMap: MutableMap<Pair<String, Int>, Map<Char, Long>>
fun freq(str: String, steps: Int): Map<Char, Long> {
    check(str.length >= 2) { "str.length < 2" }

    freqMap[str to steps]?.let { return it }
    if (steps == 0) return str.groupingBy { it }.eachCount().mapValues { it.value.toLong() }

    if (str.length == 2) {
        val addition = additions.getValue(str)

        val left = freq(str[0] + addition, steps - 1)
        val right = freq(addition + str[1], steps - 1)

        (left.keys + right.keys).associateWith { (left[it] ?: 0) + (right[it] ?: 0) }
            .mapValues { if (it.key.toString() == addition) it.value - 1 else it.value }.let {
                freqMap[str to steps] = it
                return it
            }
    } else {
        return str.zipWithNext().map { (s1, s2) ->
            val newString = s1.toString() + s2.toString()
            freq(newString, steps).mapValues { (c, i) -> if (c == s2) i - 1 else i }
        }.reduce { acc, map -> (acc.keys + map.keys).associateWith { (acc[it] ?: 0) + (map[it] ?: 0) } }
            .mapValues { if (it.key == str.last()) it.value +1 else it.value}
    }

}
