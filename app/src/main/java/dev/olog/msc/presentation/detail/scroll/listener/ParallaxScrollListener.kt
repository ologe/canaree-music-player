package dev.olog.msc.presentation.detail.scroll.listener

import android.support.v7.widget.RecyclerView
import android.view.View
import dev.olog.msc.utils.k.extension.isPortrait
import dev.olog.msc.utils.k.extension.toggleVisibility

class ParallaxScrollListener (
        private val view: View

): RecyclerView.OnScrollListener() {

    private var height = -1
    private val isPortrait = view.context.isPortrait

    override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
        if (isPortrait){
            val child = recyclerView.getChildAt(0)
            if (height == -1){
                height = child.top
            }
            val firstHolder = recyclerView.findChildViewUnder(0f,0f)
            view.toggleVisibility(firstHolder == null)

            if (firstHolder == null){
                val translation = height - child.top
                view.translationY = Math.min(0f, -translation.toFloat() * .4f)
            }
        }
    }

}