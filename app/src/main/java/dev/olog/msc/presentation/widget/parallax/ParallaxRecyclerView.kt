package dev.olog.msc.presentation.widget.parallax

import android.content.Context
import android.util.AttributeSet
import android.view.View
import dev.olog.msc.R

class ParallaxRecyclerView(
        context: Context,
        attrs: AttributeSet? = null

) : androidx.recyclerview.widget.RecyclerView(context, attrs) {


    override fun onAttachedToWindow() {
        super.onAttachedToWindow()
        if (!isInEditMode){
            addOnScrollListener(parallaxScrollListener)
        }
    }

    override fun onDetachedFromWindow() {
        super.onDetachedFromWindow()
        removeOnScrollListener(parallaxScrollListener)
    }

    private val parallaxScrollListener = object : androidx.recyclerview.widget.RecyclerView.OnScrollListener() {

        override fun onScrolled(recyclerView: androidx.recyclerview.widget.RecyclerView, dx: Int, dy: Int) {
            if (!isInEditMode){
                val firstVisible = findFirstVisibleItemPosition()
                if (firstVisible > 0) return

                val viewHolder = recyclerView.findViewHolderForAdapterPosition(firstVisible)
                if (viewHolder != null){
                    val img = viewHolder.itemView.findViewById<View>(R.id.cover)
                    val textWrapper = viewHolder.itemView.findViewById<View>(R.id.textWrapper)
                    if (img != null && img is ParallaxImageView){
                        img.translateY(viewHolder.itemView, textWrapper)
                    }
                }
            }
        }
    }

    private fun findFirstVisibleItemPosition(): Int{
        val layoutManager = layoutManager
        return when (layoutManager){
            is androidx.recyclerview.widget.LinearLayoutManager -> layoutManager.findFirstVisibleItemPosition()
            is androidx.recyclerview.widget.GridLayoutManager -> layoutManager.findFirstVisibleItemPosition()
            else -> throw IllegalArgumentException("invalid layout manager class ${layoutManager!!::class}")
        }
    }

}