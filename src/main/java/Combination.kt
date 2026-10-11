import kotlin.math.PI

open class Combination (private val bigCombination:List<Card>) {    //will be composed of player hand and table hand
    init{
        assert(bigCombination.size <= 7) {throw IllegalArgumentException() }
    }

    fun revealAllFiverCombos():List<CombinationFive> = bigCombination.combinations(5).map {CombinationFive(it)}
    fun getCombination():HandClass = revealAllFiverCombos().maxOf {it.getClass()}
    override fun toString(): String = bigCombination.joinToString (", ")
}

class CombinationFive (val combination:List<Card>)
    :Combination(combination){
    init {
        assert(combination.size == 5) {throw IllegalArgumentException()}
    }
    val cardFreqs = combination.groupingBy { it.cardVal }.eachCount()
    fun getClass():HandClass=
        if (RoyalFlush(this).isValidHand()){
            RoyalFlush(this)
        } else if (StraightFlush(this).isValidHand()){
            StraightFlush(this)
        } else if (FourOfAKind(this).isValidHand()){
            FourOfAKind(this)
        } else if (FullHouse(this).isValidHand()){
            FullHouse(this)
        } else if (Flush(this).isValidHand()){
            Flush(this)
        } else if (Straight(this).isValidHand()){
            Straight(this)
        } else if (ThreeOfAKind(this).isValidHand()){
            ThreeOfAKind(this)
        } else if (TwoPair(this).isValidHand()){
            TwoPair(this)
        } else if (Pair(this).isValidHand()){
            Pair(this)
        } else HighCard(this)

    fun allSameSuit():Boolean = combination.all {it.suit == combination[0].suit}
    fun isSequence():Boolean{
        val sortedCombination = combination.sorted()
        for (i in 1..3){
            if (sortedCombination[i].cardVal.rank - sortedCombination[i-1].cardVal.rank != 1){
                return false
            }
        }
        return if (sortedCombination.last().cardVal == CardVal.ACE &&
            sortedCombination.first().cardVal == CardVal.TWO ){
            true
        }
        else {
            sortedCombination.last().cardVal.rank - sortedCombination[3].cardVal.rank == 1
        }
    }
    fun highestCard():Card = combination.max()
    fun has(value:CardVal):Boolean = combination.find {it.cardVal == value} != null
    fun sequenceHighest():Int{
        return if (highestCard().cardVal != CardVal.ACE){
            highestCard().cardVal.rank
        } else{
            if (has(CardVal.KING)) {
                14
            } else{
                1
            }
        }
    }
}

class RoyalFlush(override val combination: CombinationFive):HandClass{
    override val tag: HandClassTag = HandClassTag.ROYAL_FLUSH
    override fun isValidHand(): Boolean =
        combination.isSequence() && combination.allSameSuit()
                && combination.sequenceHighest() == 14

    override fun equals(other: Any?): Boolean = other is RoyalFlush

    override fun toString(): String = "ROYAL FLUSH!"
    override fun hashCode(): Int {
        var result = combination.hashCode()
        result = 31 * result + tag.hashCode()
        return result
    }
}

class StraightFlush(override val combination: CombinationFive) : HandClass{
    override val tag: HandClassTag = HandClassTag.STRAIGHT_FLUSH
    override fun isValidHand(): Boolean = combination.isSequence() && combination.allSameSuit()
    override fun compareTo(other: HandClass): Int =
        if (super.compareTo(other) == 0 && other is StraightFlush && isValidHand() && other.isValidHand()){
            combination.combination.compare(other.combination.combination)
        }
        else{
            super.compareTo(other)
        }

    override fun equals(other: Any?): Boolean = other is StraightFlush && compareTo(other) == 0

    override fun toString(): String = "STRAIGHT FLUSH (${combination.highestCard().cardVal.display})"
    override fun hashCode(): Int {
        var result = combination.hashCode()
        result = 31 * result + tag.hashCode()
        return result
    }
}

