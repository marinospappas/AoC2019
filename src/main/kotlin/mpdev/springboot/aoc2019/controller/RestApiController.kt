package mpdev.springboot.aoc2019.controller

import mpdev.springboot.aoc2019.model.PuzzleSolution
import mpdev.springboot.aoc2019.solutions.PuzzleSolver
import mpdev.springboot.aoc2019.utils.AoCUtils
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/aoc2019")
class RestApiController(@Autowired var puzzleSolvers: List<PuzzleSolver>) {

    @GetMapping("/day/{day}", produces = ["application/json"])
    fun getPuzzleSolution(@PathVariable day: Int): PuzzleSolution = puzzleSolvers[AoCUtils.getIndexOfDay(day)].solve()

}