package dev.olog.shared.android.extensions

import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

fun RecyclerView.findFirstVisibleItemPosition(): Int {
    val layoutManager = layoutManager ?: return RecyclerView.NO_POSITION
    return when (layoutManager) {
        is GridLayoutManager -> layoutManager.findFirstVisibleItemPosition()
        is LinearLayoutManager -> layoutManager.findFirstVisibleItemPosition()
        else -> throw IllegalArgumentException("invalid layout manager class ${layoutManager::class}")
    }
}