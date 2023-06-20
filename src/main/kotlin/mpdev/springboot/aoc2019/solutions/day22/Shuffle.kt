package mpdev.springboot.aoc2019.solutions.day22

enum class Shuffle {
    DEAL_NEW_STACK,
    CUT_N_CARDS,
    DEAL_W_INCREMENT;
}

data class Shuffling(val operation: Shuffle, val n : Int = 0)