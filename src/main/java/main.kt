import kotlin.math.min

const val BUY_IN = 1000
const val BB = 20
const val SB = 10
const val START_DEAL = 0
const val MAX_PLAYERS = 8
const val GAME_HARD_LIMIT = 40
const val BUY_IN_THRESHOLD = 0.1

fun main(){
    println("Welcome to the Poker Engine!")
    println("----------------------------")

    var numPlayers:Int?
    println("Choose the number of players: ")
    numPlayers= readln().trim().toIntOrNull()
    while (numPlayers == null || numPlayers <= 0 || numPlayers > MAX_PLAYERS ){
        println("Please enter a valid number between 0-${MAX_PLAYERS}.")
        println("Choose the number of players: ")
        numPlayers= readln().toIntOrNull()
    }

    val players = mutableListOf<Player>()
    for (i in 1..numPlayers){
        println("Please enter the name of Player $i")
        val name = readln()
        players.add(Player(name))
        println("$name added with a buy in of $BUY_IN")
    }

    println("Initializing Deck...")

    val deck = Deck()
    deck.createDeck()
    players.forEach{it.addChips(BUY_IN,true)}

    println("Deck Initialized")

    var gameNo = 1
    var dealer = START_DEAL
    while (gameNo < GAME_HARD_LIMIT){
        for (i in players){
            if (i.chips < BUY_IN_THRESHOLD * BUY_IN){
                println("${i.name}, would you like to buy in more chips, you are currently at ${i.chips}")
                println("Type 'Y' or 'N'")
                var more = readln().trim().lowercase()
                while (more != "y"  && more != "n"){
                    println("Please enter a valid command. 'Y' or 'N'")
                    more = readln().trim().lowercase()
                }
                if (more == "y"){
                    println("How much would you like to buy in for?")
                    var amt = readln().trim().toIntOrNull()
                    while (amt == null){
                        println("Please enter a valid integer")
                        amt = readln().trim().toIntOrNull()
                    }
                    i.addChips(amt,true)
                } else{
                    println("Thanks for playing ${i.name}")
                    players.remove(i)
                }
            }
        }
        println("Starting Game No. $gameNo")
        val game = Game(dealer,players,deck)
        println("Current Standings: ")
        println(game)
        println("The Dealer is ${players[dealer].name}")
        println("Small Blind of $SB chips given by ${players[dealer.peakNextPlayer(players)].name}")
        println("Big Blind of $BB chips given by ${players[dealer.peakNextPlayer(players).peakNextPlayer(players)].name}")
        game.startGame()

        //PRE-FLOP ROUND
        game.blinds()
        while(game.active_players.any {game.round.currentBets[it] != game.round.playerMaxPutIn}
            || game.round.playersPlayed <= players.size + 1){
            println("Current Round: ${game.round.name}")
            println("Current Bets:")
            game.round.currentBets.forEach{
                val totChips = game.rounds.sumOf { r -> r.currentBets[it.key]!! }
                println("${if (it.key == game.round.current()) "-->" else ""} ${it.key.name} : ${
                    if (it.key.fold) "Folded"
                    else if (it.key.allin) "All-In w/ ${it.value} ($totChips)"
                    else "${it.value} ($totChips)"
                } ${if (players[game.round.dealerIdx] == it.key) "[D]" else ""} ${if (!it.key.fold) it.key.showHand().display() else ""}")
            }
            println("${game.round.current().name}, you have ${game.round.current().chips} chips. What would you like to do?")
            val chipsToCall = game.round.playerMaxPutIn - game.round.currentBets[game.round.current()]!!
            val covered = chipsToCall >= game.round.current().chips
            val checkCallTag = if (chipsToCall == 0) "Check" else "Call [${min(chipsToCall,game.round.current().chips)}]"

            println("You have to bet $chipsToCall chips to call")
            println("Type the appropriate option! (1, 2, 3, or 4)")
            println("(1) $checkCallTag (2) ${if (covered) "-" else "Raise"} (3) ${if (covered) "-" else "All-In"} (4) Fold")
            var option = readln().trim().toIntOrNull()
            while (option == null || option !in 1..4 || (covered && option in 2..3)){
                println("Please enter a valid number between 1-4")
                option = readln().trim().toIntOrNull()
            }
            when (option){
                1 -> {
                    game.round.call(covered)
                }
                2 -> {
                    println("By how much do you want to raise. You have ${game.round.current().chips} chips")
                    var bet = readln().trim().toIntOrNull()
                    while (bet == null || bet < game.round.playerMaxPutIn || bet > game.round.current().chips){
                        println("Please enter a valid number, ensuring you have enough balance and the bet is at least double the pot")
                        bet = readln().trim().toIntOrNull()
                    }
                    game.round.bet(bet)
                }
                3 -> {
                    game.round.allin()
                }
                4->{
                    game.round.fold()
                }
            }
            println("Current Pot Is: ${game.round.pot}")
        }
        var i = 1
        while (i <= 3) {
            game.nextRound()
            println("The current table is: ")
            println(game.publicCards.joinToString(", "))
            if (game.round.current().fold || game.round.current().allin) {
                game.round.nextTurn()
            }
            while (game.active_players.any { game.round.currentBets[it] != game.round.playerMaxPutIn }
                || game.round.playersPlayed < players.size) {
                println("Current Round: ${game.round.name}")
                println("Current Bets:")
                game.round.currentBets.forEach{
                    val totChips = game.rounds.sumOf { r -> r.currentBets[it.key]!! }
                    println("${if (it.key == game.round.current()) "-->" else ""} ${it.key.name} : ${
                        if (it.key.fold) "Folded"
                        else if (it.key.allin) "All-In w/ ${it.value} ($totChips)"
                        else "${it.value} ($totChips)"
                    } ${if (players[game.round.dealerIdx] == it.key) "[D]" else ""} ${
                        if (!it.key.fold) it.key.showHand().display() else ""
                    } - ${it.key.currentHand}")
                }
                println("${game.round.current().name}, you have ${game.round.current().chips} chips. What would you like to do?")
                val chipsToCall = game.round.playerMaxPutIn - game.round.currentBets[game.round.current()]!!
                val covered = chipsToCall >= game.round.current().chips
                val checkCallTag =
                    if (chipsToCall == 0) "Check" else "Call [${min(chipsToCall, game.round.current().chips)}]"

                println("You have to bet $chipsToCall chips to call")
                println("Type the appropriate option! (1, 2, 3, or 4)")
                println("(1) $checkCallTag (2) ${if (covered) "-" else "Raise"} (3) ${if (covered) "-" else "All-In"} (4) Fold")
                var option = readln().trim().toIntOrNull()
                while (option == null || option !in 1..4 || (covered && option in 2..3)) {
                    println("Please enter a valid number between 1-4")
                    option = readln().trim().toIntOrNull()
                }
                when (option) {
                    1 -> {
                        game.round.call(covered)
                    }

                    2 -> {
                        println("By how much do you want to raise. You have ${game.round.current().chips} chips")
                        var bet = readln().trim().toIntOrNull()
                        while (bet == null || bet < game.round.playerMaxPutIn || bet > game.round.current().chips) {
                            println("Please enter a valid number, ensuring you have enough balance and the bet is at least double the pot")
                            bet = readln().trim().toIntOrNull()
                        }
                        game.round.bet(bet)
                    }

                    3 -> {
                        game.round.allin()
                    }

                    4 -> {
                        game.round.fold()
                    }
                }
                println("Current Pot Is: ${game.round.pot + game.pot}")
            }
            i ++
        }

        dealer = dealer.peakNextPlayer(players)
        gameNo++
        game.endGame()

    }

}