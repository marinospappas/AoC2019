package mpdev.springboot.aoc2019.day10

import mpdev.springboot.aoc2019.solutions.day10.Day10
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Order
import org.junit.jupiter.api.Test
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.CsvSource

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
    fun `Solves Part 2`() {
        puzzleSolver.solvePart2()
        assertThat(puzzleSolver.result).isEqualTo(4)
    }
}
