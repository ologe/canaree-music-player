package dev.olog.feature.library.tab.adapter

import android.view.View
import androidx.core.view.isVisible
import androidx.lifecycle.Lifecycle
import androidx.recyclerview.widget.RecyclerView
import dev.olog.core.MediaId
import dev.olog.feature.base.BindingsAdapter
import dev.olog.feature.base.SetupNestedList
import dev.olog.feature.base.adapter.*
import dev.olog.feature.base.model.*
import dev.olog.feature.library.R
import dev.olog.feature.library.tab.TabFragmentViewModel
import dev.olog.media.MediaProvider
import dev.olog.shared.exhaustive
import kotlinx.android.synthetic.main.item_tab_album.view.*
import kotlinx.android.synthetic.main.item_tab_album.view.firstText
import kotlinx.android.synthetic.main.item_tab_album.view.secondText
import kotlinx.android.synthetic.main.item_tab_header.view.*
import kotlinx.android.synthetic.main.item_tab_podcast.view.*
import kotlinx.android.synthetic.main.item_tab_song.view.*

class TabFragmentAdapter(
    lifecycle: Lifecycle,
    private val onItemClick: (MediaId) -> Unit,
    private val onItemLongClick: (MediaId, View) -> Unit,
    private val mediaProvider: MediaProvider,
    private val viewModel: TabFragmentViewModel,
    private val setupNestedList: SetupNestedList
) : ObservableAdapter<DisplayableItem>(
    lifecycle,
    DiffCallbackDisplayableItem
) {

    override fun initViewHolderListeners(viewHolder: DataBoundViewHolder, viewType: Int) {
        when (viewType) {
            R.layout.item_tab_shuffle -> {
                viewHolder.setOnClickListener(this) { _, _, _ ->
                    mediaProvider.shuffle(MediaId.shuffleId(), null)
                }
            }
            R.layout.item_tab_song,
            R.layout.item_tab_podcast -> {
                viewHolder.setOnClickListener(this) { item, _, _ ->
                    onItemClick(item)

                }
                viewHolder.setOnLongClickListener(this) { item, _, _ ->
                    onItemLongClick(item.mediaId, viewHolder.itemView)
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
                    onItemLongClick(item.mediaId, viewHolder.itemView)
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

    private fun onItemClick(item: DisplayableItem){
        if (item is DisplayableTrack){
            val sort = viewModel.getAllTracksSortOrder(item.mediaId)
            mediaProvider.playFromMediaId(item.mediaId, null, sort)
        } else if (item is DisplayableAlbum){
            onItemClick(item.mediaId)
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
