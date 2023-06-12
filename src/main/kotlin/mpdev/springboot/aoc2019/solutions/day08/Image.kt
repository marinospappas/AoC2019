package mpdev.springboot.aoc2019.solutions.day08

import mpdev.springboot.aoc2019.solutions.day08.Image.Companion.HEIGHT
import mpdev.springboot.aoc2019.solutions.day08.Image.Companion.WIDTH
import mpdev.springboot.aoc2019.utils.AocException

class Image(input: String) {

    val layers = mutableListOf<Layer>()

    companion object {
        const val WIDTH = 25
        const val HEIGHT = 6
    }

    init {
        val layerSize = WIDTH * HEIGHT
        var s = input
        while (s.isNotEmpty()) {
            layers.add(Layer(s.substring(0,layerSize)))
            s = s.substring(layerSize)
        }
    }

}

class Layer(inputLayer: String) {

    val pixels = Array(HEIGHT) { charArrayOf() }

    init {
        if (inputLayer.length != WIDTH * HEIGHT)
            throw AocException("bad input layer $inputLayer")
        (0 until HEIGHT).forEach {
            pixels[it] = inputLayer.substring(it * WIDTH, (it+1)* WIDTH).toCharArray()
        }
    }

    fun countDigit(digit: Int): Int {
        var count = 0
        pixels.forEach { count += it.count { c -> c.digitToInt() == digit } }
        return count
    }
}