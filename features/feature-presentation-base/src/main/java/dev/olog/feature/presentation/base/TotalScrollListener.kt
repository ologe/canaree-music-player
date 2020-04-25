package dev.olog.feature.presentation.base

import androidx.recyclerview.widget.RecyclerView
import dev.olog.feature.presentation.base.extensions.isAtTop

class TotalScrollListener(
    private val onScrolled: (Int) -> Unit
) : RecyclerView.OnScrollListener() {

    private var totalScroll: Int = 0

    override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
        totalScroll += dy
        if (recyclerView.isAtTop) {
            totalScroll = 0
        }
        onScrolled(totalScroll)
    }
}