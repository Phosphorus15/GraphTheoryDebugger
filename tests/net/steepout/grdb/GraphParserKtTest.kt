package net.steepout.grdb

import org.junit.jupiter.api.Test

internal class GraphParserKtTest {

    @Test
    fun parseSimpleRaw() {
        "1 2 1 3 9 0 8 9 1".asSimpleRawGraph(3, 3).apply {
            listNodes().map(GraphNode::value).map(Any?::safeInt).sum() shouldBe 34
            topologicalBeginning().hashCode() shouldBe listNodes()[0].hashCode()
        }
    }

    @Test
    fun parseSimpleMatrix() {
        var node: GraphNode? = null
        "0 0 2 0 6 9 0 8 10".asSimpleMatrix(3).walk { node = it }
        node?.number.safeInt() shouldBe 1
    }

    @Test
    fun parseSimpleList() {
        "5 2 5 \n 5 0 7 \n 3 5 8".asSimpleList(6, 3).topologicalBeginning().get().number shouldBe 3
    }

    @Test
    fun parseAttributes() {
        "0 0 2 0 6 9 0 8 10".asSimpleMatrix(3).apply {
            parseAttributeList("3 a 2 b -1 c", this as AdjacencyList
                    , Pair("price", IntegerAttribute), Pair("symbol", CharAttribute))
        }.listNodes()[2].getAttributes().safeMap()["symbol"]?.shouldBe('c')
    }

    @Test
    fun parseIndexedAttributes() {
        "5 2 5 \n 5 0 7 \n 3 5 8".asSimpleList(6, 3).apply {
            parseIndexedAttributeList("0 5 a 3 7 d", 2, this as AdjacencyList
                    , Pair("price", IntegerAttribute), Pair("symbol", CharAttribute))
        }.listNodes()[3].getAttributes().safeMap()["symbol"]?.shouldBe('d')
    }

}
