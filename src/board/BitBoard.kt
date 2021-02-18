package board

typealias BitBoard = ULong

fun BitBoard.bitCount() = this.countOneBits()

val index64 = listOf(
    0, 47,  1, 56, 48, 27,  2, 60,
    57, 49, 41, 37, 28, 16,  3, 61,
    54, 58, 35, 52, 50, 42, 21, 44,
    38, 32, 29, 23, 17, 11,  4, 62,
    46, 55, 26, 59, 40, 36, 15, 53,
    34, 51, 20, 43, 31, 22, 10, 45,
    25, 39, 14, 33, 19, 30,  9, 24,
    13, 18,  8, 12,  7,  6,  5, 63
)
fun BitBoard.getFirstSetSquare(): Square {
    val debruijn64 = 0x03f79d71b4cb0a89UL
    return index64[(((this xor (this - 1UL)) * debruijn64) shr 58).toInt()]
}

fun BitBoard.toMoveList(moves: MutableList<Move>, from: Square) {
//    var b = this
//    return (0 until this.bitCount()).map {
//        val s = b.getFirstSetSquare()
//        b = b.unset(s)
//        Move(from, s)
//    }
//
    var b = this
    while(b != BitBoards.EMPTY) {
        val s = b.getFirstSetSquare()
        b = b.unset(s)
        val n = createMove(from, s)
        moves.add(n)
    }
}

fun BitBoard.containsSquare(square: Square) = this and square.toBitboard() != BitBoards.EMPTY

fun BitBoard.set(square: Square) = square.toBitboard() or this
fun BitBoard.unset(square: Square) = square.toBitboard().inv() and this

fun BitBoard.asciiRepresentation(): String {
    var ret = ""

    for(rank in (Ranks.RANK_1..Ranks.RANK_8).reversed()) {
        ret += "$rank | "
        for(file in Files.FILE_A..Files.FILE_H) {
            val square = Squares.fromRankAndFile(rank, file)
            ret += if(this.containsSquare(square)) "# " else ". "
        }
        ret += "\n"
    }

    ret += "  +-----------------\n"
    ret += "    a b c d e f g h   "

    return ret

}


class BitBoards {
    companion object {
        val EMPTY: BitBoard = 0UL
        val FULL: BitBoard = EMPTY.inv()

        val A1: BitBoard = 1u

        //files
        val rank1: BitBoard = (255u shl (8 * 0)).toULong()
        val rank2: BitBoard = (255u shl (8 * 1)).toULong()
        val rank3: BitBoard = (255u shl (8 * 2)).toULong()
        val rank4: BitBoard = (255u shl (8 * 3)).toULong()
        val rank5: BitBoard = (255u shl (8 * 4)).toULong()
        val rank6: BitBoard = (255u shl (8 * 5)).toULong()
        val rank7: BitBoard = (255u shl (8 * 6)).toULong()
        val rank8: BitBoard = (255u shl (8 * 7)).toULong()
        val ranks = listOf(rank1, rank2, rank3, rank4, rank5, rank6, rank7, rank8)

        //rows
        val fileA: BitBoard = 72340172838076673u
        val fileB: BitBoard = fileA shl 1
        val fileC: BitBoard = fileA shl 2
        val fileD: BitBoard = fileA shl 3
        val fileE: BitBoard = fileA shl 4
        val fileF: BitBoard = fileA shl 5
        val fileG: BitBoard = fileA shl 6
        val fileH: BitBoard = fileA shl 7
        val files = listOf(fileA, fileB, fileC, fileD, fileE, fileF, fileG, fileH)

    }
}
