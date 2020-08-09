package dev.olog.feature.library.album

import androidx.recyclerview.widget.DiffUtil
import dev.olog.feature.library.R
import dev.olog.feature.presentation.base.adapter.*
import dev.olog.feature.presentation.base.loadAlbumImage
import dev.olog.feature.presentation.base.model.toDomain
import dev.olog.navigation.Navigator
import kotlinx.android.synthetic.main.item_tab_album.*

internal class AlbumFragmentAdapter(
    private val navigator: Navigator
) : ObservableAdapter2<AlbumFragmentItem>(AlbumFragmentItemDiff) {

    override fun initViewHolderListeners(viewHolder: DataBoundViewHolder, viewType: Int) {
        viewHolder.setOnClickListener(this) { item, _, view ->
            navigator.toDetailFragment(item.mediaId.toDomain(), view)
        }
        viewHolder.setOnLongClickListener(this) { item, _, _ ->
            navigator.toDialog(item.mediaId.toDomain(), viewHolder.itemView, viewHolder.itemView)
        }
        viewHolder.elevateAlbumOnTouch()
    }

    override fun getItemViewType(position: Int): Int = R.layout.item_tab_album

    override fun bind(
        holder: DataBoundViewHolder,
        item: AlbumFragmentItem,
        position: Int
    ) = with(holder) {
        itemView.transitionName = "tab ${item.mediaId}"

        cover.loadAlbumImage(item.mediaId.toDomain())
        quickAction.setId(item.mediaId)
        firstText.text = item.title
        secondText.text = item.subtitle
    }
}

private object AlbumFragmentItemDiff : DiffUtil.ItemCallback<AlbumFragmentItem>() {

    override fun areItemsTheSame(oldItem: AlbumFragmentItem, newItem: AlbumFragmentItem): Boolean {
        return oldItem.mediaId == newItem.mediaId
    }

    override fun areContentsTheSame(
        oldItem: AlbumFragmentItem,
        newItem: AlbumFragmentItem
    ): Boolean {
        return oldItem == newItem
    }
}