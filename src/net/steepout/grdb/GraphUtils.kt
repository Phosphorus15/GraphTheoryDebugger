package net.steepout.grdb

import java.util.*

typealias AttributesMap = MutableMap<String, Any>
fun Any?.safeInt() = safeInt(0)
fun Any?.safeInt(default: Int): Int = if (this == null || this !is Int) default else this
fun AttributesMap?.safeMap() = this as AttributesMap

fun IGraph.walk(consumer: (GraphNode) -> Unit) {
    if (topologicalBeginning().isPresent)
        walk(topologicalBeginning().get(), consumer)
}

fun IGraph.walk(node: GraphNode, consumer: (GraphNode) -> Unit) {
    val vis = BitSet(this.size)
    val queue = LinkedList<GraphNode>()
    queue.push(node)
    while (!queue.isEmpty()) {
        val current = queue.pollLast()
        if (vis[current.number]) continue
        vis[current.number] = true
        current.listAdjacent().filter { !vis[it.number] }.forEach(queue::push)
        consumer(current)
    }
}

fun MutableMap<String, Any>.increaseTag(key: String, amount: Int = 1) = compute(key)
{ _, any -> return@compute if (any == null || any !is Int) amount else any + amount }

fun MutableMap<String, Any>.decreaseTag(key: String, amount: Int = 1) = compute(key)
{ _, any -> return@compute if (any == null || any !is Int) -amount else any - amount }

fun IGraph.evaluateDegree() {
    listNodes().forEach {
        it.listAdjacent().stream().forEach { node -> node.interiorTags.increaseTag("inDegree") }
        it.interiorTags.increaseTag("outDegree", it.listAdjacent().size)
        if (!it.interiorTags.containsKey("inDegree")) it.interiorTags["inDegree"] = 0
    }
    /*listNodes().forEach{
        println(it.interiorTags)
    }*/
}

