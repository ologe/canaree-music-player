package dev.olog.msc.presentation.detail.adapter

import androidx.databinding.ViewDataBinding
import androidx.lifecycle.Lifecycle
import androidx.recyclerview.widget.DiffUtil
import dev.olog.media.MediaProvider
import dev.olog.msc.BR
import dev.olog.msc.R
import dev.olog.msc.utils.k.extension.elevateSongOnTouch
import dev.olog.presentation.base.DataBoundViewHolder
import dev.olog.presentation.base.ObservableAdapter
import dev.olog.presentation.base.setOnClickListener
import dev.olog.presentation.base.setOnLongClickListener
import dev.olog.presentation.model.DisplayableItem
import dev.olog.presentation.navigator.Navigator
import kotlinx.android.synthetic.main.item_detail_song_most_played.view.*

class DetailMostPlayedAdapter(
    lifecycle: Lifecycle,
    private val navigator: Navigator,
    private val mediaProvider: MediaProvider

) : ObservableAdapter<DisplayableItem>(lifecycle, DiffCallbackMostPlayed) {

    override fun initViewHolderListeners(viewHolder: DataBoundViewHolder, viewType: Int) {
        viewHolder.setOnClickListener(this) { item, _, _ ->
            mediaProvider.playMostPlayed(item.mediaId)
        }

        viewHolder.setOnLongClickListener(this) { item, _, _ ->
            navigator.toDialog(item, viewHolder.itemView)
        }

        viewHolder.setOnClickListener(R.id.more, this) { item, _, view ->
            navigator.toDialog(item, view)
        }
        viewHolder.elevateSongOnTouch()
    }

    override fun bind(binding: ViewDataBinding, item: DisplayableItem, position: Int){
        binding.setVariable(BR.item, item)
        binding.setVariable(BR.position, position)
    }

    override fun onBindViewHolder(holder: DataBoundViewHolder, position: Int, payloads: MutableList<Any>) {
        if (payloads.isNotEmpty()){
            holder.itemView.index.text = (payloads[0] as Int + 1).toString()
        } else {
            super.onBindViewHolder(holder, position, payloads)

        }
    }

}

internal object DiffCallbackMostPlayed : DiffUtil.ItemCallback<DisplayableItem>() {
    override fun areItemsTheSame(oldItem: DisplayableItem, newItem: DisplayableItem): Boolean {
        return oldItem.mediaId == newItem.mediaId
    }

    override fun areContentsTheSame(oldItem: DisplayableItem, newItem: DisplayableItem): Boolean {
        val sameTitle = oldItem.title == newItem.title
        val sameSubtitle = oldItem.subtitle == newItem.subtitle
        val sameIndex = oldItem.extra!!.getInt("position") == newItem.extra!!.getInt("position")
        return sameTitle && sameSubtitle && sameIndex
    }

    override fun getChangePayload(oldItem: DisplayableItem, newItem: DisplayableItem): Any? {
        if (oldItem.extra!!.getInt("position") != newItem.extra!!.getInt("position")) {
            return newItem.extra!!.getInt("position")
        }
        return super.getChangePayload(oldItem, newItem)
    }
}