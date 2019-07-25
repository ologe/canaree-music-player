package dev.olog.shared.android.extensions

import android.util.LongSparseArray
import androidx.core.util.forEach

fun <T> LongSparseArray<T>.toList(): List<T>{
    val list = mutableListOf<T>()

    this.forEach { _, value -> list.add(value) }

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