package com.github.xyzboom

import java.util.Arrays

@OptIn(ExperimentalStdlibApi::class)
class ChessBoard(
    val cells: Array<Array<Point.PointType>> = Array(BOARD_SIZE) {
        Array(BOARD_SIZE) {
            Point.PointType.EMPTY
        }
    },
    val operationSequence: ArrayDeque<Pair<Point, PlayerType>> = ArrayDeque(),
) {
    val lastPoint get() = operationSequence.lastOrNull()?.first
    fun copy(): ChessBoard {
        return ChessBoard(Array(BOARD_SIZE) {
            cells[it].copyOf()
        }, ArrayDeque(operationSequence))
    }

    fun checkBoundary(x: Int, y: Int): Boolean {
        @Suppress("ConvertTwoComparisonsToRangeCheck")
        return x >= 0 && x < BOARD_SIZE && y >= 0 && y < BOARD_SIZE
    }

    fun checkWin(point: Point): Boolean {
        val (x, y) = point
//        println("checkWin $point")
//        println("${cells[x][y]}")
//        println("${operationSequence}")
        for (offset in 0 ..< 5) {
            val match = arrayOf(0, 0, 0, 0)
            for (i in -offset ..< -offset + 5) {
                if (this.checkBoundary(x + i, y) && this.cells[x + i][y] === this.cells[x][y]) {
//                    println("${cells[x + i][y]}, ${this.cells[x][y]}")
                    match[0]++
                }
                if (this.checkBoundary(x, y + i) && this.cells[x][y + i] === this.cells[x][y]) {
//                    println("${cells[x][y + i]}, ${this.cells[x][y]}")
                    match[1]++
                }
                if (this.checkBoundary(x + i, y + i) && this.cells[x + i][y + i] === this.cells[x][y]) {
//                    println("${cells[x + i][y + i]}, ${this.cells[x][y]}")
                    match[2]++
                }
                if (this.checkBoundary(x + i, y - i) && this.cells[x + i][y - i] === this.cells[x][y]) {
//                    println("${cells[x + i][y - i]}, ${this.cells[x][y]}")
                    match[3]++
                }
            }
            if (match.any { it == 5 }) {
                return true
            }
        }
        return false
    }

    fun move(point: Point, currentPlayer: PlayerType) {
        cells[point.x.toInt()][point.y.toInt()] = currentPlayer.toPointType()
        operationSequence.addLast(point to currentPlayer)
    }

    fun undo() {
        if (this.operationSequence.size == 0) return
        val (point, _) = this.operationSequence.removeLast()
        val (x, y) = point
        cells[x][y] = Point.PointType.EMPTY
    }


}