package mpdev.springboot.aoc2019.solutions.day18

import java.io.File
import java.util.*
import kotlin.collections.ArrayDeque
import kotlin.collections.HashMap
import kotlin.collections.HashSet
import kotlin.math.PI
import kotlin.math.abs
import kotlin.math.atan2
import kotlin.math.sqrt
import kotlin.system.measureTimeMillis

fun main() {
    val elapsed = measureTimeMillis {
        val day18 = Test2Day18()
        val input = File("src/main/resources/inputdata/input18.txt").readText()
        val result = day18.getMinimumNumberOfMovesToCollectAllKeys(input)
        println(result)
        val input2 = File("src/test/resources/inputdata/input18-2.txt").readText()
        //val result2 = day18.getMinimumNumberOfMovesToCollectAllKeys(input2)
        //println(result2)
    }
    println("$elapsed msec")
}

private typealias Node = Char

// private utility method to determine the type of identifier (key, door, wall, current position or empty)
private val Node.isKey: Boolean
    inline get() = (this - 'a') in 0..25

private val Node.isDoor: Boolean
    inline get() = (this - 'A') in 0..25

private val Node.isWall: Boolean
    inline get() = (this == '#')

private val Node.isCurrentPosition: Boolean
    inline get() = (this == '@' || (this - '0') in 0..9)

private val Node.isEmpty: Boolean
    inline get() = (this == '.')

// Gets the key corresponding with the door, does not make any sense for none door nodes.
private val Node.correspondingKey: Char
    inline get() {
        assert(this.isDoor)
        return this.lowercaseChar()
    }

/**
 * Optimized data structure based on bit masks to store which keys have been collected
 */
@JvmInline
private value class KeyCollection(private val state: Int) {

    companion object {
        private const val completeState: Int = (1 shl 26) - 1

        private val Char.shift: Int
            inline get() = this - 'a'

        fun from(keys: List<Char>): KeyCollection {
            var state = 0
            for (key in keys) {
                assert(key.isKey)
                state = state or (1 shl key.shift)
            }
            return KeyCollection(state)
        }

        /**
         * All keys
         */
        val all = KeyCollection(completeState)

        /**
         * No keys
         */
        val none = KeyCollection(0)
    }

    /**
     * Whether this collection is a subset and contains less keys than the supplied instance.
     */
    fun isProperSubsetOf(other: KeyCollection): Boolean {
        return this in other && other.state != this.state
    }

    /**
     * Whether the specified key is in the collection
     */
    operator fun contains(key: Char): Boolean {
        assert(key.isKey)
        val mask = 1 shl key.shift
        return state.and(mask) == mask
    }

    /**
     * Whether this collection contains all keys in the specified collection (i.e. is a super set of)
     */
    operator fun contains(other: KeyCollection): Boolean {
        return (state and other.state) == other.state
    }

    /**
     * Adds the supplied key
     */
    operator fun plus(key: Char): KeyCollection {
        assert(key.isKey)
        return KeyCollection(this.state or (1 shl key.shift))
    }

    /**
     * Adds all keys in the supplied key collection
     */
    operator fun plus(other: KeyCollection): KeyCollection {
        return KeyCollection(state or other.state)
    }

    override fun toString(): String {
        return (0..25).mapNotNull {
            val c = 'a' + it
            if (contains(c)) c else null
        }.toString()
    }
}

/**
 * Optimized data structure to represent up to 4 nodes (current positions) with a single integer.
 */
@JvmInline
private value class NodeCollection(private val state: Int) {

    companion object {
        fun from(nodes: List<Node>): NodeCollection {
            var state = 0
            nodes.forEachIndexed { index, node ->
                val shift = shiftFor(index)
                state = state or (node.code shl shift)
            }
            return NodeCollection(state)
        }

        fun shiftFor(index: Int): Int {
            return 8 * index
        }
    }

    inline fun forEachIndexed(perform: (Int, Node) -> Unit) {
        for (i in 0..3) {
            val shift = shiftFor(i)
            val node = (state shr shift) and 0xFF
            if (node == 0) break
            perform(i, node.toChar())
        }
    }

    fun replacingNode(index: Int, node: Node): NodeCollection {
        val shift = shiftFor(index)
        var newState = state and (0xFF shl shift).inv()
        newState = newState or (node.code shl shift)
        return NodeCollection(newState)
    }
}

