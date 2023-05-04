package mpdev.springboot.aoc2019.utils

object AoCUtils {

    // this map overrides the natural sequence of puzzle solvers
    // when some puzzle for a later day has been solved before the puzzle(s) the day(s) before
    // when all puzzle solvers have been implemented, then the index of each solver is day-1
    private val indexMap: Map<Int,Int> = mapOf(
        1 to 0,
        2 to 1,
        4 to 2,
        5 to 3,
        9 to 4,
        17 to 5,
        25 to 6
    )

    fun getIndexOfDay(day: Int) = indexMap[day] ?: (day - 1)

}