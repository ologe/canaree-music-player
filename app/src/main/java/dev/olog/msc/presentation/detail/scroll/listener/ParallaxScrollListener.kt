package dev.olog.msc.presentation.detail.scroll.listener

import android.support.v7.widget.RecyclerView
import dev.olog.msc.presentation.detail.DetailFragment
import dev.olog.msc.utils.k.extension.isPortrait
import dev.olog.msc.utils.k.extension.toggleVisibility
import kotlinx.android.synthetic.main.fragment_detail.view.*

class ParallaxScrollListener (
        private val fragment: DetailFragment

): RecyclerView.OnScrollListener() {

    private var height = -1
    private val isPortrait = fragment.context!!.isPortrait

    override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
        if (isPortrait){

            val view = fragment.view!!.cover

            recyclerView.getChildAt(0)?.let { child ->
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

}