package mpdev.springboot.aoc2019.solutions.day12

import mpdev.springboot.aoc2019.utils.AocException
import kotlin.math.abs
import kotlin.math.sign

data class Moon(val name: String, var position: Point3D, var velocity: Point3D = Point3D()) {

    private val initialPos = Point3D(position.x, position.y, position.z)
    private val initialVel = Point3D(velocity.x, velocity.y, velocity.z)

    fun init() {
        position = Point3D(initialPos.x, initialPos.y, initialPos.z)
        velocity = Point3D(initialVel.x, initialVel.y, initialVel.z)
    }

    fun xyzMatchesInitialState(i: Int) =
        position[i] == initialPos[i] && velocity[i] == initialVel[i]

    fun applyGravity(moonList: List<Moon>): Point3D {
        val newVelocity = Point3D(velocity.x, velocity.y, velocity.z)
        (moonList - this).forEach { otherMoon ->
            newVelocity.x += (otherMoon.position.x - this.position.x).sign
            newVelocity.y += (otherMoon.position.y - this.position.y).sign
            newVelocity.z += (otherMoon.position.z - this.position.z).sign
        }
        return newVelocity
    }

    fun applyVelocity() {
        position += velocity
    }

    fun calculateEnergy() =
        (abs(position.x) + abs(position.y) + abs(position.z)) * (abs(velocity.x) + abs(velocity.y) + abs(velocity.z))
}

data class Point3D(var x: Int = 0, var y: Int = 0, var z: Int = 0) {
    operator fun plus(other: Point3D) = Point3D(this.x + other.x, this.y + other.y, this.z + other.z)
    operator fun get(i: Int) =
        when (i) {
            0 -> x
            1 -> y
            2 -> z
            else -> throw AocException("invalid index for Point3D element: $i")
        }
}