class Test2Day18 {

    /**
     * Combination of node and collected (or required) keys.
     */
    private data class KeyedNode(val node: Node, val keys: KeyCollection) {
        fun isAvailable(keysInPossession: KeyCollection): Boolean {
            return keys in keysInPossession
        }
    }

    /**
     * Combination of multiple nodes (representing the different robot positions) and combination of keys in possession.
     */
    private data class KeyedNodeCollection(val nodes: NodeCollection, val keys: KeyCollection)

    /**
     * Data structure holding the map information (all the nodes that exist on the map, without any key collection info)
     */
    private class NodeMap(private val matrix: Array<Array<Node>>, private val keyNodeCoordinates: Array<Coordinate?>) {

        companion object {
            fun from(input: String): NodeMap {
                val lines = input.reader().readLines()
                val sizeY = lines.size
                val sizeX = lines.maxByOrNull { it.length }?.length ?: 0
                val matrix = Array(sizeX) {
                    Array(sizeY) {
                        '#'
                    }
                }
                val keyNodeCoordinates = Array<Coordinate?>(128) { null }
                var currentPositionIndex = 1
                for ((y, line) in lines.withIndex()) {
                    for ((x, c) in line.withIndex()) {
                        val node = if (c.isCurrentPosition) {
                            // Give each current position a unique index (1, 2, 3, 4)
                            '0' + currentPositionIndex++
                        } else {
                            c
                        }
                        if (node.isCurrentPosition || node.isKey) {
                            keyNodeCoordinates[node.code] = Coordinate(x, y)
                        }
                        matrix[x][y] = node
                    }
                }
                return NodeMap(matrix, keyNodeCoordinates)
            }
        }

        fun nodeAt(coordinate: Coordinate): Node {
            return matrix[coordinate.x][coordinate.y]
        }

        fun coordinateFor(node: Node): Coordinate {
            return keyNodeCoordinates[node.code] ?: throw IllegalArgumentException("Coordinate requested for none key node: $node")
        }

        fun isAccessible(coordinate: Coordinate, keysInPossession: KeyCollection): Boolean {
            val x = coordinate.x
            val y = coordinate.y

            if (x in matrix.indices) {
                val s = matrix[x]
                if (y in s.indices) {
                    val node = s[y]
                    val door = node.isDoor
                    return (!door && !node.isWall) || (door && keysInPossession.contains(node.lowercaseChar()))
                }
            }
            return false
        }

        inline fun getNodes(predicate: (Node) -> Boolean): List<Node> {
            val result = mutableListOf<Node>()
            for (x in matrix.indices) {
                for (y in matrix[x].indices) {
                    val node = matrix[x][y]
                    if (predicate(node)) {
                        result.add(node)
                    }
                }
            }
            return result
        }

        var pathEvaluationCount = 0
        var nodeEvaluationCount = 0

        /**
         * Method to find the paths to all keys (not necessarily the shortest) from a designated source node.
         *
         * For every unique path found the `onFound` lambda is called.
         *
         * This method uses a depth first search to traverse all possible unique paths.
         */
        private fun findAllKeyPaths(from: Node, onFound: (Path<KeyedNode>) -> Boolean) {
            fun recurse(currentCoordinate: Coordinate, currentNode: Node, visited: MutableSet<Coordinate>,
                        pathLength: Int, requiredKeys: KeyCollection) {
                if (currentNode.isKey && pathLength != 0) {
                    // Found
                    if (onFound(Path(KeyedNode(currentNode, requiredKeys), pathLength, null))) {
                        return
                    }
                }
                pathEvaluationCount++
                visited.add(currentCoordinate)
                currentCoordinate.directNeighbourSequence().forEach {
                    if (this.isAccessible(it, KeyCollection.all) && !visited.contains(it)) {
                        val neighbourNode = this.nodeAt(it)
                        val nextRequiredKeys = if (neighbourNode.isDoor) {
                            requiredKeys + neighbourNode.correspondingKey
                        } else {
                            requiredKeys
                        }
                        recurse(it, neighbourNode, visited, pathLength + 1, nextRequiredKeys)
                    }
                }
                visited.remove(currentCoordinate)
            }
            recurse(coordinateFor(from), from, HashSet(), 0, KeyCollection.none)
        }

