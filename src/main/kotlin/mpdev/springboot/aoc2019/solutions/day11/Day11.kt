package mpdev.springboot.aoc2019.solutions.day11

import mpdev.springboot.aoc2019.model.PuzzlePartSolution
import mpdev.springboot.aoc2019.solutions.PuzzleSolver
import mpdev.springboot.aoc2019.solutions.icvm.InputOutput.initInputOutput
import mpdev.springboot.aoc2019.solutions.icvm.InputOutput.getOutputValues
import mpdev.springboot.aoc2019.solutions.icvm.ICProgram
import mpdev.springboot.aoc2019.solutions.icvm.InputOutput.setInputValues
import org.springframework.stereotype.Component
import kotlin.concurrent.thread
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
        val outputValues = mutableListOf<Long>()
        initInputOutput()
        val program = ICProgram(inputData[0])
        controller.initPanels()
        val elapsed = measureTimeMillis {
            val t1 = thread(start = true, name = "paint-robot-0") {    // when input/output is required the intCode must run in a separate thread
                program.run()
            }
            val t2 = thread(start = true, name = "output-thread-0") {   // thread that collects the output - exits when non-ascii char received
                getRobotOutput(outputValues)
            }
            guideRobot(outputValues, t1)
            t1.join()
            t2.interrupt()
            result = controller.countPanels()
        }
        return PuzzlePartSolution(1, result.toString(), elapsed)
    }

    override fun solvePart2(): PuzzlePartSolution {
        log.info("solving day $day part 2")
        val outputValues = mutableListOf<Long>()
        initInputOutput()
        val program = ICProgram(inputData[0])
        controller.initPanels(2)
        val elapsed = measureTimeMillis {
            val t1 = thread(start = true, name = "paint-robot-0") {    // when input/output is required the intCode must run in a separate thread
                program.run()
            }
            val t2 = thread(start = true, name = "output-thread-0") {   // thread that collects the output - exits when non-ascii char received
                getRobotOutput(outputValues)
            }
            guideRobot(outputValues, t1)
            t1.join()
            t2.interrupt()
            controller.printGrid()
            result = controller.countPanels()
        }
        return PuzzlePartSolution(2, result.toString(), elapsed)
    }

    private fun getRobotOutput(outputValues: MutableList<Long>) {
        try {
            while (true) {
                outputValues.addAll(getOutputValues())
            }
        } catch (e: InterruptedException) {
            log.info("output thread exiting")
        }
    }

    fun guideRobot(data: MutableList<Long>, gameThread: Thread) {
        while (true) {
            val robotInput = listOf(controller.getInputForRobot().toLong())
            setInputValues(robotInput)
            if (!sendOutputToController(gameThread, data))
                return
        }
    }

    private fun sendOutputToController(gameThread: Thread, output: MutableList<Long>): Boolean {
        Thread.sleep(1)
        while (gameThread.state == Thread.State.RUNNABLE) {     // game thread state WAIT = no more output
            Thread.sleep(1)
        }
        if (gameThread.state == Thread.State.TERMINATED)     // if output.isEmpty
            return false
        controller.receiveRobotOutput(output.toList().map { it.toInt() })
        output.clear()
        return true
    }
}