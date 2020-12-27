package dev.olog.feature.detail.detail.adapter

import androidx.recyclerview.widget.DiffUtil
import dev.olog.feature.detail.R
import dev.olog.feature.detail.detail.model.DetailFragmentRelatedArtistModel
import dev.olog.navigation.Navigator
import dev.olog.lib.image.provider.ImageLoader
import dev.olog.shared.widgets.adapter.*
import kotlinx.android.synthetic.main.item_detail_related_artist.*

internal class DetailRelatedArtistsAdapter(
    private val navigator: Navigator
) : ObservableAdapter<DetailFragmentRelatedArtistModel>(DetailRelatedArtistDiff) {

    override fun getItemViewType(position: Int): Int = R.layout.item_detail_related_artist

    override fun initViewHolderListeners(viewHolder: LayoutContainerViewHolder, viewType: Int) {
        viewHolder.setOnClickListener(this) { item, _, _ ->
            navigator.toDetailFragment(item.mediaId)
        }
        viewHolder.setOnLongClickListener(this) { item, _, _ ->
            navigator.toDialog(item.mediaId, viewHolder.itemView)
        }
        viewHolder.elevateAlbumOnTouch()
    }

    override fun bind(
        holder: LayoutContainerViewHolder,
        item: DetailFragmentRelatedArtistModel,
        position: Int
    ) = holder.bindView {
        ImageLoader.loadAlbumImage(imageView!!, item.mediaId)
        firstText.text = item.title
        secondText.text = item.subtitle
//        quickAction.setId(item.mediaId)
    }
}


private object DetailRelatedArtistDiff : DiffUtil.ItemCallback<DetailFragmentRelatedArtistModel>() {

    override fun areItemsTheSame(
        oldItem: DetailFragmentRelatedArtistModel,
        newItem: DetailFragmentRelatedArtistModel
    ): Boolean {
        return oldItem.mediaId == newItem.mediaId
    }

    override fun areContentsTheSame(
        oldItem: DetailFragmentRelatedArtistModel,
        newItem: DetailFragmentRelatedArtistModel
    ): Boolean {
        return oldItem == newItem
    }
}