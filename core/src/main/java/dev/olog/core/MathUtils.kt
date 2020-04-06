package dev.olog.core

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

/**
 * list = | 100 | 200 | 300 | 400 |
 * value = 202
 * returns 1
 */
fun List<Long>.indexOfClosest(value: Long): Int {
    if (isEmpty()) {
        return -1
    }

    if (value > last()) {
        return lastIndex
    }

    return clamp(
        indexOfFirst { it > value } - 1,
        0,
        lastIndex
    )
}