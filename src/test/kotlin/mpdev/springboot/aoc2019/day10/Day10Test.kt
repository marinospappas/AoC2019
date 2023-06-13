package mpdev.springboot.aoc2019.day10

import mpdev.springboot.aoc2019.solutions.day10.Day10
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Order
import org.junit.jupiter.api.Test
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.CsvSource
import java.awt.Point

class Day10Test {

    private val day = 10                                     ///////// Update this for a new dayN test
    private val puzzleSolver = Day10()                        ///////// Update this for a new dayN test
    private var input1 = listOf(
        ".#..#",
        ".....",
        "#####",
        "....#",
        "...##"
    )
    private var input2 = listOf(
        "......#.#.",
        "#..#.#....",
        "..#######.",
        ".#.#.###..",
        ".#..#.....",
        "..#....#.#",
        "#..#....#.",
        ".##.#..###",
        "##...#..#.",
        ".#....####"
    )
    private var input3 = listOf(
        "#.#...#.#.",
        ".###....#.",
        ".#....#...",
        "##.#.#.#.#",
        "....#.#.#.",
        ".##..###.#",
        "..#...##..",
        "..##....##",
        "......#...",
        ".####.###."
    )
    private var input4 = listOf(
        ".#..#..###",
        "####.###.#",
        "....###.#.",
        "..###.##.#",
        "##.##.#.#.",
        "....###..#",
        "..#.#..#.#",
        "#..#.#.###",
        ".##...##.#",
        ".....#.#.."
    )
    private var input5 = listOf(
        ".#..##.###...#######",
        "##.############..##.",
        ".#.######.########.#",
        ".###.#######.####.#.",
        "#####.##.#.##.###.##",
        "..#####..#.#########",
        "####################",
        "#.####....###.#.#.##",
        "##.#################",
        "#####.##.###..####..",
        "..######..##.#######",
        "####.##.####...##..#",
        ".#####..#.######.###",
        "##...#.##########...",
        "#.##########.#######",
        ".####.#.###.###.#.##",
        "....##.##.###..#####",
        ".#.#.###########.###",
        "#.#.#.#####.####.###",
        "###.##.####.##.#..##"
    )
    private var inputs = listOf(input1, input2, input3, input4, input5)

    @BeforeEach
    fun setup() {
        puzzleSolver.setDay()
        puzzleSolver.inputData = input1
        puzzleSolver.initSolver()
    }

    @Test
    @Order(1)
    fun `Sets Day correctly`() {
        assertThat(puzzleSolver.day).isEqualTo(day)
    }

    @Test
    @Order(2)
    fun `Reads And Processes Input Correctly`() {
        puzzleSolver.asteroids.forEach { println(it) }
        assertThat(puzzleSolver.asteroids.size).isEqualTo(10)
    }

    @ParameterizedTest
    @CsvSource(
        "0, 7", "1, 7", "2, 6", "3, 7", "4, 7",
        "5, 7", "6, 5", "7, 7", "8, 8", "9, 7"
    )
    @Order(3)
    fun `Calculates Number of Visible Asteroids`(index: Int, expected: Int) {
        val ref = puzzleSolver.asteroids[index]
        puzzleSolver.calculateVisibleAsteroids(ref)
        puzzleSolver.asteroids.forEach { println(it) }
        assertThat(ref.visibleCount).isEqualTo(expected)
    }

    @ParameterizedTest
    @CsvSource(
        "0, 8", "1,33", "2, 35", "3, 41", "4, 210"
    )
    @Order(4)
    fun `Solves Part 1`(inputIndex: Int, expVis: Int) {
        puzzleSolver.inputData = inputs[inputIndex]
        puzzleSolver.initSolver()
        puzzleSolver.solvePart1()
        assertThat(puzzleSolver.result).isEqualTo(expVis)
    }

    @Test
    @Order(5)
    fun `Vaporises Asteroids in Correct Order`() {
        puzzleSolver.inputData = listOf(
            ".#....#####...#..",
            "##...##.#####..##",
            "##...#...#.#####.",
            "..#.....#...###..",
            "..#.#.....#....##"
        )
        puzzleSolver.initSolver()
        puzzleSolver.asteroids.forEach { a -> puzzleSolver.calculateVisibleAsteroids(a) }
        val station = puzzleSolver.asteroids.maxByOrNull { it.visibleCount }!!
        val asteroidMap = puzzleSolver.createAngularMap(station)
        val expected = listOf(
            Point(8,1), Point(9,0), Point(9,1), Point(10,0), Point(9,2), Point(11,1),
            Point(12,1), Point(11,2), Point(15,1),
            Point(12,2), Point(13,2), Point(14,2), Point(15,2), Point(12,3), Point(16,4),
            Point(15,4), Point(10,4), Point(4,4),
            Point(2,4), Point(2,3), Point(0,2), Point(1,2), Point(0,1), Point(1,1),
            Point(5,2), Point(1,0), Point(5,1),
            Point(6,1), Point(6,0), Point(7,0), Point(8,0), Point(10,1), Point(14,0),
            Point(16,1), Point(13,3), Point(14,3)
        )
        val keysList = asteroidMap.keys.toList()
        keysList.indices.forEach {
            assertThat(asteroidMap[keysList[it]]?.first()!! .absPos).isEqualTo(expected[it])
        }
    }

    @Test
    @Order(6)
    fun `Solves Part 2`() {
        puzzleSolver.inputData = input5
        puzzleSolver.initSolver()
        puzzleSolver.solvePart1()
        puzzleSolver.solvePart2()
        assertThat(puzzleSolver.result).isEqualTo(802)
    }
}
