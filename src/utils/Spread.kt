package utils

import java.util.*
import java.util.Comparator.comparing

data class SpreadStatus<N>(val from: NodeData<N>, val to: NodeData<N>)
data class NodeData<N>(val node: N, val fullPath: List<N>)

data class SpreadInstructions<N>(
    val startingNode: N,
    val canSpread: (status: SpreadStatus<N>) -> Boolean = { true },
    val preSpread: (nodeData: NodeData<N>) -> Unit = {},
    val onSpread: (status: SpreadStatus<N>) -> Unit = {},
    val postSpread: (nodeData: NodeData<N>) -> Unit = {},
) {
    fun setStartingNode(node: N) = copy(startingNode = node)
    fun setCanSpread(canSpread: (status: SpreadStatus<N>) -> Boolean) = copy(canSpread = canSpread)
    fun setPreSpread(preSpread: (nodeData: NodeData<N>) -> Unit) = copy(preSpread = preSpread)
    fun setOnSpread(onSpread: (status: SpreadStatus<N>) -> Unit) = copy(onSpread = onSpread)
    fun setPostSpread(postSpread: (nodeData: NodeData<N>) -> Unit) = copy(postSpread = postSpread)
    fun addCanSpreadRequirement(canSpread: (status: SpreadStatus<N>) -> Boolean) =
        copy(canSpread = { status -> canSpread(status) && this.canSpread(status) })

    fun addPreSpreadAction(preSpread: (nodeData: NodeData<N>) -> Unit) =
        copy(preSpread = { preSpread(it); this.preSpread(it) })

    fun addOnSpreadAction(onSpread: (status: SpreadStatus<N>) -> Unit) =
        copy(onSpread = { onSpread(it); this.onSpread(it) })

    fun addPostSpreadAction(postSpread: (nodeData: NodeData<N>) -> Unit) =
        copy(postSpread = { postSpread(it); this.postSpread(it) })
}

private fun <N> Graph<N>.spread(
    nodeData: NodeData<N>,
    neighborSpreadCall: (nodeData: NodeData<N>) -> Unit,
    instructions: SpreadInstructions<N>
) {
    instructions.preSpread(nodeData)
    getNeighbors(nodeData.node)
        .map { SpreadStatus(nodeData, NodeData(it, nodeData.fullPath + it)) }
        .filter { instructions.canSpread(it) }
        .forEach {
            instructions.onSpread(it)
            neighborSpreadCall(it.to)
        }
    instructions.postSpread(nodeData)
}

/**
 * Recursively spread to the neighbors of the given node.
 *
 * Starting on the startingNode, call preSpread, then for every neighbor with a valid canSpread, recursively spread to
 * those. Finally, call postSpread on the startingNode.
 */
fun <N> Graph<N>.depthFirstSpread(instructions: SpreadInstructions<N>) {
    fun recursiveSpread(nodeData: NodeData<N>) {
        spread(nodeData, ::recursiveSpread, instructions)
    }
    recursiveSpread(NodeData(instructions.startingNode, listOf(instructions.startingNode)))
}

fun <N> Graph<N>.prioritizedSpread(comparator: Comparator<NodeData<N>>, instructions: SpreadInstructions<N>) {
    val priorityQueue = PriorityQueue(comparator)
    priorityQueue.add(NodeData(instructions.startingNode, listOf(instructions.startingNode)))

    while (priorityQueue.isNotEmpty()) {
        val nodeData = priorityQueue.remove()
        spread(nodeData, { priorityQueue.add(it) }, instructions)
    }
}

fun <N> Graph<N>.breadthFirstSpread(instructions: SpreadInstructions<N>) =
    prioritizedSpread(comparing { it.fullPath.size }, instructions)

/**
 * Adds the requirement that you cannot spread to the same node multiple times
 */
fun <N> SpreadInstructions<N>.nonRepeating(): SpreadInstructions<N> {
    val visited = mutableSetOf<N>()
    return addPreSpreadAction { nodeData -> visited.add(nodeData.node) }
        .addCanSpreadRequirement { status -> !visited.contains(status.to.node) }
}
