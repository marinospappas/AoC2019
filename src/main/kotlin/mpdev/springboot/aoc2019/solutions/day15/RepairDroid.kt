package mpdev.springboot.aoc2019.solutions.day15

import mpdev.springboot.aoc2019.solutions.icvm.ICVM
import mpdev.springboot.aoc2019.utils.AocException
import org.slf4j.Logger
import org.slf4j.LoggerFactory

class RepairDroid(private val icvm: ICVM) {

    private val log: Logger = LoggerFactory.getLogger(this::class.java)
    private val visited = mutableSetOf<Pair<Int, Int>>()
    val spaces = mutableSetOf<Pair<Int, Int>>()
    var oxygen: Pair<Int, Int>? = null
    private var frontier = mutableSetOf(0 to 0)

    private var x = 0
    private var y = 0

    private var moves = listOf<Triple<Int, Int, Int>>()

    suspend fun explore() {
        var iterationCount = 0
        while (planMoves()) {
            iterationCount++
            if (iterationCount % 100 == 0)
                log.info("Iteration count: {}", iterationCount)
            moves@for ((x, y, direction) in moves) {
                icvm.setProgramInput(direction)
                when (icvm.getProgramOutput().last()) {
                    0 -> break@moves
                    1 -> {}
                    2 -> oxygen = x to y
                    else -> throw AocException("received unexpected value from repair droid")
                }
                this.x = x
                this.y = y
            }
        }
    }

    private fun planMoves(): Boolean {
        spaces.add(x to y)
        frontier.addAll(listOf(x to y - 1, x to y + 1, x - 1 to y, x + 1 to y) - visited)
        val (x, y) = with(frontier.iterator().takeIf { it.hasNext() } ?: return false) {
            next().also { remove() }
        }
        moves = path(this.x to this.y, x to y, spaces)
        visited.add(x to y)
        return true
    }
    fun path(
            src: Pair<Int, Int>,
            dst: Pair<Int, Int>,
            pass: Set<Pair<Int, Int>>
        ): List<Triple<Int, Int, Int>> {
        if (src == dst) {
            return emptyList()
        }
        val seen = mutableSetOf(src)
        val queue = mutableListOf<Pair<Pair<Int, Int>, List<Triple<Int, Int, Int>>>>(
            src to emptyList()
        )
        while (true) {
            val (pos, moves) = queue.removeAt(0)
            val (x, y) = pos
            for (
            next in
            listOf(
                Triple(x, y - 1, 1), Triple(x, y + 1, 2),
                Triple(x - 1, y, 3), Triple(x + 1, y, 4)
            )
            ) {
                val pos2 = next.first to next.second
                if (pos2 == dst) {
                    return moves + next
                }
                if (pos2 in seen || pos2 !in pass) {
                    continue
                }
                seen.add(pos2)
                queue.add(pos2 to moves + next)
            }
        }
    }
}