package mpdev.springboot.aoc2019.solutions.day08

class Image(input: String) {

    val layers = mutableListOf<Layer>()

    companion object {
        var WIDTH = 25
        var HEIGHT = 6
    }

    init {
        val layerSize = WIDTH * HEIGHT
        var s = input
        while (s.isNotEmpty()) {
            layers.add(Layer(s.substring(0,layerSize)))
            s = s.substring(layerSize)
        }
    }

    fun decode(): Layer {
        var image = layers.last()
        (layers.lastIndex-1 downTo 0).forEach {
            image = image.overlay(layers[it]) }
        return image
    }

}
