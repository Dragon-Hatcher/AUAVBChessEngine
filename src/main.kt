import board.*
import kotlin.random.Random
import kotlin.random.nextULong

fun main() {
//    bishopMoveMasks.forEach { println(it.asciiRepresentation()) }
//    return

//    println((291331016189280256UL).asciiRepresentation())
//    return;

//    val blockers = (BitBoards.fileD or BitBoards.rank2 or BitBoards.rank3) and rookMoveMasks[50]
//    println(blockers)
//    println(blockers.asciiRepresentation())
//    println(((blockers * rookMagics[50]) shr rookMagicsLength[50]).toInt())
//    val b = rookMagicAttacks[50][((blockers * rookMagics[50]) shr rookMagicsLength[50]).toInt()]
//    println(b.asciiRepresentation())
//    println(b)

//    val position = Position.fromFen("rnbqkbnr/pppppppp/8/8/8/8/PPPPPPPP/RNBQKBNR w KQkq - 0 1")
//    val position = Position.fromFen("r3k2r/p1ppqpb1/bn2pnp1/3PN3/1p2P3/2N2Q1p/PPPBBPPP/R3K2R w KQkq -")
//    val position = Position.fromFen("8/2p5/3p4/KP5r/1R3p1k/8/4P1P1/8 w - - ")
    val position = Position.fromFen("r3k2r/Pppp1ppp/1b3nbN/nP6/BBP1P3/q4N2/Pp1P2PP/R2Q1RK1 w kq - 0 1")

    println(position.castleKW)

//    println(position.asciiRepresentation())
//    println(position.wPawn.asciiRepresentation())
//    knightMoveMasks.forEach { println(it.asciiRepresentation()) }

//    val position = Position.fromFen("rnbq1k1r/4b3/8/8/2B5/8/4Nn2/RNBQK2R")
//    val position = Position.fromFen("8/NNNNNNNN/8/K7/8/8/8/8 w - - 0 1")
//    println(position.asciiRepresentation())
//    println(knightMoveMasks.size)
//    val time = System.currentTimeMillis()
//    for(i in 1..1000000) {
    val moves = position.pseudoLegalMoves()
    moves.forEach { println(it.notation()) }
    println(moves.size)
//    }
//    println(System.currentTimeMillis() - time)
}