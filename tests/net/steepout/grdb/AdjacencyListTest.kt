package net.steepout.grdb

import org.junit.jupiter.api.Test

internal class AdjacencyListTest {

    var list = AdjacencyList(6)

    init {
        list.listNodes().apply {
            (get(1) as SimplifiedListNode).addAdjacent(0)
            (get(0) as SimplifiedListNode).addAdjacent(2)
            (get(0) as SimplifiedListNode).addAdjacent(3)
            (get(1) as SimplifiedListNode).addAdjacent(4)
            (get(4) as SimplifiedListNode).addAdjacent(5)
            (get(5) as SimplifiedListNode).addAdjacent(3)
            get(3).value = 0xff
        }
    }

    @Test
    fun listNodes() = list.listNodes()[3].value shouldBe 0xff

    @Test
    fun topologicalBeginning() = list.topologicalBeginning().get().number shouldBe 1

    @Test
    operator fun iterator() = list.iterator().next().number shouldBe 0

    @Test
    fun addNode() = list.run { addNode(SimplifiedListNode(2, list)); this }
            .listNodes().size shouldBe 7

    @Test
    fun degreeEvaluation() {
        list.evaluateDegree()
        list.listNodes()[0].interiorTags["inDegree"] as Int shouldBe 1
        list.listNodes()[0].interiorTags["outDegree"] as Int shouldBe 2
        list.listNodes()[3].interiorTags["inDegree"] as Int shouldBe 2
    }

}
