package mpdev.springboot.aoc2019.solutions.day16

import kotlin.math.abs

class Fft(input: String) {

    var signal = input.toCharArray().map { it.digitToInt() }.toIntArray()
    private val signalLength = signal.size
    private val basePattern = intArrayOf(0, 1, 0, -1)

    fun calculatePatternForIndex(index: Int): IntArray {
        val pattern = mutableListOf<Int>()
        var baseIndex = 0
        mainloop@while (true) {
            for (i in 0 .. index) {
                pattern.add(basePattern[baseIndex])
                if (pattern.size >= signalLength+1)
                    break@mainloop
            }
            if (++baseIndex >= basePattern.size)
                baseIndex = 0
        }
        return (pattern - pattern[0]).toIntArray()
    }

    fun transform() {
        val output = IntArray(signalLength)
        output.indices.forEach {
            output[it] = transformDigit(calculatePatternForIndex(it))
        }
        signal = output
    }

    private fun transformDigit(pattern: IntArray): Int {
        var result = 0
        signal.indices.forEach { result += signal[it] * pattern[it] }
        return abs(result) % 10
    }
}