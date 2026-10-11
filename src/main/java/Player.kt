
typealias Hand = MutableList<Card>

fun Hand.display():String = this.joinToString (", ")

class Player (val name: String){
    var allin:Boolean = false
    var fold:Boolean = false
    private val hand:Hand = mutableListOf()
    var currentHand:HandClass? = null
    var chips:Int = 0
    var wins:Double = 0.0
    var buyinamt = 0
    var buyins = 0
    fun addToHand(card:Card){
        assert(hand.size <= 2)
        hand.add(card)
    }
    fun resetHand(){
        hand.clear()
        currentHand = null
        allin = false
        fold = false
    }

    fun addChips(amt:Int, buyin :Boolean = false){
        chips += amt
        if (buyin){
            buyinamt += amt
            buyins ++
        }
    }

    fun showHand():Hand = hand

    fun putIn(round:Round,amt:Int){
        assert(amt <= chips) {throw NotEnoughChipsError()}
        if (chips == amt){
            allin = true
        }
        round.pot += amt
        round.currentBets[this] = round.currentBets[this]!! +  amt
        chips -= amt
    }

}