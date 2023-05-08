package mpdev.springboot.aoc2019.solutions.day13

enum class Tile(val value: Int, val ascii: Char) {
    EMPTY(0, '.'),
    WALL(1, '+'),
    BLOCK(2, 'B'),
    HPADDLE(3, '='),
    BALL(4, 'o'),
    NONE(Int.MIN_VALUE, 'X');

    companion object {
        fun fromInt(intValue: Int): Tile {
            Tile.values().forEach { v -> if (v.value == intValue) return v }
            return NONE
        }
    }
}