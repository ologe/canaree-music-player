package dev.olog.shared.compose.component

import android.view.View
import android.view.ViewGroup
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.ComposeView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import dev.olog.shared.android.extensions.inflate
import dev.olog.shared.compose.R
import dev.olog.shared.compose.theme.CanareeTheme

abstract class ComposeListAdapter<T : Any>(
    diffCallback: DiffUtil.ItemCallback<T>
) : ListAdapter<T, ComposeViewHolder>(diffCallback) {

    final override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ComposeViewHolder {
        return ComposeViewHolder(parent, viewType)
    }

    final override fun onBindViewHolder(holder: ComposeViewHolder, position: Int) {
        val item = getItem(position)
        holder.setContent {
            Content(holder.itemView, item)
        }
    }

    override fun getItemViewType(position: Int): Int = R.layout.compose_interop

    @Composable
    abstract fun Content(view: View, item: T)

    public override fun getItem(position: Int): T {
        return super.getItem(position)
    }

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