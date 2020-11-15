package dev.olog.shared

import java.util.*

fun <T> List<T>.swapped(i: Int, j: Int): List<T> {
    if (isInBounds(i) && isInBounds(j)){
        val copy = toMutableList()
        Collections.swap(copy, i, j)
        return copy
    }
    error("invalid swap from $i to $j, list has $size items")
}

fun <T> MutableList<T>.swap(i: Int, j: Int) {
    if (isInBounds(i) && isInBounds(j)){
        Collections.swap(this, i, j)
    } else {
        error("invalid swap from $i to $j, list has $size items")
    }
}

@Suppress("NOTHING_TO_INLINE")
inline fun <T> List<T>.isInBounds(index: Int): Boolean {
    return index in 0..lastIndex
}

fun <T> MutableList<T>.removeFirst(predicate: (T) -> Boolean): Boolean {
    for (t in this) {
        if (predicate(t)) {
            this.remove(t)
            return true
        }
    }
    return false
}

operator fun<T> List<T>.component6() = get(5)
operator fun<T> List<T>.component7() = get(6)
operator fun<T> List<T>.component8() = get(7)
operator fun<T> List<T>.component9() = get(8)