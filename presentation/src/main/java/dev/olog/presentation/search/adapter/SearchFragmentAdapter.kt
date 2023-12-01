package dev.olog.presentation.search.adapter

import androidx.lifecycle.Lifecycle
import androidx.recyclerview.widget.RecyclerView
import dev.olog.media.MediaProvider
import dev.olog.presentation.BindingsAdapter
import dev.olog.presentation.R
import dev.olog.presentation.base.adapter.*
import dev.olog.presentation.base.drag.TouchableAdapter
import dev.olog.presentation.interfaces.SetupNestedList
import dev.olog.presentation.model.DisplayableAlbum
import dev.olog.presentation.model.DisplayableHeader
import dev.olog.presentation.model.DisplayableItem
import dev.olog.presentation.model.DisplayableNestedListPlaceholder
import dev.olog.presentation.model.DisplayableTrack
import dev.olog.presentation.navigator.Navigator
import dev.olog.presentation.search.SearchFragmentViewModel
import kotlinx.android.synthetic.main.item_search_album.view.cover
import kotlinx.android.synthetic.main.item_search_album.view.firstText
import kotlinx.android.synthetic.main.item_search_album.view.secondText
import kotlinx.android.synthetic.main.item_search_header.view.*
import kotlinx.android.synthetic.main.item_search_recent.view.*

class SearchFragmentAdapter(
    lifecycle: Lifecycle,
    private val setupNestedList: SetupNestedList,
    private val mediaProvider: MediaProvider,
    private val navigator: Navigator,
    private val viewModel: SearchFragmentViewModel

) : ObservableAdapter<DisplayableItem>(
    lifecycle,
    DiffCallbackDisplayableItem
), TouchableAdapter {

    override fun initViewHolderListeners(viewHolder: DataBoundViewHolder, viewType: Int) {
        when (viewType) {
            R.layout.item_search_list_albums,
            R.layout.item_search_list_artists,
            R.layout.item_search_list_folder,
            R.layout.item_search_list_playlists,
            R.layout.item_search_list_genre -> {
                val list = viewHolder.itemView as RecyclerView
                setupNestedList.setupNestedList(viewType, list)
            }
            R.layout.item_search_song -> {
                viewHolder.setOnClickListener(this) { item, _, _ ->
                    mediaProvider.playFromMediaId(item.mediaId, null, null)
                    viewModel.insertToRecent(item.mediaId)

                }
                viewHolder.setOnLongClickListener(this) { item, _, _ ->
                    navigator.toDialog(item.mediaId, viewHolder.itemView)
                }
                viewHolder.setOnClickListener(R.id.more, this) { item, _, view ->
                    navigator.toDialog(item.mediaId, view)
                }

            }
            R.layout.item_search_clear_recent -> {
                viewHolder.setOnClickListener(this) { _, _, _ ->
                    viewModel.clearRecentSearches()
                }
            }
            R.layout.item_search_recent,
            R.layout.item_search_recent_album,
            R.layout.item_search_recent_artist -> {
                viewHolder.setOnClickListener(this) { item, _, _ ->
                    if (item is DisplayableTrack) {
                        mediaProvider.playFromMediaId(item.mediaId, null, null)
                    } else {
                        navigator.toDetailFragment(item.mediaId)
                    }
                }
                viewHolder.setOnLongClickListener(this) { item, _, _ ->
                    navigator.toDialog(item.mediaId, viewHolder.itemView)
                }
                viewHolder.setOnClickListener(R.id.clear, this) { item, _, _ ->
                    viewModel.deleteFromRecent(item.mediaId)
                }
            }
        }
        when (viewType) {
            R.layout.item_search_song,
            R.layout.item_search_recent,
            R.layout.item_search_recent_album,
            R.layout.item_search_recent_artist -> viewHolder.elevateSongOnTouch()
        }
    }

    override fun bind(holder: DataBoundViewHolder, item: DisplayableItem, position: Int) {
        when (item){
            is DisplayableTrack -> bindTrack(holder, item)
            is DisplayableHeader -> bindHeader(holder, item)
            is DisplayableAlbum -> bindAlbum(holder, item)
            is DisplayableNestedListPlaceholder -> {}
        }
    }

    private fun bindTrack(holder: DataBoundViewHolder, item: DisplayableTrack){
        holder.itemView.apply {
            BindingsAdapter.loadSongImage(holder.imageView!!, item.mediaId)
            firstText.text = item.title
            if (item.album.isBlank()){
                secondText.text = item.artist
            } else {
                secondText.text = item.subtitle
            }

            explicit.onItemChanged(item.title)
        }
    }

    private fun bindAlbum(holder: DataBoundViewHolder, item: DisplayableAlbum){
        holder.itemView.apply {
            BindingsAdapter.loadAlbumImage(holder.imageView!!, item.mediaId)
            firstText.text = item.title
            secondText.text = item.subtitle
        }
    }

    private fun bindHeader(holder: DataBoundViewHolder, item: DisplayableHeader){
        if (holder.itemViewType == R.layout.item_search_header){
            holder.itemView.apply {
                title.text = item.title
                subtitle.text = item.subtitle
            }
        }
    }

    override fun canInteractWithViewHolder(viewType: Int): Boolean {
        return viewType == R.layout.item_search_song ||
                viewType == R.layout.item_search_recent
    }

    override fun onSwipedLeft(viewHolder: RecyclerView.ViewHolder) {
        val position = viewHolder.adapterPosition
        val item = getItem(position)!!
        mediaProvider.addToPlayNext(item.mediaId)
    }

    override fun afterSwipeLeft(viewHolder: RecyclerView.ViewHolder) {
        notifyItemChanged(viewHolder.adapterPosition)
    }

}