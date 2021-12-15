package utils

import java.util.Comparator

fun <N> Graph<N>.breadthFirstSearch(start: N, end: N, getWeight: (SpreadStatus<N>) -> Double?) {

    breadthFirstSpread(
        SpreadInstructions(
            startingNode = start,

        )
    )
}
