package dev.olog.presentation.search.adapter

import dev.olog.feature.presentation.base.adapter.*
import dev.olog.feature.presentation.base.loadAlbumImage
import dev.olog.feature.presentation.base.model.DisplayableAlbum
import dev.olog.presentation.navigator.Navigator
import dev.olog.presentation.search.SearchFragmentViewModel
import dev.olog.feature.presentation.base.model.toDomain
import kotlinx.android.synthetic.main.item_search_album.view.*

internal class SearchFragmentNestedAdapter(
    private val navigator: Navigator,
    private val viewModel: SearchFragmentViewModel

) : ObservableAdapter<DisplayableAlbum>(DiffCallbackDisplayableAlbum) {

    override fun initViewHolderListeners(viewHolder: DataBoundViewHolder, viewType: Int) {
        viewHolder.setOnClickListener(this) { item, _, view ->
            navigator.toDetailFragment(item.mediaId.toDomain(), view)
            viewModel.insertToRecent(item.mediaId)
        }
        viewHolder.setOnLongClickListener(this) { item, _, _ ->
            navigator.toDialog(item.mediaId.toDomain(), viewHolder.itemView, viewHolder.itemView)
        }
        viewHolder.elevateAlbumOnTouch()
    }

    override fun bind(holder: DataBoundViewHolder, item: DisplayableAlbum, position: Int) {
        holder.itemView.apply {
            transitionName = "search nested ${item.mediaId}"
            holder.imageView!!.loadAlbumImage(item.mediaId.toDomain())
            quickAction.setId(item.mediaId)
            firstText.text = item.title
            secondText?.text = item.subtitle
        }
    }

}