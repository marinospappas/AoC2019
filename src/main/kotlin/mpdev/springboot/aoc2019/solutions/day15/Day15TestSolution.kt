package mpdev.springboot.aoc2019.solutions.day15

import kotlinx.coroutines.runBlocking
import java.io.File
import kotlin.math.pow

fun main() {
    val day15 = Day15TestSolution(File("src/main/resources/inputdata/input15.txt").readLines())
    println("Day 15")
    println(day15.part1())
    println(day15.part2())
}

class Day15TestSolution(lines: List<String>) {
    private val ints: List<Long> =
        lines.first().splitToSequence(",").map { it.toLong() }.toList()

    private class Bot(private val vm: Intcode) {
        val visited: Set<Pair<Int, Int>>
            get() = _visited
        val spaces: Set<Pair<Int, Int>>
            get() = _spaces
        val oxygen: Pair<Int, Int>?
            get() = _oxygen

        private val _visited = mutableSetOf<Pair<Int, Int>>()
        private val _spaces = mutableSetOf<Pair<Int, Int>>()
        private var _oxygen: Pair<Int, Int>? = null
        private var frontier = mutableSetOf(0 to 0)

        private var x = 0
        private var y = 0

        private var moves = listOf<Triple<Int, Int, Long>>()

        suspend fun explore() {
            while (planMoves()) {
                moves@for ((x, y, direction) in moves) {
                    when (vm.getOutput({ direction })) {
                        0L -> break@moves
                        1L -> {}
                        2L -> _oxygen = x to y
                        else -> error("unexpected output or termination")
                    }
                    this.x = x
                    this.y = y
                }
            }
        }

        private fun planMoves(): Boolean {
            _spaces.add(x to y)
            frontier.addAll(listOf(x to y - 1, x to y + 1, x - 1 to y, x + 1 to y) - _visited)
            val (x, y) = with(frontier.iterator().takeIf { it.hasNext() } ?: return false) {
                next().also { remove() }
            }
            moves = path(this.x to this.y, x to y, _spaces)
            _visited.add(x to y)
            return true
        }
    }

    fun part1(): Int? {
        val bot = Bot(Intcode(ints.toMutableList()))
        runBlocking { bot.explore() }
        return path(0 to 0, bot.oxygen ?: return null, bot.spaces).size
    }

    fun part2(): Int? {
        val bot = Bot(Intcode(ints.toMutableList()))
        runBlocking { bot.explore() }
        val oxygen = bot.oxygen ?: return null
        return bot.spaces.map { path(oxygen, it, bot.spaces).size }.max()
    }

    companion object {
        private fun path(
            src: Pair<Int, Int>,
            dst: Pair<Int, Int>,
            pass: Set<Pair<Int, Int>>
        ): List<Triple<Int, Int, Long>> {
            if (src == dst) {
                return emptyList()
            }
            val seen = mutableSetOf(src)
            val queue = mutableListOf<Pair<Pair<Int, Int>, List<Triple<Int, Int, Long>>>>(
                src to emptyList()
            )
            while (true) {
                val (pos, moves) = queue.removeAt(0)
                val (x, y) = pos
                for (
                next in
                listOf(
                    Triple(x, y - 1, 1L), Triple(x, y + 1, 2L),
                    Triple(x - 1, y, 3L), Triple(x + 1, y, 4L)
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
}

class Intcode(private val mem: MutableList<Long>) {
    private var base = 0
    private var ip = 0

    private fun arg(n: Int): Int =
        when (val mode = mem[ip].toInt() / 10.toDouble().pow(n + 1).toInt() % 10) {
            0 -> mem[ip + n].toInt()
            1 -> ip + n
            2 -> base + mem[ip + n].toInt()
            else -> error("bad mode $mode")
        }

    private operator fun get(n: Int): Long = mem.getOrElse(arg(n)) { 0 }

    private operator fun set(n: Int, value: Long) {
        val addr = arg(n)
        if (addr < mem.size) {
            mem[addr] = value
        } else {
            if (mem is ArrayList) {
                mem.ensureCapacity(addr + 1)
            }
            repeat(addr - mem.size) { mem.add(0L) }
            mem.add(value)
        }
    }

    fun runBlocking(input: Iterable<Long>): List<Long> = runBlocking {
        mutableListOf<Long>().apply {
            val inputIterator = input.iterator()
            while (true) {
                add(getOutput({ inputIterator.next() }) ?: break)
            }
        }
    }

    @Suppress("ComplexMethod")
    suspend fun getOutput(input: suspend () -> Long): Long? {
        while (true) {
            when (mem[ip].toInt() % 100) {
                1 -> {
                    this[3] = this[1] + this[2]
                    ip += 4
                }
                2 -> {
                    this[3] = this[1] * this[2]
                    ip += 4
                }
                3 -> {
                    this[1] = input()
                    ip += 2
                }
                4 -> {
                    val output = this[1]
                    ip += 2
                    return output
                }
                5 -> ip = if (this[1] != 0L) this[2].toInt() else ip + 3
                6 -> ip = if (this[1] == 0L) this[2].toInt() else ip + 3
                7 -> {
                    this[3] = if (this[1] < this[2]) 1 else 0
                    ip += 4
                }
                8 -> {
                    this[3] = if (this[1] == this[2]) 1 else 0
                    ip += 4
                }
                9 -> {
                    base += this[1].toInt()
                    ip += 2
                }
                99 -> return null
                else -> error("bad opcode ${mem[ip]}")
            }
        }
    }
}