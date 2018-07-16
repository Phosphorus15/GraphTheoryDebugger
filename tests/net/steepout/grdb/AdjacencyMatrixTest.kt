package net.steepout.grdb

import org.junit.jupiter.api.Test

internal class AdjacencyMatrixTest {

    var matrix: AdjacencyMatrix = AdjacencyMatrix(10, 3)

    @Test
    fun listNodes() =
            matrix.listNodes()[7].number shouldBe 7

    @Test
    fun topologicalBeginning() =
            matrix.topologicalBeginning().get() shouldBe matrix.matrix[0][0]

    @Test
    operator fun iterator() =
            matrix.iterator().next().number shouldBe 0

    @Test
    fun resolve() =
            (matrix.resolve(4) shouldBe Pair(1, 1)).also { matrix.resolve(0) shouldBe Pair(0, 0) }

}
