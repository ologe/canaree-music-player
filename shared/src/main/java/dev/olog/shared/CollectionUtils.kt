package dev.olog.shared

import java.util.*

fun <T> MutableList<T>.clearThenAdd(list: List<T>) {
    clear()
    addAll(list)
}

fun <Key, Value> MutableMap<Key, Value>.clearThenPut(map: MutableMap<Key, Value>) {
    this.clear()
    this.putAll(map)
}

fun <T> List<T>.shuffleAndSwap(isShuffleModeEnabled: Boolean,
                               predicate: (T) -> Boolean): List<T> {

    val item = this.first(predicate)

    if (isShuffleModeEnabled){
        shuffle()
    }

    val songPosition = this.indexOf(item)
    if (songPosition != 0){
        swap(0, songPosition)
    }

    return this
}

fun <T> List<T>.shuffle(): List<T> {
    Collections.shuffle(this)
    return this
}

fun <T> List<T>.swap(i: Int, j: Int): List<T> {
    Collections.swap(this, i, j)
    return this
}