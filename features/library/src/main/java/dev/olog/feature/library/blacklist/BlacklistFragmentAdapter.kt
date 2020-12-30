package dev.olog.feature.library.blacklist

import androidx.core.view.isVisible
import dev.olog.feature.base.adapter.LayoutContainerViewHolder
import dev.olog.feature.base.adapter.SimpleAdapter
import dev.olog.feature.library.R
import dev.olog.lib.image.provider.ImageLoader
import kotlinx.android.synthetic.main.dialog_blacklist_item.*

internal class BlacklistFragmentAdapter(
    data: List<BlacklistFragmentModel>
) : SimpleAdapter<BlacklistFragmentModel>(data.toMutableList()) {

    override fun getItemViewType(position: Int): Int = R.layout.dialog_blacklist_item

    override fun initViewHolderListeners(viewHolder: LayoutContainerViewHolder, viewType: Int) {
        viewHolder.itemView.setOnClickListener {
            val item = getItem(viewHolder.adapterPosition)
            item.isBlacklisted = !item.isBlacklisted
            notifyItemChanged(viewHolder.adapterPosition)
        }
    }

    override fun LayoutContainerViewHolder.bind(
        item: BlacklistFragmentModel,
        position: Int
    ) = bindView {
        ImageLoader.loadAlbumImage(cover, item.mediaId)
        scrim.isVisible = item.isBlacklisted
        firstText.text = item.title
        secondText.text = item.displayablePath
    }

}