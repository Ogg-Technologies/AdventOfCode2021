package utils

interface Graph<N> {
    val nodes: Collection<N>
    fun getNeighbors(node: N): Collection<N>
}

class HashmapGraph<N>(private val graph: Map<N, List<N>>) : Graph<N> {
    override val nodes get() = graph.keys
    override fun getNeighbors(node: N) = graph[node] ?: emptyList()
}

fun <N> List<Pair<N, N>>.asEdgesToUndirectedGraph(): Graph<N> =
    HashmapGraph((this + this.map { pair -> pair.second to pair.first }).groupBy({ it.first }, { it.second }))