package dev.olog.feature.search.adapter

import androidx.recyclerview.widget.DiffUtil
import dev.olog.feature.base.adapter.*
import dev.olog.feature.search.model.SearchFragmentModel
import dev.olog.lib.image.provider.ImageLoader
import dev.olog.navigation.Navigator
import kotlinx.android.synthetic.main.item_search_album.*

internal class SearchFragmentNestedAdapter(
    private val navigator: Navigator,
    private val viewModel: dev.olog.feature.search.SearchFragmentViewModel
) : ObservableAdapter<SearchFragmentModel.Album>(SearchFragmentModelAlbumDiff) {

    override fun getItemViewType(position: Int): Int = getItem(position).layoutType

    override fun initViewHolderListeners(viewHolder: LayoutContainerViewHolder, viewType: Int) {
        viewHolder.setOnClickListener(this) { item, _, _ ->
            navigator.toDetailFragment(item.mediaId)
            viewModel.insertToRecent(item.mediaId)
        }
        viewHolder.setOnLongClickListener(this) { item, _, _ ->
            navigator.toDialog(item.mediaId, viewHolder.itemView)
        }
        viewHolder.elevateAlbumOnTouch()
    }

    override fun bind(
        holder: LayoutContainerViewHolder,
        item: SearchFragmentModel.Album,
        position: Int
    ) = holder.bindView {

        ImageLoader.loadAlbumImage(imageView!!, item.mediaId)
        quickAction.setId(item.mediaId)
        firstText.text = item.title
        secondText?.text = item.subtitle
    }

}

private object SearchFragmentModelAlbumDiff : DiffUtil.ItemCallback<SearchFragmentModel.Album>() {

    override fun areItemsTheSame(
        oldItem: SearchFragmentModel.Album,
        newItem: SearchFragmentModel.Album
    ): Boolean {
        return oldItem.mediaId == newItem.mediaId
    }

    override fun areContentsTheSame(
        oldItem: SearchFragmentModel.Album,
        newItem: SearchFragmentModel.Album
    ): Boolean {
        return oldItem == newItem
    }
}