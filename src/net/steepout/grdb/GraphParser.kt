package net.steepout.grdb

import java.io.StringReader
import java.util.*

fun parseSimpleMatrix(matrix: String, height: Int, width: Int): IGraph = // FIXME not to be like this
        AdjacencyMatrix(height, width).run {
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

fun parseSimpleList(matrix: String, size: Int, edges: Int): IGraph =
        AdjacencyList(size).run {
            val listObj = this
            Scanner(StringReader(matrix)).run {
                (0 until size).forEach {
                    // TODO listing
                }
            }
            this
        }

fun main(args: Array<String>) {
    parseSimpleMatrix("1 3 2 7 6 9 7 8 10", 3, 3).walk {
        println("${it.number} : ${it.value}")
    }
}