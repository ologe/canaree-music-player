package dev.olog.feature.search.adapter

import android.view.View
import androidx.lifecycle.Lifecycle
import dev.olog.core.MediaId
import dev.olog.feature.base.BindingsAdapter
import dev.olog.feature.base.model.DisplayableAlbum
import dev.olog.feature.base.model.DisplayableItem
import dev.olog.feature.base.adapter.*
import dev.olog.feature.search.SearchFragmentViewModel
import kotlinx.android.synthetic.main.item_search_album.view.*

class SearchFragmentNestedAdapter(
    lifecycle: Lifecycle,
    private val onItemClick: (MediaId) -> Unit,
    private val onItemLongClick: (MediaId, View) -> Unit,
    private val viewModel: SearchFragmentViewModel
) : ObservableAdapter<DisplayableItem>(
    lifecycle,
    DiffCallbackDisplayableItem
) {

    override fun initViewHolderListeners(viewHolder: DataBoundViewHolder, viewType: Int) {
        viewHolder.setOnClickListener(this) { item, _, _ ->
            onItemClick(item.mediaId)
            viewModel.insertToRecent(item.mediaId)
        }
        viewHolder.setOnLongClickListener(this) { item, _, _ ->
            onItemLongClick(item.mediaId, viewHolder.itemView)
        }
        viewHolder.elevateAlbumOnTouch()
    }

    override fun bind(holder: DataBoundViewHolder, item: DisplayableItem, position: Int) {
        require(item is DisplayableAlbum)

        holder.itemView.apply {
            BindingsAdapter.loadAlbumImage(holder.imageView!!, item.mediaId)
            quickAction.setId(item.mediaId)
            firstText.text = item.title
            secondText?.text = item.subtitle
        }
    }

}