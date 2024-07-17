package dev.olog.presentation.base.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.ComposeView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import dev.olog.presentation.R
import dev.olog.shared.compose.CanareeTheme

abstract class ComposeListAdapter<T : Any>(
    diffCallback: DiffUtil.ItemCallback<T> = DefaultDiffCallback()
) : ListAdapter<T, ComposeViewHolder>(diffCallback) {

    final override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ComposeViewHolder {
        if (viewType == R.layout.item_swipeable_compose) {
            val view = LayoutInflater.from(parent.context).inflate(R.layout.item_swipeable_compose, parent, false)
            return ComposeViewHolder(view)
        }
        return ComposeViewHolder(ComposeView(parent.context))
    }

    public override fun getItem(position: Int): T {
        return super.getItem(position)
    }

}

class ComposeViewHolder(view: View) : RecyclerView.ViewHolder(view) {

    private val composeView: ComposeView = when (itemView) {
        is ComposeView -> itemView
        else -> itemView.findViewById(R.id.content)
    }

    fun setContent(content: @Composable () -> Unit) {
        composeView.setContent {
            CanareeTheme {
                content()
            }
        }
    }

}