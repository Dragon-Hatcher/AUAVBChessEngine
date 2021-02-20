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

    var epW: Square = Squares.NONE
    var epB: Square = Squares.NONE
    var castleKW = true
    var castleQW = true
    var castleKB = true
    var castleQB = true

    var turn: Color = Colors.WHITE

    private var legalMoves: List<Move>? = null
    fun legalMoves(): List<Move> {
        if(legalMoves == null) {
            legalMoves = pseudoLegalMoves().filter {
                val c = this.copy()
                c.applyMove(it)
                !c.otherKingInCheck()
            }
        }
        return legalMoves!!
    }

    private fun pseudoLegalMoves(): List<Move> {

        fun pawnMoves(moves: MutableList<Move>, color: Color, pawns: BitBoard, them: BitBoard, all: BitBoard) {
            val empty = all.inv()

            if(color == Colors.WHITE) {

                val nonFinalRank = pawns and BitBoards.rank7.inv()
                val finalRank = pawns and BitBoards.rank7

                val forward1 =     (nonFinalRank                             shl 8) and empty
                val forward2 =     ((forward1 and BitBoards.rank3)           shl 8) and empty
                val captureRight = ((nonFinalRank and BitBoards.fileH.inv()) shl 9) and them
                val captureLeft =  ((nonFinalRank and BitBoards.fileA.inv()) shl 7) and them

                forward1.toMoveListFromOffset(moves, 8)
                forward2.toMoveListFromOffset(moves, 16)
                captureRight.toMoveListFromOffset(moves, 9)
                captureLeft.toMoveListFromOffset(moves, 7)

                val forward1Promote =     (finalRank                             shl 8) and empty
                val captureRightPromote = ((finalRank and BitBoards.fileH.inv()) shl 9) and them
                val captureLeftPromote =  ((finalRank and BitBoards.fileA.inv()) shl 7) and them

                forward1Promote.toMoveListFromOffsetAndPromote(moves, 8)
                captureRightPromote.toMoveListFromOffsetAndPromote(moves, 9)
                captureLeftPromote.toMoveListFromOffsetAndPromote(moves, 7)

                if(epB != Squares.NONE) {
                    val b = epB.toBitboard()
                    val right = (b and BitBoards.fileA.inv()) shr 1
                    val left = (b and BitBoards.fileH.inv()) shl 1
                    if(right and pawns != BitBoards.EMPTY) moves.add(createMoveEp(epB - 1, epB + 8))
                    if(left and pawns != BitBoards.EMPTY) moves.add(createMoveEp(epB + 1, epB + 8))
                }

            } else {

                val nonFinalRank = pawns and BitBoards.rank2.inv()
                val finalRank = pawns and BitBoards.rank2

                val forward1 =     (nonFinalRank                             shr 8) and empty
//                println("f1:\n${forward1.asciiRepresentation()}")
                val forward2 =     ((forward1 and BitBoards.rank6)           shr 8) and empty
//                println("f2:\n${forward2.asciiRepresentation()}")
                val captureRight = ((nonFinalRank and BitBoards.fileA.inv()) shr 9) and them
//                println("cr:\n${captureRight.asciiRepresentation()}")
                val captureLeft =  ((nonFinalRank and BitBoards.fileH.inv()) shr 7) and them
//                println("cl:\n${captureLeft.asciiRepresentation()}")

                forward1.toMoveListFromOffset(moves, -8)
                forward2.toMoveListFromOffset(moves, -16)
                captureRight.toMoveListFromOffset(moves, -9)
                captureLeft.toMoveListFromOffset(moves, -7)

                val forward1Promote =     (finalRank                             shr 8) and empty
                val captureRightPromote = ((finalRank and BitBoards.fileA.inv()) shr 9) and them
                val captureLeftPromote =  ((finalRank and BitBoards.fileH.inv()) shr 7) and them

                forward1Promote.toMoveListFromOffsetAndPromote(moves, -8)
                captureRightPromote.toMoveListFromOffsetAndPromote(moves, -9)
                captureLeftPromote.toMoveListFromOffsetAndPromote(moves, -7)

                if(epW != Squares.NONE) {
                    val b = epW.toBitboard()
                    val right = (b and BitBoards.fileA.inv()) shr 1
                    val left = (b and BitBoards.fileH.inv()) shl 1
                    if(right and pawns != BitBoards.EMPTY) moves.add(createMoveEp(epW - 1, epW - 8))
                    if(left and pawns != BitBoards.EMPTY) moves.add(createMoveEp(epW + 1, epW - 8))
                }

            }
        }

        fun kingMoves(moves: MutableList<Move>, king: BitBoard, us: BitBoard, color: Color, all: BitBoard) {
            val square = king.getFirstSetSquare()
            (kingMoveMasks[square] and us.inv()).toMoveList(moves, square)
            if(color == Colors.WHITE) {
                if (castleKW && all and BitBoards.CASTLE_BLOCK_KW == BitBoards.EMPTY && otherInCheck() and BitBoards.CASTLE_CHECK_KW == BitBoards.EMPTY) moves.add(createMoveWCK())
                if (castleQW && all and BitBoards.CASTLE_BLOCK_QW == BitBoards.EMPTY && otherInCheck() and BitBoards.CASTLE_CHECK_QW == BitBoards.EMPTY) moves.add(createMoveWCQ())
            } else {
                if (castleKB && all and BitBoards.CASTLE_BLOCK_KB == BitBoards.EMPTY && otherInCheck() and BitBoards.CASTLE_CHECK_KB == BitBoards.EMPTY) moves.add(createMoveBCK())
                if (castleQB && all and BitBoards.CASTLE_BLOCK_QB == BitBoards.EMPTY && otherInCheck() and BitBoards.CASTLE_CHECK_QB == BitBoards.EMPTY) moves.add(createMoveBCQ())
            }
        }

        fun knightMoves(moves: MutableList<Move>, knights: BitBoard, us: BitBoard) {
            var k = knights
            while(k != BitBoards.EMPTY) {
                val square = k.getFirstSetSquare()
                k = k.unset(square)
                val moveBB = (knightMoveMasks[square] and us.inv())
                moveBB.toMoveList(moves, square)
            }
        }

        fun rookMoves(moves: MutableList<Move>, rooks: BitBoard, us: BitBoard, all: BitBoard) {
            var r = rooks
            while(r != BitBoards.EMPTY) {
                val square = r.getFirstSetSquare()
                r = r.unset(square)
                val moveBB = rookMagicAttacks[square][(((rookMoveMasks[square] and all) * rookMagics[square]) shr rookMagicsLength[square]).toInt()] and us.inv()
                moveBB.toMoveList(moves, square)
            }
        }

        fun bishopMoves(moves: MutableList<Move>, bishops: BitBoard, us: BitBoard, all: BitBoard) {
            var b = bishops
            while(b != BitBoards.EMPTY) {
                val square = b.getFirstSetSquare()
                b = b.unset(square)
                val moveBB = bishopMagicAttacks[square][(((bishopMoveMasks[square] and all) * bishopMagics[square]) shr bishopMagicsLength[square]).toInt()] and us.inv()
                moveBB.toMoveList(moves, square)
            }
        }

        fun queenMoves(moves: MutableList<Move>, queens: BitBoard, us: BitBoard, all: BitBoard) {
            var q = queens
            while(q != BitBoards.EMPTY) {
                val square = q.getFirstSetSquare()
                q = q.unset(square)
                val moveBB = (rookMagicAttacks[square][(((rookMoveMasks[square] and all) * rookMagics[square]) shr rookMagicsLength[square]).toInt()] and us.inv()) or
                        (bishopMagicAttacks[square][(((bishopMoveMasks[square] and all) * bishopMagics[square]) shr bishopMagicsLength[square]).toInt()] and us.inv())
                moveBB.toMoveList(moves, square)
            }
        }

        val white = wPawn or wKnight or wBishop or wRook or wQueen or wKing
        val black = bPawn or bKnight or bBishop or bRook or bQueen or bKing
        val all = white or black

        val moves: MutableList<Move> = mutableListOf()

        if(turn == Colors.WHITE) {
            pawnMoves(moves, Colors.WHITE, wPawn, black, all)
            kingMoves(moves, wKing, white, Colors.WHITE, all)
            knightMoves(moves, wKnight, white)
            bishopMoves(moves, wBishop, white, all)
            rookMoves(moves, wRook, white, all)
            queenMoves(moves, wQueen, white, all)
        } else {
            pawnMoves(moves, Colors.BLACK, bPawn, white, all)
            kingMoves(moves, bKing, black, Colors.BLACK, all)
            knightMoves(moves, bKnight, black)
            bishopMoves(moves, bBishop, black, all)
            rookMoves(moves, bRook, black, all)
            queenMoves(moves, bQueen, black, all)
        }

        return moves
    }

    private var inCheck: BitBoard? = null
    fun inCheck(): BitBoard {

        if(inCheck != null) return inCheck!!

        var attackedSquares = BitBoards.EMPTY

        fun pawnMoves(color: Color, pawns: BitBoard) {
            attackedSquares = if(color == Colors.WHITE) {

                val captureRight = ((pawns and BitBoards.fileH.inv()) shl 9)
                val captureLeft =  ((pawns and BitBoards.fileA.inv()) shl 7)

                captureRight or captureLeft or attackedSquares

            } else {

                val captureRight = ((pawns and BitBoards.fileA.inv()) shr 9)
                val captureLeft =  ((pawns and BitBoards.fileH.inv()) shr 7)

                captureRight or captureLeft or attackedSquares

            }
        }

        fun knights(knights: BitBoard) {
            var k = knights
            while(k != BitBoards.EMPTY) {
                val square = k.getFirstSetSquare()
                k = k.unset(square)
                attackedSquares = knightMoveMasks[square] or attackedSquares
            }

        }

        fun rooks(rooks: BitBoard, all: BitBoard) {
            var r = rooks
            while(r != BitBoards.EMPTY) {
                val square = r.getFirstSetSquare()
                r = r.unset(square)
                attackedSquares = rookMagicAttacks[square][(((rookMoveMasks[square] and all) * rookMagics[square]) shr rookMagicsLength[square]).toInt()] or attackedSquares
            }
        }

        fun bishopMoves(bishops: BitBoard, all: BitBoard) {
            var b = bishops
            while(b != BitBoards.EMPTY) {
                val square = b.getFirstSetSquare()
                b = b.unset(square)
                attackedSquares = bishopMagicAttacks[square][(((bishopMoveMasks[square] and all) * bishopMagics[square]) shr bishopMagicsLength[square]).toInt()] or attackedSquares
            }
        }

        fun queenMoves(queens: BitBoard, all: BitBoard) {
            var q = queens
            while(q != BitBoards.EMPTY) {
                val square = q.getFirstSetSquare()
                q = q.unset(square)
                attackedSquares = (rookMagicAttacks[square][(((rookMoveMasks[square] and all) * rookMagics[square]) shr rookMagicsLength[square]).toInt()]) or
                        (bishopMagicAttacks[square][(((bishopMoveMasks[square] and all) * bishopMagics[square]) shr bishopMagicsLength[square]).toInt()]) or attackedSquares
            }
        }

        val white = wPawn or wKnight or wBishop or wRook or wQueen or wKing
        val black = bPawn or bKnight or bBishop or bRook or bQueen or bKing
        val all = white or black

        if(turn == Colors.WHITE) {
            pawnMoves(Colors.WHITE, wPawn)
            knights(wKnight)
            rooks(wRook, all)
            bishopMoves(wBishop, all)
            queenMoves(wQueen, all)
        } else {
            pawnMoves(Colors.BLACK, bPawn)
            knights(bKnight)
            rooks(bRook, all)
            bishopMoves(bBishop, all)
            queenMoves(bQueen, all)
            bKing and attackedSquares != BitBoards.EMPTY
        }

        inCheck = attackedSquares
        return inCheck!!
    }
    fun kingInCheck(): Boolean = (if(turn == Colors.WHITE) wKing else bKing) and inCheck() != BitBoards.EMPTY
    fun otherKingInCheck(): Boolean = (if(turn == Colors.WHITE) bKing else wKing) and inCheck() != BitBoards.EMPTY

    private var otherInCheck: BitBoard? = null
    fun otherInCheck(): BitBoard {

        if(otherInCheck != null) return otherInCheck!!

        var attackedSquares = BitBoards.EMPTY

        fun pawnMoves(color: Color, pawns: BitBoard) {
            attackedSquares = if(color == Colors.WHITE) {

                val captureRight = ((pawns and BitBoards.fileH.inv()) shl 9)
                val captureLeft =  ((pawns and BitBoards.fileA.inv()) shl 7)

                captureRight or captureLeft or attackedSquares

            } else {

                val captureRight = ((pawns and BitBoards.fileA.inv()) shr 9)
                val captureLeft =  ((pawns and BitBoards.fileH.inv()) shr 7)

                captureRight or captureLeft or attackedSquares

            }
        }

        fun knights(knights: BitBoard) {
            var k = knights
            while(k != BitBoards.EMPTY) {
                val square = k.getFirstSetSquare()
                k = k.unset(square)
                attackedSquares = knightMoveMasks[square] or attackedSquares
            }

        }

        fun rooks(rooks: BitBoard, all: BitBoard) {
            var r = rooks
            while(r != BitBoards.EMPTY) {
                val square = r.getFirstSetSquare()
                r = r.unset(square)
                attackedSquares = rookMagicAttacks[square][(((rookMoveMasks[square] and all) * rookMagics[square]) shr rookMagicsLength[square]).toInt()] or attackedSquares
            }
        }

        fun bishopMoves(bishops: BitBoard, all: BitBoard) {
            var b = bishops
            while(b != BitBoards.EMPTY) {
                val square = b.getFirstSetSquare()
                b = b.unset(square)
                attackedSquares = bishopMagicAttacks[square][(((bishopMoveMasks[square] and all) * bishopMagics[square]) shr bishopMagicsLength[square]).toInt()] or attackedSquares
            }
        }

        fun queenMoves(queens: BitBoard, all: BitBoard) {
            var q = queens
            while(q != BitBoards.EMPTY) {
                val square = q.getFirstSetSquare()
                q = q.unset(square)
                attackedSquares = (rookMagicAttacks[square][(((rookMoveMasks[square] and all) * rookMagics[square]) shr rookMagicsLength[square]).toInt()]) or
                        (bishopMagicAttacks[square][(((bishopMoveMasks[square] and all) * bishopMagics[square]) shr bishopMagicsLength[square]).toInt()]) or attackedSquares
            }
        }

        val white = wPawn or wKnight or wBishop or wRook or wQueen or wKing
        val black = bPawn or bKnight or bBishop or bRook or bQueen or bKing
        val all = white or black

        if(turn != Colors.WHITE) {
            pawnMoves(Colors.WHITE, wPawn)
            knights(wKnight)
            rooks(wRook, all)
            bishopMoves(wBishop, all)
            queenMoves(wQueen, all)
        } else {
            pawnMoves(Colors.BLACK, bPawn)
            knights(bKnight)
            rooks(bRook, all)
            bishopMoves(bBishop, all)
            queenMoves(bQueen, all)
            bKing and attackedSquares != BitBoards.EMPTY
        }

        otherInCheck = attackedSquares
        return otherInCheck!!
    }

    fun applyMove(move: Move) {
        inCheck = null
        otherInCheck = null
        legalMoves = null

        val from = move.from().toBitboard()
        val to = move.to().toBitboard()
        val toInv = to.inv()

        epW = Squares.NONE
        epB = Squares.NONE

        when(move.type()) {
            MoveTypes.PROMOTION -> {
                if(turn == Colors.WHITE) {
                    if(to == BitBoards.H8) this.castleKB = false
                    if(to == BitBoards.A8) this.castleQB = false
                    wPawn = wPawn xor from
                    when(move.promotion()) {
                        MovePieceType.BISHOP -> wBishop = wBishop or to
                        MovePieceType.KNIGHT -> wKnight = wKnight or to
                        MovePieceType.ROOK   -> wRook   = wRook   or to
                        MovePieceType.QUEEN  -> wQueen  = wQueen  or to
                    }
                    bBishop = bBishop and toInv
                    bKnight = bKnight and toInv
                    bRook   = bRook   and toInv
                    bQueen  = bQueen  and toInv
                } else {
                    if(to == BitBoards.H1) this.castleKW = false
                    if(to == BitBoards.A1) this.castleQW = false
                    bPawn = bPawn xor from
                    when(move.promotion()) {
                        MovePieceType.BISHOP -> bBishop = bBishop or to
                        MovePieceType.KNIGHT -> bKnight = bKnight or to
                        MovePieceType.ROOK   -> bRook   = bRook   or to
                        MovePieceType.QUEEN  -> bQueen  = bQueen  or to
                    }
                    wBishop = wBishop and toInv
                    wKnight = wKnight and toInv
                    wRook   = wRook   and toInv
                    wQueen  = wQueen  and toInv
                }
            }
            MoveTypes.EP -> {
                if(turn == Colors.WHITE) {
                    wPawn = wPawn xor from xor to
                    bPawn = bPawn xor (move.to().toBitboard() shr 8)
                } else {
                    bPawn = bPawn xor from xor to
                    wPawn = wPawn xor (move.to().toBitboard() shl 8)
                }
            }
            MoveTypes.CASTLE -> {
                when(move.to()) {
                    Squares.G1 -> {
                        castleKW = false
                        castleQW = false
                        wKing = wKing xor BitBoards.E1 xor BitBoards.G1
                        wRook = wRook xor BitBoards.H1 xor BitBoards.F1
                    }
                    Squares.C1 -> {
                        castleKW = false
                        castleQW = false
                        wKing = wKing xor BitBoards.E1 xor BitBoards.C1
                        wRook = wRook xor BitBoards.A1 xor BitBoards.D1
                    }
                    Squares.G8 -> {
                        castleKB = false
                        castleQB = false
                        bKing = bKing xor BitBoards.E8 xor BitBoards.G8
                        bRook = bRook xor BitBoards.H8 xor BitBoards.F8
                    }
                    Squares.C8 -> {
                        castleKB = false
                        castleQB = false
                        bKing = bKing xor BitBoards.E8 xor BitBoards.C8
                        bRook = bRook xor BitBoards.A8 xor BitBoards.D8
                    }
                }
            }
            MoveTypes.NORMAL -> {
                //TODO optimize this
                if(turn == Colors.WHITE) {
                    if(to == BitBoards.H8) this.castleKB = false
                    if(to == BitBoards.A8) this.castleQB = false
                    bPawn   = bPawn   and toInv
                    bBishop = bBishop and toInv
                    bKnight = bKnight and toInv
                    bRook   = bRook   and toInv
                    bQueen  = bQueen  and toInv
                    when {
                        wPawn and from != BitBoards.EMPTY -> {
                            if(move.to() - move.from() == 16) epW = move.to()
                            wPawn = wPawn xor from xor to
                        }
                        wBishop and from != BitBoards.EMPTY -> {
                            wBishop = wBishop xor from xor to
                        }
                        wKnight and from != BitBoards.EMPTY -> {
                            wKnight = wKnight xor from xor to
                        }
                        wRook and from != BitBoards.EMPTY -> {
                            if(from == BitBoards.A1) castleQW = false
                            if(from == BitBoards.H1) castleKW = false
                            wRook = wRook xor from xor to
                        }
                        wQueen and from != BitBoards.EMPTY -> {
                            wQueen = wQueen xor from xor to
                        }
                        wKing and from != BitBoards.EMPTY -> {
                            castleKW = false
                            castleQW = false
                            wKing = wKing xor from xor to
                        }
                    }
                } else {
                    if(to == BitBoards.H1) this.castleKW = false
                    if(to == BitBoards.A1) this.castleQW = false
                    wPawn   = wPawn   and toInv
                    wBishop = wBishop and toInv
                    wKnight = wKnight and toInv
                    wRook   = wRook   and toInv
                    wQueen  = wQueen  and toInv
                    when {
                        bPawn and from != BitBoards.EMPTY -> {
                            if(move.to() - move.from() == -16) epB = move.to()
                            bPawn = bPawn xor from xor to
                        }
                        bBishop and from != BitBoards.EMPTY -> {
                            bBishop = bBishop xor from xor to
                        }
                        bKnight and from != BitBoards.EMPTY -> {
                            bKnight = bKnight xor from xor to
                        }
                        bRook and from != BitBoards.EMPTY -> {
                            if(from == BitBoards.A8) castleQB = false
                            if(from == BitBoards.H8) castleKB = false
                            bRook = bRook xor from xor to
                        }
                        bQueen and from != BitBoards.EMPTY -> {
                            bQueen = bQueen xor from xor to
                        }
                        bKing and from != BitBoards.EMPTY -> {
                            castleKB = false
                            castleQB = false
                            bKing = bKing xor from xor to
                        }
                    }

                }
            }
        }

        turn = turn.other()
    }

    fun copy(): Position {
        val p = Position()
        p.wPawn    = wPawn
        p.bPawn    = bPawn
        p.wBishop  = wBishop
        p.bBishop  = bBishop
        p.wKnight  = wKnight
        p.bKnight  = bKnight
        p.wRook    = wRook
        p.bRook    = bRook
        p.wQueen   = wQueen
        p.bQueen   = bQueen
        p.wKing    = wKing
        p.bKing    = bKing
        p.castleKW = castleKW
        p.castleKB = castleKB
        p.castleQB = castleQB
        p.castleQW = castleQW
        p.epW      = epW
        p.epB      = epB
        p.turn     = turn
        return p
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
            ret += "${rank + 1} | "
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

    fun inCheckOrStalemate(): Boolean = legalMoves().isEmpty()
    fun inCheckmate(): Boolean = kingInCheck() && legalMoves().isEmpty() //TODO improve
    fun inStalemate(): Boolean = !kingInCheck() && legalMoves().isEmpty() //TODO improve

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

            var index = fen.indexOf(' ') + 1
            fun pop(): Char {
                val c = fen[index]
                index++
                return c
            }

            position.turn = if(pop() == 'w') Colors.WHITE else Colors.BLACK

            pop()

            var castle = pop()
            position.castleKW = false
            position.castleQW = false
            position.castleKB = false
            position.castleQB = false
            while(castle != ' ' && castle != '-') {
                when(castle) {
                    'K' -> position.castleKW = true
                    'Q' -> position.castleQW = true
                    'k' -> position.castleKB = true
                    'q' -> position.castleQB = true
                }
                castle = pop()
            }

            val ep = pop()
            if(ep != '-') {
                if(position.turn == Colors.WHITE) {
                    position.epW = Squares.fromRankAndFile(Ranks.RANK_3, Files.FILE_CHARS.indexOf(ep))
                } else {
                    position.epB = Squares.fromRankAndFile(Ranks.RANK_6, Files.FILE_CHARS.indexOf(ep))
                }
            }

            return position
        }
    }

}