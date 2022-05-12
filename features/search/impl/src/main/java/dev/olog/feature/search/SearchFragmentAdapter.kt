package dev.olog.feature.search

import android.view.View
import androidx.recyclerview.widget.RecyclerView
import dev.olog.core.MediaId
import dev.olog.image.provider.BindingsAdapter
import dev.olog.platform.adapter.DataBoundViewHolder
import dev.olog.platform.adapter.ObservableAdapter
import dev.olog.platform.adapter.SetupNestedList
import dev.olog.platform.adapter.drag.TouchableAdapter
import dev.olog.platform.adapter.elevateSongOnTouch
import dev.olog.platform.adapter.setOnClickListener
import dev.olog.platform.adapter.setOnLongClickListener
import dev.olog.ui.model.DiffCallbackDisplayableItem
import dev.olog.ui.model.DisplayableAlbum
import dev.olog.ui.model.DisplayableHeader
import dev.olog.ui.model.DisplayableItem
import dev.olog.ui.model.DisplayableTrack
import kotlinx.android.synthetic.main.item_search_album.view.firstText
import kotlinx.android.synthetic.main.item_search_album.view.secondText
import kotlinx.android.synthetic.main.item_search_header.view.*
import kotlinx.android.synthetic.main.item_search_recent.view.*

class SearchFragmentAdapter(
    private val setupNestedList: SetupNestedList,
    private val onItemClick: (MediaId) -> Unit,
    private val onItemLongClick: (View, MediaId) -> Unit,
    private val onRecentItemClick: (DisplayableItem) -> Unit,
    private val onSwipeLeft: (MediaId) -> Unit,
    private val onClearRecentItemClick: (MediaId) -> Unit,
    private val onClearAllRecentsClick: () -> Unit,
) : ObservableAdapter<DisplayableItem>(DiffCallbackDisplayableItem), TouchableAdapter {

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
                    onItemClick(item.mediaId)

                }
                viewHolder.setOnLongClickListener(this) { item, _, _ ->
                    onItemLongClick(viewHolder.itemView, item.mediaId)
                }
                viewHolder.setOnClickListener(R.id.more, this) { item, _, view ->
                    onItemLongClick(view, item.mediaId)
                }

            }
            R.layout.item_search_clear_recent -> {
                viewHolder.setOnClickListener(this) { _, _, _ ->
                    onClearAllRecentsClick()
                }
            }
            R.layout.item_search_recent,
            R.layout.item_search_recent_album,
            R.layout.item_search_recent_artist -> {
                viewHolder.setOnClickListener(this) { item, _, _ ->
                    onRecentItemClick(item)
                }
                viewHolder.setOnLongClickListener(this) { item, _, _ ->
                    onItemLongClick(viewHolder.itemView, item.mediaId)
                }
                viewHolder.setOnClickListener(R.id.clear, this) { item, _, _ ->
                    onClearRecentItemClick(item.mediaId)
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
        val item = getItem(position)
        onSwipeLeft(item.mediaId)
    }

    override fun afterSwipeLeft(viewHolder: RecyclerView.ViewHolder) {
        notifyItemChanged(viewHolder.adapterPosition)
    }

    override fun contentViewFor(holder: RecyclerView.ViewHolder): View {
        return holder.itemView.content
    }
}