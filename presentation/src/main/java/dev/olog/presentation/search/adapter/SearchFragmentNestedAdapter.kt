package dev.olog.presentation.search.adapter

import dev.olog.presentation.BindingsAdapter
import dev.olog.presentation.base.adapter.*
import dev.olog.presentation.model.DisplayableAlbum
import dev.olog.presentation.model.DisplayableItem
import dev.olog.presentation.navigator.Navigator
import dev.olog.presentation.search.SearchFragmentViewModel
import kotlinx.android.synthetic.main.item_search_album.view.*

class SearchFragmentNestedAdapter(
    private val navigator: Navigator,
    private val viewModel: SearchFragmentViewModel

) : ObservableAdapter<DisplayableItem>(DiffCallbackDisplayableItem) {

    override fun initViewHolderListeners(viewHolder: DataBoundViewHolder, viewType: Int) {
        viewHolder.setOnClickListener(this) { item, _, _ ->
            navigator.toDetailFragment(item.mediaId)
            viewModel.insertToRecent(item.mediaId)
        }
        viewHolder.setOnLongClickListener(this) { item, _, _ ->
            navigator.toDialog(item.mediaId, viewHolder.itemView)
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