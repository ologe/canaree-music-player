package dev.olog.presentation.fragment_detail

import android.content.Context
import android.graphics.Rect
import android.support.v7.widget.RecyclerView
import android.view.View
import org.jetbrains.anko.dip

class DetailFragmentHorizontalMarginDecoration(
        context: Context

) : RecyclerView.ItemDecoration() {

    private val padding = context.dip(8)

    override fun getItemOffsets(outRect: Rect, view: View, parent: RecyclerView, state: RecyclerView.State) {
        val position = parent.getChildAdapterPosition(view)
        if (position > 0){
            outRect.left = padding
            outRect.right = padding
        } else {
            super.getItemOffsets(outRect, view, parent, state)
        }
    }

}