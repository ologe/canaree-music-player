package dev.olog.presentation.detail

import android.view.View
import androidx.core.view.isInvisible
import androidx.core.view.isVisible
import androidx.recyclerview.widget.RecyclerView
import dev.olog.presentation.R
import dev.olog.presentation.databinding.FragmentDetailBinding
import dev.olog.shared.android.extensions.colorSurface
import dev.olog.shared.android.extensions.ctx
import dev.olog.shared.android.extensions.dimen
import dev.olog.shared.android.extensions.toggleVisibility
import dev.olog.shared.lazyFast

class HeaderVisibilityScrollListener(
    private val fragment: DetailFragment,
    private val binding: FragmentDetailBinding,
) : RecyclerView.OnScrollListener() {

    private val toolbarHeight by lazyFast {
        val statusBarHeight = binding.statusBar.height
        statusBarHeight + fragment.ctx.dimen(R.dimen.toolbar)
    }

    private var textWrapper: View? = null

    override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
        val child = recyclerView.getChildAt(0)
        val holder = recyclerView.getChildViewHolder(child)

        val view = fragment.requireView()
        val statusBar = binding.statusBar
        val toolbar = binding.toolbar
        val headerText = binding.headerText

        if (holder.itemViewType == R.layout.item_detail_image) {
            if (textWrapper == null) {
                textWrapper = child.findViewById(R.id.textWrapper)
            }
            val bottom = child.bottom - textWrapper!!.height
            val needDarkLayout = bottom - toolbarHeight < 0

            if (needDarkLayout && statusBar.isInvisible) {
                // set visible
                statusBar.toggleVisibility(visible = true, gone = false)
                toggleToolbarBackground(toolbar, show = true)
                headerText.toggleVisibility(visible = true, gone = false)
            } else if (!needDarkLayout && statusBar.isVisible) {
                // set invisible
                statusBar.toggleVisibility(visible = false, gone = false)
                toggleToolbarBackground(toolbar, show = needDarkLayout)
                headerText.toggleVisibility(visible = false, gone = false)
            }

            fragment.hasLightStatusBarColor = needDarkLayout

        } else {
            if (!statusBar.isVisible) {
                statusBar.toggleVisibility(visible = true, gone = false)
                toggleToolbarBackground(toolbar, show = true)
                headerText.toggleVisibility(visible = true, gone = false)

                fragment.hasLightStatusBarColor = true
            }
        }
    }

    private fun toggleToolbarBackground(toolbar: View, show: Boolean) {
        if (show && toolbar.background == null) {
            toolbar.setBackgroundColor(toolbar.context.colorSurface())
        } else if (!show && toolbar.background != null) {
            toolbar.background = null
        }
    }

}