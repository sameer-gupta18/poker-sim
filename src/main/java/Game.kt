import kotlin.math.abs
import kotlin.math.max
import kotlin.math.min
import kotlin.math.roundToInt
import kotlin.math.roundToLong

enum class round_names{
    PREFLOP,FLOP,TURN,RIVER
}

typealias PublicCards = MutableList<Card>

class Round(val name:round_names, val players:List<Player>,var dealerIdx:Int,private val game:Game){

    var currentTurn = dealerIdx
    var playerMaxPutIn = 0;
    var pot = 0
    val currentBets = mutableMapOf(
        *players.zip(List(players.size) {0}).toTypedArray()
    )
    var playersPlayed = 0
    fun nextTurn(){
        currentTurn = currentTurn.peakNextPlayer(players)
        playersPlayed++
        while (current().fold || current().allin){
            currentTurn = currentTurn.peakNextPlayer(players)
            playersPlayed++
            if (playersPlayed > players.size){
                break
            }
        }
    }

    fun fold(){
        current().fold = true
        nextTurn()
    }

    fun allin(){
        current().allin = true
        bet(current().chips)
    }

    fun endRound(game:Game){
        players.forEach {
            game.individualPots[it] = game.individualPots[it]!! + currentBets
                .map { bet -> min(bet.value,currentBets[it]!!)}
                .sum()
        }
        game.addToPot(pot)

    }

    fun call(covered:Boolean){
        if(!covered){
            bet(playerMaxPutIn - currentBets[current()]!!)
        } else {
            allin()
        }
    }

    fun bet(amt:Int){
        current().putIn(this,amt)
        if (currentBets[current()]!! > playerMaxPutIn){
            playerMaxPutIn = currentBets[current()]!!
        }
        nextTurn()
    }

    fun current():Player = players[currentTurn]
}

fun Int.peakNextPlayer(players:List<Player>):Int = (this + 1) % players.size

class Game (private val dealer:Int,private val players:List<Player>,private val deck:Deck){
    var pot:Int = 0
    var individualPots =
        mutableMapOf(
            *players.zip(List(players.size) { 0 }).toTypedArray()
        ) //the number of chips a player can win
    val rounds:List<Round> = listOf(
        Round(round_names.PREFLOP, players,dealer, this),
        Round(round_names.FLOP, players,dealer, this),
        Round(round_names.TURN, players,dealer, this),
        Round(round_names.RIVER, players,dealer, this),
    )
    var currRound = 0
    val round
        get() = rounds[currRound]

    val publicCards:PublicCards = mutableListOf()

    val active_players:List<Player>
        get() = players.filter {
            !it.allin && !it.fold
        }

    fun addToPot(amt:Int){
        pot += amt
    }

    fun nextRound(){
        assert(currRound < rounds.size - 1) {throw IllegalArgumentException()}
        round.endRound(this)
        currRound ++
        round.currentTurn = rounds[currRound - 1].currentTurn
        when (round.name){
            round_names.PREFLOP -> openCard(0)
            round_names.FLOP -> openCard(3)
            round_names.TURN -> openCard(1)
            round_names.RIVER -> openCard(1)
        }
        players.forEach {
            it.currentHand = Combination(it.showHand() + publicCards).getCombination()
        }
    }

    fun startGame(){
        val idx = dealer.peakNextPlayer(players)
        val playersOrdered = players.drop(idx) + players.take(idx)
        for (i in 0..1){
            for (player in playersOrdered){
                deck.dealPlayer(player)
            }
        }
    }

    fun blinds(){
        round.currentTurn ++
        round.bet(SB)
        round.bet(BB)
    }

    fun openCard(num:Int){
        assert(deck.numCards >= num+1) {throw NotEnoughCardsError()}
        deck.burn()
        for (i in 1..num) {
            deck.openCard(this)
        }
        assert(publicCards.size <= 5) {throw IllegalArgumentException() }
    }

    override fun toString(): String {
        var str = ""
        val winners = players.sortedByDescending { it.chips }
        winners.forEachIndexed { i, card ->
            val profit = card.chips - card.buyinamt
            str += "${i+1}. ${card.name} : ${card.chips} chips | ${card.wins} wins | ${if (profit >= 0) "+${profit}" else "$profit"} [${card.buyins} Buy-Ins]\n"
        }
        return str
    }

    fun endGame(){

        val rankings = players.filter {!it.fold}.groupBy {it.currentHand!!}.toList().sortedByDescending { it.first }.toMutableList()
//remove folded players
        var playerIdx = 0
        while (pot != 0){
            val winner = rankings.removeFirst().second
            winner.forEach {
                val amt = individualPots[it]!! / winner.size
                it.addChips(amt)
                pot -= amt
                it.wins += 1.0 / winner.size
                if (playerIdx < rankings.size - 1){
                    for (player in rankings){
                        player.second.forEach { pl ->
                            individualPots[pl] = max(individualPots[pl]!! - amt,0)
                        }
                    }
                }

            }
            playerIdx++
        }

        pot = 0
        individualPots = mutableMapOf(
            *players.zip(List(players.size) { 0 }).toTypedArray()
        )
        currRound = 0

        deck.resetDeck()
        players.forEach {
            it.resetHand()
        }

    }
}