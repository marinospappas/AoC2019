package mpdev.springboot.aoc2019.day24

import mpdev.springboot.aoc2019.input.InputDataReader
import mpdev.springboot.aoc2019.solutions.day24.BugTiles
import mpdev.springboot.aoc2019.solutions.day24.Day24
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Order
import org.junit.jupiter.api.Test
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.CsvSource
import java.awt.Point

class Day24Test {

    private val day = 24                                     ///////// Update this for a new dayN test
    private val puzzleSolver = Day24()                        ///////// Update this for a new dayN test
    private val inputDataReader = InputDataReader("src/test/resources/inputdata/input")
    private var inputLines: MutableList<String> = inputDataReader.read(day).toMutableList()

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
    fun `Reads and Processes Input Correctly`() {
        val map = BugTiles()
        map.initPanels2D(inputLines)
        map.printGrid2D()
        assertThat(BugTiles.MAX_X).isEqualTo(5)
        assertThat(BugTiles.MAX_Y).isEqualTo(5)
        assertThat(map.countBugs2D()).isEqualTo(8)
    }

    @ParameterizedTest
    @CsvSource(
        "0, 0, 1",
        "1, 0, 0",
        "3, 0, 2",
        "4, 0, 0",
        "0, 1, 1",
        "3, 1, 1",
        "0, 2, 1",
        "3, 2, 2",
        "4, 2, 1",
        "2, 3, 0",
        "0, 4, 0",
    )
    @Order(3)
    fun `Counts Adjacent Bugs`(x: Int, y: Int, expected: Int) {
        val map = BugTiles()
        map.initPanels2D(inputLines)
        map.printGrid2D()
        assertThat(map.countAdjacentBugs2D(Point(x,y), map.tiles0)).isEqualTo(expected)
    }

    @Test
    @Order(4)
    fun `Runs Bug Cycle`() {
        val expected = listOf(16, 12, 13, 10)
        val map = BugTiles()
        map.initPanels2D(inputLines)
        map.printGrid2D()
        repeat(4) {
            map.run1cycle2D()
            map.printGrid2D()
            assertThat(map.countBugs2D()).isEqualTo(expected[it])
        }
    }

    @Test
    @Order(5)
    fun `Solves Part 1`() {
        puzzleSolver.solvePart1()
        assertThat(puzzleSolver.result).isEqualTo(2129920)
    }

    @ParameterizedTest
    @CsvSource(
        "0, 0, 0, 1", "0, 1, 0, 0", "0, 3, 0, 2", "0, 4, 0, 0", "0, 0, 1, 1",
        "0, 3, 1, 1", "0, 0, 2, 1", "0, 3, 2, 2", "0, 4, 2, 1", "0, 2, 3, 0", "0, 0, 4, 0",
        "1, 0, 0, 0", "1, 1, 0, 0", "1, 3, 0, 0", "1, 3, 2, 0", "1, 2, 3, 0",
        "1, 4, 0, 1", "1, 4, 1, 1", "1, 4, 2, 1", "1, 4, 3, 1",
        "1, 0, 4, 1", "1, 1, 4, 1", "1, 2, 4, 1", "1, 3, 4, 1", "1, 4, 4, 2",
        "-1, 0, 0, 0", "-1, 4, 1, 0", "-1, 0, 3, 0", "-1, 4, 3, 0", "-1, 2, 4, 0", "-1, 3, 4, 0", "-1, 0, 4, 0",
        "-1, 1, 1, 0", "-1, 3, 1, 0", "-1, 1, 3, 0", "-1, 3, 3, 0",
        "-1, 1, 2, 3", "-1, 3, 2, 2", "-1, 2, 1, 1", "-1, 2, 4, 0", "-1, 2, 3, 1"
    )
    @Order(6)
    fun `Counts Adjacent Bugs on Multiple Levels`(level: Int, x: Int, y: Int, expected: Int) {
        val map = BugTiles()
        map.initPanels3D(inputLines)
        map.tiles3D[-1] = mutableMapOf()
        map.tiles3D[1] = mutableMapOf()
        assertThat(map.countAdjacentBugs3D(level, Point(x,y), map.tiles3D)).isEqualTo(expected)
    }

    @Test
    @Order(7)
    fun `Runs Bug Cycle on Multiple Levels`() {
        val map = BugTiles()
        map.initPanels3D(inputLines)
        map.printGrid3D()
        repeat(10) {
            map.run1cycle3D()
        }
        map.printGrid3D()
        assertThat(map.countBugs3D()).isEqualTo(99)
    }

    @Test
    @Order(8)
    fun `Solves Part 2`() {
        puzzleSolver.NUM_OF_CYCLES = 10
        puzzleSolver.solvePart2()
        assertThat(puzzleSolver.result).isEqualTo(99)
    }
}
