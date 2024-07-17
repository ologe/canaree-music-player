package dev.olog.presentation.base.adapter

import androidx.recyclerview.widget.ListAdapter

fun <T> ListAdapter<T, *>.indexOf(predicate: (T) -> Boolean): Int {
    return currentList.indexOfFirst(predicate)
}