package com.github.xyzboom

import kotlin.math.abs
import kotlin.math.max

@OptIn(ExperimentalStdlibApi::class)
fun aiPlay(chessBoard: ChessBoard, ratio: Double): Point {
    var cntSearch = 0
    var cntCut = 0
    var result = Point(0, 0)

    fun calculateScore(
        player: PlayerType,
        x: Int,
        y: Int,
        scoreShapes: ArrayList<Triple<Int, Point, List<Point>>>,
    ): Int {
        var totalScore = 0
        outer@ for (dir in 0 ..< 4) {
            val (dx, dy) = direction[dir]

            // 如果此方向上，该点已经有得分形状，不重复计算
            for ((_, direction, points) in scoreShapes) {
                if (direction.x.toInt() == dx && direction.y.toInt() == dy) {
                    var pointsIndex = 0
                    var anyPointEquals = false
                    while (pointsIndex < points.size) {
                        val point = points[pointsIndex]
                        if (point.x.toInt() == dx && point.y.toInt() == dy) {
                            anyPointEquals = true
                            break
                        }
                        pointsIndex++
                    }
                    if (anyPointEquals)
                        continue@outer
                }
            }

            // 在一个方向上，只取最大的得分项
            var maxScore = Triple<Int, Point?, List<Point>>(0, null, ArrayList())

            val shapes = ArrayDeque<Point>() // 获取棋型

            for (offset in -MAX_SHAPE_LEN ..< 0) {
                val nx = x + offset * dx
                val ny = y + offset * dy
                shapes.addLast(Point(nx, ny))
            }

            // 通过移动头尾来更新棋型, 减少循环次数
            for (offset in 0 ..< MAX_SHAPE_LEN) {
                val nx = x + offset * dx
                val ny = y + offset * dy

                shapes.removeFirst()
                shapes.addLast(Point(nx, ny))
                var i = EstimatedScore.size - 1
                while (i >= 0 && EstimatedScore[i].first > maxScore.first) {
                    val (score, targetShape) = EstimatedScore[i]
                    // 可以理解为, 当前棋子是目标棋型的第 (targetShape.length - offset) 颗棋子
                    if (targetShape.size - offset < 0) {
                        i--
                        continue
                    }
                    // 那么由于滑动窗口从左移到右, 实际匹配的应该时是从尾部向前匹配
                    val dis = shapes.size - targetShape.size

                    var notMatched = false
                    var targetIndex = 0
                    while (targetIndex < targetShape.size) {
                        val pt = shapes[targetIndex + dis]
                        val (ptX, ptY) = pt
                        val ptType = if (chessBoard.checkBoundary(ptX, ptY)) {
                            chessBoard.cells[ptX][ptY].value
                        } else -1
                        if (targetShape[targetIndex] * player.value != ptType) {
                            notMatched = true
                            break
                        }
                        targetIndex += 1
                    }
                    if (!notMatched) {
                        // println("find maxScore: $maxScore")
                        maxScore = Triple(score, Point(dx, dy), shapes)
                    }
                    i--
                }
            }

            if (maxScore.second === null) continue

            // 计算两个形状相交，如两个三子相交，得分增加
            // 一个子的除外
            for ((score, _, shape) in scoreShapes) {
                var pt2Index = 0
                while (pt2Index < maxScore.third.size) {
                    val pt2 = maxScore.third[pt2Index]
                    var pt1Index = 0
                    while (pt1Index < shape.size) {
                        val pt1 = shape[pt1Index]
                        if (pt1 == pt2) {
                            totalScore += (score + maxScore.first) * 4
                        }
                        pt1Index++
                    }
                    pt2Index++
                }
            }

            scoreShapes.add(Triple(maxScore.first, maxScore.second!!, maxScore.third))
//            println("maxScore: $maxScore")
            totalScore += maxScore.first
        }
        return totalScore
    }

    fun evaluate(player: PlayerType): Int {
        val operations = chessBoard.operationSequence
        var totalScore = 0

        // 自己的得分
        var myScore = 0
        val myScoreShapes = ArrayList<Triple<Int, Point, List<Point>>>()

        // 敌人的得分
        var opponentScore = 0
        val opponentScoreShapes = ArrayList<Triple<Int, Point, List<Point>>>()

        for (operation in operations) {
            val (point, type) = operation
            val (x, y) = point
            if (type === player) {
                myScore += calculateScore(type, x, y, myScoreShapes)
            } else {
                opponentScore += calculateScore(type, x, y, opponentScoreShapes)
            }
        }
        if (player === PlayerType.AI) {
            opponentScore *= ratio.toInt()
            println("AI: $myScore Player: $opponentScore")
        } else {
            myScore *= ratio.toInt()
            println("AI: $opponentScore Player: $myScore")
        }
        totalScore = myScore - opponentScore

        return totalScore
    }

    fun inSearchScope(x: Int, y: Int): Boolean {
        for ((dx, dy) in direction) {
            val nx = x + dx
            val ny = y + dy
            if (chessBoard.checkBoundary(nx, ny) && chessBoard.cells[nx][ny] !== Point.PointType.EMPTY) {
                return true
            }
        }

        return false
    }

    fun negamax(currentPlayer: PlayerType, depth: Int, alpha: Int, beta: Int): Int {
//        println("negamax: $com.github.xyzboom.getCurrentPlayer, $depth, $alpha, $beta")
        var alpha = alpha
        if (depth == 0 || chessBoard.checkWin(chessBoard.operationSequence.last().first)) {
            println("开始估计: depth: $depth")
            return evaluate(currentPlayer)
        }

        val blankPoints = ArrayList<Point>(BOARD_SIZE * BOARD_SIZE)

        // 获取当前可落子位置, 并跳过没有相邻棋子的点
        for (x in 0 ..< BOARD_SIZE) {
            for (y in 0 ..< BOARD_SIZE) {
                if (chessBoard.cells[x][y] === Point.PointType.EMPTY
                    && inSearchScope(x, y)
                ) {
                    blankPoints.add(Point(x, y))
                }
            }
        }

        val lastPoint = chessBoard.operationSequence.last().first
//        println("last: $lastPoint")
        // 按最后落子的位置排序
        blankPoints.sortWith { a, b ->
            return@sortWith max(
                abs(a.x - lastPoint.x) + abs(a.y - lastPoint.y),
                abs(b.x - lastPoint.x) + abs(b.y - lastPoint.y)
            ).toInt()
        }
//        println("blankPoints: $blankPoints")
        cntSearch += blankPoints.size
        for (point in blankPoints) {
            chessBoard.move(point, currentPlayer)
//            println("try: $point")
            val value = -(negamax(currentPlayer.switchPlayer(), depth - 1, -beta, -alpha))
            chessBoard.undo()

            if (value > alpha) {
                if (depth == MAX_DEPTH) {
//                    println("value: $value alpha: $alpha beta: $beta")
                    result = point
                }
                // alpha + beta剪枝
                if (value >= beta) {
                    cntCut++
                    return beta
                }
                alpha = value
            }
        }
        return alpha
    }

    negamax(PlayerType.AI, MAX_DEPTH, -Int.MAX_VALUE, Int.MAX_VALUE)
//    negamax(com.github.xyzboom.PlayerType.AI, 0, -Int.MAX_VALUE, Int.MAX_VALUE)
    print("本次共剪枝次数：${cntCut}, ")
    print("本次共搜索次数：${cntSearch}, ")
    println("(${result.x}, ${result.y})")

    return result
}