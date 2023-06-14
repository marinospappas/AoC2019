package mpdev.springboot.aoc2019.solutions.day14

import mpdev.springboot.aoc2019.model.PuzzlePartSolution
import mpdev.springboot.aoc2019.solutions.PuzzleSolver
import mpdev.springboot.aoc2019.utils.AocException
import org.springframework.stereotype.Component
import kotlin.math.ceil
import kotlin.system.measureTimeMillis

@Component
class Day14: PuzzleSolver() {

    companion object {
        const val MAX_ORE = 1_000_000_000_000L
    }

    final override fun setDay() {
        day = 14         ////// update this when a puzzle solver for a new day is implemented
    }

    init {
        setDay()
    }

    // key = chemical produced, data = quantity of chem produced, list of ingredients
    var reactions = mutableMapOf<String, Pair<Long, List<Chemical>> >()
    var leftovers = mutableMapOf<String, Long>()
    var result = 0L

    override fun initSolver() {
        reactions = mutableMapOf()
        leftovers = mutableMapOf()
        inputData.forEach { inputLine ->
            val match = Regex("""(.+) => (\d+) ([A-Z]+)""").find(inputLine)
            try {
                val (ingredients, quantity, name) = match!!.destructured
                reactions[ name ] = Pair(quantity.toLong(), parseIngredients(ingredients))
            } catch (e: Exception) {
                throw AocException("bad input line $inputLine")
            }
        }
    }

    fun parseIngredients(ingredients: String): List<Chemical> {
        val chemList = mutableListOf<Chemical>()
        val chems = ingredients.split(",")
        chems.forEach {
            val match = Regex("""(\d+) ([A-Z]+)""").find(it)
            try {
                val (quantity, name) = match!!.destructured
                chemList.add(Chemical(quantity.toLong(), name))
            } catch (e: Exception) {
                throw AocException("bad ingredients list $ingredients")
            }
        }
        return chemList
    }

    override fun solvePart1(): PuzzlePartSolution {
        val elapsed = measureTimeMillis {
            result = reactionChain(Chemical(1, "FUEL"))
        }
        return PuzzlePartSolution(1, result.toString(), elapsed)
    }

    override fun solvePart2(): PuzzlePartSolution {
        val elapsed = measureTimeMillis {
            result = tryBinaryProduction(0L, MAX_ORE)
        }
        return PuzzlePartSolution(2, result.toString(), elapsed)
    }

    fun reactionChain(chemical: Chemical): Long {
        val reaction = reactions[chemical.name] ?: throw AocException("reaction not found for product ${chemical.name}")
        val requiredQuan = manageLeftOvers(chemical.name, chemical.quantity)
        if (requiredQuan == 0L)
            return 0
        val ingredients = reaction.second
        val reactionQuan = reaction.first
        val quantityRatio = ceil(requiredQuan.toDouble() / reactionQuan).toInt()
        updateLeftOvers(chemical.name, requiredQuan, quantityRatio, reactionQuan)
        return if (ingredients.size == 1 && ingredients[0].name == "ORE")
            quantityRatio * ingredients[0].quantity
        else
            ingredients.sumOf { reactionChain(Chemical(quantityRatio * it.quantity, it.name)) }
    }

    fun manageLeftOvers(name: String, reqQuan: Long): Long {
        val leftover = leftovers[name] ?: 0L
        return if (leftover >= reqQuan) {
            leftovers[name] = leftover - reqQuan
            0L
        } else {
            leftovers[name] = 0L
            reqQuan - leftover
        }
    }

    fun updateLeftOvers(name: String, reqQuan: Long, quanRatio: Int, reactionQuan: Long) {
        val thisLeftover = quanRatio * reactionQuan - reqQuan
        leftovers[name] = (leftovers[name] ?: 0) + thisLeftover
    }

    fun tryBinaryProduction(from: Long, to: Long): Long {
        var start = from
        var end = to
        var count = 0
        while (start <= end) {
            ++count
            val mid = (start + end) / 2
            leftovers = mutableMapOf()
            val oreNeeded = reactionChain(Chemical(mid, "FUEL"))
            when (oreNeeded.compareTo(MAX_ORE)) {
                0 -> { log.info("part 2: number of iterations = $count"); return mid }
                1 -> end = mid - 1
                -1 -> start = mid + 1
            }
        }
        log.info("part 2: number of iterations = $count")
        return start - 1
    }
}

data class Chemical(val quantity: Long, val name: String)