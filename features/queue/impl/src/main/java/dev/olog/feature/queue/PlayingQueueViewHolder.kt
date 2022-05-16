package dev.olog.feature.queue

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import dev.olog.feature.queue.databinding.ItemPlayingQueueBinding
import dev.olog.image.provider.BindingsAdapter
import dev.olog.ui.textColorPrimary
import dev.olog.ui.textColorSecondary

class PlayingQueueViewHolder(
    viewGroup: ViewGroup,
    private val binding: ItemPlayingQueueBinding = ItemPlayingQueueBinding.inflate(
        LayoutInflater.from(viewGroup.context), viewGroup, false
    )
) : RecyclerView.ViewHolder(binding.root) {

    fun bind(item: QueueItem) = with(binding) {
        bindDefault(item)

        // load image
        BindingsAdapter.loadSongImage(cover, item.mediaId)
    }

    fun rebind(
        item: QueueItem
    ) {
        bindDefault(item)

        // do not reload image
    }

    private fun bindDefault(item: QueueItem) = with(binding) {
        BindingsAdapter.setBoldIfTrue(firstText, item.isCurrentSong)
        firstText.text = item.title
        secondText.text = item.subtitle
        explicit.onItemChanged(item.title)

        // set text and text color
        val textColor = calculateTextColor(root.context, item.relativePosition)
        index.text = item.relativePosition
        index.setTextColor(textColor)
    }

    private fun calculateTextColor(context: Context, positionInList: String): Int {
        return if (positionInList.startsWith("-")) context.textColorSecondary()
        else context.textColorPrimary()
    }

}