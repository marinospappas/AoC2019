package mpdev.springboot.aoc2019.solutions.day17

class AsciiProcessor(data: String) {

    var grid: Array<CharArray>

    init {
        val dataArray = data.split("\n")
        grid = Array(dataArray.size) { i -> dataArray[i].toCharArray() }
    }

    fun process(): Int {
        var result = 0
        for (i in grid.indices)
            for (j in grid[i].indices) {
                if (isIntersection(i, j))
                    result += i * j
            }
        return result
    }

    private fun isIntersection(y: Int, x: Int): Boolean {
        if (grid[y][x] != '#')
            return false
        if (y == 0 || y == grid.size-1 || x == 0 || x == grid.first().size-1)
            return false
        if (grid[y-1][x] == '#' && grid[y+1][x] == '#' && grid[y][x-1] == '#' && grid[y][x+1] == '#')
            return true
        return false
    }
}