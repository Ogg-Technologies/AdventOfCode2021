package test

import org.junit.jupiter.api.Test
import utils.*

internal class SpreadKtTest {

    @Test
    fun `breadthFirstSpread never decrease path length`() {
        val grid = gridOf(Vector(5, 5), 0)
        var pathLength = 0
        grid.asGraph(Neighborhoods.vonNeumann).breadthFirstSpread(
            SpreadInstructions(
                startingNode = Vector(2, 2),
                preSpread = {
                    assert(it.fullPath.size >= pathLength)
                    pathLength = it.fullPath.size
                }
            ).nonRepeating()
        )
    }
}