        private val edgeCache = HashMap<Node, Collection<Path<KeyedNode>>>(32)

        private fun edgesFrom(node: Node, currentKeys: KeyCollection): Collection<Path<KeyedNode>> {
            return edgeCache.getOrPut(node) {
                val foundPaths = HashMap<Node, Path<KeyedNode>>(32)
                findAllKeyPaths(node) { nodePath ->
                    // As an optimization we only need to find keys which are not yet collected, because at the time this
                    // method is called the optimal path to those nodes will already be found using Dijkstra
                    if (!currentKeys.contains(nodePath.destination.node)) {

                        // Stop when a key is found that is not in the current collection.
                        // It does not make sense to traverse the same path beyond that,
                        // because we arrive at a different key calling this method again

                        val existingEdge = foundPaths[nodePath.destination.node]
                        if (
                            existingEdge == null || // add non-existent paths
                            nodePath.pathLength < existingEdge.pathLength  || // add shorter paths
                            nodePath.destination.keys.isProperSubsetOf(existingEdge.destination.keys) // less keys needed
                        ) {
                            foundPaths[nodePath.destination.node] = nodePath
                        }
                        true
                    } else {
                        false
                    }
                }
                foundPaths.values
            }
        }

        /**
         * Performs the specified lambda for each relevant edge of the specified node collection
         *
         * Relevant means that the key the edge points to has not been collected yet and that it is reachable given the keys
         * in possession.
         */
        private inline fun forEachRelevantEdge(nodeCollection: KeyedNodeCollection, perform: (Int, Path<KeyedNode>) -> Unit) {
            nodeCollection.nodes.forEachIndexed { index, subNode ->
                edgesFrom(subNode, nodeCollection.keys).forEach { edge ->
                    // Only add keys that have not yet been collected and from paths that are actually available
                    if (edge.destination.isAvailable(nodeCollection.keys) && !nodeCollection.keys.contains(edge.destination.node)) {
                        perform(index, edge)
                    }
                }
            }
        }

        /**
         * Finds the minimum path given the initial positions and keys in the graph to collect all keys.
         *
         * This uses the Dijkstra algorithm with a Fibonacci Heap as a priority queue for faster performance.
         *
         * The graph contains all info about the maze (weight of edges are lengths of paths between key nodes)
         */
        fun minimumPathToCollectAllKeys(): Int? {

            // Use a FibonacciHeap to optimize performance. A FibonacciHeap allows reordering of the priority queue without a
            // remove and subsequent add.
            // The heap is automatically ordered by NodePath ascending (increasing path length, so shortest paths are first)
            val pending = FibonacciHeap<KeyedNodeCollection>()

            // The initial positions
            val initialNodes = this.getNodes { it.isCurrentPosition }

            // This is the collection of keys to check against for completeness
            val completeKeyCollection = KeyCollection.from(this.getNodes { it.isKey })

            // The initial nodes (starting positions of the robots) in combination with an empty key collection
            val initialNodeCollection = KeyedNodeCollection(NodeCollection.from(initialNodes), KeyCollection.none)

            // Add the initial nodes to the pending queue
            pending.update(initialNodeCollection, 0)
            val settled = HashSet<KeyedNodeCollection>()

            while (true) {
                // If the queue is empty: break out of the loop
                val current = pending.poll() ?: break

                // Process the node collection with the lowest path length
                val currentNodeCollection = current.destination

                // If this collection contains all the keys we were looking for: we're done!
                if (completeKeyCollection in currentNodeCollection.keys) {
                    return current.pathLength.toInt()
                }

                nodeEvaluationCount++

                // Add to the settled set to avoid revisiting the same node
                settled.add(currentNodeCollection)

                forEachRelevantEdge(currentNodeCollection) { nodeIndex, edge ->
                    val neighbour = KeyedNodeCollection(currentNodeCollection.nodes.replacingNode(nodeIndex, edge.destination.node),
                        currentNodeCollection.keys + edge.destination.node)
                    if (!settled.contains(neighbour)) {
                        pending.update(neighbour, current.pathLength.toInt() + edge.pathLength.toInt())
                    }
                }
            }
            return null
        }
    }

