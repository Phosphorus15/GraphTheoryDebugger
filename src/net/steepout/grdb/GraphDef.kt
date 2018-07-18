package net.steepout.grdb

import java.lang.IllegalArgumentException
import java.util.*

interface IGraphNode {

    var number: Int // Unique

    var value: Any // Mono Value

    var interiorTags: AttributesMap

    fun hasAttributes(): Boolean

    fun getAttributes(): AttributesMap?

    fun listAdjacent(): List<GraphNode>

}

abstract class GraphNode : IGraphNode {

    fun asOptional() = Optional.of(this)

    override fun toString(): String = "Node instance of ${javaClass.name} - node $number(value = $value)" +
            if (hasAttributes()) "\n" + getAttributes().toString() else ""

}

abstract class SimplifiedGraphNode(override var number: Int) : GraphNode() {

    override var value: Any = 0

    override var interiorTags: MutableMap<String, Any> = mutableMapOf()

    override fun hasAttributes() = false

    override fun getAttributes(): AttributesMap? = null

}

abstract class IGraph(var size: Int) : Iterable<GraphNode> {

    var tags: Map<String, Any> = mutableMapOf()

    abstract fun listNodes(): List<GraphNode>

    abstract fun topologicalBeginning(): Optional<GraphNode>

}

typealias Matrix<T> = Array<Array<T>>

class RawGraphIterator(var matrix: RawGraph) : Iterator<GraphNode> {

    var sequence: Int = 0

    override fun hasNext() = sequence < (matrix.height * matrix.width)

    override fun next(): GraphNode {
        val i = sequence++
        return matrix.matrix[i / matrix.width][i % matrix.width]
    }

}

class SimplifiedMatrixNode(number: Int, private val matrix: RawGraph) : SimplifiedGraphNode(number) {

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

interface ListNodeMeta {

    fun addAdjacent(node: GraphNode): Edge?

    fun addDoubleAdjacent(node: SimplifiedListNode) = addAdjacent(node).also { node.addAdjacent(asOptional().get()) }

    fun asOptional(): Optional<GraphNode>

    fun acquireNode(number: Int): Optional<GraphNode>

    fun addAdjacent(number: Int) = addAdjacent(acquireNode(number).get())

    fun addDoubleAdjacent(number: Int) = addDoubleAdjacent(acquireNode(number).get() as SimplifiedListNode)

    fun notifyAdjacency(node: GraphNode)

    fun listAdjacent(): List<GraphNode>

}

open class SimplifiedListNode(number: Int, private val list: AdjacencyList) : SimplifiedGraphNode(number), ListNodeMeta {

    override fun notifyAdjacency(node: GraphNode) = adjacent.add(node).run {}

    override fun acquireNode(number: Int): Optional<GraphNode> = Optional.ofNullable(list.listNodes()[number])

    private val adjacentEdge = mutableListOf<Edge>()

    private val adjacent = mutableListOf<GraphNode>()

    override fun listAdjacent(): List<GraphNode> = adjacentEdge.map(Edge::second)

    override fun addAdjacent(node: GraphNode) =
            if (!listAdjacent().contains(node))
                Edge(this, list.addNode(node), mutableMapOf()).apply { adjacentEdge.add(this) }
            else null

}

class AttributedListNode(number: Int, private val list: AdjacencyList) : SimplifiedListNode(number, list) {

    private val attributeList = mutableMapOf<String, Any>()

    override fun hasAttributes(): Boolean = true

    override fun getAttributes() = attributeList

    operator fun get(key: String): Any? = attributeList[key]

}

class RawGraph(val height: Int, val width: Int, provider: (Int, RawGraph) -> GraphNode) : IGraph(height * width) {

    val matrix: Matrix<GraphNode> = Matrix(height) { x ->
        Array(width) { y ->
            provider(x * width + y, this)
        }
    }

    constructor(height: Int, width: Int) : this(height, width, { number, matrix ->
        SimplifiedMatrixNode(number, matrix)
    })

    init {
        if (height * width <= 0) throw IllegalArgumentException("The size of graph must be positive !")
    }

    override fun listNodes(): List<GraphNode> =
            matrix.asList().map(Array<GraphNode>::asList).flatten()

    override fun topologicalBeginning() = Optional.of(matrix[0][0])

    override fun iterator(): Iterator<GraphNode> = RawGraphIterator(this)

    fun resolve(i: Int): Pair<Int, Int> = Pair(i / width, i % width)

}

val AttributedAdjacencyListBuilder: (Int, AdjacencyList) -> GraphNode = { number, list -> AttributedListNode(number, list) }
val SimplifiedAdjacencyListBuilder: (Int, AdjacencyList) -> GraphNode = { number, list -> SimplifiedListNode(number, list) }

class AdjacencyList(size: Int, provider: (Int, AdjacencyList) -> GraphNode) : IGraph(size) {

    constructor(size: Int) : this(size, SimplifiedAdjacencyListBuilder)

    private val nodes = (0 until size).map { provider(it, this) }.toMutableList()

    override fun listNodes(): List<GraphNode> = nodes

    override fun topologicalBeginning() = nodes.also { evaluateDegree() }.filter { it.interiorTags["outDegree"] != 0 }
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

typealias Edge = Triple<GraphNode, GraphNode, MutableMap<String, Any>>
