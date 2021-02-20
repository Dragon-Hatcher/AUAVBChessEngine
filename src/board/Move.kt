package board

import kotlin.math.pow

//0-5: from
//6-11: to
//12-13: promotion: 0 = knight, 1 = bishop, 2 = rook, 3 = queen
//14-15: flag: 0 = none, 1 = promotion, 2 = castle, 3 = ep
//   ffppttttttffffff
typealias Move = Int

fun createMove(from: Square, to: Square): Move = from or (to shl 6)
fun createMoveEp(from: Square, to: Square): Move = from or (to shl 6) or MoveTypes.EP
fun createMove(from: Square, to: Square, piece: Int): Move = from or (to shl 6) or piece or MoveTypes.PROMOTION

fun createMoveWCK(): Move = createMove(Squares.E1, Squares.G1) or MoveTypes.CASTLE
fun createMoveWCQ(): Move = createMove(Squares.E1, Squares.C1) or MoveTypes.CASTLE
fun createMoveBCK(): Move = createMove(Squares.E8, Squares.G8) or MoveTypes.CASTLE
fun createMoveBCQ(): Move = createMove(Squares.E8, Squares.C8) or MoveTypes.CASTLE

fun Move.from(): Square = this and 0b111111
fun Move.to(): Square = this and 0b111111000000 shr 6

fun Move.notation(): String = this.from().algebraic() + "" + this.to().algebraic() + this.promotionLetter()

fun Move.type() = this and MoveTypes.MASK
fun Move.promotion() = this and MovePieceType.MASK
fun Move.promotionLetter() = when {
    this.type() != MoveTypes.PROMOTION -> ""
    this.promotion() == MovePieceType.BISHOP -> "b"
    this.promotion() == MovePieceType.KNIGHT -> "n"
    this.promotion() == MovePieceType.ROOK -> "r"
    else -> "q"
}

class MoveTypes {
    companion object {
        const val NORMAL    = 0b00_00_000000_000000
        const val PROMOTION = 0b01_00_000000_000000
        const val CASTLE    = 0b10_00_000000_000000
        const val EP        = 0b11_00_000000_000000
        const val MASK      = 0b11_00_000000_000000
    }
}

class MovePieceType {
    companion object {
        const val BISHOP    = 0b00_00_000000_000000
        const val KNIGHT    = 0b00_01_000000_000000
        const val ROOK      = 0b00_10_000000_000000
        const val QUEEN     = 0b00_11_000000_000000
        const val MASK      = 0b00_11_000000_000000
    }
}

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

val rookMoveMasks: List<BitBoard> = Squares.all.map { square ->
    var b = BitBoards.EMPTY
    for (i in square.file() - 1 downTo 1) {
        val s = Squares.fromRankAndFile(square.rank(), i); b = b.set(s)
    }
    for (i in square.file() + 1..6) {
        val s = Squares.fromRankAndFile(square.rank(), i); b = b.set(s)
    }
    for (i in square.rank() - 1 downTo 1) {
        val s = Squares.fromRankAndFile(i, square.file()); b = b.set(s)
    }
    for (i in square.rank() + 1..6) {
        val s = Squares.fromRankAndFile(i, square.file()); b = b.set(s)
    }
    b
}

val rookMagics: List<ULong> = listOf(
    0x80004000976080UL,
    0x1040400010002000UL,
    0x4880200210000980UL,
    0x5280080010000482UL,
    0x200040200081020UL,
    0x2100080100020400UL,
    0x4280008001000200UL,
    0x1000a4425820300UL,
    0x29002100800040UL,
    0x4503400040201004UL,
    0x209002001004018UL,
    0x1131000a10002100UL,
    0x9000800120500UL,
    0x10e001804820010UL,
    0x29000402000100UL,
    0x2002000d01c40292UL,
    0x80084000200c40UL,
    0x10004040002002UL,
    0x201030020004014UL,
    0x80012000a420020UL,
    0x129010008001204UL,
    0x6109010008040002UL,
    0x950010100020004UL,
    0x803a0000c50284UL,
    0x80004100210080UL,
    0x200240100140UL,
    0x20004040100800UL,
    0x4018090300201000UL,
    0x4802010a00102004UL,
    0x2001000900040002UL,
    0x4a02104001002a8UL,
    0x2188108200204401UL,
    0x40400020800080UL,
    0x880402000401004UL,
    0x10040800202000UL,
    0x604410a02001020UL,
    0x200200206a001410UL,
    0x86000400810080UL,
    0x428200040600080bUL,
    0x2001000041000082UL,
    0x80002000484000UL,
    0x210002002c24000UL,
    0x401a200100410014UL,
    0x5021000a30009UL,
    0x218000509010010UL,
    0x4000400410080120UL,
    0x20801040010UL,
    0x29040040820011UL,
    0x4080400024800280UL,
    0x500200040100440UL,
    0x2880142001004100UL,
    0x412020400a001200UL,
    0x18c028004080080UL,
    0x884001020080401UL,
    0x210810420400UL,
    0x801048745040200UL,
    0x4401002040120082UL,
    0x408200210012UL,
    0x110008200441UL,
    0x2010002004100901UL,
    0x801000800040211UL,
    0x480d000400820801UL,
    0x820104201280084UL,
    0x1001040311802142UL,
)