    /**
     * Finds the reachable nodes of the map, given the current set of keys in possession
     * Main method which gives the answer to both part 1 and part 2
     */
    fun getMinimumNumberOfMovesToCollectAllKeys(input: String): Int {
        val map = NodeMap.from(input)
        val result = map.minimumPathToCollectAllKeys() ?: throw IllegalStateException("Could not find minimum path")

        println("Processed ${map.pathEvaluationCount} coordinates and ${map.nodeEvaluationCount} nodes")

        return result
    }
}

data class Coordinate(val x: Int, val y: Int) : Comparable<Coordinate> {

    companion object {
        val origin = Coordinate(0, 0)
        val up = Coordinate(0, -1)
        val down = Coordinate(0, 1)
        val left = Coordinate(-1, 0)
        val right = Coordinate(1, 0)
        val upLeft = up + left
        val downLeft = down + left
        val upRight = up + right
        val downRight = down + right
        val directNeighbourDirections = arrayOf(up, left, right, down)
        val indirectNeighbourDirections = arrayOf(upLeft, downLeft, upRight, downRight)
        val allNeighbourDirections = directNeighbourDirections + indirectNeighbourDirections
    }

    /**
     * Comparison, ordering from top-left to bottom-right
     */
    override fun compareTo(other: Coordinate): Int {
        val comparY = this.y.compareTo(other.y)
        return if (comparY != 0)
            comparY
        else
            this.x.compareTo(other.x)
        /*return compare(
            { this.y.compareTo(other.y) },
            { this.x.compareTo(other.x) }
        )*/
    }

    fun offset(xOffset: Int, yOffset: Int): Coordinate {
        return Coordinate(x + xOffset, y + yOffset)
    }

    fun offset(vector: Coordinate): Coordinate {
        return offset(vector.x, vector.y)
    }

    /**
     * Returns the diff with the supplied coordinate as a new coordinate (representing the vector)
     */
    fun vector(to: Coordinate): Coordinate {
        return Coordinate(to.x - this.x, to.y - this.y)
    }

    /**
     * Returns the Manhatten distance to the specified coordinate
     */
    fun manhattenDistance(to: Coordinate): Int {
        return abs(x - to.x) + abs(y - to.y)
    }

    /**
     * Returns the shortest Euclidean distance to the specified coordinate
     */
    fun distance(to: Coordinate): Double {
        val deltaX = (x - to.x).toDouble()
        val deltaY = (y - to.y).toDouble()
        return sqrt(deltaX * deltaX + deltaY * deltaY)
    }

    /**
     * The invers
     */
    fun inverted(): Coordinate {
        return Coordinate(-x, -y)
    }

    /**
     * Normalizes the coordinate by dividing both x and y by their greatest common divisor
     */
    fun normalized(): Coordinate {
        val factor = greatestCommonDivisor(abs(this.x.toLong()), abs(this.y.toLong())).toInt()
        return Coordinate(this.x / factor, this.y / factor)
    }

    /**
     * Rotates this coordinate (representing a vector) using the specified rotation direction
     */
    fun rotate(direction: RotationDirection): Coordinate {
        return when (direction) {
            RotationDirection.Left -> Coordinate(this.y, -this.x)
            RotationDirection.Right -> Coordinate(-this.y, this.x)
        }
    }

    /**
     * Optionally rotates this coordinate (representing a vector). Does nothing if the supplied direction is null.
     */
    fun optionalRotate(direction: RotationDirection?): Coordinate {
        return when (direction) {
            null -> this
            else -> rotate(direction)
        }
    }

