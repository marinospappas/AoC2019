package mpdev.springboot.aoc2019.solutions.day16

import mpdev.springboot.aoc2019.utils.lastDigit
import kotlin.math.abs

class Fft(input: String) {

    var signal = input.toCharArray().map { it.digitToInt() }.toIntArray()
    private val signalLength = signal.size
    private val basePattern = intArrayOf(0, 1, 0, -1)

    var patterns = Array<Pair<Set<Int>, Set<Int>>>(signalLength) { Pair(setOf(), setOf()) }

    fun initPatterns() {
        signal.indices.forEach { patterns[it] = calculatePatternForIndex(it) }
    }

    fun calculatePatternForIndex(index: Int): Pair<Set<Int>, Set<Int>> {
        val posIndices = mutableSetOf<Int>()
        val negIndices = mutableSetOf<Int>()
        var baseIndex = 0
        var signalIndex = -1
        mainloop@while (true) {
            for (i in 0 .. index) {
                if (signalIndex >= 0) {
                    if (basePattern[baseIndex] == 1)
                        posIndices.add(signalIndex)
                    else if (basePattern[baseIndex] == -1)
                        negIndices.add(signalIndex)
                }
                if (++signalIndex >= signalLength)
                    break@mainloop
            }
            if (++baseIndex >= basePattern.size)
                baseIndex = 0
        }
        return Pair(posIndices, negIndices)
    }

    fun transform() {
        val output = IntArray(signalLength)
        output.indices.forEach {
            output[it] = transformDigit(patterns[it])
        }
        signal = output
    }

    private fun transformDigit(transformation: Pair<Set<Int>, Set<Int>>): Int {
        var resultPlus = 0
        var resultMinus = 0
        val posIndices = transformation.first
        val negIndices = transformation.second
        posIndices.forEach { resultPlus += signal[it] }
        negIndices.forEach { resultMinus += signal[it] }
        return abs(resultPlus - resultMinus) % 10
    }

    fun fastTransform() {
        // by Todd Ginsberg
        signal.indices.reversed().fold(0) { carry, idx ->
            (signal[idx] + carry).lastDigit().also { signal[idx] = it }
        }
    }
}