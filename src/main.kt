import board.*
import kotlin.random.Random
import kotlin.random.nextULong

fun main() {
//    val position = Position.fromFen("rnbqkbnr/pppppppp/8/8/8/8/PPPPPPPP/RNBQKBNR w KQkq - 0 1")
//    println(position.asciiRepresentation())
//    println(position.wPawn.asciiRepresentation())
//    knightMoveMasks.forEach { println(it.asciiRepresentation()) }

    val position = Position.fromFen("8/NNNNNNNN/8/K7/8/8/8/8 w - - 0 1")
    println(position.asciiRepresentation())
    println(knightMoveMasks.size)
    val time = System.currentTimeMillis()
    for(i in 1..1000000) {
        position.pseudoLegalMoves()
    }
    println(System.currentTimeMillis() - time)
}