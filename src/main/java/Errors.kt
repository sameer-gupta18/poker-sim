class NotEnoughChipsError:Exception(){
    override val message:String = "You do not have enough chips. Please Buy In More, or quit"
}

class NotEnoughCardsError:Exception(){
    override val message: String = "There are not enough cards in the stack, please reduce the number of players."
}