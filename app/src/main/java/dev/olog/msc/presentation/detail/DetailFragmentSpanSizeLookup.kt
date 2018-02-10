package dev.olog.msc.presentation.detail

import android.support.v7.widget.GridLayoutManager
import android.support.v7.widget.RecyclerView
import dev.olog.msc.R

class DetailFragmentSpanSizeLookup(
        list: RecyclerView

) : GridLayoutManager.SpanSizeLookup() {

    private val adapter = list.adapter  as DetailFragmentAdapter

    override fun getSpanSize(position: Int): Int {
        val item = adapter.getItemAt(position)
        return if (item.type == R.layout.item_detail_album) 1 else 2
    }
}