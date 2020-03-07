package dev.olog.offlinelyrics

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import dev.olog.shared.android.extensions.textColorPrimaryInverse
import dev.olog.shared.android.extensions.textColorSecondary
import dev.olog.shared.clamp
import kotlinx.android.synthetic.main.item_offline_lyrics.view.*
import kotlin.properties.Delegates

class OfflineLyricsAdapter(
    private val onSelectedChanged: (Int) -> Unit
) : ListAdapter<OfflineLyricsLine, RecyclerView.ViewHolder>(OfflineLyricsDiff) {

    var selectedIndex by Delegates.observable(0) { _, old, new ->
        if (old != new) {
            onSelectedChanged(new)
        }
    }
        private set

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val view = inflater.inflate(R.layout.item_offline_lyrics, parent, false)
        return object : RecyclerView.ViewHolder(view) {}
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {

        holder.itemView.apply {
            line.text = getItem(position).value
            bindSelected(this, position)
        }
    }

    private fun bindSelected(view: View, position: Int) {
        val context = view.context

        val isCurrent = selectedIndex == position

        val color = if (isCurrent) {
            context.textColorPrimaryInverse()
        } else {
            context.textColorSecondary()
        }

        view.apply {
            line.alpha = if (isCurrent) 1f else .5f
            line.setTextColor(color)
        }
    }

    override fun onBindViewHolder(
        holder: RecyclerView.ViewHolder,
        position: Int,
        payloads: MutableList<Any>
    ) {
        val payload = payloads.filterIsInstance<Int>().firstOrNull()
        if (payload != null) {
            bindSelected(holder.itemView, position)
        } else {
            super.onBindViewHolder(holder, position, payloads)
        }
    }

    fun updateTime(time: Long) {
        var index = currentList.indexOfFirst { it.time > time }
        index = clamp(index - 1, 0, currentList.lastIndex)
        if (index != selectedIndex) {
            notifyItemChanged(index)
            notifyItemChanged(selectedIndex)
            selectedIndex = index
        }
    }
}

private object OfflineLyricsDiff : DiffUtil.ItemCallback<OfflineLyricsLine>() {

    override fun areItemsTheSame(oldItem: OfflineLyricsLine, newItem: OfflineLyricsLine): Boolean {
        return oldItem == newItem
    }

    override fun areContentsTheSame(
        oldItem: OfflineLyricsLine,
        newItem: OfflineLyricsLine
    ): Boolean {
        return areItemsTheSame(oldItem, newItem)
    }
}