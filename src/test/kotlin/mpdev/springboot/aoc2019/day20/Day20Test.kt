package mpdev.springboot.aoc2019.day20

import mpdev.springboot.aoc2019.input.InputDataReader
import mpdev.springboot.aoc2019.solutions.day20.Day20
import mpdev.springboot.aoc2019.solutions.day20.Maze
import mpdev.springboot.aoc2019.utils.Dijkstra
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Order
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import java.awt.Point

class Day20Test {

    private val day = 20                       ///////// Update this for a new dayN test
    private val puzzleSolver = Day20()         ///////// Update this for a new dayN test
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
        puzzleSolver.maze.printMaze()
        assertThat(puzzleSolver.maze.maxX).isEqualTo(31)
        assertThat(puzzleSolver.maze.maxY).isEqualTo(33)
        assertThat(puzzleSolver.maze.data.size).isEqualTo(700)
    }

    @Test
    @Order(3)
    fun `Calculates Fastest Path for single level maze`() {
        val input = maze1()
        val maze = Maze(input)
        maze.printMaze()
        maze.createGraphP1()
        val algo = Dijkstra<Point>()
        val res = algo.runIt(maze.getStartP1(), maze.getEndP1())
        assertThat(res.minCost).isEqualTo(23)
    }

    @Test
    @Order(5)
    fun `Solves Part 1`() {
        val result = puzzleSolver.solvePart1().result.toInt()
        assertThat(result).isEqualTo(58)
    }

    @Test
    @Order(6)
    fun `Calculates Fastest Path for recursive maze`() {
        val input = maze1()
        val maze = Maze(input)
        maze.printMaze()
        maze.createGraphP2()
        val algo = Dijkstra<Pair<Int,Point>>()
        val res = algo.runIt(maze.getStartP2(), maze.getEndP2())
        assertThat(res.minCost).isEqualTo(26)
    }

    @Test
    @Order(7)
    fun `Calculates Fastest Path for large recursive maze`() {
        val input = maze2()
        val maze = Maze(input)
        maze.printMaze()
        maze.createGraphP2()
        maze.depthLimit = 20
        val algo = Dijkstra<Pair<Int,Point>>()
        val res = algo.runIt(maze.getStartP2(), maze.getEndP2())
        assertThat(res.minCost).isEqualTo(396)
    }

    @Test
    @Order(8)
    fun `Part 2 for Test data cannot be solved`() {
        puzzleSolver.DEPTH_LIMIT = 1000
        assertThrows<Dijkstra.DijkstraException> { puzzleSolver.solvePart2() }
    }

    private fun maze1() = listOf(
        "#######0#########",
        "#######.........#",
        "#######.#######.#",
        "#######.#######.#",
        "#######b#######.#",
        "#####       ###.#",
        "b..##       ###.#",
        "##.##       ###.#",
        "##..d       ###.#",
        "#####       ###.#",
        "#########f#####.#",
        "d.#######...###.#",
        "#.#########.###.#",
        "f.#########.....#",
        "###########9#####"
    )

    private fun maze2() = listOf(
                "###########9#a#b#c#######d###############",
                "#...#.......#.#.......#.#.......#.#.#...#",
                "###.#.#.#.#.#.#.#.###.#.#.#######.#.#.###",
                "#.#...#.#.#...#.#.#...#...#...#.#.......#",
                "#.###.#######.###.###.#.###.###.#.#######",
                "#...#.......#.#...#...#.............#...#",
                "#.#########e#######f#g#######h#######.###",
                "#...#.#                           #.#.#.#",
                "#.###.#                           #.#.#.#",
                "#.#...#                           #...#.#",
                "#.###.#                           #.###.#",
                "#.#...i                           c.#.#.h",
                "#.###.#                           #.#.#.#",
                "j.....#                           #.....#",
                "#######                           #######",
                "#.#...d                           #.....g",
                "#.###.#                           #.###.#",
                "#.....#                           #...#.#",
                "###.###                           #.#.#.#",
                "k...#.#                           l.#.#.#",
                "#####.#                           #######",
                "#.....j                           m.#...#",
                "###.#.#                           #.###.#",
                "f...#.#                           #.....l",
                "###.###                           #.#.#.#",
                "#.....#                           #.#.#.#",
                "###.###########k###b#######a#########.###",
                "#.....#...#.....#.......#...#.....#.#...#",
                "#####.#.###.#######.#######.###.###.#.#.#",
                "#.......#.......#.#.#.#.#...#...#...#.#.#",
                "#####.###.#####.#.#.#.#.###.###.#.###.###",
                "#.......#.....#.#...#...............#...#",
                "#############0#i#e###m###################"
    )
}
