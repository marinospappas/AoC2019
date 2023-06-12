package mpdev.springboot.aoc2019.solutions.day06

import mpdev.springboot.aoc2019.model.PuzzlePartSolution
import mpdev.springboot.aoc2019.solutions.PuzzleSolver
import mpdev.springboot.aoc2019.utils.Dijkstra
import mpdev.springboot.aoc2019.utils.Graph
import org.springframework.stereotype.Component
import kotlin.system.measureTimeMillis

@Component
class Day06: PuzzleSolver() {

    final override fun setDay() {
        day = 6         ////// update this when a puzzle solver for a new day is implemented
    }

    init {
        setDay()
    }

    val orbitMap = OrbitMap()

    var result = 0

    override fun initSolver() {
        inputData.forEach { orbitMap.addToMap(it) }
    }

    override fun solvePart1(): PuzzlePartSolution {
        result = 0
        val elapsed = measureTimeMillis {
            result = orbitMap.map.keys.sumOf { orbitMap.countOrbits(it) }
        }
        return PuzzlePartSolution(1, result.toString(), elapsed)
    }

    override fun solvePart2(): PuzzlePartSolution {
        val graph = Graph<String>()
        orbitMap.map.keys.forEach { graph.addNode(it) }
        graph.addNode("COM")
        orbitMap.map.forEach { (k,v) -> graph.connectBothWays(k, v) }
        val algo = Dijkstra<String>()
        result = 0
        val elapsed = measureTimeMillis {
            val res = algo.runIt(graph[orbitMap.getStart()], graph[orbitMap.getEnd()])
            result = res.minCost
        }
        return PuzzlePartSolution(2, result.toString(), elapsed)
    }

}