package mpdev.springboot.aoc2019.solutions.day20

import mpdev.springboot.aoc2019.model.PuzzlePartSolution
import mpdev.springboot.aoc2019.solutions.PuzzleSolver
import mpdev.springboot.aoc2019.utils.Dijkstra
import org.springframework.stereotype.Component
import java.awt.Point
import kotlin.system.measureTimeMillis

@Component
class Day20: PuzzleSolver() {

    final override fun setDay() {
        day = 20         ////// update this when a puzzle solver for a new day is implemented
    }

    init {
        setDay()
    }

    lateinit var maze: Maze
    var DEPTH_LIMIT = 50
    var result = 0

    override fun initSolver() {
        maze = Maze(inputData)
    }

    override fun solvePart1(): PuzzlePartSolution {
        maze.createGraphP1()
        val algo = Dijkstra<Point>()
        result = 0
        val elapsed = measureTimeMillis {
            val res = algo.runIt(maze.getStartP1(), maze.getEndP1())
            result = res.minCost
        }
        return PuzzlePartSolution(1, result.toString(), elapsed)
    }

    override fun solvePart2(): PuzzlePartSolution {
        maze.createGraphP2()
        maze.depthLimit = DEPTH_LIMIT
        val algo = Dijkstra<Pair<Int,Point>>()
        result = 0
        val elapsed = measureTimeMillis {
            val res = algo.runIt(maze.getStartP2(), maze.getEndP2())
            result = res.minCost
        }
        return PuzzlePartSolution(2, result.toString(), elapsed)
    }

}