@file:Suppress("NOTHING_TO_INLINE")

package dev.olog.shared.extensions

import java.util.*

fun <T> List<T>.swap(i: Int, j: Int): List<T> {
    if (isInBounds(i) && isInBounds(j)){
        Collections.swap(this, i, j)
    }
    return this
}

private fun <T> List<T>.isInBounds(index: Int): Boolean {
    return index in 0..lastIndex
}

inline fun <T> List<T>.startWith(item: T): List<T> {
    val list = this.toMutableList()
    list.add(0, item)
    return list
}

inline fun <T> List<T>.startWith(data: List<T>): List<T> {
    val list = this.toMutableList()
    list.addAll(0, data)
    return list
}

inline fun <T> List<T>.startWithIfNotEmpty(item: T): List<T> {
    if (this.isNotEmpty()){
        return startWith(item)
    }
    return this
}

inline fun <T> List<T>.startWithIfNotEmpty(item: List<T>): List<T> {
    if (this.isNotEmpty()){
        return startWith(item)
    }
    return this
}

inline fun <K, V> MutableMap<K, MutableList<V>>.deepCopy(): MutableMap<K, MutableList<V>> {
    val result = mutableMapOf<K, MutableList<V>>()
    for ((key, value) in this){
        result[key] = value.toMutableList()
    }
    return result
}

fun <T> MutableList<T>.doIf(predicate: Boolean, action: MutableList<T>.() -> Any): MutableList<T> {
    if (predicate){
        this.action()
    }
    return this
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