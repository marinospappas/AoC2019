package mpdev.springboot.aoc2019.solutions.day12

import kotlin.math.abs
import kotlin.math.sign

data class Moon(val name: String, var position: Point3D, var velocity: Point3D = Point3D()) {

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
}