package mpdev.springboot.aoc2019.solutions

import mpdev.springboot.aoc2019.input.InputDataReader
import mpdev.springboot.aoc2019.model.PuzzlePartSolution
import mpdev.springboot.aoc2019.model.PuzzleSolution
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired

abstract class PuzzleSolver {

    protected val log: Logger = LoggerFactory.getLogger(this::class.java)

    @Autowired
    lateinit var inputDataReader: InputDataReader

    lateinit var inputData: List<String>

    var day: Int = 0

    fun solve(): PuzzleSolution {
        log.info("solver for day {} called", day)
        inputData = inputDataReader.read(day)
        initSolver()
        return PuzzleSolution(day = day, solution = setOf(solvePart1(), solvePart2()))
    }

    abstract fun setDay()
    abstract fun initSolver()
    abstract fun solvePart1(): PuzzlePartSolution
    abstract fun solvePart2(): PuzzlePartSolution

}