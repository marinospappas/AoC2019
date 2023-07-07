package mpdev.springboot.aoc2019.day18

import mpdev.springboot.aoc2019.input.InputDataReader
import mpdev.springboot.aoc2019.solutions.day18.Day18
import mpdev.springboot.aoc2019.solutions.day18.Vault
import mpdev.springboot.aoc2019.utils.Dijkstra
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Order
import org.junit.jupiter.api.Test
import java.awt.Point

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
        assertThat(puzzleSolver.vault.finalKeysList.size).isEqualTo(9)
    }

    @Test
    @Order(3)
    fun `Calculates Fastest Path`() {
        val vault = Vault(inputLines)
        vault.printVault()
        vault.createGraph()
        val algo = Dijkstra<Vault.GraphKey>(updateNodeId = { key -> vault.updateGraphIdWithKey(key) })
        val res = algo.runIt(vault.getStart(), vault.getEnd())
        assertThat(res.minCost).isEqualTo(23)
    }

    @Test
    @Order(5)
    fun `Solves Part 1`() {
        val result = puzzleSolver.solvePart1().result.toInt()
        assertThat(result).isEqualTo(58)
    }


    private fun vault1() = listOf(
        ""
    )

}
