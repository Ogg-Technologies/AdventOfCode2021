package prob10

import java.io.File
import java.util.*

fun main() {
    val lines = File("src/prob10Input.txt").readLines()
    part2(lines)
}

private fun part1(lines: List<String>) {
    lines
        .map { parseLine(it) }
        .filter { it.isCorrupted() }
        .also { it.forEach { it.printCorruptedError() } }
        .sumOf { it.getSyntaxErrorScore() }
        .also { println("Score = $it") }
}

private fun part2(lines: List<String>) {
    val sortedResults = lines
        .map { parseLine(it) }
        .filter { it.isIncomplete() && !it.isCorrupted() }
        .also { it.forEach { println("${it.startingString} could be closed with ${it.getClosingString()}, has score ${it.getCompletionScore()}") } }
        .sortedBy { it.getCompletionScore() }

    val middleResult = sortedResults[sortedResults.size / 2]
    print("Middle score = ${middleResult.getCompletionScore()}")
}

private data class ParseResult(val startingString: String, val remainingString: String, val bracketDeque: Deque<Char>) {
    fun isCorrupted() = remainingString.isNotEmpty() && (bracketDeque.isEmpty() || bracketDeque.peek() != remainingString.first())
    fun isIncomplete() = remainingString.isEmpty() && bracketDeque.isNotEmpty()

    fun printCorruptedError() {
        require(isCorrupted())
        println("${startingString}: Expected ${brackets[bracketDeque.peek()]} but found ${remainingString.first()}")
    }

    fun getSyntaxErrorScore(): Int {
        require(isCorrupted())
        return syntaxErrorScores.getValue(remainingString.first())
    }

    fun getClosingString(): String {
        require(isIncomplete())
        return bracketDeque.map { brackets[it] }.joinToString(separator = "")
    }

    fun getCompletionScore(): Long {
        require(isIncomplete())
        return getClosingString().fold(0) { acc, c -> acc*5 + completionScores.getValue(c) }
    }
}


private val brackets = mapOf(
    '{' to '}',
    '[' to ']',
    '(' to ')',
    '<' to '>'
)
private val openingBrackets = brackets.keys
private val closingBrackets = brackets.values
private val syntaxErrorScores = mapOf(
    ')' to 3,
    ']' to 57,
    '}' to 1197,
    '>' to 25137
)
private val completionScores = mapOf(
    ')' to 1,
    ']' to 2,
    '}' to 3,
    '>' to 4
)

private fun parseLine(line: String): ParseResult {
    val bracketDeque = LinkedList<Char>()
    var remainingString = line
    while (remainingString.isNotEmpty()) {
        val char = remainingString.first()
        if (openingBrackets.contains(char)) {
            bracketDeque.addFirst(char)
        } else if (closingBrackets.contains(char)) {
            if (bracketDeque.isEmpty())
                return ParseResult(line, remainingString, bracketDeque)
            if (brackets[bracketDeque.first] != char)
                return ParseResult(line, remainingString, bracketDeque)
            bracketDeque.removeFirst()
        }
        remainingString = remainingString.substring(1)
    }
    return ParseResult(line, remainingString, bracketDeque)
}