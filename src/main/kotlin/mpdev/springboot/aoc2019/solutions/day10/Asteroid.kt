package mpdev.springboot.aoc2019.solutions.day10

import mpdev.springboot.aoc2019.utils.minus
import java.awt.Point

data class Asteroid(val absPos: Point) {

    var relPos = Point(0,0)
    var distance: Long = 0L
    var angle: Angle = Angle()
    var visibleCount = 0

    fun calcRelPos(refPoint: Point) {
        relPos = absPos - refPoint
    }

    fun calcRelVector() {
        distance = relPos.x.toLong() * relPos.x + relPos.y.toLong() * relPos.y
        angle.tanPhi = if (relPos.x != 0) relPos.y.toDouble() / relPos.x.toDouble()
                        else if (relPos.y >= 0) Double.MAX_VALUE else -Double.MAX_VALUE
        angle.quarter = Quarter.fromXY(relPos.x, relPos.y)
    }

    override fun toString() =
        "Asteroid(absPos=[${absPos.x},${absPos.y}], relPos=[${relPos.x},${relPos.y}], distance=$distance, angle=$angle, visible=$visibleCount)"
}

data class Angle(var quarter: Quarter = Quarter.N, var tanPhi: Double = 0.0): Comparable<Angle> {

    override fun compareTo(other: Angle): Int {
        if (quarter != other.quarter)
            return quarter.compareTo(other.quarter)
        else
            return compareTan(other)
    }

    private fun compareTan(other: Angle) = tanPhi.compareTo(other.tanPhi)
}

enum class Quarter {
    A, B, C, D, N;
    companion object {
        fun fromXY(x: Int, y: Int) =
            when {
                x >= 0 && y >= 0 -> B
                x < 0 && y >= 0  -> C
                x < 0 && y < 0   -> D
                else             -> A
            }
    }
}