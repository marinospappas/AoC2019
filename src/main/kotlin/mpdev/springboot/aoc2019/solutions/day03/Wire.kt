package mpdev.springboot.aoc2019.solutions.day03

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
        (1..length).forEach { i ->
            when (direction) {
                'U' -> pointsList.add(Point(lastPoint.x, lastPoint.y+i))
                'D' -> pointsList.add(Point(lastPoint.x, lastPoint.y-i))
                'R' -> pointsList.add(Point(lastPoint.x+i, lastPoint.y))
                'L' -> pointsList.add(Point(lastPoint.x-i, lastPoint.y))
            }
        }
    }
}