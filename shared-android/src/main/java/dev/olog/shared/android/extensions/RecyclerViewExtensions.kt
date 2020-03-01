package dev.olog.shared.android.extensions

import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

val RecyclerView.findFirstVisibleItem: Int
    get() {
        return when (val layoutManager = this.layoutManager) {
            is LinearLayoutManager -> layoutManager.findFirstVisibleItemPosition()
            is GridLayoutManager -> layoutManager.findFirstVisibleItemPosition()
            null -> throw IllegalArgumentException("layout manager not set")
            else -> throw IllegalArgumentException("invalid layout manager type=${layoutManager::class.java.canonicalName}")
        }
    }

val RecyclerView.findLastVisibleItem: Int
    get() {
        return when (val layoutManager = this.layoutManager) {
            is LinearLayoutManager -> layoutManager.findLastVisibleItemPosition()
            is GridLayoutManager -> layoutManager.findLastVisibleItemPosition()
            null -> throw IllegalArgumentException("layout manager not set")
            else -> throw IllegalArgumentException("invalid layout manager type=${layoutManager::class.java.canonicalName}")
        }
    }