package mpdev.springboot.aoc2019.solutions.day11

import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import mpdev.springboot.aoc2019.model.PuzzlePartSolution
import mpdev.springboot.aoc2019.solutions.PuzzleSolver
import mpdev.springboot.aoc2019.solutions.icvm.ICVM
import org.springframework.stereotype.Component
import kotlin.system.measureTimeMillis

@Component
class Day11: PuzzleSolver() {

    final override fun setDay() {
        day = 11         ////// update this when a puzzle solver for a new day is implemented
    }

    init {
        setDay()
    }

    var result = 0
    override fun initSolver() {}

    private var controller = RobotController()

    override fun solvePart1(): PuzzlePartSolution {
        log.info("solving day $day part 1")
        val icvm = ICVM(inputData[0])
        controller.initPanels()
        var elapsed: Long = 0L
        //TODO: fix part1
        /*
        runBlocking {
            elapsed = measureTimeMillis {
                val job = launch { icvm.runProgram() }
                guideRobot(icvm)
                icvm.waitProgram(job)
            }
        }
        */
        result = controller.countPanels()
        return PuzzlePartSolution(1, result.toString(), elapsed)
    }

    override fun solvePart2(): PuzzlePartSolution {
        log.info("solving day $day part 2")
        val icvm = ICVM(inputData[0])
        controller.initPanels(2)
        var elapsed: Long
        runBlocking {
            elapsed = measureTimeMillis {
                val job = launch { icvm.runProgram() }
                guideRobot(icvm)
                icvm.waitProgram(job)
            }
        }
        controller.printGrid()
        result = controller.countPanels()
        return PuzzlePartSolution(2, result.toString(), elapsed)
    }

    suspend fun guideRobot(icvm: ICVM) {
        while (true) {
            val input = controller.getInputForRobot()
            icvm.setProgramInput(input)
            val output = icvm.getProgramOutput()
            if (output.isNotEmpty())
                controller.receiveRobotOutput(output)
            if (!icvm.programIsRunning())
                break
        }
    }
}