val rookMagicsLength: List<Int> = listOf(
    12, 11, 11, 11, 11, 11, 11, 12,
    11, 10, 10, 10, 10, 10, 10, 11,
    11, 10, 10, 10, 10, 10, 10, 11,
    11, 10, 10, 10, 10, 10, 10, 11,
    11, 10, 10, 10, 10, 10, 10, 11,
    11, 10, 10, 10, 10, 10, 10, 11,
    11, 10, 10, 10, 10, 10, 10, 11,
    12, 11, 11, 11, 11, 11, 11, 12
).map { 64 - it }

val rookMagicAttacks: List<List<BitBoard>> = Squares.all.map { square ->
    val allSquares = rookMoveMasks[square].toSquareList()
    val combos = (2.0).pow(allSquares.size.toDouble()).toInt()
    val results: MutableMap<ULong, BitBoard> = mutableMapOf()
    for (mask in 0 until combos) {
        val maskedSquares = allSquares.filterIndexed { i, _ -> (mask shr i) and 1 == 1 }.toSet()
        val maskedBitboard = maskedSquares.toBitboard()
        val magicKey = (maskedBitboard * rookMagics[square]) shr rookMagicsLength[square]

        var attacksBB = BitBoards.EMPTY

        for (i in square.file() - 1 downTo 0) {
            val s = Squares.fromRankAndFile(square.rank(), i); attacksBB =
                attacksBB.set(s); if (s in maskedSquares) break; }
        for (i in square.file() + 1..7) {
            val s = Squares.fromRankAndFile(square.rank(), i); attacksBB =
                attacksBB.set(s); if (s in maskedSquares) break; }
        for (i in square.rank() - 1 downTo 0) {
            val s = Squares.fromRankAndFile(i, square.file()); attacksBB =
                attacksBB.set(s); if (s in maskedSquares) break; }
        for (i in square.rank() + 1..7) {
            val s = Squares.fromRankAndFile(i, square.file()); attacksBB =
                attacksBB.set(s); if (s in maskedSquares) break; }

        results[magicKey] = attacksBB
    }

    val magicsCount = (2.0).pow((64 - rookMagicsLength[square]).toDouble()).toULong()
    (0UL..magicsCount).map { results[it] ?: BitBoards.EMPTY }
}

val bishopMoveMasks: List<BitBoard> = Squares.all.map { square ->
    var b = BitBoards.EMPTY
    var x = square.rank()
    var y = square.file()
    fun reset() {
        x = square.rank()
        y = square.file()
    }

    while (x > 1 && y > 1) {
        x--; y--; b = b.set(Squares.fromRankAndFile(x, y))
    }; reset()
    while (x > 1 && y < 6) {
        x--; y++; b = b.set(Squares.fromRankAndFile(x, y))
    }; reset()
    while (x < 6 && y > 1) {
        x++; y--; b = b.set(Squares.fromRankAndFile(x, y))
    }; reset()
    while (x < 6 && y < 6) {
        x++; y++; b = b.set(Squares.fromRankAndFile(x, y))
    }; reset()

    b = b.unset(square)
    b
}

