package dev.olog.presentation.tab.adapter

import android.view.View
import androidx.core.view.isVisible
import androidx.recyclerview.widget.RecyclerView
import dev.olog.core.MediaId
import dev.olog.media.MediaProvider
import dev.olog.presentation.BindingsAdapter
import dev.olog.presentation.R
import dev.olog.presentation.base.adapter.*
import dev.olog.presentation.interfaces.SetupNestedList
import dev.olog.presentation.model.*
import dev.olog.presentation.navigator.Navigator
import dev.olog.presentation.tab.TabFragmentViewModel
import dev.olog.shared.exhaustive
import kotlinx.android.synthetic.main.item_tab_album.view.*
import kotlinx.android.synthetic.main.item_tab_album.view.firstText
import kotlinx.android.synthetic.main.item_tab_album.view.secondText
import kotlinx.android.synthetic.main.item_tab_header.view.*
import kotlinx.android.synthetic.main.item_tab_podcast.view.duration as durationView
import kotlinx.android.synthetic.main.item_tab_song.view.*
import kotlinx.android.synthetic.main.item_tab_song.view.isPlaying

internal class TabFragmentAdapter(
    private val navigator: Navigator,
    private val mediaProvider: MediaProvider,
    private val viewModel: TabFragmentViewModel,
    private val setupNestedList: SetupNestedList

) : ObservableAdapter<DisplayableItem>(DiffCallbackDisplayableItem),
    CanShowIsPlaying by CanShowIsPlayingImpl() {


    override fun initViewHolderListeners(viewHolder: DataBoundViewHolder, viewType: Int) {
        when (viewType) {
            R.layout.item_tab_shuffle -> {
                viewHolder.setOnClickListener(this) { _, _, _ ->
                    mediaProvider.shuffle(MediaId.shuffleId, null)
                }
            }
            R.layout.item_tab_song,
            R.layout.item_tab_podcast -> {
                viewHolder.setOnClickListener(this) { item, _, view ->
                    onItemClick(view, item)

                }
                viewHolder.setOnLongClickListener(this) { item, _, _ ->
                    navigator.toDialog(item.mediaId, viewHolder.itemView)
                }
                viewHolder.elevateSongOnTouch()
            }
            R.layout.item_tab_album,
            R.layout.item_tab_artist,
            R.layout.item_tab_auto_playlist -> {
                viewHolder.setOnClickListener(this) { item, _, view ->
                    onItemClick(view, item)
                }
                viewHolder.setOnLongClickListener(this) { item, _, _ ->
                    navigator.toDialog(item.mediaId, viewHolder.itemView)
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

    override fun onBindViewHolder(
        holder: DataBoundViewHolder,
        position: Int,
        payloads: MutableList<Any>
    ) {
        val payload = payloads.filterIsInstance<Boolean>().firstOrNull()
        if (payload != null) {
            holder.itemView.isPlaying.animateVisibility(payload)
        } else {
            super.onBindViewHolder(holder, position, payloads)
        }
    }

    private fun onItemClick(view: View, item: DisplayableItem){
        if (item is DisplayableTrack){
            val sort = viewModel.getAllTracksSortOrder(item.mediaId)
            mediaProvider.playFromMediaId(item.mediaId, null, sort)
        } else if (item is DisplayableAlbum){
            navigator.toDetailFragment(item.mediaId, view)
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
            durationView?.let {
                val durationString = item.idInPlaylist.toString() + "m"
                it.text = durationString
            }
            explicit?.onItemChanged(item.title)
            isPlaying.toggleVisibility(item.mediaId == playingMediaId)
        }
    }

    private fun bindAlbum(holder: DataBoundViewHolder, item: DisplayableAlbum){
        holder.itemView.apply {
            transitionName = item.mediaId.toString()
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
