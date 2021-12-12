package utils

/**
 * Describes the status about the spread from one tile to another.
 * @property from The tile to spread from
 * @property to The tile to spread to
 * @property distance The distance in steps from the starting tile to the "to" tile
 */
data class SpreadStatus(val from: Vector, val to: Vector, val stepsFromStart: Int)

data class SpreadInstructions(
    val startingPos: Vector,
    val neighborhood: List<Vector>,
    val canSpread: (status: SpreadStatus) -> Boolean,
    val preSpread: (pos: Vector) -> Unit = {},
    val onSpread: (status: SpreadStatus) -> Unit = {},
    val postSpread: (pos: Vector) -> Unit = {},
) {
    fun setStartingPos(pos: Vector) = copy(startingPos = pos)
    fun setNeighborhood(neighborhood: List<Vector>) = copy(neighborhood = neighborhood)
    fun setCanSpread(canSpread: (status: SpreadStatus) -> Boolean) = copy(canSpread = canSpread)
    fun setPreSpread(preSpread: (pos: Vector) -> Unit) = copy(preSpread = preSpread)
    fun setOnSpread(onSpread: (status: SpreadStatus) -> Unit) = copy(onSpread = onSpread)
    fun setPostSpread(postSpread: (pos: Vector) -> Unit) = copy(postSpread = postSpread)
    fun addCanSpreadRequirement(canSpread: (status: SpreadStatus) -> Boolean) =
        copy(canSpread = { status -> canSpread(status) && this.canSpread(status) })

    fun addPreSpreadAction(preSpread: (pos: Vector) -> Unit) =
        copy(preSpread = { pos -> preSpread(pos); this.preSpread(pos) })

    fun addOnSpreadAction(onSpread: (status: SpreadStatus) -> Unit) =
        copy(onSpread = { status -> onSpread(status); this.onSpread(status) })

    fun addPostSpreadAction(postSpread: (pos: Vector) -> Unit) =
        copy(postSpread = { pos -> postSpread(pos); this.postSpread(pos) })
}


/**
 * Recursively spread to the neighbors of the given tile.
 *
 * Starting on the startingPos, call preSpread, then for every neighbor with a valid canSpread, recursively spread to
 * those. Finally, call postSpread on the startingPos.
 */
fun <T> TileWorld<T>.depthFirstSpread(instructions: SpreadInstructions) {
    fun neighborsTo(pos: Vector) = instructions.neighborhood.map { pos + it }

    fun spread(pos: Vector, stepsFromStart: Int) {
        instructions.preSpread(pos)

        for (neighbor in neighborsTo(pos)) {
            val status = SpreadStatus(pos, neighbor, stepsFromStart)
            if (instructions.canSpread(status)) {
                instructions.onSpread(status)
                spread(neighbor, stepsFromStart + 1)
            }
        }

        instructions.postSpread(pos)
    }
    spread(instructions.startingPos, 0)
}

/**
 * Adds the requirement that you cannot spread to the same tile multiple times
 */
fun <T> SpreadInstructions.nonRepeating(): SpreadInstructions {
    val visited = mutableSetOf<Vector>()
    return addPreSpreadAction { pos -> visited.add(pos) }
        .addCanSpreadRequirement { status -> !visited.contains(status.to) }
}

/**
 * Adds the requirement that you cannot spread to a position outside the map.
 */
fun <T> SpreadInstructions.boundedTo(world: RectangularTileWorld<T>): SpreadInstructions =
    addCanSpreadRequirement { status -> status.to isWithin world }
