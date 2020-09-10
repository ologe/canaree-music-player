package dev.olog.feature.library.artist

import androidx.recyclerview.widget.DiffUtil
import dev.olog.feature.library.R
import dev.olog.feature.presentation.base.adapter.*
import dev.olog.feature.presentation.base.loadAlbumImage
import dev.olog.feature.presentation.base.model.toDomain
import dev.olog.navigation.Navigator

//internal class ArtistFragmentAdapter(
//    private val navigator: Navigator
//) : ObservableAdapter2<ArtistFragmentItem>(ArtistFragmentItemDiff) {
//
//    override fun initViewHolderListeners(viewHolder: DataBoundViewHolder, viewType: Int) {
//        viewHolder.setOnClickListener(this) { item, _, view ->
//            navigator.toDetailFragment(item.mediaId.toDomain(), view)
//        }
//        viewHolder.setOnLongClickListener(this) { item, _, _ ->
//            navigator.toDialog(item.mediaId.toDomain(), viewHolder.itemView, viewHolder.itemView)
//        }
//        viewHolder.elevateAlbumOnTouch()
//    }
//
//    override fun getItemViewType(position: Int): Int = R.layout.item_tab_artist
//
//    override fun bind(
//        holder: DataBoundViewHolder,
//        item: ArtistFragmentItem,
//        position: Int
//    ) = with(holder) {
//        itemView.transitionName = "tab ${item.mediaId}"
//
//        cover.loadAlbumImage(item.mediaId.toDomain())
//        quickAction.setId(item.mediaId)
//        firstText.text = item.title
//        secondText.text = item.subtitle
//    }
//}
//
//private object ArtistFragmentItemDiff : DiffUtil.ItemCallback<ArtistFragmentItem>() {
//
//    override fun areItemsTheSame(oldItem: ArtistFragmentItem, newItem: ArtistFragmentItem): Boolean {
//        return oldItem.mediaId == newItem.mediaId
//    }
//
//    override fun areContentsTheSame(
//        oldItem: ArtistFragmentItem,
//        newItem: ArtistFragmentItem
//    ): Boolean {
//        return oldItem == newItem
//    }
//}