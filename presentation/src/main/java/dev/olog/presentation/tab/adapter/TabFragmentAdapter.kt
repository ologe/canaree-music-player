package dev.olog.presentation.tab.adapter

import android.annotation.SuppressLint
import android.content.res.ColorStateList
import android.view.View
import androidx.core.view.isVisible
import androidx.recyclerview.widget.RecyclerView
import dev.olog.core.MediaId
import dev.olog.media.MediaProvider
import dev.olog.presentation.R
import dev.olog.presentation.base.adapter.*
import dev.olog.presentation.interfaces.SetupNestedList
import dev.olog.presentation.loadAlbumImage
import dev.olog.presentation.loadSongImage
import dev.olog.presentation.model.*
import dev.olog.presentation.navigator.Navigator
import dev.olog.presentation.tab.TabFragmentViewModel
import dev.olog.presentation.toDomain
import dev.olog.shared.android.extensions.colorAccent
import dev.olog.shared.android.extensions.textColorPrimary
import dev.olog.shared.exhaustive
import kotlinx.android.synthetic.main.item_tab_album.view.*
import kotlinx.android.synthetic.main.item_tab_album.view.firstText
import kotlinx.android.synthetic.main.item_tab_album.view.secondText
import kotlinx.android.synthetic.main.item_tab_header.view.*
import kotlinx.android.synthetic.main.item_tab_podcast.view.*
import kotlinx.android.synthetic.main.item_tab_song.view.*
import kotlinx.android.synthetic.main.item_tab_song.view.isPlaying

internal class TabFragmentAdapter(
    private val navigator: Navigator,
    private val mediaProvider: MediaProvider,
    private val viewModel: TabFragmentViewModel,
    private val setupNestedList: SetupNestedList

) : ObservableAdapter<DisplayableItem>(DiffCallbackDisplayableItem),
    CanShowIsPlaying by CanShowIsPlayingImpl() {

    private var podcastPositions = emptyMap<Long, Int>()

    override fun initViewHolderListeners(viewHolder: DataBoundViewHolder, viewType: Int) {
        when (viewType) {
            R.layout.item_tab_shuffle -> {
                viewHolder.setOnClickListener(this) { _, _, _ ->
                    mediaProvider.shuffle(MediaId.SHUFFLE_ID, null)
                }
            }
            R.layout.item_tab_song,
            R.layout.item_tab_podcast -> {
                viewHolder.setOnClickListener(this) { item, _, view ->
                    onItemClick(view, item)

                }
                viewHolder.setOnLongClickListener(this) { item, _, _ ->
                    navigator.toDialog(item.mediaId, viewHolder.itemView, viewHolder.itemView)
                }
                viewHolder.elevateSongOnTouch()
            }
            R.layout.item_tab_album,
            R.layout.item_tab_podcast_playlist,
            R.layout.item_tab_artist,
            R.layout.item_tab_auto_playlist,
            R.layout.item_tab_podcast_auto_playlist-> {
                viewHolder.setOnClickListener(this) { item, _, view ->
                    onItemClick(view, item)
                }
                viewHolder.setOnLongClickListener(this) { item, _, _ ->
                    navigator.toDialog(item.mediaId, viewHolder.itemView, viewHolder.itemView)
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
        val item = getItem(position)
        val payload = payloads.filterIsInstance<Boolean>().firstOrNull()
        if (payload != null) {
            holder.itemView.isPlaying.animateVisibility(payload)
            if (item is DisplayableTrack) {
                bindPodcastProgressBarTint(holder.itemView, item)
            }
        }

        val updatePodcastPosition = payloads.filterIsInstance<Unit>().firstOrNull()
        if (updatePodcastPosition != null && item is DisplayableTrack) {
            bindPodcast(holder.itemView, item)
        }

        if (payloads.isEmpty()) {
            super.onBindViewHolder(holder, position, payloads)
        }
    }

    private fun onItemClick(view: View, item: DisplayableItem){
        if (item is DisplayableTrack){
            val sort = viewModel.getAllTracksSortOrder()
            mediaProvider.playFromMediaId(item.mediaId.toDomain(), null, sort)
        } else if (item is DisplayableAlbum){
            navigator.toDetailFragment(item.mediaId, view)
        }
    }

    override fun bind(holder: DataBoundViewHolder, item: DisplayableItem, position: Int) {
        holder.itemView.transitionName = "tab ${item.mediaId}"

        when (item){
            is DisplayableTrack -> bindTrack(holder, item)
            is DisplayableAlbum -> bindAlbum(holder, item)
            is DisplayableHeader -> bindHeader(holder, item)
            is DisplayableNestedListPlaceholder -> {}
        }.exhaustive
    }

    private fun bindTrack(holder: DataBoundViewHolder, item: DisplayableTrack) {
        holder.itemView.apply {
            holder.imageView!!.loadSongImage(item.mediaId.toDomain())
            firstText.text = item.title
            secondText.text = item.subtitle
            explicit?.onItemChanged(item.title)
            isPlaying.toggleVisibility(item.mediaId == playingMediaId)

            if (holder.itemViewType == R.layout.item_tab_podcast) {
                bindPodcast(this, item)
                bindPodcastProgressBarTint(this, item)
            }
        }
    }

    @SuppressLint("SetTextI18n")
    private fun bindPodcast(view: View, item: DisplayableTrack) {
        val duration = item.duration.toInt()
        val progress = podcastPositions[item.mediaId.id.toLong()] ?: 0
        view.progressBar?.max = duration
        view.progressBar?.progress = progress

        val percentage = (progress.toFloat() / duration.toFloat() * 100f).toInt()
        view.percentage?.text = "$percentage%"
    }

    private fun bindPodcastProgressBarTint(view: View, item: DisplayableTrack) {
        val color = if (item.mediaId == playingMediaId) {
            view.context.colorAccent()
        } else {
            view.context.textColorPrimary()
        }
        view.progressBar?.progressTintList = ColorStateList.valueOf(color)
    }

    private fun bindAlbum(holder: DataBoundViewHolder, item: DisplayableAlbum){
        holder.itemView.apply {
            holder.imageView!!.loadAlbumImage(item.mediaId.toDomain())
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

    fun updatePodcastPositions(positions: Map<Long, Int>) {
        this.podcastPositions = positions
        for (index in currentList.indices) {
            notifyItemChanged(index, Unit)
        }
    }

}
