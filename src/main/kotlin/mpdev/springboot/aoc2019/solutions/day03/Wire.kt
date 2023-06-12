package mpdev.springboot.aoc2019.solutions.day03

import mpdev.springboot.aoc2019.utils.AocException
import mpdev.springboot.aoc2019.utils.manhattan
import java.awt.Point

class Wire(val path: String) {

    val pointsList = mutableSetOf(Point(0,0))

    init {
        path.split(",").forEach { addPoints(it) }
    }

    private fun addPoints(wireSegment: String) {
        val lastPoint = pointsList.last()
        val direction = wireSegment.first()
        val length = wireSegment.substring(1).toInt()
        (1..length).forEach {
            val dir = Direction.fromChar(direction)
            pointsList.add(Point(lastPoint.x + it * dir.xIncr, lastPoint.y + it * dir.yIncr))
        }
    }

    fun calculateDistanceToPoints(): Map<Point,Int> {
        var previousPt = Point(0, 0)
        var prevDist = 0
        val distances = mutableMapOf<Point,Int>()
        pointsList.forEach { point ->
            if (distances[point] == null)
                distances[point] = prevDist + point.manhattan(previousPt)
            prevDist += point.manhattan(previousPt)
            previousPt = Point(point.x, point.y)
        }
        return distances
    }
}

enum class Direction(val xIncr: Int, val yIncr: Int) {
    U(0,1),
    D(0,-1),
    R(1,0),
    L(-1,0);

    companion object {
        fun fromChar(direction: Char): Direction {
            Direction.values().forEach { if (it.name.first() == direction) return it }
            throw AocException("invalid direction of wire $direction")
        }
    }
}