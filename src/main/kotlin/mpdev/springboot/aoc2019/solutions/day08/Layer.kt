package mpdev.springboot.aoc2019.solutions.day08

import mpdev.springboot.aoc2019.utils.AocException

class Layer(inputLayer: String = "") {

    companion object {
        const val TRANSPARENT = '2'
        const val BLACK = '0'
    }

    private val pixels = Array(Image.HEIGHT) { charArrayOf() }

    init {
        if (inputLayer.isNotEmpty()) {
            if (inputLayer.length != Image.WIDTH * Image.HEIGHT)
                throw AocException("bad input layer $inputLayer")
            (0 until Image.HEIGHT).forEach {
                pixels[it] = inputLayer.substring(it * Image.WIDTH, (it + 1) * Image.WIDTH).toCharArray()
            }
        }
    }

    fun countDigit(digit: Int): Int {
        var count = 0
        pixels.forEach { count += it.count { c -> c.digitToInt() == digit } }
        return count
    }

    fun overlay(layerOnTop: Layer): Layer {
        val resLayer = Layer()
        (0 until Image.HEIGHT).forEach { y ->
            val row = CharArray(Image.WIDTH)
            (0 until Image.WIDTH).forEach { x ->
                row[x] = if (layerOnTop.pixels[y][x] == TRANSPARENT) this.pixels[y][x] else layerOnTop.pixels[y][x]
            }
            resLayer.pixels[y] = row
        }
        return resLayer
    }

    fun print() {
        (0 until Image.HEIGHT).forEach { y ->
            (0 until Image.WIDTH).forEach { x ->
               print(if (pixels[y][x] == BLACK) "  " else "██")
            }
            println()
        }
    }
}