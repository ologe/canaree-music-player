@file:Suppress("NOTHING_TO_INLINE")

package dev.olog.shared

import java.util.*

fun <T> List<T>.swap(i: Int, j: Int): List<T> {
    if (isInBounds(i) && isInBounds(j)){
        Collections.swap(this, i, j)
    }
    return this
}

fun <T> List<T>.isInBounds(index: Int): Boolean {
    return index in 0..lastIndex
}

fun <T> List<T>.startWith(item: T): List<T> {
    val list = this.toMutableList()
    list.add(0, item)
    return list
}

fun <T> List<T>.startWith(data: List<T>): List<T> {
    val list = this.toMutableList()
    list.addAll(0, data)
    return list
}

fun <T> List<T>.startWithIfNotEmpty(item: T): List<T> {
    if (this.isNotEmpty()){
        return startWith(item)
    }
    return this
}

fun <T> MutableList<T>.doIf(predicate: Boolean, action: MutableList<T>.() -> Any): MutableList<T> {
    if (predicate){
        this.action()
    }
    return this
}

fun <T> MutableList<T>.removeFirst(predicate: (T) -> Boolean): Boolean {
    val index = indexOfFirst(predicate)
    if (isInBounds(index)) {
        return removeAt(index) != null
    }
    return false
}

operator fun<T> List<T>.component6() = get(5)