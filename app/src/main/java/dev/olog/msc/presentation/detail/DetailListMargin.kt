package dev.olog.msc.presentation.detail

import android.content.Context
import android.graphics.Rect
import android.support.v7.widget.RecyclerView
import android.view.View
import dev.olog.msc.R
import dev.olog.msc.dagger.qualifier.ApplicationContext
import dev.olog.msc.utils.k.extension.dimen
import javax.inject.Inject

class DetailListMargin @Inject constructor(
        @ApplicationContext context: Context

) : RecyclerView.ItemDecoration() {

    private val margin = context.dimen(R.dimen.detail_album_margin_horizontal)

    override fun getItemOffsets(outRect: Rect, view: View, parent: RecyclerView, state: RecyclerView.State) {
        super.getItemOffsets(outRect, view, parent, state)
        val holder = parent.getChildViewHolder(view)
        if (holder != null && holder.itemViewType == R.layout.item_detail_album){
            val position = parent.getChildAdapterPosition(view)
            searchForAlbumsHeader(outRect, parent, position)
        }
    }

    private fun searchForAlbumsHeader(outRect: Rect, parent: RecyclerView, currentViewPosition: Int){
        for (index in 1..4){
            parent.getChildAt(currentViewPosition - index)?.let {
                val upperHolder = parent.getChildViewHolder(it)
                if (upperHolder.itemViewType != R.layout.item_detail_album){
                    applyMargin(outRect, index % 2 == 1)
                    return
                }
            }
        }
    }

    private fun applyMargin(outRect: Rect, isLeft: Boolean){
        if (isLeft){
            outRect.left = margin
        } else {
            outRect.right = margin
        }
    }

}