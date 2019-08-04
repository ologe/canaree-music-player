package dev.olog.presentation.offlinelyrics2

import androidx.lifecycle.Lifecycle
import androidx.recyclerview.widget.DiffUtil
import dev.olog.presentation.base.adapter.DataBoundViewHolder
import dev.olog.presentation.base.adapter.ObservableAdapter
import kotlinx.android.synthetic.main.item_offline_lyrics_2.view.*

class OfflineLyricsAdapter(
    lifecycle: Lifecycle
) : ObservableAdapter<LyricsModel>(lifecycle, DiffCallbackLyricsModel) {

    override fun initViewHolderListeners(viewHolder: DataBoundViewHolder, viewType: Int) {

    }

    override fun bind(holder: DataBoundViewHolder, item: LyricsModel, position: Int) {
        holder.view.content.text = item.content
    }
}

internal object DiffCallbackLyricsModel : DiffUtil.ItemCallback<LyricsModel>() {
    override fun areItemsTheSame(oldItem: LyricsModel, newItem: LyricsModel): Boolean {
        return oldItem.mediaId == newItem.mediaId
    }

    override fun areContentsTheSame(oldItem: LyricsModel, newItem: LyricsModel): Boolean {
        return oldItem == newItem
    }

    // TODO on payload change
}