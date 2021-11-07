package dev.olog.feature.search.adapter

import android.view.View
import androidx.lifecycle.Lifecycle
import androidx.recyclerview.widget.RecyclerView
import dev.olog.core.MediaId
import dev.olog.media.MediaProvider
import dev.olog.feature.base.BindingsAdapter
import dev.olog.feature.base.drag.TouchableAdapter
import dev.olog.feature.base.SetupNestedList
import dev.olog.feature.base.model.DisplayableAlbum
import dev.olog.feature.base.model.DisplayableHeader
import dev.olog.feature.base.model.DisplayableItem
import dev.olog.feature.base.model.DisplayableTrack
import dev.olog.feature.base.adapter.*
import dev.olog.feature.search.R
import dev.olog.feature.search.SearchFragmentViewModel
import kotlinx.android.synthetic.main.item_search_album.view.firstText
import kotlinx.android.synthetic.main.item_search_album.view.secondText
import kotlinx.android.synthetic.main.item_search_header.view.*
import kotlinx.android.synthetic.main.item_search_recent.view.*

class SearchFragmentAdapter(
    lifecycle: Lifecycle,
    private val setupNestedList: SetupNestedList,
    private val mediaProvider: MediaProvider,
    private val onItemClick: (MediaId) -> Unit,
    private val onItemLongClick: (MediaId, View) -> Unit,
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
                    onItemLongClick(item.mediaId, viewHolder.itemView)
                }
                viewHolder.setOnClickListener(R.id.more, this) { item, _, view ->
                    onItemLongClick(item.mediaId, view)
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
                        onItemClick(item.mediaId)
                    }
                }
                viewHolder.setOnLongClickListener(this) { item, _, _ ->
                    onItemLongClick(item.mediaId, viewHolder.itemView)
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