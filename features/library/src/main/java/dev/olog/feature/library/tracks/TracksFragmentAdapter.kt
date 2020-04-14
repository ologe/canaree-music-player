package dev.olog.feature.library.tracks

import android.annotation.SuppressLint
import android.content.res.ColorStateList
import android.view.View
import androidx.recyclerview.widget.RecyclerView
import dev.olog.domain.MediaId
import dev.olog.feature.library.R
import dev.olog.feature.presentation.base.adapter.*
import dev.olog.feature.presentation.base.loadSongImage
import dev.olog.feature.presentation.base.model.DisplayableItem
import dev.olog.feature.presentation.base.model.DisplayableTrack
import dev.olog.feature.presentation.base.model.toDomain
import dev.olog.lib.media.MediaProvider
import dev.olog.navigation.Navigator
import dev.olog.shared.android.extensions.colorAccent
import dev.olog.shared.android.extensions.textColorPrimary
import kotlinx.android.synthetic.main.item_podcast.view.*
import kotlinx.android.synthetic.main.item_track.view.*
import kotlinx.android.synthetic.main.item_track.view.firstText
import kotlinx.android.synthetic.main.item_track.view.isPlaying
import kotlinx.android.synthetic.main.item_track.view.secondText

internal class TracksFragmentAdapter(
    private val mediaProvider: MediaProvider,
    private val navigator: Navigator,
    private val viewModel: TracksFragmentViewModel
) : ObservableAdapter<DisplayableItem>(DiffCallbackDisplayableItem),
    CanShowIsPlaying by CanShowIsPlayingImpl(){

    private var podcastPositions = emptyMap<Long, Int>()


    override fun initViewHolderListeners(viewHolder: DataBoundViewHolder, viewType: Int) {
        when (viewType) {
            R.layout.item_tracks_header -> {
                viewHolder.setOnClickListener(R.id.play, this) { _, _, _ ->
                    val firstItem = getData().first { it is DisplayableTrack }
                    val sort = viewModel.getAllTracksSortOrder()
                    mediaProvider.playFromMediaId(firstItem.mediaId.toDomain(), null, sort)
                }
                viewHolder.setOnClickListener(R.id.shuffle, this) { _, _, _ ->
                    mediaProvider.shuffle(MediaId.SHUFFLE_ID, null)
                }
            }
            R.layout.item_track,
            R.layout.item_podcast -> {
                viewHolder.setOnClickListener(this) { item, _, view ->
                    val sort = viewModel.getAllTracksSortOrder()
                    mediaProvider.playFromMediaId(item.mediaId.toDomain(), null, sort)

                }
                viewHolder.setOnLongClickListener(this) { item, _, _ ->
                    navigator.toDialog(item.mediaId.toDomain(), viewHolder.itemView, viewHolder.itemView)
                }
                viewHolder.elevateSongOnTouch()
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
            if (item is DisplayableTrack && holder.isPodcast()) {
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

    override fun bind(holder: DataBoundViewHolder, item: DisplayableItem, position: Int) {
        when (item) {
            is DisplayableTrack -> bindTrack(holder, item)
        }
    }

    private fun bindTrack(holder: DataBoundViewHolder, item: DisplayableTrack) {
        holder.itemView.apply {
            holder.imageView!!.loadSongImage(item.mediaId.toDomain())
            firstText.text = item.title
            secondText.text = item.subtitle
            explicit?.onItemChanged(item.title)
            isPlaying.toggleVisibility(item.mediaId == playingMediaId)

            if (holder.isPodcast()) {
                bindPodcast(this, item)
                bindPodcastProgressBarTint(this, item)
            }
        }
    }

    private fun RecyclerView.ViewHolder.isPodcast(): Boolean {
        return itemViewType == R.layout.item_podcast
    }

    @SuppressLint("SetTextI18n")
    private fun bindPodcast(view: View, item: DisplayableTrack) {
        val duration = item.duration.toInt()
        val progress = podcastPositions[item.mediaId.id.toLong()] ?: 0
        view.progressBar.max = duration
        view.progressBar.progress = progress

        val percentage = (progress.toFloat() / duration.toFloat() * 100f).toInt()
        view.percentage.text = "$percentage%"
    }

    private fun bindPodcastProgressBarTint(view: View, item: DisplayableTrack) {
        val color = if (item.mediaId == playingMediaId) {
            view.context.colorAccent()
        } else {
            view.context.textColorPrimary()
        }
        view.progressBar.progressTintList = ColorStateList.valueOf(color)
    }

    fun updatePodcastPositions(positions: Map<Long, Int>) {
        this.podcastPositions = positions
        for (index in currentList.indices) {
            notifyItemChanged(index, Unit)
        }
    }

}