package mpdev.springboot.aoc2019.solutions.day22

import mpdev.springboot.aoc2019.solutions.day22.Shuffle.*

class Deck(val numOfCards: Long = 10_007L) {

    lateinit var cards: List<Int>

    fun initDeck() {
        cards = Array(numOfCards.toInt()) { it }.toList()
    }

    fun dealToNewStack() {
        cards = cards.reversed()
    }

    fun cutNCards(n: Int) {
        if (n > 0) {
            cards = cards.subList(n, cards.size) + cards.subList(0, n)
        }
        if (n < 0) {
            cards = cards.subList(cards.size + n, cards.size) +
                    cards.subList(0, cards.size + n)
        }
    }

    fun dealWithIncrementN(n: Int) {
        val newCardDeck = Array(numOfCards.toInt()) { -1 }
        cards.indices.forEach {
            val newIndex = (it * n) % numOfCards.toInt()
            newCardDeck[newIndex] = cards[it]
        }
        cards = newCardDeck.toList()
    }

    fun executeShuffle(shuffle: Shuffling) {
        when (shuffle.operation) {
            DEAL_NEW_STACK -> dealToNewStack()
            CUT_N_CARDS -> cutNCards(shuffle.n)
            DEAL_W_INCREMENT -> dealWithIncrementN(shuffle.n)
        }
    }

    fun findPreviousIndex(index: Long, shuffle: Shuffling): Long {
        return when (shuffle.operation) {
            DEAL_NEW_STACK -> { numOfCards - 1 - index }
            CUT_N_CARDS -> { 0 }
            DEAL_W_INCREMENT -> {
                val round = index % shuffle.n
                round * shuffle.n + index / shuffle.n
            }
        }
    }

}