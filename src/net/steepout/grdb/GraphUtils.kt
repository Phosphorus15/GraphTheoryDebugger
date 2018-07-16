package net.steepout.grdb

import java.util.*

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
{ _: String, any: Any? -> return@compute if (any == null || any !is Int) amount else any + amount }

fun MutableMap<String, Any>.decreaseTag(key: String, amount: Int = 1) = compute(key)
{ _: String, any: Any? -> return@compute if (any == null || any !is Int) -amount else any - amount }

fun IGraph.evaluateDegree() {
    listNodes().forEach {
        it.listAdjacent().stream().peek { node -> node.interiorTags.increaseTag("inDegree") }
        it.interiorTags.increaseTag("outDegree", it.listAdjacent().size)
    }
}
