package mpdev.springboot.aoc2019.solutions.day18

data class VaultPoint(var vaultItem: VaultItem, var value: Char)

enum class VaultItem {
    EMPTY, WALL, GATE, KEY, START
}