package mpdev.springboot.aoc2019.solutions.day18

import mpdev.springboot.aoc2019.model.PuzzlePartSolution
import mpdev.springboot.aoc2019.solutions.PuzzleSolver
import mpdev.springboot.aoc2019.utils.Dijkstra
import org.springframework.stereotype.Component
import java.io.File
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
    lateinit var vault2: VaultPart2
    var result = 0

    override fun initSolver() {
        vault = Vault(inputData)
        vault2 = VaultPart2(File("src/main/resources/inputdata/input18-2.txt").readLines())
    }

    override fun solvePart1(): PuzzlePartSolution {
        vault.printVault()
        vault.createGraph()
        val algo = Dijkstra(vault.graph.costs)
        result = 0
        val elapsed = measureTimeMillis {
            val res = algo.runIt(vault.getStart(), { id -> vault.atEnd(id) })
            result = res.minCost
            log.info("Dijkstra iterations: {}", res.numberOfIterations)
            log.info("getNeighbours ran {} times for {} msec", vault.countGetNeighbours, vault.totalElapsed)
            log.info("neighbours (level 2) cache size: {} cache hits: {}", vault.neighboursCache.size, vault.countGetNeighbours-vault.countFindNeighbours)
            log.info("key graph (level 1) cache size: {} cache hits: {}", vault.keysGraphCache.size, vault.countFindNeighbours-vault.countCalcNeighbours)
            res.path.forEach { log.info("path: {}",it) }
        }
        return PuzzlePartSolution(1, result.toString(), elapsed)
    }

    override fun solvePart2(): PuzzlePartSolution {
        vault2.printVault()
        vault2.createGraph2()
        val algo = Dijkstra(vault2.graph2.costs)
        result = 0
        val elapsed = measureTimeMillis {
            val res = algo.runIt(vault2.getStart2(), { id -> vault2.atEnd(id) })
            result = res.minCost
            log.info("Dijkstra iterations: {}", res.numberOfIterations)
            log.info("getNeighbours ran {} times for {} msec", vault.countGetNeighbours, vault.totalElapsed)
            log.info("neighbours (level 2) cache size: {} cache hits: {}", vault.neighboursCache.size, vault.countGetNeighbours-vault.countFindNeighbours)
            log.info("key graph (level 1) cache size: {} cache hits: {}", vault.keysGraphCache.size, vault.countFindNeighbours-vault.countCalcNeighbours)
            res.path.forEach { log.info("path: {}",it) }
        }
        return PuzzlePartSolution(2, result.toString(), elapsed)
    }

}