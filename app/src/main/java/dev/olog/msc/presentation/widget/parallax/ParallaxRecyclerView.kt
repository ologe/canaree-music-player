package dev.olog.msc.presentation.widget.parallax

import android.content.Context
import android.support.v7.widget.GridLayoutManager
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.util.AttributeSet
import android.view.View
import dev.olog.msc.R

class ParallaxRecyclerView(
        context: Context,
        attrs: AttributeSet? = null

) : RecyclerView(context, attrs) {


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

    private val parallaxScrollListener = object : RecyclerView.OnScrollListener() {

        override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
            if (!isInEditMode){
                val firstVisible = findFirstVisibleItemPosition()
                if (firstVisible > 0) return

                val viewHolder = recyclerView.findViewHolderForAdapterPosition(firstVisible)
                if (viewHolder != null){
                    val img = viewHolder.itemView.findViewById<View>(R.id.cover)
                    if (img != null && img is ParallaxImageView){
                        img.translateY(viewHolder.itemView)
                    }
                }
            }
        }
    }

    private fun findFirstVisibleItemPosition(): Int{
        val layoutManager = layoutManager
        return when (layoutManager){
            is LinearLayoutManager -> layoutManager.findFirstVisibleItemPosition()
            is GridLayoutManager -> layoutManager.findFirstVisibleItemPosition()
            else -> throw IllegalArgumentException("invalid layout manager class ${layoutManager!!::class}")
        }
    }

}