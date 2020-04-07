package dev.olog.presentation.recentlyadded

import androidx.recyclerview.widget.RecyclerView
import dev.olog.lib.media.MediaProvider
import dev.olog.presentation.R
import dev.olog.presentation.base.adapter.*
import dev.olog.presentation.base.drag.IDragListener
import dev.olog.presentation.base.drag.TouchableAdapter
import dev.olog.presentation.loadSongImage
import dev.olog.presentation.model.DisplayableTrack
import dev.olog.presentation.navigator.Navigator
import dev.olog.presentation.toDomain
import kotlinx.android.synthetic.main.item_recently_added.view.*

internal class RecentlyAddedFragmentAdapter(
    private val navigator: Navigator,
    private val mediaProvider: MediaProvider,
    private val dragListener: IDragListener

) : ObservableAdapter<DisplayableTrack>(DiffCallbackDisplayableTrack),
    TouchableAdapter,
    CanShowIsPlaying by CanShowIsPlayingImpl() {

    override fun initViewHolderListeners(viewHolder: DataBoundViewHolder, viewType: Int) {
        viewHolder.setOnClickListener(this) { item, _, _ ->
            mediaProvider.playFromMediaId(item.mediaId.toDomain(), null, null)
        }
        viewHolder.setOnLongClickListener(this) { item, _, _ ->
            navigator.toDialog(item.mediaId, viewHolder.itemView, viewHolder.itemView)
        }
        viewHolder.setOnClickListener(R.id.more, this) { item, _, view ->
            navigator.toDialog(item.mediaId, view, viewHolder.itemView)
        }
        viewHolder.elevateAlbumOnTouch()
        viewHolder.setOnDragListener(R.id.dragHandle, dragListener)
    }

    override fun bind(holder: DataBoundViewHolder, item: DisplayableTrack, position: Int) {

        holder.itemView.apply {
            isPlaying.toggleVisibility(item.mediaId == playingMediaId)
            holder.imageView!!.loadSongImage(item.mediaId.toDomain())
            firstText.text = item.title
            secondText.text = item.subtitle
            explicit.onItemChanged(item.title)
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

    override fun canInteractWithViewHolder(viewType: Int): Boolean {
        return viewType == R.layout.item_recently_added
    }

    override fun afterSwipeLeft(viewHolder: RecyclerView.ViewHolder) {
        val item = getItem(viewHolder.adapterPosition)
        mediaProvider.addToPlayNext(item.mediaId.toDomain())
    }

}