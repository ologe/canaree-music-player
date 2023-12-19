package dev.olog.shared

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

fun remap(iMin: Float, iMax: Float, oMin: Float, oMax: Float, v: Float): Float {
    val t = inverseLerp(iMin, iMax, v)
    return lerp(oMin, oMax, t)
}

fun lerp(a: Float, b: Float, t: Float): Float {
    return (1f - t) * a + b * t;
}

fun inverseLerp(a: Float, b: Float, v: Float): Float {
    return (v - a) / (b - a)
}