    fun directNeighbourSequence(): Sequence<Coordinate> {
        return sequence {
            repeat(4) {
                yield(this@Coordinate + directNeighbourDirections[it])
            }
        }
    }

    fun indirectNeighbourSequence(): Sequence<Coordinate> {
        return sequence {
            repeat(4) {
                yield(this@Coordinate + indirectNeighbourDirections[it])
            }
        }
    }

    fun allNeighbourSequence(): Sequence<Coordinate> {
        return sequence {
            repeat(8) {
                yield(this@Coordinate + allNeighbourDirections[it])
            }
        }
    }

    /**
     * Returns the direct neighbours of this coordinate
     */
    val directNeighbours: List<Coordinate>
        get() {
            return List(4) {
                this + directNeighbourDirections[it]
            }
        }

    /**
     * Returns the indirect neighbours of this coordinate, defined as the diagonal neighbours
     */
    val indirectNeighbours: List<Coordinate>
        get() {
            return List(4) {
                this + indirectNeighbourDirections[it]
            }
        }

    val allNeighbours: List<Coordinate>
        get() = List(8) {
            if (it < 4) directNeighbours[it] else indirectNeighbours[it - 4]
        }

    /**
     * Returns the angle between 0 and 2 * PI relative to the specified vector
     */
    fun angle(to: Coordinate): Double {
        val a = to.x.toDouble()
        val b = to.y.toDouble()
        val c = this.x.toDouble()
        val d = this.y.toDouble()

        val atanA = atan2(a, b)
        val atanB = atan2(c, d)

        val angle = atanA - atanB

        return if (angle < 0) angle + 2 * PI else angle
    }

    /**
     * Breadth first search to find the shortest path to all reachable coordinates in a single sweep
     */
    inline fun <T> reachableCoordinates(reachable: (Coordinate) -> Boolean, process: (CoordinatePath) -> T?): T? {
        val list = ArrayDeque<CoordinatePath>()
        val visited = mutableSetOf<Coordinate>()
        list.add(CoordinatePath(this, 0))
        var start = true
        while (true) {
            //val current = list.pollFirst() ?: return null
            val current = list.firstOrNull() ?: return null
            if (start) {
                start = false
            } else {
                process(current)?.let {
                    return it
                }
            }
            visited.add(current.coordinate)
            current.coordinate.directNeighbourSequence().forEach { neighbour ->
                if (!visited.contains(neighbour) && reachable(neighbour)) {
                    list.add(CoordinatePath(neighbour, current.pathLength + 1))
                }
            }
        }
    }

    operator fun get(index: Int): Int {
        return when (index) {
            0 -> x
            1 -> y
            else -> throw IllegalArgumentException("Invalid index supplied")
        }
    }

    operator fun plus(other: Coordinate): Coordinate {
        return offset(other)
    }

    operator fun unaryMinus(): Coordinate {
        return inverted()
    }

    operator fun minus(other: Coordinate): Coordinate {
        return offset(other.inverted())
    }

    operator fun times(other: Coordinate): Coordinate {
        return Coordinate(this.x * other.x, this.y * other.y)
    }

    operator fun times(scalar: Int): Coordinate {
        return Coordinate(this.x * scalar, this.y * scalar)
    }

    operator fun rangeTo(other: Coordinate): CoordinateRange {
        return CoordinateRange(this, other)
    }

    override fun toString(): String {
        return "($x,$y)"
    }
}

data class Insets(val left: Int, val right: Int, val top: Int, val bottom: Int) {
    companion object {
        fun square(dimension: Int): Insets {
            return Insets(dimension, dimension, dimension, dimension)
        }

        fun rectangle(horizontal: Int, vertical: Int): Insets {
            return Insets(horizontal, horizontal, vertical, vertical)
        }
    }
}

/**
 * Range to enumerate coordinates between the (minx, miny) and (maxx, maxy) found in a list of coordinates.
 */
