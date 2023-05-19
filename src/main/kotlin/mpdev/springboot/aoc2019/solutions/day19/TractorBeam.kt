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

    private fun board2Grid(board: List<Point>): Array<CharArray> {
        val grid: Array<CharArray> = Array(MAX_X) { CharArray(MAX_X) { '.' } }
        board.forEach { point -> grid[point.y][point.x] = '#' }
        return grid
    }

    fun printBeam() {
        val grid = board2Grid(beamPoints)
        print("   ")
        for (i in grid.first().indices)
            print(if (i%10 == 0) i/10 else " ")
        println()
        print("   ")
        for (i in grid.first().indices)
            print(i%10)
        println()
        for (i in grid.indices) {
            print("${String.format("%2d",i)} ")
            for (j in grid.first().indices)
                print(grid[i][j])
            println()
        }
    }
}