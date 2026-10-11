enum class HandClassTag(val score:Int) {
    ROYAL_FLUSH(10),
    STRAIGHT_FLUSH(9),
    FOUR_OF_A_KIND(8),
    FULL_HOUSE(7),
    FLUSH(6),
    STRAIGHT(5),
    THREE_OF_A_KIND(4),
    TWO_PAIR(3),
    PAIR(2),
    HIGH_CARD(1)
}

interface HandClass : Comparable<HandClass> {
    val tag:HandClassTag
    val combination:CombinationFive
    override fun equals(other: Any?): Boolean
    override fun compareTo(other: HandClass): Int = tag.score.compareTo(other.tag.score)
    fun isValidHand():Boolean
    override fun toString(): String
}