class CoordinateRange(private val minMaxCoordinate: Pair<Coordinate, Coordinate>) : Iterable<Coordinate>,
    ClosedRange<Coordinate> {

    constructor(minCoordinate: Coordinate, width: Int, height: Int) : this(
        Pair(
            minCoordinate,
            minCoordinate + Coordinate(width - 1, height - 1)
        )
    )

    constructor(minCoordinate: Coordinate, maxCoordinate: Coordinate) : this(Pair(minCoordinate, maxCoordinate))
    constructor(collection: Collection<Coordinate>) : this(collection.minMaxCoordinate())

    companion object {
        private fun Collection<Coordinate>.minMaxCoordinate(): Pair<Coordinate, Coordinate> {
            var minX = Integer.MAX_VALUE
            var minY = Integer.MAX_VALUE
            var maxX = Integer.MIN_VALUE
            var maxY = Integer.MIN_VALUE
            for (coordinate in this) {
                minX = kotlin.math.min(minX, coordinate.x)
                minY = kotlin.math.min(minY, coordinate.y)
                maxX = kotlin.math.max(maxX, coordinate.x)
                maxY = kotlin.math.max(maxY, coordinate.y)
            }
            return Pair(Coordinate(minX, minY), Coordinate(maxX, maxY))
        }
    }

    private class CoordinateIterator(val minCoordinate: Coordinate, val maxCoordinate: Coordinate) :
        Iterator<Coordinate> {
        private var nextCoordinate: Coordinate? = if (minCoordinate <= maxCoordinate) minCoordinate else null

        override fun hasNext(): Boolean {
            return nextCoordinate != null
        }

        override fun next(): Coordinate {
            val next = nextCoordinate
                ?: throw IllegalStateException("Next called on iterator while there are no more elements to iterate over")
            nextCoordinate = when {
                next.x < maxCoordinate.x -> next.offset(1, 0)
                next.y < maxCoordinate.y -> Coordinate(minCoordinate.x, next.y + 1)
                else -> null
            }
            return next
        }
    }

    override fun iterator(): Iterator<Coordinate> = CoordinateIterator(minMaxCoordinate.first, minMaxCoordinate.second)

    override fun toString(): String {
        return "$start..$endInclusive"
    }

    override val endInclusive: Coordinate
        get() = minMaxCoordinate.second
    override val start: Coordinate
        get() = minMaxCoordinate.first


    val sizeX: Int
        get() = endInclusive.x - start.x + 1

    val sizeY: Int
        get() = endInclusive.y - start.y + 1

    val size: Int
        get() = sizeX * sizeY

    override fun contains(value: Coordinate): Boolean {
        return value.x in start.x..endInclusive.x && value.y in start.y..endInclusive.y
    }

    fun inset(insets: Insets): CoordinateRange {
        return CoordinateRange(start + Coordinate(insets.left, insets.top), endInclusive - Coordinate(insets.right, insets.bottom))
    }
}

fun Collection<Coordinate>.range(): CoordinateRange = CoordinateRange(this)

val Collection<Coordinate>.minX: Int
    get() = this.minOf { it.x }

val Collection<Coordinate>.minY: Int
    get() = this.minOf { it.y }

val Collection<Coordinate>.maxX: Int
    get() = this.maxOf { it.x }

val Collection<Coordinate>.maxY: Int
    get() = this.maxOf { it.y }

val Collection<Coordinate>.sizeX: Int
    get() = this.maxX + 1

val Collection<Coordinate>.sizeY: Int
    get() = this.maxY + 1

val <E> Map<Coordinate, E>.minX: Int
    get() = this.keys.minX

val <E> Map<Coordinate, E>.minY: Int
    get() = this.keys.minY

val <E> Map<Coordinate, E>.maxX: Int
    get() = this.keys.maxX

val <E> Map<Coordinate, E>.maxY: Int
    get() = this.keys.maxY

val <E> Map<Coordinate, E>.sizeX: Int
    get() = this.maxX + 1

val <E> Map<Coordinate, E>.sizeY: Int
    get() = this.maxY + 1

