package net.steepout.grdb

import java.lang.IllegalArgumentException
import java.util.*

interface GraphNode {

    var number: Int // Unique

    var interiorTags: Map<String, Any>

    fun hasAttributes(): Boolean

    fun getAttributes(): Map<String, Any>?

    fun listAdjacent(): List<GraphNode>

}

abstract class SimplifiedGraphNode(override var number: Int) : GraphNode {

    override var interiorTags: Map<String, Any> = mutableMapOf()

    override fun hasAttributes() = false

    override fun getAttributes(): Nothing? = null

}

abstract class IGraph(var size: Int) : Iterable<GraphNode> {

    abstract fun listNodes(): List<GraphNode>

    abstract fun topologicalBeginning(): Optional<GraphNode>

}

typealias Matrix<T> = Array<Array<T>>

class AdjacencyMatrixIterator(var matrix: AdjacencyMatrix) : Iterator<GraphNode> {

    var sequence: Int = 0

    override fun hasNext() = sequence < (matrix.height * matrix.width)

    override fun next(): GraphNode {
        val i = sequence++
        return matrix.matrix[i / matrix.width][i % matrix.width]
    }

}

class SimplifiedMatrixNode(number: Int, private val matrix: AdjacencyMatrix) : SimplifiedGraphNode(number) {

    val loc = matrix.resolve(number)

    override fun listAdjacent() = mutableListOf(upper(loc), lower(loc), left(loc), right(loc))
            .filter(Optional<GraphNode>::isPresent).map(Optional<GraphNode>::get)

    fun upper(loc: Pair<Int, Int>): Optional<GraphNode> = if (loc.first <= 0)
        Optional.empty() else
        Optional.of(matrix.matrix[loc.first - 1][loc.second])

    fun lower(loc: Pair<Int, Int>): Optional<GraphNode> = if (loc.first > matrix.height)
        Optional.empty() else
        Optional.of(matrix.matrix[loc.first + 1][loc.second])

    fun left(loc: Pair<Int, Int>): Optional<GraphNode> = if (loc.second <= 0)
        Optional.empty() else
        Optional.of(matrix.matrix[loc.first][loc.second - 1])

    fun right(loc: Pair<Int, Int>): Optional<GraphNode> = if (loc.first > matrix.width)
        Optional.empty() else
        Optional.of(matrix.matrix[loc.first - 1][loc.second + 1])

}

class AdjacencyMatrix(val height: Int, val width: Int) : IGraph(height * width) {

    val matrix: Matrix<GraphNode> = Matrix(height) { x ->
        Array<GraphNode>(width) { y ->
            SimplifiedMatrixNode(x * width + y + 1, this)
        }
    }

    init {
        if (height * width <= 0) throw IllegalArgumentException("The size of matrix must be positive !")
    }

    override fun listNodes(): List<GraphNode> =
            matrix.asList().map(Array<GraphNode>::asList).flatten()

    override fun topologicalBeginning() = Optional.of(matrix[0][0])

    override fun iterator(): Iterator<GraphNode> = AdjacencyMatrixIterator(this)

    fun resolve(i: Int): Pair<Int, Int> = Pair(i / width, i % width)

}
