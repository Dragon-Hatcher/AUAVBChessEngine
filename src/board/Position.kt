package board

typealias Fen = String

class Position {

    var wPawn: BitBoard = BitBoards.EMPTY
    var wRook: BitBoard = BitBoards.EMPTY
    var wKnight: BitBoard = BitBoards.EMPTY
    var wBishop: BitBoard = BitBoards.EMPTY
    var wQueen: BitBoard = BitBoards.EMPTY
    var wKing: BitBoard = BitBoards.EMPTY
    var bPawn: BitBoard = BitBoards.EMPTY
    var bRook: BitBoard = BitBoards.EMPTY
    var bKnight: BitBoard = BitBoards.EMPTY
    var bBishop: BitBoard = BitBoards.EMPTY
    var bQueen: BitBoard = BitBoards.EMPTY
    var bKing: BitBoard = BitBoards.EMPTY
    var turn: Color = Colors.WHITE

    fun pseudoLegalMoves(): List<Move> {

        val all = mutableListOf<Move>()
        val white = wPawn or wKnight or wBishop or wRook or wQueen or wKing
        val black = bPawn or bKnight or bBishop or bRook or bQueen or bKing

        val moves: MutableList<Move> = mutableListOf()

        if(turn == Colors.WHITE) {
            kingMoves(moves, wKing, white, black)
            knightMoves(moves, wKnight, white, black)
        } else {
            kingMoves(moves, bKing, black, white)
            knightMoves(moves, bKnight, black, white)
        }

        return all
    }

    fun kingMoves(moves: MutableList<Move>, king: BitBoard, us: BitBoard, them: BitBoard) {
        val square = king.getFirstSetSquare()
        (kingMoveMasks[square] xor us).toMoveList(moves, square)
    }

    fun knightMoves(moves: MutableList<Move>, knights: BitBoard, us: BitBoard, them: BitBoard) {
        var k = knights
        while(k != BitBoards.EMPTY) {
            val square = k.getFirstSetSquare()
            k = k.unset(square)
            val moveBB = (knightMoveMasks[square] xor us)
            moveBB.toMoveList(moves, square)
        }
    }


    fun setPiece(square: Square, piece: Piece) {
        when(piece) {
            Pieces.W_PAWN   -> wPawn   = wPawn.set(square)
            Pieces.W_BISHOP -> wBishop = wBishop.set(square)
            Pieces.W_KNIGHT -> wKnight = wKnight.set(square)
            Pieces.W_ROOK   -> wRook   = wRook.set(square)
            Pieces.W_QUEEN  -> wQueen  = wQueen.set(square)
            Pieces.W_KING   -> wKing   = wKing.set(square)
            Pieces.B_PAWN   -> bPawn   = bPawn.set(square)
            Pieces.B_BISHOP -> bBishop = bBishop.set(square)
            Pieces.B_KNIGHT -> bKnight = bKnight.set(square)
            Pieces.B_ROOK   -> bRook   = bRook.set(square)
            Pieces.B_QUEEN  -> bQueen  = bQueen.set(square)
            Pieces.B_KING   -> bKing   = bKing.set(square)
        }
    }

    fun asciiRepresentation(): String {
        fun squareChar(square: Square): Char {
            val mask = square.toBitboard()
            return when {
                wPawn   and mask != BitBoards.EMPTY -> 'P'
                wKnight and mask != BitBoards.EMPTY -> 'N'
                wBishop and mask != BitBoards.EMPTY -> 'B'
                wRook   and mask != BitBoards.EMPTY -> 'R'
                wQueen  and mask != BitBoards.EMPTY -> 'Q'
                wKing   and mask != BitBoards.EMPTY -> 'K'
                bPawn   and mask != BitBoards.EMPTY -> 'p'
                bKnight and mask != BitBoards.EMPTY -> 'n'
                bBishop and mask != BitBoards.EMPTY -> 'b'
                bRook   and mask != BitBoards.EMPTY -> 'r'
                bQueen  and mask != BitBoards.EMPTY -> 'q'
                bKing   and mask != BitBoards.EMPTY -> 'k'
                else -> '.'
            }
        }

        var ret = ""

        for(rank in (Ranks.RANK_1..Ranks.RANK_8).reversed()) {
            ret += "$rank | "
            for(file in Files.FILE_A..Files.FILE_H) {
                val square = Squares.fromRankAndFile(rank, file)
                ret += squareChar(square) + " "
            }
            ret += "\n"
        }

        ret += "  +-----------------\n"
        ret += "    a b c d e f g h   "

        return ret
    }

    fun fen(): String {
        return ""
    }

    companion object {
        /**
         * Generates a [Position] object from the inputted [Fen]
         */
        fun fromFen(fen: Fen): Position {
            /*
               A FEN string defines a particular position using only the ASCII character set.

               A FEN string contains six fields separated by a space. The fields are:

               1) Piece placement (from white's perspective). Each rank is described, starting
                  with rank 8 and ending with rank 1. Within each rank, the contents of each
                  square are described from file A through file H. Following the Standard
                  Algebraic Notation (SAN), each piece is identified by a single letter taken
                  from the standard English names. White pieces are designated using upper-case
                  letters ("PNBRQK") whilst Black uses lowercase ("pnbrqk"). Blank squares are
                  noted using digits 1 through 8 (the number of blank squares), and "/"
                  separates ranks.

               2) Active color. "w" means white moves next, "b" means black.

               3) Castling availability. If neither side can castle, this is "-". Otherwise,
                  this has one or more letters: "K" (White can castle kingside), "Q" (White
                  can castle queenside), "k" (Black can castle kingside), and/or "q" (Black
                  can castle queenside).

               4) En passant target square (in algebraic notation). If there's no en passant
                  target square, this is "-". If a pawn has just made a 2-square move, this
                  is the position "behind" the pawn. Following X-FEN standard, this is recorded only
                  if there is a pawn in position to make an en passant capture, and if there really
                  is a pawn that might have advanced two squares.

               5) Halfmove clock. This is the number of halfmoves since the last pawn advance
                  or capture. This is used to determine if a draw can be claimed under the
                  fifty-move rule.

               6) Fullmove number. The number of the full move. It starts at 1, and is
                  incremented after Black's move.
            */

            val position = Position()

            var rank = Ranks.RANK_8
            var file = Files.FILE_A

            outer@ for(char in fen) {
                val square = Squares.fromRankAndFile(rank, file)
                when {
                    char.isDigit() -> file += Character.getNumericValue(char)
                    char == '/' -> {
                        rank--
                        file = Files.FILE_A
                    }
                    char == ' ' -> break@outer
                    else -> {
                        position.setPiece(square, Pieces.fromChar(char))
                        file++
                    }
                }
            }

            //TODO other fen stuff

            return position
        }
    }

}