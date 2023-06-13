package mpdev.springboot.aoc2019.day12

import mpdev.springboot.aoc2019.input.InputDataReader
import mpdev.springboot.aoc2019.solutions.day12.Day12
import mpdev.springboot.aoc2019.solutions.day12.Point3D
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Order
import org.junit.jupiter.api.Test

class Day12Test {

    private val day = 12                                     ///////// Update this for a new dayN test
    private val puzzleSolver = Day12()                        ///////// Update this for a new dayN test
    private val inputDataReader = InputDataReader("src/test/resources/inputdata/input")
    private var inputLines: MutableList<String> = inputDataReader.read(day).toMutableList()

    @BeforeEach
    fun setup() {
        puzzleSolver.setDay()
        puzzleSolver.inputData = inputLines
        puzzleSolver.initSolver()
    }

    @Test
    @Order(1)
    fun `Sets Day correctly`() {
        assertThat(puzzleSolver.day).isEqualTo(day)
    }

    @Test
    @Order(2)
    fun `Reads and Processes Input Correctly`() {
        puzzleSolver.moons.forEach { println(it) }
        assertThat(puzzleSolver.moons.size).isEqualTo(4)
    }

    @Test
    @Order(3)
    fun `Applies Gravity and Velocity`() {
        // after 1 step
        puzzleSolver.applyGravity()
        puzzleSolver.moons.forEach { moon -> moon.applyVelocity() }
        assertThat(puzzleSolver.moons[0].position).isEqualTo(Point3D(2,-1,1))
        assertThat(puzzleSolver.moons[0].velocity).isEqualTo(Point3D(3,-1,-1))
        assertThat(puzzleSolver.moons[1].position).isEqualTo(Point3D(3,-7,-4))
        assertThat(puzzleSolver.moons[1].velocity).isEqualTo(Point3D(1,3,3))
        assertThat(puzzleSolver.moons[2].position).isEqualTo(Point3D(1,-7,5))
        assertThat(puzzleSolver.moons[2].velocity).isEqualTo(Point3D(-3,1,-3))
        assertThat(puzzleSolver.moons[3].position).isEqualTo(Point3D(2,2,0))
        assertThat(puzzleSolver.moons[3].velocity).isEqualTo(Point3D(-1,-3,1))
        // after 10 steps
        repeat(9) {
            puzzleSolver.applyGravity()
            puzzleSolver.moons.forEach { moon -> moon.applyVelocity() }
        }
        assertThat(puzzleSolver.moons[0].position).isEqualTo(Point3D(2,1,-3))
        assertThat(puzzleSolver.moons[0].velocity).isEqualTo(Point3D(-3,-2,1))
        assertThat(puzzleSolver.moons[1].position).isEqualTo(Point3D(1,-8,0))
        assertThat(puzzleSolver.moons[1].velocity).isEqualTo(Point3D(-1,1,3))
        assertThat(puzzleSolver.moons[2].position).isEqualTo(Point3D(3,-6,1))
        assertThat(puzzleSolver.moons[2].velocity).isEqualTo(Point3D(3,2,-3))
        assertThat(puzzleSolver.moons[3].position).isEqualTo(Point3D(2,0,4))
        assertThat(puzzleSolver.moons[3].velocity).isEqualTo(Point3D(1,-1,-1))
    }

    @Test
    @Order(4)
    fun `Calculates Energy`() {
        repeat(10) {
            puzzleSolver.applyGravity()
            puzzleSolver.moons.forEach { moon -> moon.applyVelocity() }
        }
        assertThat(puzzleSolver.moons[0].calculateEnergy()).isEqualTo(36)
        assertThat(puzzleSolver.moons[1].calculateEnergy()).isEqualTo(45)
        assertThat(puzzleSolver.moons[2].calculateEnergy()).isEqualTo(80)
        assertThat(puzzleSolver.moons[3].calculateEnergy()).isEqualTo(18)
    }

    @Test
    @Order(5)
    fun `Solves Part 1`() {
        puzzleSolver.inputData = listOf(
            "Io <x=-8, y=-10, z=0>",
            "Europa <x=5, y=5, z=10>",
            "Ganymedes <x=2, y=-7, z=3>",
            "Callisto <x=9, y=-8, z=-3>"
        )
        puzzleSolver.initSolver()
        puzzleSolver.STEPS = 100
        puzzleSolver.solvePart1()
        assertThat(puzzleSolver.result).isEqualTo(1940)
    }

    @Test
    @Order(6)
    fun `Solves Part 2`() {
        puzzleSolver.solvePart2()
    }
}
