package board

typealias Rank = Int

class Ranks {
    companion object {
        val RANKS = 8
        val RANK_1 = 0
        val RANK_2 = 1
        val RANK_3 = 2
        val RANK_4 = 3
        val RANK_5 = 4
        val RANK_6 = 5
        val RANK_7 = 6
        val RANK_8 = 7
    }
}

typealias File = Int

class Files {
    companion object {
        val FILES = 8
        val FILE_A = 0
        val FILE_B = 1
        val FILE_C = 2
        val FILE_D = 3
        val FILE_E = 4
        val FILE_F = 5
        val FILE_G = 6
        val FILE_H = 7

        val FILE_CHARS = listOf('a', 'b', 'c', 'd', 'e', 'f', 'g', 'h')
    }
}

typealias PieceType = Int

class PieceTypes {
    companion object {
        const val NONE: PieceType = 0
        const val PAWN: PieceType = 1
        const val KNIGHT: PieceType = 2
        const val BISHOP: PieceType = 3
        const val ROOK: PieceType = 4
        const val QUEEN: PieceType = 5
        const val KING: PieceType = 6
    }
}

typealias Piece = Int

class Pieces {
    companion object {
        val W_PAWN: Piece   = PieceTypes.PAWN
        val W_KNIGHT: Piece = PieceTypes.KNIGHT
        val W_BISHOP: Piece = PieceTypes.BISHOP
        val W_ROOK: Piece   = PieceTypes.ROOK
        val W_QUEEN: Piece  = PieceTypes.QUEEN
        val W_KING: Piece   = PieceTypes.KING

        val B_PAWN: Piece   = 8 + PieceTypes.PAWN
        val B_KNIGHT: Piece = 8 + PieceTypes.KNIGHT
        val B_BISHOP: Piece = 8 + PieceTypes.BISHOP
        val B_ROOK: Piece   = 8 + PieceTypes.ROOK
        val B_QUEEN: Piece  = 8 + PieceTypes.QUEEN
        val B_KING: Piece   = 8 + PieceTypes.KING

        fun fromChar(char: Char) =
            when(char) {
                'P' -> W_PAWN
                'N' -> W_KNIGHT
                'B' -> W_BISHOP
                'R' -> W_ROOK
                'Q' -> W_QUEEN
                'K' -> W_KING
                'p' -> B_PAWN
                'n' -> B_KNIGHT
                'b' -> B_BISHOP
                'r' -> B_ROOK
                'q' -> B_QUEEN
                'k' -> B_KING
                else -> throw Exception()
            }
    }
}

typealias Color = Int

fun Color.other(): Color = this xor 1

class Colors {
    companion object {
        val WHITE: Color = 0
        val BLACK: Color = 1
    }
}