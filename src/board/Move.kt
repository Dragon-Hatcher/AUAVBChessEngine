package board

//0-5: from
//6-11: to
typealias Move = Int

fun createMove(from: Square, to: Square): Move = from or (to shr 6)

val kingMoveMasks = Squares.all.map { square ->
    val f = square.file()
    val r = square.rank()

    var mask = BitBoards.EMPTY
    val squares = listOfNotNull(
        if (r != Ranks.RANK_8) square.add(Directions.NORTH, 1) else null,
        if (f != Files.FILE_H) square.add(Directions.EAST, 1) else null,
        if (r != Ranks.RANK_1) square.add(Directions.SOUTH, 1) else null,
        if (f != Files.FILE_A) square.add(Directions.WEST, 1) else null,
        if (r != Ranks.RANK_8 && f != Files.FILE_H) square.add(Directions.NORTH, 1).add(Directions.EAST, 1) else null,
        if (f != Files.FILE_H && r != Ranks.RANK_1) square.add(Directions.EAST, 1).add(Directions.SOUTH, 1) else null,
        if (r != Ranks.RANK_1 && f != Files.FILE_A) square.add(Directions.SOUTH, 1).add(Directions.WEST, 1) else null,
        if (f != Files.FILE_A && r != Ranks.RANK_8) square.add(Directions.WEST, 1).add(Directions.NORTH, 1) else null,
    )
    squares.forEach { mask = mask.set(it) }
    mask
}

val knightMoveMasks = Squares.all.map { square ->
    val f = square.file()
    val r = square.rank()

    var mask = BitBoards.EMPTY
    val squares = listOfNotNull(
        if (r < Ranks.RANK_7 && f != Files.FILE_H) square.add(Directions.NORTH, 2).add(Directions.EAST, 1) else null,
        if (r < Ranks.RANK_7 && f != Files.FILE_A) square.add(Directions.NORTH, 2).add(Directions.WEST, 1) else null,
        if (r > Ranks.RANK_2 && f != Files.FILE_H) square.add(Directions.SOUTH, 2).add(Directions.EAST, 1) else null,
        if (r > Ranks.RANK_2 && f != Files.FILE_A) square.add(Directions.SOUTH, 2).add(Directions.WEST, 1) else null,
        if (f < Files.FILE_G && r != Ranks.RANK_8) square.add(Directions.EAST, 2).add(Directions.NORTH, 1) else null,
        if (f < Files.FILE_G && r != Ranks.RANK_1) square.add(Directions.EAST, 2).add(Directions.SOUTH, 1) else null,
        if (f > Files.FILE_B && r != Ranks.RANK_8) square.add(Directions.WEST, 2).add(Directions.NORTH, 1) else null,
        if (f > Files.FILE_B && r != Ranks.RANK_1) square.add(Directions.WEST, 2).add(Directions.SOUTH, 1) else null,
    )
    squares.forEach { mask = mask.set(it) }
    mask
}
