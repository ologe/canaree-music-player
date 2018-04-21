package dev.olog.msc.utils.k.extension

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