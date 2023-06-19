package mpdev.springboot.aoc2019.solutions.day24

import java.awt.Point
import kotlin.math.pow

class BugTiles {

    companion object  {
        var MAX_X: Int = 0
        var MAX_Y: Int = 0
        val BUG = '#'
        val EMPTY = '.'
        lateinit var MID_TOP: Point
        lateinit var MID_BOT: Point
        lateinit var MID_LEFT: Point
        lateinit var MID_RGHT: Point
    }

    var tiles0 = mutableMapOf<Point,Boolean>()      // part1 2D
    var tiles3D = mutableMapOf<Int, MutableMap<Point,Boolean>>()    // part2 3D (multiple levels)

    fun initPanels2D(input: List<String>) {
        tiles0 = mutableMapOf()
        MAX_X = input[0].length
        MAX_Y = input.size
        MID_TOP = Point(MAX_X/2, MAX_Y/2-1)
        MID_BOT = Point(MAX_X/2, MAX_Y/2+1)
        MID_LEFT = Point(MAX_X/2-1, MAX_Y/2)
        MID_RGHT = Point(MAX_X/2+1, MAX_Y/2)
        for (y in input.indices) {
            for (x in input.first().indices)
                if (input[y][x] == BUG)
                    tiles0[Point(x,y)] = true
        }
    }

    fun initPanels3D(input: List<String>) {
        initPanels2D(input)
        tiles3D = mutableMapOf(0 to tiles0)
    }

    fun findRepeatedState() {
        val states = mutableSetOf<List<Int>>()
        do {
            states.add(getState2D())
            run1cycle2D()
        } while (!states.contains(getState2D()))
    }

    fun getBioDiversity() = getState2D().sumOf { 2.0.pow(it).toInt() }

    private fun getState2D() = tiles0.keys.map { it.y * MAX_Y + it.x }.sorted()

    fun run1cycle2D() {
        val prevState = tiles0.toMap()
        (0 until MAX_X).forEach { x ->
            (0 until MAX_Y).forEach { y ->
                val pos = Point(x, y)
                val adjacent = countAdjacentBugs2D(pos, prevState)
                if (prevState[pos] == true && adjacent != 1)
                    tiles0.remove(pos)
                else
                    if (prevState[pos] != true && (adjacent == 1 || adjacent == 2))
                        tiles0[pos] = true
            }
        }
    }

    fun run1cycle3D() {
        // add level above and level below
        if (countBugs2D_(tiles3D[tiles3D.keys.min()]) > 0)
            tiles3D[tiles3D.keys.min()-1] = mutableMapOf()
        if (countBugs2D_(tiles3D[tiles3D.keys.max()]) > 0)
            tiles3D[tiles3D.keys.max()+1] = mutableMapOf()
        val prevState = tiles3D.clone()
        tiles3D.keys.forEach { level ->
            val prevStateThisLevel = prevState[level]!!
            val tiles = tiles3D[level]!!
            (0 until MAX_X).forEach { x ->
                (0 until MAX_Y).forEach { y ->
                    if (x == MAX_X/2 && y == MAX_Y/2)
                        return@forEach
                    val pos = Point(x, y)
                    val adjacent = countAdjacentBugs3D(level, pos, prevState)
                    if (prevStateThisLevel[pos] == true && adjacent != 1)
                        tiles.remove(pos)
                    else
                        if (prevStateThisLevel[pos] != true && (adjacent == 1 || adjacent == 2))
                            tiles[pos] = true
                }
            }
        }
    }

    fun countBugs2D() = countBugs2D_(tiles0)

    fun countBugs3D() = tiles3D.values.sumOf { countBugs2D_(it) }

    private fun countBugs2D_(tiles: Map<Point,Boolean>?) = tiles?.size ?: 0

    fun countAdjacentBugs2D(pos: Point, tiles: Map<Point,Boolean>?): Int {
        var count = 0
        for ((adjX, adjY) in setOf(Pair(pos.x-1,pos.y), Pair(pos.x+1,pos.y), Pair(pos.x,pos.y-1), Pair(pos.x,pos.y+1)))
            if (tiles?.get(Point(adjX, adjY)) == true)
                ++count
        return count
    }

    fun countAdjacentBugs3D(level: Int, pos: Point, tiles3D: Map<Int, Map<Point,Boolean>>): Int {
        var count = countAdjacentBugs2D(pos, tiles3D[level])
        // check the perimeter and add numbers of bugs from level above
        count += getAdjacentFromLevelAbove(pos, tiles3D[level-1])
        // check around the middle tile and add numbers from level below
        count += getAdjacentFromLevelBelow(pos, tiles3D[level+1])
        return count
    }

    private fun getAdjacentFromLevelAbove(pos: Point, tiles: Map<Point,Boolean>?): Int {
        if (tiles == null)
            return 0
        var count = 0
        if (pos.y == 0)
            if (tiles[MID_TOP] == true)
                ++count
        if (pos.y == MAX_Y-1)
            if (tiles[MID_BOT] == true)
                ++count
        if (pos.x == 0)
            if (tiles[MID_LEFT] == true)
                ++count
        if (pos.x == MAX_X-1)
            if (tiles[MID_RGHT] == true)
                ++count
        return count
    }

    private fun getAdjacentFromLevelBelow(pos: Point, tiles: Map<Point,Boolean>?): Int {
        if (tiles == null)
            return 0
        var count = 0
        if (pos == MID_TOP)
            count += tiles.keys.count { it.y == 0 }
        if (pos == MID_BOT)
            count += tiles.keys.count { it.y == MAX_Y-1 }
        if (pos == MID_LEFT)
            count += tiles.keys.count { it.x == 0 }
        if (pos == MID_RGHT)
            count += tiles.keys.count { it.x == MAX_X-1 }
        return count
    }

    fun printGrid2D() {
        printGrid2D_(tiles0)
    }

    fun printGrid3D() {
        tiles3D.entries.sortedBy { it.key }.forEach {
            println("level: ${it.key}")
            printGrid2D_(it.value, recursive = true)
        }
    }

    private fun tiles2Grid(tiles: Map<Point,Boolean>): Array<CharArray> {
        val grid: Array<CharArray> = Array(MAX_Y) { CharArray(MAX_X) { EMPTY } }
        tiles.keys.forEach { pos -> grid[pos.y][pos.x] = BUG }
        return grid
    }

    private fun printGrid2D_(tiles: Map<Point,Boolean>, recursive: Boolean = false) {
        val grid = tiles2Grid(tiles)
        if (recursive)
            grid[MAX_Y/2][MAX_X/2] = '?'
        for (y in grid.indices) {
            for (x in grid.first().indices)
                print("${grid[y][x]}")
            println()
        }
        println()
    }

    private fun Map<Int, Map<Point,Boolean>>.clone(): Map<Int, Map<Point,Boolean>> {
        val clone = mutableMapOf<Int, Map<Point,Boolean>>()
        this.forEach { entry -> clone[entry.key] = entry.value.toMap() }
        return clone
    }

}