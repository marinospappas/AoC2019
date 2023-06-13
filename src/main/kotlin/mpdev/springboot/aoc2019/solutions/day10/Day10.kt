package mpdev.springboot.aoc2019.solutions.day10

import mpdev.springboot.aoc2019.model.PuzzlePartSolution
import mpdev.springboot.aoc2019.solutions.PuzzleSolver
import org.springframework.stereotype.Component
import java.awt.Point
import kotlin.system.measureTimeMillis

@Component
class Day10: PuzzleSolver() {

    final override fun setDay() {
        day = 10         ////// update this when a puzzle solver for a new day is implemented
    }

    init {
        setDay()
    }

    var asteroids = mutableListOf<Asteroid>()
    var result = 0

    override fun initSolver() {
        asteroids = mutableListOf()
        inputData.indices.forEach { y ->
            inputData[y].toCharArray().indices.forEach { x ->
                if (inputData[y].toCharArray()[x] == '#')
                    asteroids.add(Asteroid(Point(x,y)))
            }
        }
    }

    override fun solvePart1(): PuzzlePartSolution {
        result = 0
        val elapsed = measureTimeMillis {
            asteroids.forEach { a -> calculateVisibleAsteroids(a) }
            asteroids.forEach { println(it) }
            result = asteroids.maxOf { a -> a.visibleCount }
        }
        return PuzzlePartSolution(1, result.toString(), elapsed)
    }

    override fun solvePart2(): PuzzlePartSolution {
        result = 0
        val elapsed = measureTimeMillis {
        }
        return PuzzlePartSolution(2, result.toString(), elapsed)
    }

    fun calculateVisibleAsteroids(fromAsteroid: Asteroid) {
        asteroids.forEach { a -> a.calcRelPos(fromAsteroid.absPos); a.calcRelAngle() }
        //println((asteroids - fromAsteroid).map { it.angle })
        //println( HashSet((asteroids - fromAsteroid).map { it.angle }))
        fromAsteroid.visibleCount = HashSet((asteroids - fromAsteroid).map { it.angle }).size
    }
}