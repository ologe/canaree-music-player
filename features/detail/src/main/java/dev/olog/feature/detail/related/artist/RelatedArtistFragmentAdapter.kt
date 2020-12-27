package dev.olog.feature.detail.related.artist

import androidx.recyclerview.widget.DiffUtil
import dev.olog.feature.base.adapter.*
import dev.olog.feature.detail.R
import dev.olog.lib.image.provider.ImageLoader
import dev.olog.navigation.Navigator
import kotlinx.android.synthetic.main.item_related_artist.*

class RelatedArtistFragmentAdapter(
    private val navigator: Navigator
) : ObservableAdapter<RelatedArtistFragmentModel>(RelatedArtistFragmentModelDiff) {

    override fun getItemViewType(position: Int): Int = R.layout.item_related_artist

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
        item: RelatedArtistFragmentModel,
        position: Int
    ) = holder.bindView {
        ImageLoader.loadAlbumImage(imageView!!, item.mediaId)
        quickAction.setId(item.mediaId)
        firstText.text = item.title
        secondText.text = item.subtitle
    }


}

private object RelatedArtistFragmentModelDiff : DiffUtil.ItemCallback<RelatedArtistFragmentModel>() {

    override fun areItemsTheSame(
        oldItem: RelatedArtistFragmentModel,
        newItem: RelatedArtistFragmentModel
    ): Boolean {
        return oldItem.mediaId == newItem.mediaId
    }

    override fun areContentsTheSame(
        oldItem: RelatedArtistFragmentModel,
        newItem: RelatedArtistFragmentModel
    ): Boolean {
        return oldItem == newItem
    }
}