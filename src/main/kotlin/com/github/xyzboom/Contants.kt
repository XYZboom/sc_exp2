package com.github.xyzboom

const val MAX_DEPTH = 3
const val MAX_SHAPE_LEN = 6
val direction = listOf(
    0 to 1,
    1 to 0,
    1 to 1,
    1 to -1,
    0 to -1,
    -1 to 0,
    -1 to 1,
    -1 to -1,
/*    0 to 1,
    1 to 0,
    1 to 1,
    1 to -1,
    0 to -1,
    -1 to 0,
    -1 to 1,
    -1 to -1,*/
)

const val BOARD_SIZE = 19
const val BOARD_UI_WIDTH = 600

val EstimatedScore = listOf(
    1 to intArrayOf(1, 1, 0),
    1 to intArrayOf(0, 1, 1),
    3 to intArrayOf(0, 1, 0, 1, 0),
    5 to intArrayOf(0, 1, 1, 0),
    7 to intArrayOf(0, 1, 1, 1),
    7 to intArrayOf(1, 1, 1, 0),
    10 to intArrayOf(0, 1, 1, 0, 1),
    10 to intArrayOf(1, 1, 0, 1, 0),
    30 to intArrayOf(0, 1, 1, 1, 0),
    30 to intArrayOf(0, 1, 1, 0, 1, 0),
    30 to intArrayOf(0, 1, 0, 1, 1, 0),
//    40 to intArrayOf(1, 1, 1, 1, 0),
//    40 to intArrayOf(0, 1, 1, 1, 1),
//    40 to intArrayOf(1, 1, 1, 0, 1),
//    40 to intArrayOf(1, 1, 0, 1, 1),
//    40 to intArrayOf(1, 0, 1, 1, 1),
    70 to intArrayOf(0, 1, 1, 1, 1, 0),
    Int.MAX_VALUE to intArrayOf(1, 1, 1, 1, 1),
    Int.MAX_VALUE to intArrayOf(0, 1, 1, 1, 1, 1),
    Int.MAX_VALUE to intArrayOf(1, 1, 1, 1, 1, 0),
)