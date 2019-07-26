package dev.olog.presentation.offlinelyrics2

import androidx.databinding.ViewDataBinding
import androidx.lifecycle.Lifecycle
import androidx.recyclerview.widget.DiffUtil
import dev.olog.presentation.BR
import dev.olog.presentation.base.adapter.DataBoundViewHolder
import dev.olog.presentation.base.adapter.ObservableAdapter

class OfflineLyricsAdapter(
    lifecycle: Lifecycle
) : ObservableAdapter<LyricsModel>(lifecycle, DiffCallbackLyricsModel) {

    override fun initViewHolderListeners(viewHolder: DataBoundViewHolder, viewType: Int) {

    }

    override fun bind(binding: ViewDataBinding, item: LyricsModel, position: Int) {
        binding.setVariable(BR.item, item)
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