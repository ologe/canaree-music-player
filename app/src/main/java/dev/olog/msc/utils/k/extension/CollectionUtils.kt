package dev.olog.msc.utils.k.extension

import java.util.*

fun <T> List<T>.swap(i: Int, j: Int): List<T> {
    Collections.swap(this, i, j)
    return this
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