class FourOfAKind(override val combination: CombinationFive) : HandClass{
    override val tag: HandClassTag = HandClassTag.FOUR_OF_A_KIND
    private val maxCard = combination.cardFreqs.entries.find {it.value == 4}?.key
    override fun isValidHand(): Boolean = maxCard != null
    override fun equals(other: Any?): Boolean = other is FourOfAKind && compareTo(other) == 0
    override fun compareTo(other: HandClass): Int {
        if (super.compareTo(other) == 0 && other is FourOfAKind && isValidHand() && other.isValidHand()){
            val cardComp = maxCard!!.compareTo(other.maxCard!!)
            return if (cardComp == 0 )
                combination.cardFreqs.entries.find {it.key != maxCard}!!.key.rank
                    .compareTo(other.combination.cardFreqs.entries.find {it.key != maxCard}!!.key.rank)
                   else cardComp
        }
        else{
            return super.compareTo(other)
        }

    }

    override fun toString(): String = if (isValidHand()) "FOUR OF A KIND (${maxCard?.display})" else ""
    override fun hashCode(): Int {
        var result = combination.hashCode()
        result = 31 * result + tag.hashCode()
        result = 31 * result + (maxCard?.hashCode() ?: 0)
        return result
    }
}

class FullHouse(override val combination: CombinationFive) : HandClass{
    override val tag: HandClassTag = HandClassTag.FULL_HOUSE
    private val tripletCard = combination.cardFreqs.entries.find {it.value == 3}?.key
    private val pairCard = combination.cardFreqs.entries.find {it.value == 2}?.key
    override fun isValidHand(): Boolean = tripletCard != null && pairCard != null
    override fun equals(other: Any?): Boolean = other is FullHouse && compareTo(other) == 0
    override fun compareTo(other: HandClass): Int {
        if (super.compareTo(other) == 0 && other is FullHouse && isValidHand() && other.isValidHand()){
            val cardComp = tripletCard!!.rank.compareTo(other.tripletCard!!.rank)
            return if (cardComp == 0) pairCard!!.rank.compareTo(other.pairCard!!.rank)
                   else cardComp
        }
        else{
            return super.compareTo(other)
        }
    }
    override fun toString(): String = "FULL HOUSE (${tripletCard?.display}s full of ${pairCard?.display}s)"
    override fun hashCode(): Int {
        var result = combination.hashCode()
        result = 31 * result + tag.hashCode()
        result = 31 * result + (tripletCard?.hashCode() ?: 0)
        result = 31 * result + (pairCard?.hashCode() ?: 0)
        return result
    }
}

class Flush(override val combination: CombinationFive) : HandClass{
    override val tag: HandClassTag = HandClassTag.FLUSH
    override fun compareTo(other: HandClass): Int {
        return if (super.compareTo(other) == 0 && other is Flush && isValidHand() && other.isValidHand()){
            combination.combination.compare(other.combination.combination)
        } else{
            super.compareTo(other)
        }
    }
    override fun equals(other: Any?): Boolean = other is Flush && compareTo(other) == 0
    override fun isValidHand(): Boolean = combination.allSameSuit()

    override fun toString(): String = "FLUSH (${combination.highestCard().cardVal.display})"
    override fun hashCode(): Int {
        var result = combination.hashCode()
        result = 31 * result + tag.hashCode()
        return result
    }
}

class Straight(override val combination: CombinationFive) : HandClass{
    override val tag: HandClassTag = HandClassTag.STRAIGHT
    override fun isValidHand(): Boolean = combination.isSequence()
    override fun compareTo(other: HandClass): Int {
        return if (super.compareTo(other) == 0 && other is Straight && isValidHand() && other.isValidHand()){
            combination.combination.compare(other.combination.combination)
        }
        else{
            super.compareTo(other)
        }
    }
    override fun equals(other: Any?): Boolean = other is Straight && compareTo(other) == 0
    override fun toString(): String = "STRAIGHT (${combination.highestCard().cardVal.display})"
    override fun hashCode(): Int {
        var result = combination.hashCode()
        result = 31 * result + tag.hashCode()
        return result
    }
}

class ThreeOfAKind(override val combination: CombinationFive) : HandClass{
    private val tripCard = combination.cardFreqs.entries.find {it.value == 3}?.key
    override val tag: HandClassTag = HandClassTag.THREE_OF_A_KIND
    override fun isValidHand(): Boolean = tripCard != null

