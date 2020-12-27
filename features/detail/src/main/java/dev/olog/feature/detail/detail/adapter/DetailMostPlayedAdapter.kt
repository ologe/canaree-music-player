package dev.olog.feature.detail.detail.adapter

import androidx.recyclerview.widget.DiffUtil
import dev.olog.feature.detail.R
import dev.olog.feature.detail.detail.model.DetailFragmentMostPlayedModel
import dev.olog.lib.media.MediaProvider
import dev.olog.navigation.Navigator
import dev.olog.lib.image.provider.ImageLoader
import dev.olog.shared.widgets.adapter.*
import kotlinx.android.synthetic.main.item_detail_song_most_played.*

internal class DetailMostPlayedAdapter(
    private val navigator: Navigator,
    private val mediaProvider: MediaProvider
) : ObservableAdapter<DetailFragmentMostPlayedModel>(DetailMostPlayedDiff) {

    override fun getItemViewType(position: Int): Int = R.layout.item_detail_song_most_played

    override fun initViewHolderListeners(viewHolder: LayoutContainerViewHolder, viewType: Int) {
        viewHolder.setOnClickListener(this) { item, _, _ ->
            mediaProvider.playFromMediaId(item.mediaId, null, null)
        }

        viewHolder.setOnLongClickListener(this) { item, _, _ ->
            navigator.toDialog(item.mediaId, viewHolder.itemView)
        }

        viewHolder.setOnClickListener(R.id.more, this) { item, _, view ->
            navigator.toDialog(item.mediaId, view)
        }
        viewHolder.elevateSongOnTouch()
    }

    override fun bind(
        holder: LayoutContainerViewHolder,
        item: DetailFragmentMostPlayedModel,
        position: Int
    ) = holder.bindView {

        ImageLoader.loadSongImage(imageView!!, item.mediaId)
        firstText.text = item.title
        secondText.text = item.subtitle
        index.text = item.formattedPosition
        explicit.onItemChanged(item.title)
    }

    override fun onBindViewHolder(
        holder: LayoutContainerViewHolder,
        position: Int,
        payloads: MutableList<Any>
    ) = holder.bindView {
        if (payloads.isNotEmpty()) {
            val positionInList = (payloads[0] as Int + 1).toString()
            index.text = positionInList
        } else {
            super.onBindViewHolder(holder, position, payloads)
        }
    }

}

private object DetailMostPlayedDiff : DiffUtil.ItemCallback<DetailFragmentMostPlayedModel>() {

    override fun areItemsTheSame(oldItem: DetailFragmentMostPlayedModel, newItem: DetailFragmentMostPlayedModel): Boolean {
        return oldItem.mediaId == newItem.mediaId
    }

    override fun areContentsTheSame(oldItem: DetailFragmentMostPlayedModel, newItem: DetailFragmentMostPlayedModel): Boolean {
        return oldItem == newItem
    }

    override fun getChangePayload(oldItem: DetailFragmentMostPlayedModel, newItem: DetailFragmentMostPlayedModel): Any? {
        if (oldItem.position != newItem.position) {
            return newItem.position
        }
        return super.getChangePayload(oldItem, newItem)
    }
}