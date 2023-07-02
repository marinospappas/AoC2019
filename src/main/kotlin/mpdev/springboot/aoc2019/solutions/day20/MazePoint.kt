package mpdev.springboot.aoc2019.solutions.day20

data class MazePoint(var mazeItem: MazeItem, var value: Char)

enum class MazeItem {
    EMPTY, WALL, CONNECTION, START, END
}