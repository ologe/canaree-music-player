package dev.olog.shared

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

    return (indexOfFirst { it > value } - 1)
        .coerceIn(0, lastIndex)
}