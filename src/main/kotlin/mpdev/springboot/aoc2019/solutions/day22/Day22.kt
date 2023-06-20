package mpdev.springboot.aoc2019.solutions.day22

import mpdev.springboot.aoc2019.model.PuzzlePartSolution
import mpdev.springboot.aoc2019.solutions.PuzzleSolver
import mpdev.springboot.aoc2019.solutions.day22.Shuffle.*
import mpdev.springboot.aoc2019.utils.AocException
import org.springframework.stereotype.Component
import java.math.BigInteger
import java.math.BigInteger.TWO
import kotlin.system.measureTimeMillis

@Component
class Day22: PuzzleSolver() {

    var NUM_OF_CARDS = 10_007L

    final override fun setDay() {
        day = 22         ////// update this when a puzzle solver for a new day is implemented
    }

    init {
        setDay()
    }

    lateinit var deck: Deck
    lateinit var shuffleList: MutableList<Shuffling>
    var result = 0L

    override fun initSolver() {
        deck = Deck(NUM_OF_CARDS)
        deck.initDeck()
        shuffleList = mutableListOf()
        initShuffleList()
    }

    override fun solvePart1(): PuzzlePartSolution {
        log.info("solving day $day part 1")
        val elapsed = measureTimeMillis {
            shuffleList.forEach { deck.executeShuffle(it) }
        }
        result = deck.cards.indexOf(2019).toLong()
        return PuzzlePartSolution(1, result.toString(), elapsed)
    }

    override fun solvePart2(): PuzzlePartSolution {
        log.info("solving day $day part 2")
        val elapsed = measureTimeMillis {
            result = modularArithmeticPart2(BigInteger("2020")).toLong()
        }
        return PuzzlePartSolution(2, result.toString(), elapsed)
    }

    fun initShuffleList() {
        inputData.forEach { inputLine ->
            val match = Regex("""(.*) (-?\d+)?""").find(inputLine)
            try {
                val (cmd, n) = match!!.destructured
                when (cmd) {
                    "deal into new" -> shuffleList.add(Shuffling(DEAL_NEW_STACK))
                    "deal with increment" -> shuffleList.add(Shuffling(DEAL_W_INCREMENT, n.toInt()))
                    "cut" -> shuffleList.add(Shuffling(CUT_N_CARDS, n.toInt()))
                }
            } catch (e: Exception) {
                throw AocException("bad input line $inputLine")
            }
        }
    }

    // part 2 by Todd Ginsberg https://todd.ginsberg.com/post/advent-of-code/2019/day22/
    fun modularArithmeticPart2(find: BigInteger): BigInteger {
        val NUMBER_OF_CARDS = BigInteger("119315717514047")
        val memory = arrayOf(BigInteger.ONE, BigInteger.ZERO)
        inputData.reversed().forEach { instruction ->
            when {
                "cut" in instruction ->
                    memory[1] += instruction.getBigInteger()
                "increment" in instruction ->
                    instruction.getBigInteger().modPow(NUMBER_OF_CARDS - TWO, NUMBER_OF_CARDS).also {
                        memory[0] *= it
                        memory[1] *= it
                    }
                "stack" in instruction -> {
                    memory[0] = memory[0].negate()
                    memory[1] = (memory[1].inc()).negate()
                }
            }
            memory[0] %= NUMBER_OF_CARDS
            memory[1] %= NUMBER_OF_CARDS
        }
        val power = memory[0].modPow(101741582076661.toBigInteger(), NUMBER_OF_CARDS)
        return ((power * find) + ((memory[1] * (power + NUMBER_OF_CARDS.dec())) * ((memory[0].dec()).modPow(NUMBER_OF_CARDS - TWO, NUMBER_OF_CARDS)))).mod(NUMBER_OF_CARDS)
    }

    private fun String.getBigInteger(): BigInteger =
        this.split(" ").last().toBigInteger()
}