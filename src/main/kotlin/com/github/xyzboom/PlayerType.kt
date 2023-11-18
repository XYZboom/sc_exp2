package com.github.xyzboom

enum class PlayerType(val value: Int) {
    PLAYER(1),
    AI(2);

    fun switchPlayer(): PlayerType {
        return when (this) {
            PLAYER -> AI
            AI -> PLAYER
        }
    }

    fun toPointType(): Point.PointType {
        return when (this) {
            PLAYER -> Point.PointType.BLACK
            AI -> Point.PointType.WHITE
        }
    }
}