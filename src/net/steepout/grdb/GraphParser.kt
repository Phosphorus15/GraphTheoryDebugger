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
        AdjacencyList(size).apply {
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
        AdjacencyList(size).apply {
            val listObj = this
            Scanner(StringReader(matrix)).apply {
                (0 until edges).forEach {
                    (listObj.listNodes()[nextInt()] as ListNodeMeta)
                            .addAdjacent(nextInt())?.third?.put("distance", nextInt())
                }
            }
        }

fun main(args: Array<String>) {
    parseSimpleMatrix("0 0 2 0 6 9 0 8 10", 3).walk {
        println("${it.number} : ${it.value}")
    }
}
