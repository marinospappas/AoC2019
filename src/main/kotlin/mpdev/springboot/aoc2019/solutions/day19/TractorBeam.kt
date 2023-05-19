package mpdev.springboot.aoc2019.solutions.day19

import java.awt.Point

class TractorBeam {

    companion object {
        const val MAX_X = 50
        const val MAX_Y = 50
    }

    private val beamPoints = mutableListOf<Point>()

    fun numberOfPointsInBeam() = beamPoints.count()

    fun areaPoints(): List<Point> {
        val listOfPoints = mutableListOf<Point>()
        (0 until MAX_X).forEach { x ->
            (0 until MAX_Y).forEach { y ->
                listOfPoints.add(Point(x,y))
            }
        }
        return listOfPoints
    }

    fun addBeamPoint(p: Point) {
        beamPoints.add(p)
    }
}