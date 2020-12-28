package dev.olog.feature.base.adapter

import androidx.recyclerview.widget.RecyclerView

fun interface SetupNestedList {
    fun setupNestedList(layoutId: Int, recyclerView: RecyclerView)
}