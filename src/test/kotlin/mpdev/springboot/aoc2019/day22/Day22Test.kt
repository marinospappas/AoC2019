package mpdev.springboot.aoc2019.day22

import mpdev.springboot.aoc2019.input.InputDataReader
import mpdev.springboot.aoc2019.solutions.day22.Day22
import mpdev.springboot.aoc2019.solutions.day22.Deck
import mpdev.springboot.aoc2019.solutions.day22.Shuffle
import mpdev.springboot.aoc2019.solutions.day22.Shuffling
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Disabled
import org.junit.jupiter.api.Order
import org.junit.jupiter.api.Test
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.CsvSource

class Day22Test {

    private val day = 22                                     ///////// Update this for a new dayN test
    private val puzzleSolver = Day22()                        ///////// Update this for a new dayN test
    private val inputDataReader = InputDataReader("src/test/resources/inputdata/input")
    private var inputLines: MutableList<String> = inputDataReader.read(day).toMutableList()
    private lateinit var deck: Deck

    @BeforeEach
    fun setup() {
        deck = Deck(10)
        deck.initDeck()
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
        puzzleSolver.shuffleList.forEach { println(it) }
        assertThat(puzzleSolver.shuffleList.size).isEqualTo(10)
    }

    @Test
    @Order(3)
    fun `Shuffle Deal Into a New Stack`() {
        deck.dealToNewStack()
        assertThat(deck.cards).isEqualTo(listOf(9,8,7,6,5,4,3,2,1,0))
    }

    @ParameterizedTest
    @CsvSource(
        "3, '3,4,5,6,7,8,9,0,1,2'",
        "-4, '6,7,8,9,0,1,2,3,4,5'"
    )
    @Order(4)
    fun `Shuffle Cut N Cards`(n: Int, expected: String) {
        deck.initDeck()
        deck.cutNCards(n)
        assertThat(deck.cards).isEqualTo(expected.split(",").map { it.toInt() }.toList())
    }

    @Test
    @Order(5)
    fun `Shuffle Deal with Increment N`() {
        deck.dealWithIncrementN(3)
        assertThat(deck.cards).isEqualTo(listOf(0,7,4,1,8,5,2,9,6,3))
    }

    @Test
    @Order(6)
    fun `Shuffle in Multiple Ways`() {
        deck.initDeck()
        deck.dealWithIncrementN(7)
        deck.dealToNewStack()
        deck.dealToNewStack()
        assertThat(deck.cards).isEqualTo(listOf(0,3,6,9,2,5,8,1,4,7))

        deck.initDeck()
        deck.cutNCards(6)
        deck.dealWithIncrementN(7)
        deck.dealToNewStack()
        assertThat(deck.cards).isEqualTo(listOf(3,0,7,4,1,8,5,2,9,6))

        deck.initDeck()
        deck.dealWithIncrementN(7)
        deck.dealWithIncrementN(9)
        deck.cutNCards(-2)
        assertThat(deck.cards).isEqualTo(listOf(6,3,0,7,4,1,8,5,2,9))

        deck.initDeck()
        deck.dealToNewStack()
        deck.cutNCards(-2)
        deck.dealWithIncrementN(7)
        deck.cutNCards(8)
        deck.cutNCards(-4)
        deck.dealWithIncrementN(7)
        deck.cutNCards(3)
        deck.dealWithIncrementN(9)
        deck.dealWithIncrementN(3)
        deck.cutNCards(-1)
        assertThat(deck.cards).isEqualTo(listOf(9,2,5,8,1,4,7,0,3,6))
    }

    @Test
    @Order(7)
    fun `Solves Part 1`() {
        puzzleSolver.NUM_OF_CARDS = 10
        puzzleSolver.initSolver()
        puzzleSolver.shuffleList.forEach { puzzleSolver.deck.executeShuffle(it) }
        assertThat(puzzleSolver.deck.cards).isEqualTo(listOf(9,2,5,8,1,4,7,0,3,6))
    }

    @Test
    @Order(8)
    fun `Find Previous Index for Deal New Stack`() {
        deck.cards = listOf(9,8,7,6,5,4,3,2,1,0)
        repeat(10) {
            println(deck.findPreviousIndex(it.toLong(), Shuffling(Shuffle.DEAL_NEW_STACK)))
            assertThat(deck.findPreviousIndex(it.toLong(), Shuffling(Shuffle.DEAL_NEW_STACK)).toInt()).isEqualTo(9-it)
        }
    }
    @Test
    @Order(9)
    fun `Find Previous Index for Deal With Increment`() {
        deck.cards = listOf(0,7,4,1,8,5,2,9,6,3)
        repeat(10) {
            println(deck.findPreviousIndex(it.toLong(), Shuffling(Shuffle.DEAL_W_INCREMENT, 3)))
            //assertThat(deck.findPreviousIndex(it.toLong(), Shuffling(Shuffle.DEAL_NEW_STACK)).toInt()).isEqualTo(9-it)
        }
    }

    @Test
    @Disabled
    fun `Solves Part 2`() {
        puzzleSolver.solvePart2()
        assertThat(puzzleSolver.result).isEqualTo(0)
    }
}