val bishopMagics: List<ULong> = listOf(
    0x1024b002420160UL,
    0x1008080140420021UL,
    0x2012080041080024UL,
    0xc282601408c0802UL,
    0x2004042000000002UL,
    0x12021004022080UL,
    0x880414820100000UL,
    0x4501002211044000UL,
    0x20402222121600UL,
    0x1081088a28022020UL,
    0x1004c2810851064UL,
    0x2040080841004918UL,
    0x1448020210201017UL,
    0x4808110108400025UL,
    0x10504404054004UL,
    0x800010422092400UL,
    0x40000870450250UL,
    0x402040408080518UL,
    0x1000980a404108UL,
    0x1020804110080UL,
    0x8200c02082005UL,
    0x40802009a0800UL,
    0x1000201012100UL,
    0x111080200820180UL,
    0x904122104101024UL,
    0x4008200405244084UL,
    0x44040002182400UL,
    0x4804080004021002UL,
    0x6401004024004040UL,
    0x404010001300a20UL,
    0x428020200a20100UL,
    0x300460100420200UL,
    0x404200c062000UL,
    0x22101400510141UL,
    0x104044400180031UL,
    0x2040040400280211UL,
    0x8020400401010UL,
    0x20100110401a0040UL,
    0x100101005a2080UL,
    0x1a008300042411UL,
    0x120a025004504000UL,
    0x4001084242101000UL,
    0xa020202010a4200UL,
    0x4000002018000100UL,
    0x80104000044UL,
    0x1004009806004043UL,
    0x100401080a000112UL,
    0x1041012101000608UL,
    0x40400c250100140UL,
    0x80a10460a100002UL,
    0x2210030401240002UL,
    0x6040aa108481b20UL,
    0x4009004050410002UL,
    0x8106003420200e0UL,
    0x1410500a08206000UL,
    0x92548802004000UL,
    0x1040041241028UL,
    0x120042025011UL,
    0x8060104054400UL,
    0x20004404020a0a01UL,
    0x40008010020214UL,
    0x4000050209802c1UL,
    0x208244210400UL,
    0x10140848044010UL,
)

val bishopMagicsLength: List<Int> = listOf(
    6, 5, 5, 5, 5, 5, 5, 6,
    5, 5, 5, 5, 5, 5, 5, 5,
    5, 5, 7, 7, 7, 7, 5, 5,
    5, 5, 7, 9, 9, 7, 5, 5,
    5, 5, 7, 9, 9, 7, 5, 5,
    5, 5, 7, 7, 7, 7, 5, 5,
    5, 5, 5, 5, 5, 5, 5, 5,
    6, 5, 5, 5, 5, 5, 5, 6
).map { 64 - it }

val bishopMagicAttacks: List<List<BitBoard>> = Squares.all.map { square ->
    val allSquares = bishopMoveMasks[square].toSquareList()
    val combos = (2.0).pow(allSquares.size.toDouble()).toInt()
    val results: MutableMap<ULong, BitBoard> = mutableMapOf()
    for (mask in 0 until combos) {
        val maskedSquares = allSquares.filterIndexed { i, _ -> (mask shr i) and 1 == 1 }.toSet()
        val maskedBitboard = maskedSquares.toBitboard()
        val magicKey = (maskedBitboard * bishopMagics[square]) shr bishopMagicsLength[square]

        var attacksBB = BitBoards.EMPTY

        var x = square.rank()
        var y = square.file()
        fun reset() {
            x = square.rank()
            y = square.file()
        }

        while (x >= 0 && y >= 0) {
            val s = Squares.fromRankAndFile(x, y);
            x--; y--;
            attacksBB = attacksBB.set(s);
            if (s in maskedSquares) break
        };
        reset()
        while (x >= 0 && y <= 7) {
            val s = Squares.fromRankAndFile(x, y);
            x--; y++;
            attacksBB = attacksBB.set(s);
            if (s in maskedSquares) break
        };
        reset()
        while (x <= 7 && y >= 0) {
            val s = Squares.fromRankAndFile(x, y);
            x++; y--;
            attacksBB = attacksBB.set(s);
            if (s in maskedSquares) break
        };
        reset()
        while (x <= 7 && y <= 7) {
            val s = Squares.fromRankAndFile(x, y);
            x++; y++;
            attacksBB = attacksBB.set(s);
            if (s in maskedSquares) break
        };
        reset()

        attacksBB = attacksBB.unset(square)

        results[magicKey] = attacksBB
    }

    val magicsCount = (2.0).pow((64 - bishopMagicsLength[square]).toDouble()).toULong()
    (0UL..magicsCount).map { results[it] ?: BitBoards.EMPTY }
}