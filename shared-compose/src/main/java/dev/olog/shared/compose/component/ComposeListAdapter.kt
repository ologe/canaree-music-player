package dev.olog.shared.compose.component

import android.view.ViewGroup
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.ComposeView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import dev.olog.shared.android.extensions.inflate
import dev.olog.shared.compose.R
import dev.olog.shared.compose.theme.CanareeTheme
import dev.olog.shared.widgets.adapter.CustomListAdapter
import dev.olog.shared.widgets.adapter.SwipeableItem

abstract class ComposeListAdapter<T : Any>(
    diffCallback: DiffUtil.ItemCallback<T>
) : CustomListAdapter<T, ComposeViewHolder>(diffCallback) {

    final override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ComposeViewHolder {
        return ComposeViewHolder(parent, viewType)
    }

    final override fun onBindViewHolder(holder: ComposeViewHolder, position: Int) {
        val item = getItem(position)
        holder.setContent {
            Content(holder, item)
        }
    }

    @Suppress("UNCHECKED_CAST")
    override fun onBindViewHolder(
        holder: ComposeViewHolder,
        position: Int,
        payloads: MutableList<Any>
    ) {
        holder.setContent {
            Content(holder, (payloads.getOrNull(0) as T?) ?: getItem(position))
        }
    }

    final override fun getItemViewType(position: Int): Int {
        return when (getItem(position)) {
            is SwipeableItem -> R.layout.compose_interop_swipeable
            else -> R.layout.compose_interop
        }
    }

    @Composable
    abstract fun Content(viewHolder: ComposeViewHolder, item: T)

}

class ComposeViewHolder(
    parent: ViewGroup,
    viewType: Int,
) : RecyclerView.ViewHolder(parent.inflate(viewType)) {

    private val composeView = itemView.findViewById<ComposeView>(R.id.content)

    fun setContent(content: @Composable () -> Unit) {
        composeView.setContent {
            CanareeTheme {
                content()
            }
        }
    }

}