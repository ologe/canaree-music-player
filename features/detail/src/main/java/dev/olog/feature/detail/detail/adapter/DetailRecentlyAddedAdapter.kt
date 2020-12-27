package dev.olog.feature.detail.detail.adapter

import androidx.recyclerview.widget.DiffUtil
import dev.olog.feature.base.adapter.*
import dev.olog.feature.detail.R
import dev.olog.feature.detail.detail.model.DetailFragmentRecentlyAddedModel
import dev.olog.lib.media.MediaProvider
import dev.olog.navigation.Navigator
import dev.olog.lib.image.provider.ImageLoader
import kotlinx.android.synthetic.main.item_detail_song_recent.*

internal class DetailRecentlyAddedAdapter(
    private val navigator: Navigator,
    private val mediaProvider: MediaProvider
) : ObservableAdapter<DetailFragmentRecentlyAddedModel>(DetailRecentlyAddedDiff) {

    override fun getItemViewType(position: Int): Int = R.layout.item_detail_song_recent

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
        item: DetailFragmentRecentlyAddedModel,
        position: Int
    ) = holder.bindView {

        ImageLoader.loadSongImage(imageView!!, item.mediaId)
        firstText.text = item.title
        secondText.text = item.subtitle
        explicit.onItemChanged(item.title)
    }

}

private object DetailRecentlyAddedDiff : DiffUtil.ItemCallback<DetailFragmentRecentlyAddedModel>() {

    override fun areItemsTheSame(
        oldItem: DetailFragmentRecentlyAddedModel,
        newItem: DetailFragmentRecentlyAddedModel
    ): Boolean {
        return oldItem.mediaId == newItem.mediaId
    }

    override fun areContentsTheSame(
        oldItem: DetailFragmentRecentlyAddedModel,
        newItem: DetailFragmentRecentlyAddedModel
    ): Boolean {
        return oldItem == newItem
    }
}