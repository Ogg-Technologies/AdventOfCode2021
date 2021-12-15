package utils

import java.util.*

fun <N> Graph<N>.breadthFirstSearch(start: N, end: N, getWeight: (SpreadStatus<N>) -> Double?) {

    breadthFirstSpread(
        SpreadInstructions(
            startingNode = start,

            )
    )
}

/**
 * Finds the shortest path using A*
 *
 * A* prioritises visiting nodes with the lowest fScore = gScore + hScore
 * gScore is the distance from the starting node to the current node using the best known path
 * hScore is an estimate of the distance from the current node to the end node
 *
 * @param start: the starting node
 * @param end: the ending node
 * @param calculateDistance: a function that calculates the distance between two nodes (sometimes known as the weight)
 * @param estimateDistanceToEnd: a function that estimates the distance to the end node (sometimes known as the heuristic or h score).
 *                               Must never be greater than the actual distance to the end node.
 */
fun <N> Graph<N>.aStarShortestPath(
    start: N,
    end: N,
    calculateDistance: (node: N, neighbor: N) -> Double,
    estimateDistanceToEnd: (node: N) -> Double
): List<N>? {
    val gScore = HashMap<N, Double>().withDefault { Double.POSITIVE_INFINITY }.apply { put(start, 0.0) }
    fun fScore(n: N) = gScore.getValue(n) + estimateDistanceToEnd(n)
    val open = PriorityQueue<N>(Comparator.comparing { fScore(it) }).apply { add(start) }
    val cameFrom = HashMap<N, N>()
    fun N.path(): List<N> = cameFrom[this]?.let { it.path() + this } ?: emptyList()

    while (open.isNotEmpty()) {
        val current = open.remove()
        if (current == end) return current.path()

        for (neighbor in getNeighbors(current)) {
            val tentativeGScore = gScore.getValue(current) + calculateDistance(current, neighbor)

            if (tentativeGScore < gScore.getValue(neighbor)) {
                cameFrom[neighbor] = current
                gScore[neighbor] = tentativeGScore
                if (!open.contains(neighbor)) {
                    open.add(neighbor)
                }
            }
        }
    }
    return null
}

