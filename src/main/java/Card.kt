import java.util.Comparator

enum class Suit(val display:String){
    SPADES("Spades"),
    DIAMONDS("Diamonds"),
    HEARTS("Hearts"),
    CLUBS("Clubs")
}

enum class CardVal(val display:String,val rank:Int){
    TWO("2",2),
    THREE("3",3),
    FOUR("4",4),
    FIVE("5",5),
    SIX("6",6),
    SEVEN("7",7),
    EIGHT("8",8),
    NINE("9",9),
    TEN("10",10),
    JACK("J",11),
    QUEEN("Q",12),
    KING("K",13),
    ACE("A",14)
}



class Card (val suit:Suit,val cardVal:CardVal): Comparable<Card> {
    override fun toString(): String = "${cardVal.display} of ${suit.display}"
    override fun equals(other: Any?): Boolean = other is Card && other.suit == suit && other.cardVal == cardVal
    fun valEqual(other:Card) = cardVal == other.cardVal
    override fun compareTo(other: Card): Int = cardVal.rank.compareTo(other.cardVal.rank)
    override fun hashCode(): Int {
        var result = suit.hashCode()
        result = 31 * result + cardVal.hashCode()
        return result
    }
    fun isSameSuit(other:Card):Boolean = this.suit == other.suit
}