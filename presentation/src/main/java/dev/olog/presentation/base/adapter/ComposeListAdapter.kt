package dev.olog.presentation.base.adapter

import android.view.ViewGroup
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.ComposeView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import dev.olog.shared.compose.CanareeTheme

abstract class ComposeListAdapter<T : Any>(
    diffCallback: DiffUtil.ItemCallback<T> = DefaultDiffCallback()
) : ListAdapter<T, ComposeViewHolder>(diffCallback) {

    final override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ComposeViewHolder {
        return ComposeViewHolder(ComposeView(parent.context))
    }

    public override fun getItem(position: Int): T {
        return super.getItem(position)
    }

}

class ComposeViewHolder(
    private val composeView: ComposeView,
) : RecyclerView.ViewHolder(composeView) {

    fun setContent(content: @Composable () -> Unit) {
        composeView.setContent {
            CanareeTheme {
                content()
            }
        }
    }

}