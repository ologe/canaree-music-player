package dev.olog.feature.detail.adapter

import dev.olog.feature.presentation.base.adapter.*
import dev.olog.lib.media.MediaProvider
import dev.olog.presentation.R
import dev.olog.feature.presentation.base.loadSongImage
import dev.olog.feature.presentation.base.model.DisplayableTrack
import dev.olog.navigation.Navigator
import dev.olog.feature.presentation.base.model.toDomain
import kotlinx.android.synthetic.main.item_detail_related_artist.view.firstText
import kotlinx.android.synthetic.main.item_detail_related_artist.view.secondText
import kotlinx.android.synthetic.main.item_detail_song_recent.view.explicit
import kotlinx.android.synthetic.main.item_detail_song_recent.view.isPlaying

internal class DetailRecentlyAddedAdapter(
    private val navigator: Navigator,
    private val mediaProvider: MediaProvider
) : ObservableAdapter<DisplayableTrack>(DiffCallbackDisplayableTrack),
    CanShowIsPlaying by CanShowIsPlayingImpl() {

    override fun initViewHolderListeners(viewHolder: DataBoundViewHolder, viewType: Int) {
        viewHolder.setOnClickListener(this) { item, _, _ ->
            mediaProvider.playRecentlyAdded(item.mediaId.toDomain())
        }
        viewHolder.setOnLongClickListener(this) { item, _, _ ->
            navigator.toDialog(item.mediaId.toDomain(), viewHolder.itemView, viewHolder.itemView)
        }

        viewHolder.setOnClickListener(R.id.more, this) { item, _, view ->
            navigator.toDialog(item.mediaId.toDomain(), view, viewHolder.itemView)
        }
        viewHolder.elevateSongOnTouch()
    }

    override fun onBindViewHolder(
        holder: DataBoundViewHolder,
        position: Int,
        payloads: MutableList<Any>
    ) {
        val payload = payloads.filterIsInstance<Boolean>().firstOrNull()
        if (payload != null) {
            holder.itemView.isPlaying.toggleVisibility(payload)
        } else {
            super.onBindViewHolder(holder, position, payloads)
        }
    }

    override fun bind(holder: DataBoundViewHolder, item: DisplayableTrack, position: Int) {
        holder.itemView.apply {
            transitionName = "detail recent ${item.mediaId}"
            holder.imageView!!.loadSongImage(item.mediaId.toDomain())
            isPlaying.toggleVisibility(item.mediaId == playingMediaId)
            firstText.text = item.title
            secondText.text = item.subtitle
            explicit.onItemChanged(item.title)
        }
    }

}