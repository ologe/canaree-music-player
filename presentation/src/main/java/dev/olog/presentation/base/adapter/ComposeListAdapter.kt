package dev.olog.presentation.base.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.ComposeView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import dev.olog.presentation.R
import dev.olog.shared.compose.CanareeTheme

abstract class ComposeListAdapter<T : Any>(
    diffCallback: DiffUtil.ItemCallback<T> = DefaultDiffCallback()
) : InteractableListAdapter<T, ComposeViewHolder>(diffCallback) {

    override fun getItemViewType(position: Int): Int {
        val item = getItem(position)
        if (item is InteractableItem && item.isInteractable) {
            return R.layout.item_swipeable_compose
        }
        return super.getItemViewType(position)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ComposeViewHolder {
        if (viewType == R.layout.item_swipeable_compose) {
            val view = LayoutInflater.from(parent.context).inflate(R.layout.item_swipeable_compose, parent, false)
            return ComposeViewHolder(view)
        }
        return ComposeViewHolder(ComposeView(parent.context))
    }

    final override fun onBindViewHolder(holder: ComposeViewHolder, position: Int) {
        bind(holder, getItem(position), position)
    }

    @Suppress("UNCHECKED_CAST")
    override fun onBindViewHolder(
        holder: ComposeViewHolder,
        position: Int,
        payloads: MutableList<Any>
    ) {
        val item = payloads.getOrNull(0) as? T
        bind(holder, item ?: getItem(position), position)
    }

    protected abstract fun bind(holder: ComposeViewHolder, item: T, position: Int)

}

open class ComposeViewHolder(view: View) : RecyclerView.ViewHolder(view) {

    private val composeView: ComposeView = when (itemView) {
        is ComposeView -> itemView as ComposeView
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