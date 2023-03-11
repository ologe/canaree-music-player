package dev.olog.shared

import android.util.LongSparseArray

fun <T> LongSparseArray<T>.toList(): List<T>{
    val list = mutableListOf<T>()

    for (index in 0 until size()) {
        list.add(valueAt(index))
    }
    return list
}

fun <T> LongSparseArray<T>.toggle(key: Long, item: T){
    val current = this.get(key)
    if (current == null){
        this.append(key, item)
    } else {
        this.remove(key)
    }
}