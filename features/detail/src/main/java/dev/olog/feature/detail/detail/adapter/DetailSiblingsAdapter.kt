package dev.olog.feature.detail.detail.adapter

import androidx.recyclerview.widget.DiffUtil
import dev.olog.feature.detail.R
import dev.olog.feature.detail.detail.model.DetailFragmentAlbumModel
import dev.olog.navigation.Navigator
import dev.olog.presentation.BindingsAdapter
import dev.olog.presentation.base.adapter.*
import kotlinx.android.synthetic.main.item_detail_album.*

internal class DetailSiblingsAdapter(
    private val navigator: Navigator
) : ObservableAdapter<DetailFragmentAlbumModel>(DetailSiblingDiff) {

    override fun getItemViewType(position: Int): Int = R.layout.item_detail_album

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
        item: DetailFragmentAlbumModel,
        position: Int
    ) = holder.bindView {

        BindingsAdapter.loadAlbumImage(imageView!!, item.mediaId)
        quickAction.setId(item.mediaId)
        firstText.text = item.title
        secondText.text = item.subtitle
    }
}

private object DetailSiblingDiff : DiffUtil.ItemCallback<DetailFragmentAlbumModel>() {

    override fun areItemsTheSame(
        oldItem: DetailFragmentAlbumModel,
        newItem: DetailFragmentAlbumModel
    ): Boolean {
        return oldItem.mediaId == newItem.mediaId
    }

    override fun areContentsTheSame(
        oldItem: DetailFragmentAlbumModel,
        newItem: DetailFragmentAlbumModel
    ): Boolean {
        return oldItem == newItem
    }
}