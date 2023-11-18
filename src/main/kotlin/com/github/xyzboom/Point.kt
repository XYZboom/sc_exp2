package com.github.xyzboom

class Point(val x: Double, val y: Double) {

    constructor(x: Int, y: Int) : this(x.toDouble(), y.toDouble())

    enum class PointType(val value: Int) {
        EMPTY(0), BLACK(1), WHITE(2);
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other == null || this::class != other::class) return false

        other as Point

        if (x != other.x) return false
        if (y != other.y) return false

        return true
    }

    override fun hashCode(): Int {
        var result = x.hashCode()
        result = 31 * result + y.hashCode()
        return result
    }

    operator fun component1() = x.toInt()
    operator fun component2() = y.toInt()

    override fun toString(): String {
        return "com.github.xyzboom.Point(x=$x, y=$y)"
    }

}