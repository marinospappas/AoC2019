package mpdev.springboot.aoc2019.solutions.day18

import mpdev.springboot.aoc2019.model.PuzzlePartSolution
import mpdev.springboot.aoc2019.solutions.PuzzleSolver
import mpdev.springboot.aoc2019.utils.Dijkstra
import org.springframework.stereotype.Component
import kotlin.system.measureTimeMillis

@Component
class Day18: PuzzleSolver() {

    final override fun setDay() {
        day = 18         ////// update this when a puzzle solver for a new day is implemented
    }

    init {
        setDay()
    }

    lateinit var vault: Vault
    var result = 0

    override fun initSolver() {
        vault = Vault(inputData)
    }

    override fun solvePart1(): PuzzlePartSolution {
        vault.createGraph()
        val algo = Dijkstra(vault.graph.costs)
        result = 0
        val elapsed = measureTimeMillis {
            val res = algo.runIt(vault.getStart(), { id -> vault.atEnd(id) })
            result = res.minCost
            log.info("Dijkstra iterations: {}", res.numberOfIterations)
            res.path.forEach { log.info("path: {}",it) }
        }
        return PuzzlePartSolution(1, result.toString(), elapsed)
    }

    override fun solvePart2(): PuzzlePartSolution {
        result = 0
        val elapsed = measureTimeMillis {
        }
        return PuzzlePartSolution(2, result.toString(), elapsed)
    }

}