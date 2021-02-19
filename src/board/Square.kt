package board

typealias Square = Int

fun Square.toBitboard(): BitBoard = BitBoards.A1 shl this

/**
 * Unlike [toBitboard] this function does not assume this is a real square.
 */
fun Square.toBitboardSafe(): BitBoard = if(this !in Squares.A1..Squares.H8) BitBoards.EMPTY else this.toBitboard()

fun Square.add(direction: Direction, magnitude: Int) = this + direction * magnitude

fun Square.rank(): Rank = this / 8
fun Square.file(): File = this % 8

fun Square.algebraic() = listOf('a','b','c','d','e','f','g','h')[this.file()] + (this.rank() + 1).toString()

fun Collection<Square>.toBitboard(): BitBoard {
    var b = BitBoards.EMPTY
    this.forEach { b = b.set(it) }
    return b
}

class Squares {
    companion object {
        fun fromRankAndFile(rank: Rank, file: File) = rank * Files.FILES + file

        val A1: Square = fromRankAndFile(Ranks.RANK_1, Files.FILE_A)
        val A8: Square = fromRankAndFile(Ranks.RANK_8, Files.FILE_A)
        val A2: Square = fromRankAndFile(Ranks.RANK_2, Files.FILE_A)
        val C1: Square = fromRankAndFile(Ranks.RANK_1, Files.FILE_C)
        val C8: Square = fromRankAndFile(Ranks.RANK_8, Files.FILE_C)
        val E1: Square = fromRankAndFile(Ranks.RANK_1, Files.FILE_E)
        val E8: Square = fromRankAndFile(Ranks.RANK_8, Files.FILE_E)
        val G1: Square = fromRankAndFile(Ranks.RANK_1, Files.FILE_G)
        val G8: Square = fromRankAndFile(Ranks.RANK_8, Files.FILE_G)
        val H8: Square = fromRankAndFile(Ranks.RANK_8, Files.FILE_H)

        val NONE: Square = H8 + 1

        val all: List<Square> = (A1..H8).map{it}
    }
}

typealias Direction = Int

class Directions {
    companion object {
        const val NORTH: Direction = 8
        const val EAST: Direction = 1
        const val SOUTH: Direction = -8
        const val WEST: Direction = -1
    }
}