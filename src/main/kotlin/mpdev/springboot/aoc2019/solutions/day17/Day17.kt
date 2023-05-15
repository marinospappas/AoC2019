package mpdev.springboot.aoc2019.solutions.day17

import mpdev.springboot.aoc2019.model.PuzzlePartSolution
import mpdev.springboot.aoc2019.solutions.PuzzleSolver
import mpdev.springboot.aoc2019.solutions.icvm.InputOutput.getOutputValues
import mpdev.springboot.aoc2019.solutions.icvm.InputOutput.getOutputValuesAscii
import mpdev.springboot.aoc2019.solutions.icvm.InputOutput.setInputValuesAscii
import mpdev.springboot.aoc2019.solutions.icvm.Program
import org.springframework.stereotype.Component
import kotlin.concurrent.thread
import kotlin.system.measureTimeMillis

@Component
class Day17: PuzzleSolver() {

    final override fun setDay() {
        day = 17         ////// update this when a puzzle solver for a new day is implemented
    }

    init {
        setDay()
    }

    var result = 0

    private val inputStringsPart2 = arrayOf(
        "A,B,B,A,B,C,A,C,B,C\n",
        "L,4,L,6,L,8,L,12\n",
        "L,8,R,12,L,12\n",
        "R,12,L,6,L,6,L,8\n",
        "n\n"
    )

    override fun initSolver() {}

    override fun solvePart1(): PuzzlePartSolution {
        log.info("solving day $day part 1")
        val program = Program(inputData[0])
        val elapsed = measureTimeMillis {
            thread(start = true, name = "vacuum-robot-0") {    // when input/output is required the intCode must run in a separate thread
                program.run()
            }.join()
            val output = getOutputValuesAscii().trim('\n')
            val asciiProcessor = AsciiProcessor(output)
            result = asciiProcessor.process()
        }
        return PuzzlePartSolution(1, result.toString(), elapsed)
    }

    override fun solvePart2(): PuzzlePartSolution {
        log.info("solving day $day part 2")
        result = 0
        val program = Program(inputData[0])
        program.setMemory(0, 2)
        val elapsed = measureTimeMillis {
            val t1 = thread(start = true, name = "vacuum-robot-0") {    // when input/output is required the intCode must run in a separate thread
                program.run()
            }
            inputStringsPart2.forEach { s -> setInputValuesAscii(s) }
            val t2 = thread(start = true, name = "output-thread-0") {   // thread that collects the output - exits when non-ascii char received
                while (true) {
                    result = getOutputValues(clearChannel = false).last().toInt()
                    if (result > 127)
                        break
                    print(getOutputValuesAscii())
                }
            }
            t1.join()
            t2.join()
        }
        return PuzzlePartSolution(2, result.toString(), elapsed)
    }

}