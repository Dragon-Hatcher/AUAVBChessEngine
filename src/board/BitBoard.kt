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
    var b = this
    while(b != BitBoards.EMPTY) {
        val s = b.getFirstSetSquare()
        b = b.unset(s)
        val n = createMove(from, s)
        moves.add(n)
    }
}

fun BitBoard.toMoveListFromOffset(moves: MutableList<Move>, offset: Int) {
    var b = this
    while(b != BitBoards.EMPTY) {
        val s = b.getFirstSetSquare()
        b = b.unset(s)
        val n = createMove(s - offset, s)
        moves.add(n)
    }
}

fun BitBoard.toMoveListFromOffsetAndPromote(moves: MutableList<Move>, offset: Int) {
    var b = this
    while(b != BitBoards.EMPTY) {
        val s = b.getFirstSetSquare()
        b = b.unset(s)
        moves.add(createMove(s - offset, s, MovePieceType.BISHOP))
        moves.add(createMove(s - offset, s, MovePieceType.KNIGHT))
        moves.add(createMove(s - offset, s, MovePieceType.ROOK))
        moves.add(createMove(s - offset, s, MovePieceType.QUEEN))
    }
}


fun BitBoard.toSquareList(moves: MutableList<Move> = mutableListOf()): List<Square> {
    var b = this
    while(b != BitBoards.EMPTY) {
        val s = b.getFirstSetSquare()
        b = b.unset(s)
        val n = s
        moves.add(n)
    }
    return moves
}

fun BitBoard.containsSquare(square: Square) = this and square.toBitboard() != BitBoards.EMPTY

fun BitBoard.set(square: Square) = square.toBitboard() or this
fun BitBoard.unset(square: Square) = square.toBitboard().inv() and this

fun BitBoard.asciiRepresentation(): String {
    var ret = ""

    for(rank in (Ranks.RANK_1..Ranks.RANK_8).reversed()) {
        ret += "${rank + 1} | "
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
        val B1: BitBoard = A1 shl 1
        val C1: BitBoard = B1 shl 1
        val D1: BitBoard = C1 shl 1
        val E1: BitBoard = D1 shl 1
        val F1: BitBoard = E1 shl 1
        val G1: BitBoard = F1 shl 1
        val H1: BitBoard = G1 shl 1

        val A8: BitBoard = A1 shl 56
        val B8: BitBoard = A8 shl 1
        val C8: BitBoard = B8 shl 1
        val D8: BitBoard = C8 shl 1
        val E8: BitBoard = D8 shl 1
        val F8: BitBoard = E8 shl 1
        val G8: BitBoard = F8 shl 1
        val H8: BitBoard = G8 shl 1

        //files
        val rank1: BitBoard = (255u shl (8 * 0)).toULong()
        val rank2: BitBoard = rank1 shl 8
        val rank3: BitBoard = rank2 shl 8
        val rank4: BitBoard = rank3 shl 8
        val rank5: BitBoard = rank4 shl 8
        val rank6: BitBoard = rank5 shl 8
        val rank7: BitBoard = rank6 shl 8
        val rank8: BitBoard = rank7 shl 8
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

        //area to check for castle checks
        val CASTLE_CHECK_QW = C1 or D1 or E1
        val CASTLE_CHECK_KW = E1 or F1 or G1
        val CASTLE_CHECK_QB = C8 or D8 or E8
        val CASTLE_CHECK_KB = E8 or F8 or G8

        val CASTLE_BLOCK_QW = B1 or C1 or D1
        val CASTLE_BLOCK_KW = F1 or G1
        val CASTLE_BLOCK_QB = B8 or C8 or D8
        val CASTLE_BLOCK_KB = F8 or G8

    }
}
