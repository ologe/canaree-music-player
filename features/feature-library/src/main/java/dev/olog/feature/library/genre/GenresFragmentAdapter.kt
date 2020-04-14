package dev.olog.feature.library.genre

import dev.olog.core.extensions.findActivity
import dev.olog.feature.presentation.base.adapter.*
import dev.olog.feature.presentation.base.loadSongImage
import dev.olog.feature.presentation.base.model.DisplayableAlbum
import dev.olog.feature.presentation.base.model.toDomain
import dev.olog.navigation.Navigator
import kotlinx.android.synthetic.main.item_genre.view.*

internal class GenresFragmentAdapter(
    private val navigator: Navigator
) : ObservableAdapter<DisplayableAlbum>(DiffCallbackDisplayableAlbum) {

    override fun initViewHolderListeners(viewHolder: DataBoundViewHolder, viewType: Int) {
        viewHolder.setOnClickListener(this) { item, _, view ->
            navigator.toDetailFragment(view.findActivity(), item.mediaId.toDomain(), view)
        }
        viewHolder.elevateSongOnTouch()
    }

    override fun bind(holder: DataBoundViewHolder, item: DisplayableAlbum, position: Int) {
        holder.itemView.transitionName = "genre ${item.mediaId}"

        holder.itemView.apply {
            holder.imageView!!.loadSongImage(item.mediaId.toDomain())
            firstText.text = item.title
            secondText?.text = item.subtitle
        }
    }

}