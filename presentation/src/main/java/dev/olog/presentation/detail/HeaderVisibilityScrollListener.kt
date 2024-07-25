package dev.olog.presentation.detail

import android.view.View
import androidx.core.view.isInvisible
import androidx.core.view.isVisible
import androidx.recyclerview.widget.LinearLayoutManager
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

    override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
        val lm = recyclerView.layoutManager as? LinearLayoutManager? ?: return
        val firstVisible = lm.findFirstVisibleItemPosition()
        val viewHolder = recyclerView.findViewHolderForAdapterPosition(firstVisible)


        if (viewHolder != null) {
            val bottom = viewHolder.itemView.bottom
            val needDarkLayout = bottom - toolbarHeight < 0

            if (needDarkLayout && binding.statusBar.isInvisible) {
                // set visible
                binding.statusBar.toggleVisibility(visible = true, gone = false)
                toggleToolbarBackground(binding.toolbar, show = true)
            } else if (!needDarkLayout && binding.statusBar.isVisible) {
                // set invisible
                binding.statusBar.toggleVisibility(visible = false, gone = false)
                toggleToolbarBackground(binding.toolbar, show = needDarkLayout)
            }

            fragment.hasLightStatusBarColor = needDarkLayout

        } else {
            if (!binding.statusBar.isVisible) {
                binding.statusBar.toggleVisibility(visible = true, gone = false)
                toggleToolbarBackground(binding.toolbar, show = true)

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