val <E> Map<Coordinate, E>.minCoordinate: Coordinate
    get() = Coordinate(minX, minY)

val <E> Map<Coordinate, E>.maxCoordinate: Coordinate
    get() = Coordinate(maxX, maxY)

val <E> Map<Coordinate, E>.coordinateRange: CoordinateRange
    get() = CoordinateRange(keys)

class Path<N>(val destination: N, val pathLength: Int, val parent: Path<N>?) : Comparable<Path<N>> {
    val nodeCount: Int = if (parent == null) 1 else parent.nodeCount + 1

    override fun compareTo(other: Path<N>): Int {
        return this.pathLength.compareTo(other.pathLength)
    }

    operator fun contains(node: N): Boolean {
        return any { it == node }
    }

    inline fun any(where: (N) -> Boolean): Boolean {
        var current: Path<N>? = this
        while (current != null) {
            if (where.invoke(current.destination)) return true
            current = current.parent
        }
        return false
    }

    inline fun nodes(where: (N) -> Boolean): Collection<N> {
        val result = ArrayDeque<N>()
        any {
            if (where(it)) result.addFirst(it)
            false
        }
        return result
    }

    val allNodes: Collection<N>
        get() {
            return nodes { true }
        }
}

val Path<Coordinate>.completeDirections: Collection<Coordinate>
    get() {
        val result = ArrayDeque<Coordinate>()
        var current: Path<Coordinate>? = this
        while (true) {
            val parent = current?.parent ?: break
            val direction = current.destination - parent.destination
            result.addFirst(direction)
            current = parent
        }
        return result
    }

class CoordinatePath(val coordinate: Coordinate, val pathLength: Int) : Comparable<CoordinatePath> {
    override fun compareTo(other: CoordinatePath): Int {
        return this.pathLength.compareTo(other.pathLength)
    }
}

/**
 * Breadth first search algorithm to find the shortest paths between unweighted nodes.
 */
inline fun <reified N, T> shortestPath(
    from: N,
    neighbours: (Path<N>) -> Collection<N>,
    reachable: (Path<N>, N) -> Boolean = { _, _ -> true },
    process: (Path<N>) -> T?
): T? {
    val pending = ArrayDeque<Path<N>>()
    val visited = mutableSetOf<N>()
    pending.add(Path(from, 0, null))
    while (true) {
        //val current = pending.pollFirst() ?: return null
        val current = pending.firstOrNull() ?: return null
        if (visited.contains(current.destination)) continue
        visited += current.destination
        process(current)?.let {
            return it
        }
        for (neighbour in neighbours(current)) {
            if (neighbour in visited) continue
            if (reachable(current, neighbour)) {
                pending.add(Path(neighbour, current.pathLength + 1, current))
            }
        }
    }
}

/**
 * Dijkstra's algorithm to find the shortest path between weighted nodes.
 */
inline fun <N, T> shortestWeightedPath(
    from: N,
    neighbours: (N) -> Collection<Pair<N, Int>>,
    process: (Path<N>) -> T?
): T? {
    val pending = PriorityQueue<Path<N>>()
    pending.add(Path(from, 0, null))
    val settled = mutableSetOf<N>()
    while (true) {
        val current = pending.poll() ?: break
        if (settled.contains(current.destination)) continue
        process(current)?.let {
            return it
        }
        val currentNode = current.destination
        settled.add(currentNode)
        for ((neighbour, neighbourWeight) in neighbours(currentNode)) {
            val newDistance = current.pathLength + neighbourWeight
            pending.add(Path(neighbour, newDistance, current))
        }
    }
    return null
}

enum class RotationDirection {
    Left, Right;

    companion object {}
}

/**
 * Computes the greatest common divisor of two integers.
 */
tailrec fun greatestCommonDivisor(a: Long, b: Long): Long {
    if (b == 0L) {
        return a
    }
    return greatestCommonDivisor(b, a % b)
}

tailrec fun greatestCommonDivisor(a: Int, b: Int): Int {
    if (b == 0) {
        return a
    }
    return greatestCommonDivisor(b, a % b)
}