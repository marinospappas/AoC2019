package mpdev.springboot.aoc2019.config

import mpdev.springboot.aoc2019.solutions.PuzzleSolver
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
class PuzzleSolversConfig {

    @Autowired
    lateinit var puzzleSolversList: List<PuzzleSolver>

    @Bean
    fun puzzleSolvers(): Map<Int,PuzzleSolver> {
        val solvers = mutableMapOf<Int,PuzzleSolver>()
        puzzleSolversList.forEach { puzzleSolver -> solvers[puzzleSolver.day] = puzzleSolver }
        return solvers
    }
}