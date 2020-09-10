package dev.olog.feature.library.track

import android.annotation.SuppressLint
import android.content.res.ColorStateList
import android.view.View
import androidx.recyclerview.widget.DiffUtil
import dev.olog.domain.MediaId
import dev.olog.feature.library.R
import dev.olog.feature.presentation.base.adapter.*
import dev.olog.feature.presentation.base.loadSongImage
import dev.olog.feature.presentation.base.model.DisplayableTrack
import dev.olog.feature.presentation.base.model.PresentationId
import dev.olog.feature.presentation.base.model.toDomain
import dev.olog.lib.media.MediaProvider
import dev.olog.navigation.Navigator
import dev.olog.shared.android.extensions.colorAccent
import dev.olog.shared.android.extensions.textColorPrimary
import dev.olog.shared.exhaustive

//internal class TrackFragmentAdapter(
//    private val mediaProvider: MediaProvider,
//    private val navigator: Navigator,
//    private val viewModel: TrackFragmentViewModel
//) : ObservableAdapter2<TrackFragmentItem>(TrackFragmentItemDiff),
//    CanShowIsPlaying by CanShowIsPlayingImpl() {
//
//    private var podcastPositions = emptyMap<Long, Int>()
//
//    override fun initViewHolderListeners(viewHolder: DataBoundViewHolder, viewType: Int) {
//        viewHolder.setOnClickListener(this) { item, _, v ->
//            when (item) {
//                is TrackFragmentItem.Shuffle -> onShuffleClick()
//                is TrackFragmentItem.Track -> onItemClick(v, item.mediaId)
//                is TrackFragmentItem.Podcast -> onItemClick(v, item.mediaId)
//            }.exhaustive
//        }
//        viewHolder.setOnLongClickListener(this) { item, _, v ->
//            when (item) {
//                is TrackFragmentItem.Shuffle -> {}
//                is TrackFragmentItem.Track -> onItemLongClick(v, item.mediaId)
//                is TrackFragmentItem.Podcast -> onItemLongClick(v, item.mediaId)
//            }.exhaustive
//        }
//        viewHolder.elevateSongOnTouch()
//    }
//
//    private fun onShuffleClick() {
//        mediaProvider.shuffle(MediaId.SHUFFLE_ID, null)
//    }
//
//    private fun onItemClick(view: View, id: PresentationId.Track) {
//        val sort = viewModel.sortOrder
//        mediaProvider.playFromMediaId(id.toDomain(), null, sort)
//    }
//
//    private fun onItemLongClick(view: View, id: PresentationId.Track) {
//        navigator.toDialog(id.toDomain(), view, view)
//    }
//
//    override fun getItemViewType(position: Int): Int = when (getItem(position)) {
//        is TrackFragmentItem.Shuffle -> R.layout.item_tab_shuffle
//        is TrackFragmentItem.Track -> R.layout.item_tab_song
//        is TrackFragmentItem.Podcast -> R.layout.item_tab_podcast
//    }
//
//    override fun getMediaId(item: TrackFragmentItem): PresentationId? = when (item) {
//        is TrackFragmentItem.Shuffle -> null
//        is TrackFragmentItem.Track -> item.mediaId
//        is TrackFragmentItem.Podcast -> item.mediaId
//    }
//
//    override fun onBindViewHolder(
//        holder: DataBoundViewHolder,
//        position: Int,
//        payloads: MutableList<Any>
//    ) = with (holder) {
//
//        if (payloads.isEmpty()) {
//            super.onBindViewHolder(holder, position, payloads)
//            return@with
//        }
//
//        val item = getItem(position)
//        val payload = payloads.filterIsInstance<Boolean>().firstOrNull()
//        if (payload != null) {
//            when (item) {
//                is TrackFragmentItem.Track -> bindTrack(holder, item, true)
//                is TrackFragmentItem.Podcast -> bindPodcast(holder, item, true)
//            }
//        }
//
//        val updatePodcastPosition = payloads.filterIsInstance<Unit>().firstOrNull()
//        if (updatePodcastPosition != null && item is TrackFragmentItem.Podcast) {
//            bindPodcast(holder, item, true)
//        }
//    }
//
//    override fun bind(holder: DataBoundViewHolder, item: TrackFragmentItem, position: Int) {
//        when (item) {
//            is TrackFragmentItem.Shuffle -> {}
//            is TrackFragmentItem.Track -> bindTrack(holder, item, false)
//            is TrackFragmentItem.Podcast -> bindPodcast(holder, item, false)
//        }.exhaustive
//    }
//
//    private fun bindTrack(
//        holder: DataBoundViewHolder,
//        item: TrackFragmentItem.Track,
//        animateIsPlaying: Boolean
//    ) = with (holder) {
//        cover.loadSongImage(item.mediaId.toDomain())
//        firstText.text = item.title
//        secondText.text = item.subtitle
//        explicit.onItemChanged(item.title)
//        if (animateIsPlaying) {
//            isPlaying.animateVisibility(item.mediaId == playingMediaId)
//        } else {
//            isPlaying.toggleVisibility(item.mediaId == playingMediaId)
//        }
//    }
//
//    @SuppressLint("SetTextI18n")
//    private fun bindPodcast(
//        holder: DataBoundViewHolder,
//        item: TrackFragmentItem.Podcast,
//        animateIsPlaying: Boolean
//    ) = with (holder){
//        cover.loadSongImage(item.mediaId.toDomain())
//        firstText.text = item.title
//        secondText.text = item.subtitle
//        if (animateIsPlaying) {
//            isPlaying.animateVisibility(item.mediaId == playingMediaId)
//        } else {
//            isPlaying.toggleVisibility(item.mediaId == playingMediaId)
//        }
//
//        val duration = item.duration.toInt()
//        val progress = podcastPositions[item.mediaId.id.toLong()] ?: 0
//        progressBar.max = duration
//        progressBar.progress = progress
//
//        val percentageValue = (progress.toFloat() / duration.toFloat() * 100f).toInt()
//        percentage.text = "$percentageValue%"
//
//        val color = if (item.mediaId == playingMediaId) {
//            itemView.context.colorAccent()
//        } else {
//            itemView.context.textColorPrimary()
//        }
//        progressBar.progressTintList = ColorStateList.valueOf(color)
//    }
//
//    fun updatePodcastPositions(positions: Map<Long, Int>) {
//        this.podcastPositions = positions
//        for (index in currentList.indices) {
//            notifyItemChanged(index, Unit)
//        }
//    }
//
//}
//
//private object TrackFragmentItemDiff : DiffUtil.ItemCallback<TrackFragmentItem>() {
//
//    override fun areItemsTheSame(oldItem: TrackFragmentItem, newItem: TrackFragmentItem): Boolean {
//        if (oldItem is TrackFragmentItem.Track && newItem is TrackFragmentItem.Track) {
//            return oldItem.mediaId == newItem.mediaId
//        }
//        if (oldItem is TrackFragmentItem.Podcast && newItem is TrackFragmentItem.Podcast) {
//            return oldItem.mediaId == newItem.mediaId
//        }
//        return oldItem == newItem
//    }
//
//    override fun areContentsTheSame(
//        oldItem: TrackFragmentItem,
//        newItem: TrackFragmentItem
//    ): Boolean {
//        return oldItem == newItem
//    }
//}