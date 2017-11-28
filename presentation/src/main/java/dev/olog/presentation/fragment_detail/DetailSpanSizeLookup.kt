package dev.olog.presentation.fragment_detail

import android.support.v7.widget.GridLayoutManager
import android.support.v7.widget.RecyclerView
import dev.olog.presentation.R

class DetailSpanSizeLookup(
        list: RecyclerView

) : GridLayoutManager.SpanSizeLookup() {

    private val adapter = list.adapter as DetailAdapter

    override fun getSpanSize(position: Int): Int {
        return if (adapter.getItem(position).type == R.layout.item_detail_album) 1 else 2
    }
}