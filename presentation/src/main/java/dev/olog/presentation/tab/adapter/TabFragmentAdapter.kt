package dev.olog.presentation.tab.adapter

import android.view.View
import androidx.core.view.isVisible
import androidx.recyclerview.widget.RecyclerView
import dev.olog.core.MediaId
import dev.olog.image.provider.BindingsAdapter
import dev.olog.platform.adapter.DataBoundViewHolder
import dev.olog.platform.adapter.ObservableAdapter
import dev.olog.platform.adapter.SetupNestedList
import dev.olog.platform.adapter.elevateAlbumOnTouch
import dev.olog.platform.adapter.elevateSongOnTouch
import dev.olog.platform.adapter.setOnClickListener
import dev.olog.platform.adapter.setOnLongClickListener
import dev.olog.presentation.R
import dev.olog.shared.extension.exhaustive
import dev.olog.ui.model.DiffCallbackDisplayableItem
import dev.olog.ui.model.DisplayableAlbum
import dev.olog.ui.model.DisplayableHeader
import dev.olog.ui.model.DisplayableItem
import dev.olog.ui.model.DisplayableNestedListPlaceholder
import dev.olog.ui.model.DisplayableTrack
import kotlinx.android.synthetic.main.item_tab_album.view.*
import kotlinx.android.synthetic.main.item_tab_album.view.firstText
import kotlinx.android.synthetic.main.item_tab_album.view.secondText
import kotlinx.android.synthetic.main.item_tab_header.view.*
import kotlinx.android.synthetic.main.item_tab_podcast.view.*
import kotlinx.android.synthetic.main.item_tab_song.view.*

internal class TabFragmentAdapter(
    private val setupNestedList: SetupNestedList,
    private val onShuffleClick: (MediaId) -> Unit,
    private val onItemClick: (DisplayableItem) -> Unit,
    private val onItemLongClick: (View, MediaId) -> Unit,
) : ObservableAdapter<DisplayableItem>(DiffCallbackDisplayableItem) {

    override fun initViewHolderListeners(viewHolder: DataBoundViewHolder, viewType: Int) {
        when (viewType) {
            R.layout.item_tab_shuffle -> {
                viewHolder.setOnClickListener(this) { _, _, _ ->
                    onShuffleClick(MediaId.shuffleId())
                }
            }
            R.layout.item_tab_song,
            R.layout.item_tab_podcast -> {
                viewHolder.setOnClickListener(this) { item, _, _ ->
                    onItemClick(item)

                }
                viewHolder.setOnLongClickListener(this) { item, _, _ ->
                    onItemLongClick(viewHolder.itemView, item.mediaId)
                }
                viewHolder.elevateSongOnTouch()
            }
            R.layout.item_tab_album,
            R.layout.item_tab_artist,
            R.layout.item_tab_auto_playlist -> {
                viewHolder.setOnClickListener(this) { item, _, _ ->
                    onItemClick(item)
                }
                viewHolder.setOnLongClickListener(this) { item, _, _ ->
                    onItemLongClick(viewHolder.itemView, item.mediaId)
                }
                viewHolder.elevateAlbumOnTouch()
            }
            R.layout.item_tab_last_played_album_horizontal_list,
            R.layout.item_tab_last_played_artist_horizontal_list,
            R.layout.item_tab_new_album_horizontal_list,
            R.layout.item_tab_new_artist_horizontal_list -> {
                val view = viewHolder.itemView as RecyclerView
                setupNestedList.setupNestedList(viewType, view)
            }
        }
    }

    override fun bind(holder: DataBoundViewHolder, item: DisplayableItem, position: Int) {
        when (item){
            is DisplayableTrack -> bindTrack(holder, item)
            is DisplayableAlbum -> bindAlbum(holder, item)
            is DisplayableHeader -> bindHeader(holder, item)
            is DisplayableNestedListPlaceholder -> {}
        }.exhaustive
    }

    private fun bindTrack(holder: DataBoundViewHolder, item: DisplayableTrack){
        holder.itemView.apply {
            BindingsAdapter.loadSongImage(holder.imageView!!, item.mediaId)
            firstText.text = item.title
            secondText.text = item.subtitle
            duration?.let {
                val durationString = item.idInPlaylist.toString() + "m"
                it.text = durationString
            }
            explicit?.onItemChanged(item.title)
        }
    }

    private fun bindAlbum(holder: DataBoundViewHolder, item: DisplayableAlbum){
        holder.itemView.apply {
            BindingsAdapter.loadAlbumImage(holder.imageView!!, item.mediaId)
            quickAction?.setId(item.mediaId)
            firstText.text = item.title
            secondText?.text = item.subtitle
            explicit?.isVisible = false
        }
    }

    private fun bindHeader(holder: DataBoundViewHolder, item: DisplayableHeader){
        if (holder.itemViewType == R.layout.item_tab_header){
            holder.itemView.title.text = item.title
        }
    }

}
