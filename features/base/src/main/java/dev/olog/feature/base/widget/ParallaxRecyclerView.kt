package dev.olog.feature.base.widget

import android.content.Context
import android.util.AttributeSet
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.dummy.view.*

class ParallaxRecyclerView( // TODO restore in :features:detail
    context: Context,
    attrs: AttributeSet? = null

) : RecyclerView(context, attrs) {


    override fun onAttachedToWindow() {
        super.onAttachedToWindow()
        if (!isInEditMode) {
            addOnScrollListener(parallaxScrollListener)
        }
    }

    override fun onDetachedFromWindow() {
        super.onDetachedFromWindow()
        removeOnScrollListener(parallaxScrollListener)
    }

    private val parallaxScrollListener = object : RecyclerView.OnScrollListener() {

        override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
            if (!isInEditMode) {
                val firstVisible = findFirstVisibleItemPosition()
                if (firstVisible > 0) return

                val viewHolder = recyclerView.findViewHolderForAdapterPosition(firstVisible)
                if (viewHolder != null) {
                    val img = viewHolder.itemView.cover
                    val textWrapper = viewHolder.itemView.textWrapper
                    if (img != null && img is ParallaxImageView) {
                        img.translateY(viewHolder.itemView, textWrapper)
                    }
                }
            }
        }
    }

    private fun findFirstVisibleItemPosition(): Int {
        return when (val layoutManager = layoutManager) {
            is LinearLayoutManager -> layoutManager.findFirstVisibleItemPosition()
            is GridLayoutManager -> layoutManager.findFirstVisibleItemPosition()
            else -> throw IllegalArgumentException("invalid layout manager class ${layoutManager!!::class}")
        }
    }

}