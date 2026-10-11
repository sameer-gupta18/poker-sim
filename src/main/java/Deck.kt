

class Deck {
    var cards:MutableList<Card> = mutableListOf()
    val numCards
        get() = cards.size
    fun shuffleDeck(){
            cards.shuffle()
    }
    fun createDeck(){
        for (i in Suit.entries){
            for (j in CardVal.entries){
                cards.add(Card(i,j))
            }
        }
        shuffleDeck()
    }
    

    fun resetDeck(){
        cards = mutableListOf()
        createDeck()
    }

    fun openCard(game:Game){
        game.publicCards.add(cards.removeAt(0))
    }

    fun burn(){
        cards.removeAt(0)
    }

    fun dealPlayer(player:Player){
        player.addToHand(cards.removeAt(0))
    }



}