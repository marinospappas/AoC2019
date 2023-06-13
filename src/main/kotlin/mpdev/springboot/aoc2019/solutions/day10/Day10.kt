package mpdev.springboot.aoc2019.solutions.day10

import mpdev.springboot.aoc2019.model.PuzzlePartSolution
import mpdev.springboot.aoc2019.solutions.PuzzleSolver
import org.springframework.stereotype.Component
import java.awt.Point
import kotlin.collections.HashSet
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
    lateinit var station: Asteroid
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
            station = asteroids.maxByOrNull { it.visibleCount }!!
            result = asteroids.maxOf { a -> a.visibleCount }
        }
        return PuzzlePartSolution(1, result.toString(), elapsed)
    }

    override fun solvePart2(): PuzzlePartSolution {
        result = 0
        val elapsed = measureTimeMillis {
            val asteroidMap = createAngularMap(station)
            val key200 = asteroidMap.keys.toList()[199]
            val asteroid200Pos = asteroidMap[key200]?.first()!! .absPos
            result = asteroid200Pos.x * 100 + asteroid200Pos.y
        }
        return PuzzlePartSolution(2, result.toString(), elapsed)
    }

    fun calculateVisibleAsteroids(fromAsteroid: Asteroid) {
        asteroids.forEach { a -> a.calcRelPos(fromAsteroid.absPos); a.calcRelVector() }
        fromAsteroid.visibleCount = HashSet((asteroids - fromAsteroid).map { it.angle }).size
    }

    fun createAngularMap(station: Asteroid): Map<Angle,List<Asteroid>> {
        asteroids.forEach { a -> a.calcRelPos(station.absPos); a.calcRelVector() }
        val asteroidMap = (asteroids - station).groupBy { it.angle }.toSortedMap()
        asteroidMap.forEach { (k,v) -> asteroidMap[k] = v.sortedBy { it.distance } }
        return asteroidMap
    }

}