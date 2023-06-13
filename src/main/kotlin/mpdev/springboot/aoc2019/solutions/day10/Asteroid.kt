package mpdev.springboot.aoc2019.solutions.day10

import mpdev.springboot.aoc2019.utils.minus
import java.awt.Point

data class Asteroid(val absPos: Point) {

    var relPos = Point(0,0)
    var angle: Angle = Angle()
    var visibleCount = 0

    fun calcRelPos(refPoint: Point) {
        relPos = absPos - refPoint
    }

    fun calcRelAngle() {
        angle.tanPhi = if (relPos.x == 0) Double.MAX_VALUE else relPos.y.toDouble() / relPos.x.toDouble()
        angle.quarter = Quarter.fromXY(relPos.x, relPos.y)
    }

    override fun toString() =
        "Asteroid(absPos=[${absPos.x},${absPos.y}], relPos=[${relPos.x},${relPos.y}], angle=$angle, visible=$visibleCount)"
}

data class Angle(var quarter: Quarter = Quarter.N, var tanPhi: Double = 0.0)

enum class Quarter {
    A, B, C, D, N;
    companion object {
        fun fromXY(x: Int, y: Int) =
            when {
                x >= 0 && y >= 0 -> A
                x < 0 && y >= 0  -> B
                x < 0 && y < 0   -> C
                else             -> D
            }
    }
}