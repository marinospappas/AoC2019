package mpdev.springboot.aoc2019.day18

import mpdev.springboot.aoc2019.input.InputDataReader
import mpdev.springboot.aoc2019.solutions.day18.*
import mpdev.springboot.aoc2019.utils.Dijkstra
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Order
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestInstance
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.Arguments
import org.junit.jupiter.params.provider.MethodSource
import java.awt.Point
import java.io.File
import java.util.stream.Stream

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class Day18Test {

    private val day = 18                       ///////// Update this for a new dayN test
    private val puzzleSolver = Day18()         ///////// Update this for a new dayN test
    private val inputDataReader = InputDataReader("src/test/resources/inputdata/input")
    private var inputLines: List<String> = inputDataReader.read(day)

    @BeforeEach
    fun setup() {
        puzzleSolver.setDay()
        puzzleSolver.inputData = inputLines
        puzzleSolver.initSolver()
    }

    @Test
    @Order(1)
    fun `Sets Day correctly`() {
        assertThat(puzzleSolver.day).isEqualTo(day)
    }

    @Test
    @Order(2)
    fun `Reads Input and sets Maze up`() {
        puzzleSolver.vault.printVault()
        println(puzzleSolver.vault.finalKeysList)
        assertThat(puzzleSolver.vault.maxX).isEqualTo(24)
        assertThat(puzzleSolver.vault.maxY).isEqualTo(6)
        assertThat(puzzleSolver.vault.data.size).isEqualTo(144)
    }

    @Test
    @Order(3)
    fun `List of Neighbour nodes is calculated using shortest path`() {
        val vault = Vault(vault19())
        vault.printVault()
        vault.createGraph()
        val startKey = Vault.GraphKey(Point(1, 1), 0)
        val neighbours = vault.getNeighbours(startKey)
        println("size: ${neighbours.size}")
        val firstNeighbourKey = Vault.GraphKey(Point(10, 1),0.addKey('a'))
        val secondNeighbourKey =  Vault.GraphKey(Point(10, 4),0.addKey('b'))
        println(neighbours.map { it.nodeId })
        println("distances: ")
        println("distance of first: ${vault.graph.costs[Pair(startKey,firstNeighbourKey)]}")
        println("distance of last : ${vault.graph.costs[Pair(startKey,secondNeighbourKey)]}")
        assertThat(neighbours.size).isEqualTo(2)
        assertThat(neighbours.first().nodeId.position).isEqualTo(Point(10,1))
        assertThat(neighbours.first().nodeId.keys).isEqualTo(0.addKey('a'))
        assertThat(neighbours.last().nodeId.position).isEqualTo(Point(10,4))
        assertThat(neighbours.last().nodeId.keys).isEqualTo(0.addKey('b'))
        assertThat(vault.graph.costs[Pair(startKey,firstNeighbourKey)]).isEqualTo(9)
        assertThat(vault.graph.costs[Pair(startKey,secondNeighbourKey)]).isEqualTo(14)
    }

    @Test
    @Order(4)
    fun `Calculates List of Neighbour nodes with Keys - vault1`() {
        val vault = Vault(vault11())
        vault.printVault()
        vault.createGraph()
        println()
        var neighbours = vault.getNeighbours(Vault.GraphKey(Point(5, 1), 0))
        println("size: ${neighbours.size}")
        println(neighbours.map { it.nodeId })
        println("distances: ${vault.graph.costs}")
        println("distance of last: ${vault.graph.costs[Pair(Vault.GraphKey(Point(5, 1),0),Vault.GraphKey(Point(7, 1), 0.addKey('a')))]}")
        println("cache: ${vault.neighboursCache}")
        assertThat(neighbours.size).isEqualTo(1)
        assertThat(neighbours.last().nodeId.position).isEqualTo(Point(7,1))
        assertThat(neighbours.last().nodeId.keys).isEqualTo(0.addKey('a'))

        neighbours = vault.getNeighbours(Vault.GraphKey(Point(7, 1), 0.addKey('a')))
        println(neighbours.size)
        println(neighbours.map { it.nodeId })
        println("distances: ${vault.graph.costs}")
        assertThat(neighbours.size).isEqualTo(1)
        assertThat(neighbours.last().nodeId.position).isEqualTo(Point(1,1))
        assertThat(neighbours.last().nodeId.keys).isEqualTo(0.addKey('a').addKey('b'))
    }

    @Test
    @Order(5)
    fun `Calculates List of Neighbour nodes with Keys - vault2`() {
        val vault = Vault(vault12())
        vault.printVault()
        vault.createGraph()
        var neighbours = vault.getNeighbours(Vault.GraphKey(Point(15, 1),0))
        println(neighbours.size)
        println(neighbours.map { it.nodeId })
        println("distances: ${vault.graph.costs}")
        assertThat(neighbours.size).isEqualTo(1)
        assertThat(neighbours.last().nodeId.position).isEqualTo(Point(17,1))
        assertThat(neighbours.last().nodeId.keys).isEqualTo(0.addKey('a'))

        neighbours = vault.getNeighbours(Vault.GraphKey(Point(17, 1), 0.addKey('a')))
        println(neighbours.size)
        println(neighbours.map { it.nodeId })
        println("distances: ${vault.graph.costs}")
        assertThat(neighbours.size).isEqualTo(1)
        assertThat(neighbours.last().nodeId.position).isEqualTo(Point(11,1))
        assertThat(neighbours.last().nodeId.keys).isEqualTo(0.addKey('a').addKey('b'))

        neighbours = vault.getNeighbours(Vault.GraphKey(Point(11, 1), 0.addKey('a').addKey('b')))
        println(neighbours.size)
        println(neighbours.map { it.nodeId })
        println("distances: ${vault.graph.costs}")
        assertThat(neighbours.size).isEqualTo(1)
        assertThat(neighbours.last().nodeId.position).isEqualTo(Point(21,1))
        assertThat(neighbours.last().nodeId.keys).isEqualTo(0.addKey('a').addKey('b').addKey('c'))

        neighbours = vault.getNeighbours(Vault.GraphKey(Point(21, 1), 0.addKey('a').addKey('b').addKey('c')))
        println(neighbours.size)
        println(neighbours.map { it.nodeId })
        println("distances: ${vault.graph.costs}")
        assertThat(neighbours.size).isEqualTo(2)
        assertThat(neighbours.last().nodeId.position).isEqualTo(Point(1,3))
        assertThat(neighbours.last().nodeId.keys).isEqualTo(0.addKey('a').addKey('b').addKey('c').addKey('d'))
    }

    @Test
    @Order(6)
    fun `Calculates List of Neighbour nodes with Keys - vault3`() {
        val vault = Vault(vault13())
        vault.printVault()
        vault.createGraph()
        val neighbours = vault.getNeighbours(Vault.GraphKey(Point(18, 3), 0.addKey('a').addKey('b').addKey('c').addKey('d').addKey('e')))
        println(neighbours.size)
        println(neighbours.map { it.nodeId })
        println("distances: ${vault.graph.costs}")
        assertThat(neighbours.size).isEqualTo(1)
        assertThat(neighbours.last().nodeId.position).isEqualTo(Point(22,1))
        assertThat(neighbours.last().nodeId.keys).isEqualTo(0.addKey('a').addKey('b').addKey('c').addKey('d').addKey('e').addKey('f'))
    }

    @Test
    @Order(7)
    fun `Calculates List of Neighbour nodes with Keys - test input`() {
        val vault = Vault(inputLines)
        vault.printVault()
        vault.createGraph()
        var neighbours = vault.getNeighbours(Vault.GraphKey(Point(1, 1), 0))
        println(neighbours.size)
        println(neighbours.map { it.nodeId })
        assertThat(neighbours.size).isEqualTo(4)
        assertThat(neighbours.last().nodeId.position).isEqualTo(Point(16,1))
        assertThat(neighbours.last().nodeId.keys).isEqualTo(0.addKey('a'))

        neighbours = vault.getNeighbours(Vault.GraphKey(Point(3, 2),0.addKey('a').addKey('d')))
        println(neighbours.size)
        println(neighbours.map { it.nodeId })
        assertThat(neighbours.size).isEqualTo(4)
        assertThat(neighbours.last().nodeId.position).isEqualTo(Point(17,1))
        assertThat(neighbours.last().nodeId.keys).isEqualTo(0.addKey('a').addKey('c').addKey('d'))
    }

    @ParameterizedTest
    @MethodSource("minPathParameters")
    @Order(8)
    fun `Calculates Shortest Path`(vaultInput: List<String>, expected: Int) {
        val vault = Vault(vaultInput)
        vault.printVault()
        vault.createGraph()
        val algo = Dijkstra(vault.graph.costs)
        val res = algo.runIt(vault.getStart(), { id -> vault.atEnd(id) } )
        println("shortest path: ${res.minCost}")
        res.path.forEach { println(it) }
        println("Dijkstra iterations: ${res.numberOfIterations}")
        println("getNeighbours ran ${vault.countGetNeighbours} times for ${vault.totalElapsed} msec")
        println("neighbours cache: ${vault.neighboursCache.size}")
        println("cache hits: ${vault.cacheHits}")
        assertThat(res.minCost).isEqualTo(expected)
    }

    private fun minPathParameters() =
        Stream.of(
            Arguments.of(vault11(), 8),
            Arguments.of(vault12(), 86),
            Arguments.of(vault13(), 132),
            Arguments.of(vault14(), 39),
            Arguments.of(vault15(), 136),
            Arguments.of(File("src/test/resources/inputdata/input18.txt").readLines(), 81)
        )

    @Test
    @Order(9)
    fun `Solves Part 1`() {
        val result = puzzleSolver.solvePart1().result.toInt()
        puzzleSolver.vault.printVault()
        assertThat(result).isEqualTo(81)
    }

    @Test
    @Order(10)
    fun `Reads Input and sets Graph up for Part 2`() {
        val inputLines = File("src/test/resources/inputdata/input18-2.txt").readLines()
        val vault = VaultPart2(inputLines)
        vault.createGraph2()
        vault.printVault()
        println(vault.finalKeysList.keysList())
        println(vault.graph2.get(VaultPart2.GraphKey2(vault.startList,0)).nodeId)
        assertThat(vault.maxX).isEqualTo(13)
        assertThat(vault.maxY).isEqualTo(9)
        assertThat(vault.data.size).isEqualTo(117)
        assertThat(vault.startList).isEqualTo(listOf(Point(5,3), Point(7,3), Point(5,5), Point(7,5)))
        assertThat(vault.finalKeysList).isEqualTo(32767)
    }

    @Test
    @Order(11)
    fun `Calculates List of Neighbour nodes with Keys Part 2 - vault1`() {
        val vault = VaultPart2(vault21())
        vault.printVault()
        vault.createGraph2()
        println()
        var neighbours = vault.getNeighbours(VaultPart2.GraphKey2(vault.startList, 0))
        println("size: ${neighbours.size}")
        println(neighbours.map { it.nodeId })
        println("distances: ${vault.graph2.costs}")
        println("cache l2: ${vault.neighbours2Cache}")
        println("cache l1: ${vault.keysGraphCache}")
        assertThat(neighbours.size).isEqualTo(1)
        assertThat(neighbours.last().nodeId.positionsList).isEqualTo(listOf(Point(1, 1),Point(4, 2),Point(2, 4),Point(4, 4)))
        assertThat(neighbours.last().nodeId.keys).isEqualTo(0.addKey('a'))

        neighbours = vault.getNeighbours(VaultPart2.GraphKey2(mutableListOf(Point(1, 1),Point(4, 2),Point(2, 4),Point(4, 4)), 0.addKey('a')))
        println("size: ${neighbours.size}")
        println(neighbours.map { it.nodeId })
        println("distances: ${vault.graph2.costs}")
        println("cache l2: ${vault.neighbours2Cache}")
        println("cache l1: ${vault.keysGraphCache}")
        assertThat(neighbours.size).isEqualTo(1)
        assertThat(neighbours.last().nodeId.positionsList).isEqualTo(listOf(Point(1, 1),Point(4, 2),Point(2, 4),Point(5, 5)))
        assertThat(neighbours.last().nodeId.keys).isEqualTo(0.addKey('a').addKey('b'))
    }

    @Test
    @Order(12)
    fun `Calculates List of Neighbour nodes with Keys Part 2 - vault2`() {
        val vault = VaultPart2(vault22())
        vault.printVault()
        vault.createGraph2()
        println()
        var neighbours = vault.getNeighbours(VaultPart2.GraphKey2(vault.startList, 0))
        println("size: ${neighbours.size}")
        println(neighbours.map { it.nodeId })
        println("distances: ${vault.graph2.costs}")
        println("cache l2: ${vault.neighbours2Cache}")
        println("cache l1: ${vault.keysGraphCache}")
        assertThat(neighbours.size).isEqualTo(3)
        assertThat(neighbours.last().nodeId.positionsList).isEqualTo(listOf(Point(6, 2),Point(8, 2),Point(6, 4),Point(13, 5)))
        assertThat(neighbours.last().nodeId.keys).isEqualTo(0.addKey('c'))

        neighbours = vault.getNeighbours(VaultPart2.GraphKey2(mutableListOf(Point(6, 2),Point(8, 2),Point(6, 4),Point(13, 5)), 0.addKey('c')))
        println("size: ${neighbours.size}")
        println(neighbours.map { it.nodeId })
        println("distances: ${vault.graph2.costs}")
        println("cache l2: ${vault.neighbours2Cache}")
        println("cache l1: ${vault.keysGraphCache}")
        assertThat(neighbours.size).isEqualTo(2)
        assertThat(neighbours.last().nodeId.positionsList).isEqualTo(listOf(Point(6, 2),Point(8, 2),Point(1, 5),Point(13, 5)))
        assertThat(neighbours.last().nodeId.keys).isEqualTo(0.addKey('b').addKey('c'))
    }

    @Test
    @Order(13)
    fun `Calculates List of Neighbour nodes with Keys Part 2 - vault3`() {
        val vault = VaultPart2(vault23())
        vault.printVault()
        vault.createGraph2()
        println()
        var neighbours = vault.getNeighbours(VaultPart2.GraphKey2(vault.startList, 0))
        println("size: ${neighbours.size}")
        println(neighbours.map { it.nodeId })
        println("distances: ${vault.graph2.costs}")
        println("cache l2: ${vault.neighbours2Cache}")
        println("cache l1: ${vault.keysGraphCache}")
        assertThat(neighbours.size).isEqualTo(1)
        assertThat(neighbours.last().nodeId.positionsList).isEqualTo(listOf(Point(4, 1),Point(7, 2),Point(5, 4),Point(7, 4)))
        assertThat(neighbours.last().nodeId.keys).isEqualTo(0.addKey('a'))

        neighbours = vault.getNeighbours(VaultPart2.GraphKey2(mutableListOf(Point(4, 1),Point(7, 2),Point(5, 4),Point(7, 4)), 0.addKey('a')))
        println("size: ${neighbours.size}")
        println(neighbours.map { it.nodeId })
        println("distances: ${vault.graph2.costs}")
        println("cache l2: ${vault.neighbours2Cache}")
        println("cache l1: ${vault.keysGraphCache}")
        assertThat(neighbours.size).isEqualTo(1)
        assertThat(neighbours.last().nodeId.positionsList).isEqualTo(listOf(Point(4, 1),Point(7, 2),Point(3, 5),Point(7, 4)))
        assertThat(neighbours.last().nodeId.keys).isEqualTo(0.addKey('a').addKey('b'))
    }

    @Test
    @Order(14)
    fun `Calculates List of Neighbour nodes with Keys Part 2 - test input`() {
        val vault = VaultPart2(File("src/test/resources/inputdata/input18-2.txt").readLines())
        vault.printVault()
        vault.createGraph2()
        println()
        var neighbours = vault.getNeighbours(VaultPart2.GraphKey2(vault.startList, 0))
        println("size: ${neighbours.size}")
        println(neighbours.map { it.nodeId })
        println("distances: ${vault.graph2.costs}")
        println("cache l2: ${vault.neighbours2Cache}")
        println("cache l1: ${vault.keysGraphCache}")
        assertThat(neighbours.size).isEqualTo(2)
        assertThat(neighbours.last().nodeId.positionsList).isEqualTo(listOf(Point(5, 2),Point(7, 3),Point(5, 5),Point(7, 5)))
        assertThat(neighbours.last().nodeId.keys).isEqualTo(0.addKey('e'))

        neighbours = vault.getNeighbours(VaultPart2.GraphKey2(mutableListOf(Point(5, 2),Point(7, 3),Point(5, 5),Point(7, 5)), 0.addKey('e')))
        println("size: ${neighbours.size}")
        println(neighbours.map { it.nodeId })
        println("distances: ${vault.graph2.costs}")
        println("cache l2: ${vault.neighbours2Cache}")
        println("cache l1: ${vault.keysGraphCache}")
        assertThat(neighbours.size).isEqualTo(2)
        assertThat(neighbours.last().nodeId.positionsList).isEqualTo(listOf(Point(5, 2),Point(9, 1),Point(5, 5),Point(7, 5)))
        assertThat(neighbours.last().nodeId.keys).isEqualTo(0.addKey('e').addKey('h'))
    }

    @ParameterizedTest
    @MethodSource("minPathParametersPart2")
    @Order(18)
    fun `Calculates Shortest Path Part 2`(vaultInput: List<String>, expected: Int) {
        val vault = VaultPart2(vaultInput)
        vault.printVault()
        vault.createGraph2()
        val algo = Dijkstra(vault.graph2.costs)
        val res = algo.runIt(vault.getStart2(), { id -> vault.atEnd(id) } )
        println("shortest path: ${res.minCost}")
        res.path.forEach { println(it) }
        println("Dijkstra iterations: ${res.numberOfIterations}")
        println("getNeighbours ran ${vault.countGetNeighbours} times for ${vault.totalElapsed} msec")
        println("neighbours cache: ${vault.neighboursCache.size}")
        println("cache hits: ${vault.cacheHits}")
        assertThat(res.minCost).isEqualTo(expected)
    }

    private fun minPathParametersPart2() =
        Stream.of(
            Arguments.of(vault21(), 8),
            Arguments.of(vault22(), 24),
            Arguments.of(vault23(), 32),
            Arguments.of(File("src/test/resources/inputdata/input18-2.txt").readLines(), 72),
        )

    @Test
    @Order(9)
    fun `Solves Part 2`() {
        val inputLines = File("src/test/resources/inputdata/input18-2.txt").readLines()
        puzzleSolver.vault2 = VaultPart2(inputLines)
        val result = puzzleSolver.solvePart2().result.toInt()
        puzzleSolver.vault2.printVault()
        assertThat(result).isEqualTo(72)
    }

    private fun vault11() = listOf(
        "#########",
        "#b.A.@.a#",
        "#########"
    )

    private fun vault12() = listOf(
        "########################",
        "#f.D.E.e.C.b.A.@.a.B.c.#",
        "######################.#",
        "#d.....................#",
        "########################"
    )

    private fun vault13() = listOf(
        "########################",
        "#...............b.C.D.f#",
        "#.######################",
        "#.....@.a.B.c.d.A.e.F.g#",
        "########################"
    )

    private fun vault14() = listOf(
        "#################",
        "#..G..c...e..H..#",
        "########.########",
        "#..A..b...f..D..#",
        "########@########",
        "#..E..a...g..B..#",
        "########.########",
        "#..F..d...h..C..#",
        "#################"
    )

    private fun vault15() = listOf(
        "#################",
        "#i.G..c...e..H.p#",
        "########.########",
        "#j.A..b...f..D.o#",
        "########@########",
        "#k.E..a...g..B.n#",
        "########.########",
        "#l.F..d...h..C.m#",
        "#################"
    )

    private fun vault19() = listOf(
        "############",
        "#@........a#",
        "#.##########",
        "#.#........#",
        "#...######b#",
        "###.####...#",
        "#........###",
        "############"
    )

    private fun vault21() = listOf(
        "#######",
        "#a.#Cd#",
        "##@#@##",
        "#######",
        "##@#@##",
        "#cB#Ab#",
        "#######"
    )

    private fun vault22() = listOf(
        "###############",
        "#d.ABC.#.....a#",
        "######@#@######",
        "###############",
        "######@#@######",
        "#b.....#.....c#",
        "###############"
    )

    private fun vault23() = listOf(
        "#############",
        "#DcBa.#.GhKl#",
        "#.###@#@#I###",
        "#e#d#####j#k#",
        "###C#@#@###J#",
        "#fEbA.#.FgHi#",
        "#############"
    )
}
