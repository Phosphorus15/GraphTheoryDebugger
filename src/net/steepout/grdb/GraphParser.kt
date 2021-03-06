package net.steepout.grdb

import java.io.StringReader
import java.util.*

fun String.asSimpleRawGraph(height: Int, width: Int) = parseSimpleRaw(this, height, width)
fun String.asSimpleMatrix(size: Int) = parseSimpleMatrix(this, size)
fun String.asSimpleList(size: Int, edges: Int) = parseSimpleList(this, size, edges)

fun parseSimpleRaw(matrix: String, height: Int, width: Int): IGraph = // FIXME not to be like this
        RawGraph(height, width).run {
            val matrixObj = this
            Scanner(StringReader(matrix)).run {
                (0 until height).forEach { x ->
                    (0 until width).forEach { y ->
                        matrixObj.matrix[x][y].value = nextInt()
                    }
                }
            }
            this
        }

fun parseSimpleMatrix(matrix: String, size: Int): IGraph =
        AdjacencyList(size, AttributedAdjacencyListBuilder).apply {
            val listObj = this
            Scanner(StringReader(matrix)).apply {
                (0 until size).forEach { i ->
                    (0 until size).forEach { j ->
                        nextInt().apply {
                            if (this > 0) (listObj.listNodes()[i] as ListNodeMeta)
                                    .addAdjacent(j)?.third?.put("distance", this)
                        }
                    }
                }
            }
        }

fun parseSimpleList(matrix: String, size: Int, edges: Int): IGraph =
        AdjacencyList(size, AttributedAdjacencyListBuilder).apply {
            val listObj = this
            Scanner(StringReader(matrix)).apply {
                (0 until edges).forEach {
                    (listObj.listNodes()[nextInt()] as ListNodeMeta)
                            .addAdjacent(nextInt())?.third?.put("distance", nextInt())
                }
            }
        }

val IntegerAttribute = { scanner: Scanner -> scanner.nextInt() }
val LineAttribute = { scanner: Scanner -> scanner.nextLine() }
val StringAttribute = { scanner: Scanner -> scanner.next() }
/**
 * Warning ! this might have disposed some value
 * for example : "a b c" -> 'a' #=> buffer : " b c" , while "ab c" -> 'a' #=> buffer : " c"
 */
val CharAttribute = { scanner: Scanner -> scanner.next()[0] }

fun parseAttributeList(source: String, list: AdjacencyList, vararg attrs: Pair<String, (Scanner) -> Any>): Unit =
        Scanner(StringReader(source)).run {
            list.listNodes().forEach { node ->
                attrs.forEach {
                    if (node.hasAttributes()) (node.getAttributes() as MutableMap<String, Any>)[it.first] = it.second(this)
                }
            }
        }

fun parseIndexedAttributeList(source: String, count: Int, list: AdjacencyList
                              , vararg attrs: Pair<String, (Scanner) -> Any>): Unit =
        Scanner(StringReader(source)).run {
            (0 until count).forEach {
                list.listNodes()[nextInt()].apply {
                    attrs.forEach {
                        if (hasAttributes()) (getAttributes() as MutableMap<String, Any>)[it.first] = it.second(this@run)
                    }
                }
            }
        }

fun main(args: Array<String>) {

}
