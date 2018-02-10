package dev.olog.msc.utils.k.extension

import java.util.*

fun <T> MutableList<T>.clearThenAdd(list: List<T>) {
    clear()
    addAll(list)
}

fun <Key, Value> MutableMap<Key, Value>.clearThenPut(map: MutableMap<Key, Value>) {
    this.clear()
    this.putAll(map)
}

fun <T> List<T>.shuffle(): List<T> {
    Collections.shuffle(this)
    return this
}

fun <T> List<T>.swap(i: Int, j: Int): List<T> {
    Collections.swap(this, i, j)
    return this
}