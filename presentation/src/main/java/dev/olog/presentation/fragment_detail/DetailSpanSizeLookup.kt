package dev.olog.presentation.fragment_detail

import android.support.v7.widget.GridLayoutManager
import android.support.v7.widget.RecyclerView
import dev.olog.presentation.R
import dev.olog.presentation.utils.delegates.weakRef

class DetailSpanSizeLookup(
        list: RecyclerView

) : GridLayoutManager.SpanSizeLookup() {

    private val adapter by weakRef(list.adapter as DetailAdapter)

    override fun getSpanSize(position: Int): Int {
        val item = adapter.getItemAt(position)
        return if (item.type == R.layout.item_detail_album) 1 else 2
    }
}