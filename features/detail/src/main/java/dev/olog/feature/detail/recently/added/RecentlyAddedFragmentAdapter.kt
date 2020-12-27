package dev.olog.feature.detail.recently.added

import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import dev.olog.feature.base.adapter.*
import dev.olog.feature.base.adapter.drag.IDragListener
import dev.olog.feature.base.adapter.drag.TouchableAdapter
import dev.olog.feature.detail.R
import dev.olog.lib.image.provider.ImageLoader
import dev.olog.lib.media.MediaProvider
import dev.olog.navigation.Navigator
import kotlinx.android.synthetic.main.item_recently_added.*

class RecentlyAddedFragmentAdapter(
    private val navigator: Navigator,
    private val mediaProvider: MediaProvider,
    private val dragListener: IDragListener
) : ObservableAdapter<RecentlyAddedFragmentModel>(RecentlyAddedFragmentModelDiff),
    TouchableAdapter {

    override fun getItemViewType(position: Int): Int = R.layout.item_recently_added

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
        viewHolder.elevateAlbumOnTouch()
        viewHolder.setOnDragListener(R.id.dragHandle, dragListener)
    }

    override fun bind(
        holder: LayoutContainerViewHolder,
        item: RecentlyAddedFragmentModel,
        position: Int
    ) = holder.bindView {

        ImageLoader.loadSongImage(imageView!!, item.mediaId)
        firstText.text = item.title
        secondText.text = item.subtitle
        explicit.onItemChanged(item.title)
    }

    override fun canInteractWithViewHolder(viewType: Int): Boolean {
        return viewType == R.layout.item_recently_added
    }

    override fun afterSwipeLeft(viewHolder: RecyclerView.ViewHolder) {
        val item = getItem(viewHolder.adapterPosition)
        mediaProvider.addToPlayNext(item.mediaId)
    }

}

private object RecentlyAddedFragmentModelDiff : DiffUtil.ItemCallback<RecentlyAddedFragmentModel>() {

    override fun areItemsTheSame(
        oldItem: RecentlyAddedFragmentModel,
        newItem: RecentlyAddedFragmentModel
    ): Boolean {
        return oldItem.mediaId == newItem.mediaId
    }

    override fun areContentsTheSame(
        oldItem: RecentlyAddedFragmentModel,
        newItem: RecentlyAddedFragmentModel
    ): Boolean {
        return oldItem == newItem
    }
}