package com.github.xyzboom

import com.github.xyzboom.PlayerType.AI
import com.github.xyzboom.PlayerType.PLAYER
import com.github.xyzboom.ui.GameFrame
import javax.swing.SwingUtilities



fun main() {
    SwingUtilities.invokeLater {
        GameFrame().apply {

        }
    }
}

/*fun move(point: Point) {
    chessBoard.move(point, currentPlayer)
//    chessBoard.drawCanvas()
    println("com.github.xyzboom.move result :${chessBoard.cells[point.x.toInt()][point.y.toInt()]}")
    if (check(point)) return
    currentPlayer = currentPlayer.switchPlayer()
}

fun check(point: Point): Boolean {
    println("before com.github.xyzboom.check:${chessBoard.cells[point.x.toInt()][point.y.toInt()]}")
    val result = chessBoard.checkWin(point)
    println("win result: $result")
    if (result) {
        finished = true
    }
    return result
}*/