    override fun compareTo(other: HandClass): Int {
        if (super.compareTo(other) == 0 && other is ThreeOfAKind && isValidHand() && other.isValidHand()){
            val cardComp = tripCard!!.compareTo(other.tripCard!!)
            return if (cardComp == 0 ){
                combination.cardFreqs.filter {it.key != tripCard}.keys.toList().map{Card(Suit.SPADES,it)}
                    .compare(other.combination.cardFreqs.filter {it.key != other.tripCard}.keys.toList().map {Card(Suit.SPADES,it)})
            } else {
                cardComp
            }
        }
        else{
            return super.compareTo(other)
        }
    }
    override fun equals(other: Any?): Boolean = other is ThreeOfAKind && compareTo(other) == 0
    override fun toString(): String = "THREE OF A KIND (${tripCard?.display})"
    override fun hashCode(): Int {
        var result = combination.hashCode()
        result = 31 * result + (tripCard?.hashCode() ?: 0)
        result = 31 * result + tag.hashCode()
        return result
    }
}

class TwoPair(override val combination: CombinationFive) : HandClass{
    private val pairs = combination.cardFreqs.filter {it.value == 2}.entries.toList().sortedByDescending { it.key.rank}
    private val singular = combination.cardFreqs.entries.find {it.value == 1}
    override val tag: HandClassTag = HandClassTag.TWO_PAIR

    override fun compareTo(other: HandClass): Int {
        if (super.compareTo(other) == 0 && other is TwoPair && isValidHand() && other.isValidHand()){
            val cardComp = pairs[0].key.rank.compareTo(other.pairs[0].key.rank)
            if (cardComp == 0 ){
                val cardComp2 = pairs[1].key.rank.compareTo(other.pairs[1].key.rank)
                return if (cardComp2 == 0){
                    singular!!.key.rank.compareTo(other.singular!!.key.rank)
                }
                else{
                    cardComp2
                }
            } else {
                return cardComp
            }
        }
        else{
            return super.compareTo(other)
        }
    }
    override fun isValidHand(): Boolean = pairs.size == 2
    override fun equals(other: Any?): Boolean = other is TwoPair && compareTo(other) == 0
    override fun toString(): String = "TWO PAIR (${pairs.getOrNull(0)?.key?.display}, ${pairs.getOrNull(1)?.key?.display})"
    override fun hashCode(): Int {
        var result = combination.hashCode()
        result = 31 * result + pairs.hashCode()
        result = 31 * result + (singular?.hashCode() ?: 0)
        result = 31 * result + tag.hashCode()
        return result
    }
}

class Pair(override val combination: CombinationFive) : HandClass{
    override val tag: HandClassTag = HandClassTag.PAIR
    private val pair = combination.cardFreqs.entries.find {it.value == 2}?.key
    private val others = combination.cardFreqs.entries.filter {it.value == 1}.map {Card(Suit.SPADES,it.key)}
    override fun isValidHand(): Boolean = pair != null && others.isNotEmpty()

    override fun compareTo(other: HandClass): Int {
        if (super.compareTo(other) == 0 && other is Pair && isValidHand() && other.isValidHand()){
            val cardComp = pair!!.rank.compareTo(other.pair!!.rank)
            return if (cardComp == 0){
                others.compare(other.others)
            }
            else{
                cardComp
            }
        }
        else{
            return super.compareTo(other)
        }
    }
    override fun equals(other: Any?): Boolean = other is Pair && compareTo(other) == 0
    override fun toString(): String = "PAIR (${pair?.display})"
    override fun hashCode(): Int {
        var result = combination.hashCode()
        result = 31 * result + tag.hashCode()
        result = 31 * result + (pair?.hashCode() ?: 0)
        result = 31 * result + others.hashCode()
        return result
    }
}

class HighCard(override val combination: CombinationFive) : HandClass{
    override val tag: HandClassTag = HandClassTag.HIGH_CARD
    override fun isValidHand(): Boolean = true
    override fun compareTo(other: HandClass): Int {
        return if (super.compareTo(other) == 0 && other is HighCard && isValidHand() && other.isValidHand()){
            combination.combination.compare(other.combination.combination)
        } else{
            super.compareTo(other)
        }
    }
    override fun equals(other: Any?): Boolean = other is HighCard && compareTo(other) == 0 && super.equals(other)
    override fun toString(): String = "HIGH CARD (${combination.highestCard().cardVal.display})"
    override fun hashCode(): Int {
        var result = combination.hashCode()
        result = 31 * result + tag.hashCode()
        return result
    }
}

fun main(){
    for (i in 1..10) {
        val deck = Deck()
        deck.createDeck()
        val list = mutableListOf<Card>()
        for (i in 1..7) {
            list.add(deck.cards.removeAt(0))
        }
        val combination = Combination(list)

            println("$combination - ${combination.getCombination()}")


    }
}