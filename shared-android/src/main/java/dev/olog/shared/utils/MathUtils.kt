package dev.olog.shared.utils

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

fun indexOfClosest(value: Int, list: List<Int>): Int {
    var min = Integer.MAX_VALUE
    var closestIndex = -1

    for (index in 0..list.lastIndex){
        val v = list[index]

        val diff = Math.abs(v - value)

        if (diff < min) {
            min = diff
            closestIndex = index
        }
    }

    return closestIndex
}