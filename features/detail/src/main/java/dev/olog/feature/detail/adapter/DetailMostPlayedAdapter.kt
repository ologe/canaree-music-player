package dev.olog.feature.detail.adapter

import androidx.recyclerview.widget.DiffUtil
import dev.olog.feature.detail.R
import dev.olog.feature.presentation.base.adapter.*
import dev.olog.lib.media.MediaProvider
import dev.olog.feature.presentation.base.loadSongImage
import dev.olog.feature.presentation.base.model.DisplayableTrack
import dev.olog.navigation.Navigator
import dev.olog.feature.presentation.base.model.toDomain

internal class DetailMostPlayedAdapter(
    private val navigator: Navigator,
    private val mediaProvider: MediaProvider

) : ObservableAdapter<DisplayableTrack>(DiffCallbackMostPlayed),
    CanShowIsPlaying by CanShowIsPlayingImpl() {

    override fun initViewHolderListeners(viewHolder: DataBoundViewHolder, viewType: Int) {
        viewHolder.setOnClickListener(this) { item, _, _ ->
            mediaProvider.playMostPlayed(item.mediaId.toDomain())
        }

        viewHolder.setOnLongClickListener(this) { item, _, _ ->
            navigator.toDialog(item.mediaId.toDomain(), viewHolder.itemView, viewHolder.itemView)
        }

        viewHolder.setOnClickListener(R.id.more, this) { item, _, view ->
            navigator.toDialog(item.mediaId.toDomain(), view, viewHolder.itemView)
        }
        viewHolder.elevateSongOnTouch()
    }

    override fun bind(holder: DataBoundViewHolder, item: DisplayableTrack, position: Int) {
        holder.itemView.apply {
            transitionName = "detail most played ${item.mediaId}"
            holder.imageView!!.loadSongImage(item.mediaId.toDomain())
//            isPlaying.toggleVisibility(item.mediaId == playingMediaId)
//            firstText.text = item.title
//            secondText.text = item.subtitle
//            index.text = (item.idInPlaylist + 1).toString()
//            explicit.onItemChanged(item.title)
        }
    }

    override fun onBindViewHolder(
        holder: DataBoundViewHolder,
        position: Int,
        payloads: MutableList<Any>
    ) {
        val index = payloads.filterIsInstance<Int>().firstOrNull()
        if (index != null) {
            val positionInList = (index + 1).toString()
//            holder.itemView.index.text = positionInList
        }
        val payload = payloads.filterIsInstance<Boolean>().firstOrNull()
        if (payload != null) {
//            holder.itemView.isPlaying.animateVisibility(payload)
        }
        if (payloads.isEmpty()) {
            super.onBindViewHolder(holder, position, payloads)
        }
    }

}

internal object DiffCallbackMostPlayed : DiffUtil.ItemCallback<DisplayableTrack>() {
    override fun areItemsTheSame(oldItem: DisplayableTrack, newItem: DisplayableTrack): Boolean {
        return oldItem.mediaId == newItem.mediaId
    }

    override fun areContentsTheSame(oldItem: DisplayableTrack, newItem: DisplayableTrack): Boolean {
        val sameTitle = oldItem.title == newItem.title
        val sameArtist = oldItem.artist == newItem.artist
        val sameAlbum = oldItem.album == newItem.album
        val sameIndex = oldItem.idInPlaylist == newItem.idInPlaylist
        return sameTitle && sameArtist && sameAlbum && sameIndex
    }

    override fun getChangePayload(oldItem: DisplayableTrack, newItem: DisplayableTrack): Any? {
        if (oldItem.idInPlaylist != newItem.idInPlaylist) {
            return newItem.idInPlaylist
        }
        return super.getChangePayload(oldItem, newItem)
    }
}