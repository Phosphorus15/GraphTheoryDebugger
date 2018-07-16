package net.steepout.grdb

import java.lang.IllegalArgumentException
import java.util.*

interface GraphNode {

    var number: Int // Unique

    var value: Any // Mono Value

    var interiorTags: MutableMap<String, Any>

    fun hasAttributes(): Boolean

    fun getAttributes(): Map<String, Any>?

    fun listAdjacent(): List<GraphNode>

    fun asOptional() = Optional.of(this)

}

abstract class SimplifiedGraphNode(override var number: Int) : GraphNode {

    override var value: Any = 0

    override var interiorTags: MutableMap<String, Any> = mutableMapOf()

    override fun hasAttributes() = false

    override fun getAttributes(): Nothing? = null

}

abstract class IGraph(var size: Int) : Iterable<GraphNode> {

    var tags: Map<String, Any> = mutableMapOf()

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

    private val loc = matrix.resolve(number)

    override fun listAdjacent() = mutableListOf(upper(loc), lower(loc), left(loc), right(loc))
            .filter(Optional<GraphNode>::isPresent).map(Optional<GraphNode>::get)

    fun upper(loc: Pair<Int, Int>): Optional<GraphNode> = if (loc.first <= 0)
        Optional.empty() else
        Optional.of(matrix.matrix[loc.first - 1][loc.second])

    fun lower(loc: Pair<Int, Int>): Optional<GraphNode> = if (loc.first >= matrix.height - 1)
        Optional.empty() else
        Optional.of(matrix.matrix[loc.first + 1][loc.second])

    fun left(loc: Pair<Int, Int>): Optional<GraphNode> = if (loc.second <= 0)
        Optional.empty() else
        Optional.of(matrix.matrix[loc.first][loc.second - 1])

    fun right(loc: Pair<Int, Int>): Optional<GraphNode> = if (loc.second >= matrix.width - 1)
        Optional.empty() else
        Optional.of(matrix.matrix[loc.first][loc.second + 1])

}

class SimplifiedListNode(number: Int, private val list: AdjacencyList) : SimplifiedGraphNode(number) {

    private val adjacent = mutableListOf<GraphNode>()

    override fun listAdjacent(): List<GraphNode> = adjacent

    fun addAdjacent(node: GraphNode) {
        if (!adjacent.contains(node)) {
            adjacent.add(list.addNode(node))
        }
    }

    fun addDoubleAdjacent(node: SimplifiedListNode) = addAdjacent(node).also { node.addAdjacent(this) }

    fun addAdjacent(number: Int) = addAdjacent(list.listNodes()[number])

    fun addDoubleAdjacent(number: Int) = addDoubleAdjacent(list.listNodes()[number] as SimplifiedListNode)

}

class AdjacencyMatrix(val height: Int, val width: Int, provider: (Int, AdjacencyMatrix) -> GraphNode) : IGraph(height * width) {

    val matrix: Matrix<GraphNode> = Matrix(height) { x ->
        Array(width) { y ->
            provider(x * width + y, this)
        }
    }

    constructor(height: Int, width: Int) : this(height, width, { number, matrix ->
        SimplifiedMatrixNode(number, matrix)
    })

    init {
        if (height * width <= 0) throw IllegalArgumentException("The size of matrix must be positive !")
    }

    override fun listNodes(): List<GraphNode> =
            matrix.asList().map(Array<GraphNode>::asList).flatten()

    override fun topologicalBeginning() = Optional.of(matrix[0][0])

    override fun iterator(): Iterator<GraphNode> = AdjacencyMatrixIterator(this)

    fun resolve(i: Int): Pair<Int, Int> = Pair(i / width, i % width)

}

class AdjacencyList(size: Int, provider: (Int, AdjacencyList) -> GraphNode) : IGraph(size) {

    constructor(size: Int) : this(size, { number, list -> SimplifiedListNode(number, list) })

    private val nodes = (0 until size).map { provider(it, this) }.toMutableList()

    override fun listNodes(): List<GraphNode> = nodes

    override fun topologicalBeginning() = nodes.also { evaluateDegree() }
            .first { it.interiorTags["inDegree"] == 0 }.asOptional()

    override fun iterator(): Iterator<GraphNode> = listNodes().iterator()

    fun addNode(node: GraphNode): GraphNode {
        if (!nodes.contains(node)) {
            node.number = nodes.size
            nodes.add(node)
            size++
        }
        return node
    }

    fun removeNode(node: GraphNode): Boolean = if (nodes.contains(node)) {
        size--; nodes.remove(node); true
    } else false

}
