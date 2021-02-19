import board.Position

fun perft(position: Position, depth: Int): Int =
    when {
        position.inCheckOrStalemate() -> 0
        depth == 1 -> position.legalMoves().size
        else -> {
            val moves = position.legalMoves()
            moves.map {
                val c = position.copy()
                c.applyMove(it)
                perft(c, depth - 1)
            }.sum()
        }
    }


//fun perft(board: Board, depth: Int, player: Boolean): Int {
//    var ret = 0
//    if (sideInCheckmate(board, true) || sideInCheckmate(board, false) || sideInStalemate(
//            board,
//            true
//        ) || sideInStalemate(board, false)
//    ) {
//        return 0
//    }
//    if (depth == 1) {
//        val moves: Array<Move> = allLegalMoves(board, player)
//        for (i in moves.indices) {
//            println(moves[i].x1.toString() + ", " + moves[i].y1 + " | " + moves[i].x2 + ", " + moves[i].y2)
//        }
//        return allLegalMoves(board, player).length
//    }
//    val moves: Array<Move> = allLegalMoves(board, player)
//    for (i in moves.indices) {
//        println(moves[i].x1.toString() + ", " + moves[i].y1 + " | " + moves[i].x2 + ", " + moves[i].y2)
//        var newBoard = Board()
//        newBoard = board.copyBoard()
//        newBoard.applyMove(moves[i])
//        ret += perft(newBoard, depth - 1, !player)
//    }
//    return ret
//}