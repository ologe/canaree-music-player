package dev.olog.presentation.tab.adapter

import android.widget.TextView
import androidx.lifecycle.Lifecycle
import dev.olog.presentation.BindingsAdapter
import dev.olog.presentation.R
import dev.olog.presentation.base.adapter.*
import dev.olog.presentation.model.DisplayableAlbum
import dev.olog.presentation.model.DisplayableItem
import dev.olog.presentation.navigator.Navigator
import dev.olog.presentation.widgets.QuickActionView

internal class TabFragmentNestedAdapter(
    lifecycle: Lifecycle,
    private val navigator: Navigator

) : ObservableAdapter<DisplayableItem>(
    lifecycle,
    DiffCallbackDisplayableItem
) {

    override fun initViewHolderListeners(viewHolder: DataBoundViewHolder, viewType: Int) {
        viewHolder.setOnClickListener(this) { item, _, _ ->
            navigator.toDetailFragment(item.mediaId)
        }
        viewHolder.setOnLongClickListener(this) { item, _, _ ->
            navigator.toDialog(item.mediaId, viewHolder.itemView)
        }
        viewHolder.elevateAlbumOnTouch()
    }

    override fun bind(holder: DataBoundViewHolder, item: DisplayableItem, position: Int) {
        require(item is DisplayableAlbum)

        BindingsAdapter.loadAlbumImage(holder.imageView!!, item.mediaId)
        holder.itemView.findViewById<QuickActionView>(R.id.quickAction).setId(item.mediaId)
        holder.itemView.findViewById<TextView>(R.id.firstText).text = item.title
        holder.itemView.findViewById<TextView>(R.id.secondText).text = item.subtitle
    }

}