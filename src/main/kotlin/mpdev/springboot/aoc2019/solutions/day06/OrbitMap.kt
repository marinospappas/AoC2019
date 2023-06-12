package mpdev.springboot.aoc2019.solutions.day06

import mpdev.springboot.aoc2019.utils.AocException

class OrbitMap {

    val map = mutableMapOf<String,String>()

    fun addToMap(item: String) {
        val items = item.split(")")
        if (items.size < 2)
            throw AocException("bad input line $item")
        map[items[1]] = items[0]
    }

    fun countOrbits(name: String): Int {
        var orbits = 0
        var key = name
        var value: String?
        while (map[key].also { value = it } != null) {
            ++orbits
            key = value!!
        }
        return orbits
    }

    fun getStart() = map["YOU"]!!

    fun getEnd() = map["SAN"]!!
}