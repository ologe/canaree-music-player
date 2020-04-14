package dev.olog.feature.library.folder.normal

import dev.olog.core.extensions.findActivity
import dev.olog.feature.presentation.base.adapter.DataBoundViewHolder
import dev.olog.feature.presentation.base.adapter.DiffCallbackDisplayableAlbum
import dev.olog.feature.presentation.base.adapter.ObservableAdapter
import dev.olog.feature.presentation.base.adapter.setOnClickListener
import dev.olog.feature.presentation.base.loadAlbumImage
import dev.olog.feature.presentation.base.model.DisplayableAlbum
import dev.olog.feature.presentation.base.model.toDomain
import dev.olog.navigation.Navigator
import kotlinx.android.synthetic.main.item_folder.view.*

internal class FoldersNormalFragmentAdapter(
    private val navigator: Navigator
) : ObservableAdapter<DisplayableAlbum>(DiffCallbackDisplayableAlbum) {

    override fun initViewHolderListeners(viewHolder: DataBoundViewHolder, viewType: Int) {
        viewHolder.setOnClickListener(this) { item, _, view ->
            navigator.toDetailFragment(view.findActivity(), item.mediaId.toDomain(), view)
        }
    }

    override fun bind(holder: DataBoundViewHolder, item: DisplayableAlbum, position: Int) {
        holder.itemView.apply {
            // TODO load song image if span is 1
            holder.imageView!!.loadAlbumImage(item.mediaId.toDomain())
            quickAction?.setId(item.mediaId)
            firstText.text = item.title
            secondText.text = item.subtitle
        }
    }

}