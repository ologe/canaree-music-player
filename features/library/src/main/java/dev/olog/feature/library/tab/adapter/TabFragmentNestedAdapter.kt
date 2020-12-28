package dev.olog.feature.library.tab.adapter

import androidx.recyclerview.widget.DiffUtil
import dev.olog.feature.base.adapter.*
import dev.olog.feature.library.tab.model.TabFragmentModel
import dev.olog.lib.image.provider.ImageLoader
import dev.olog.navigation.Navigator
import kotlinx.android.synthetic.main.item_tab_album_last_played.*

internal class TabFragmentNestedAdapter(
    private val navigator: Navigator,
) : ObservableAdapter<TabFragmentModel.Album>(TabFragmentModelAlbumDiff) {

    override fun getItemViewType(position: Int): Int = getItem(position).layoutType

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
        item: TabFragmentModel.Album,
        position: Int
    ) = holder.bindView {
        ImageLoader.loadAlbumImage(cover, item.mediaId)
        quickAction.setId(item.mediaId)
        firstText.text = item.title
        secondText.text = item.subtitle
    }

}

private object TabFragmentModelAlbumDiff : DiffUtil.ItemCallback<TabFragmentModel.Album>() {

    override fun areItemsTheSame(
        oldItem: TabFragmentModel.Album,
        newItem: TabFragmentModel.Album
    ): Boolean {
        return oldItem.mediaId == newItem.mediaId
    }

    override fun areContentsTheSame(
        oldItem: TabFragmentModel.Album,
        newItem: TabFragmentModel.Album
    ): Boolean {
        return oldItem == newItem
    }
}