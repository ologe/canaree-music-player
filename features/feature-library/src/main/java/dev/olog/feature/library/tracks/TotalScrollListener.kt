package dev.olog.feature.library.tracks

import androidx.recyclerview.widget.RecyclerView

class TotalScrollListener(
    private val onScrolled: (Int) -> Unit
) : RecyclerView.OnScrollListener() {

    private var totalScroll: Int = 0

    override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
        totalScroll += dy
        onScrolled(totalScroll)
    }
}