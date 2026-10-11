fun List<Card>.combinations(r:Int):List<List<Card>>{
    val result = mutableListOf<List<Card>>()

    fun combinationsGenerate(start:Int,current:MutableList<Card>){
        if (current.size == r){
            result.add(current.toList())
            return
        }

        for (i in start until this.size){
            current.add(this[i])
            combinationsGenerate(i+1,current)
            current.removeAt(current.lastIndex)
        }
    }
    combinationsGenerate(0,mutableListOf())
    return result
}

fun List<Card>.compare(other:List<Card>):Int{
    val sortedCards = this.sortedByDescending { it.cardVal.rank }
    val otherCards = other.sortedByDescending { it.cardVal.rank }
    var res = -1
    for (i in sortedCards.indices) {
        res = sortedCards[i].compareTo(otherCards[i])
        if (res != 0) break
    }
    return res
}