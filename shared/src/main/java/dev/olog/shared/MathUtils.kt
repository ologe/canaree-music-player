package dev.olog.shared

import kotlin.math.abs

fun clamp(value: Int, min: Int, max: Int): Int {
    if (value < min) {
        return min
    } else if (value > max) {
        return max
    }
    return value
}

fun clamp(value: Long, min: Long, max: Long): Long {
    if (value < min) {
        return min
    } else if (value > max) {
        return max
    }
    return value
}

fun clamp(value: Float, min: Float, max: Float): Float {
    if (value < min) {
        return min
    } else if (value > max) {
        return max
    }
    return value
}

fun indexOfClosest(value: Long, list: List<Long>): Int {
    var min = Long.MAX_VALUE
    var closestIndex = -1

    for (index in 0..list.lastIndex) {
        val v = list[index]

        val diff = abs(v - value)

        if (diff < min) {
            min = diff
            closestIndex = index
        }
    }

    return closestIndex
}