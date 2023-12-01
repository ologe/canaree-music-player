package dev.olog.shared.android.extensions

import androidx.recyclerview.widget.ListAdapter

fun <T : Any> ListAdapter<T, *>.indexOf(predicate: (T) -> Boolean): Int {
    return currentList.indexOfFirst(predicate)
}