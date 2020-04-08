package dev.olog.feature.presentation.base.extensions

import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import kotlinx.coroutines.android.awaitFrame

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

suspend fun RecyclerView.awaitAnimationEnd() {
    while (itemAnimator?.isRunning == true) {
        awaitFrame()
    }
}