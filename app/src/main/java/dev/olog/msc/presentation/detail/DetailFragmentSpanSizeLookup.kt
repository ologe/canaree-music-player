package dev.olog.msc.presentation.detail

import android.support.v7.widget.GridLayoutManager
import dev.olog.msc.R

class DetailFragmentSpanSizeLookup(
        private val adapter: DetailFragmentAdapter

) : GridLayoutManager.SpanSizeLookup() {

    override fun getSpanSize(position: Int): Int {
        val item = adapter.elementAt(position)
        return if (item.type == R.layout.item_detail_album) 1 else 2
    }
}