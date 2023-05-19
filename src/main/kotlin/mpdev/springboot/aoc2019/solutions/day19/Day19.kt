package mpdev.springboot.aoc2019.solutions.day19

import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import mpdev.springboot.aoc2019.model.PuzzlePartSolution
import mpdev.springboot.aoc2019.solutions.PuzzleSolver
import mpdev.springboot.aoc2019.solutions.icvm.ICVM
import org.springframework.stereotype.Component
import kotlin.system.measureTimeMillis

@Component
class Day19: PuzzleSolver() {

    final override fun setDay() {
        day = 19         ////// update this when a puzzle solver for a new day is implemented
    }

    init {
        setDay()
    }

    var result = 0
    override fun initSolver() {}
    private lateinit var beam: TractorBeam

    override fun solvePart1(): PuzzlePartSolution {
        log.info("solving day $day part 1")
        beam = TractorBeam()
        var elapsed: Long
        runBlocking {
            elapsed = measureTimeMillis {
                var count = 0
                beam.areaPoints().forEach { point ->
                    val icvm = ICVM(inputData[0])
                    val job = launch { icvm.runProgram() }
                    icvm.setProgramInput(listOf(point.x, point.y))
                    val droneFeedback = icvm.getProgramOutput().first()
                    job.cancel()
                    icvm.waitProgram(job)
                    if (droneFeedback == 1) {
                        beam.addBeamPoint(point)
                        ++count
                    }
                    if (count % 10 == 0)
                        log.info("found so far {} points in the beam", count)
                }
            }
        }
        result = beam.numberOfPointsInBeam()
        return PuzzlePartSolution(1, result.toString(), elapsed)
    }

    override fun solvePart2(): PuzzlePartSolution {
        log.info("solving day $day part 2")
        val icvm = ICVM(inputData[0])
        icvm.setProgramMemory(0, 2)
        var elapsed: Long
        runBlocking {
            elapsed = measureTimeMillis {
                //val job = launch { icvm.runProgram() }

                //icvm.waitProgram(job)
            }
        }
        return PuzzlePartSolution(2, result.toString(), elapsed)
    }

}