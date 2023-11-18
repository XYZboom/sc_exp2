package com.github.xyzboom.ui

import com.github.xyzboom.*
import com.github.xyzboom.Point
import java.awt.*
import java.awt.event.MouseAdapter
import java.awt.event.MouseEvent
import javax.swing.JButton
import javax.swing.JFrame
import javax.swing.JLabel
import javax.swing.JPanel
import javax.swing.SwingWorker
import kotlin.math.roundToInt

class GameFrame : JFrame() {
    var chessBoard = ChessBoard()
    var currentPlayer = PlayerType.PLAYER
    var finished = false
    private val boardPanel = BoardPanel()
    private val startButton = JButton("开始")
    private val undoButton = JButton("悔棋")
    private val infoLabel = JLabel()

    init {
        title = "五子棋游戏"
        defaultCloseOperation = EXIT_ON_CLOSE
        setSize(600, 600)
        isVisible = true

        layout = BorderLayout()
        val southPanel = JPanel()
        southPanel.add(startButton)
        southPanel.add(undoButton)
        add(southPanel, BorderLayout.SOUTH)
        add(infoLabel)

        val centerPanel = JPanel()
        centerPanel.layout = GridBagLayout()
        centerPanel.add(boardPanel)

        add(centerPanel, BorderLayout.CENTER)

        startButton.addMouseListener(object :MouseAdapter() {
            override fun mouseClicked(e: MouseEvent) {
                currentPlayer = PlayerType.PLAYER
                chessBoard = ChessBoard()
                finished = false
                repaint()
            }
        })

        undoButton.addMouseListener(object : MouseAdapter() {
            override fun mouseClicked(e: MouseEvent) {
                if (currentPlayer === PlayerType.PLAYER || finished) {
                    chessBoard.undo()
                    chessBoard.undo()
                    finished = false
                    repaint()
                }
            }
        })

        boardPanel.addMouseListener(object : MouseAdapter() {
            override fun mouseClicked(e: MouseEvent) {
                val x = e.x / (e.component.width / BOARD_SIZE)
                val y = e.y / (e.component.height / BOARD_SIZE)
                if (currentPlayer === PlayerType.PLAYER
                    && chessBoard.cells[x][y] == Point.PointType.EMPTY
                ) {
                    move(Point(x, y))
                    if (!finished) {
                        println("ai start")
                        val worker = object : SwingWorker<Point, Unit>() {
                            override fun doInBackground(): Point {
                                return aiPlay(chessBoard.copy(), 5.0)
                            }

                            override fun done() {
                                move(get())
                            }
                        }
                        worker.execute()
                    }
                }
            }
        })

        // 设置窗口的最小大小为600x600
        minimumSize = Dimension((BOARD_UI_WIDTH * 1.3).toInt(), (BOARD_UI_WIDTH * 1.3).toInt())
    }

    private fun move(point: Point) {
        chessBoard.move(point, currentPlayer)
        boardPanel.repaint()
        println("move result :${chessBoard.cells[point.x.toInt()][point.y.toInt()]}")
        if (check(point)) return
        currentPlayer = currentPlayer.switchPlayer()
    }

    private fun check(point: Point): Boolean {
        println("before check:${chessBoard.cells[point.x.toInt()][point.y.toInt()]}")
        val result = chessBoard.checkWin(point)
        println("win result: $result")
        if (result) {
            finished = true
            infoLabel.text = (
                    when (currentPlayer) {
                        PlayerType.PLAYER -> "你赢了"
                        PlayerType.AI -> "你输了"
                    }
                    )
        }
        return result
    }

    inner class BoardPanel : JPanel() {

        init {
            preferredSize = Dimension(BOARD_UI_WIDTH, BOARD_UI_WIDTH)
        }

        override fun paintComponent(g: Graphics) {
            super.paintComponent(g)

            val cellSize = BOARD_UI_WIDTH * 1.0 / BOARD_SIZE

            // 绘制棋盘
            g.color = Color(139, 69, 19)  // 棋盘颜色
            g.fillRect(0, 0, width, height)

            g.color = Color.BLACK
            for (i in 0 until BOARD_SIZE) {
                g.drawLine(
                    (cellSize / 2 - 1).roundToInt(), (i * cellSize + cellSize / 2).roundToInt(),
                    (BOARD_UI_WIDTH - cellSize / 2 + 1).roundToInt(), (i * cellSize + cellSize / 2).roundToInt()
                )
                g.drawLine(
                    (i * cellSize + cellSize / 2).roundToInt(), (cellSize / 2 - 1).roundToInt(),
                    (i * cellSize + cellSize / 2).roundToInt(), (BOARD_UI_WIDTH - cellSize / 2 + 1).roundToInt()
                )
            }

            // 绘制棋子
            for (x in 0 until BOARD_SIZE) {
                for (y in 0 until BOARD_SIZE) {
                    val piece = chessBoard.cells[x][y]
                    if (piece != Point.PointType.EMPTY) {
                        val centerX = x * cellSize + cellSize / 2
                        val centerY = y * cellSize + cellSize / 2
                        if (piece == Point.PointType.BLACK) {
                            g.color = Color.BLACK
                        } else {
                            g.color = Color.WHITE
                        }
                        g.fillOval(
                            (centerX - cellSize / 2).roundToInt(),
                            (centerY - cellSize / 2).roundToInt(),
                            cellSize.roundToInt(), cellSize.roundToInt()
                        )
                        if (x == chessBoard.lastPoint?.x?.toInt() && y == chessBoard.lastPoint?.y?.toInt()) {
                            g.color = Color.GREEN
                            g as Graphics2D
                            g.stroke = BasicStroke(1.5f)
                            g.drawOval(
                                (centerX - cellSize / 2).roundToInt(),
                                (centerY - cellSize / 2).roundToInt(),
                                cellSize.roundToInt(), cellSize.roundToInt()
                            )
                        }
                    }
                }
            }
        }
    }
}