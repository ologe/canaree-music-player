package dev.olog.feature.library.playlists

import androidx.core.view.isVisible
import dev.olog.core.extensions.findActivity
import dev.olog.domain.MediaId
import dev.olog.domain.MediaIdCategory
import dev.olog.domain.entity.AutoPlaylist
import dev.olog.feature.library.R
import dev.olog.feature.presentation.base.adapter.*
import dev.olog.feature.presentation.base.loadAlbumImage
import dev.olog.feature.presentation.base.model.*
import dev.olog.navigation.Navigator
import dev.olog.shared.exhaustive
import dev.olog.shared.throwNotHandled
import kotlinx.android.synthetic.main.item_tab_album.view.*
import kotlinx.android.synthetic.main.item_tab_album.view.firstText
import kotlinx.android.synthetic.main.item_tab_album.view.secondText
import kotlinx.android.synthetic.main.item_home_header.view.*
import kotlinx.android.synthetic.main.item_track.view.*

internal class PlaylistsFragmentAdapter (
    private val navigator: Navigator
) : ObservableAdapter<DisplayableItem>(DiffCallbackDisplayableItem) {

    override fun initViewHolderListeners(viewHolder: DataBoundViewHolder, viewType: Int) {
        when (viewType) {
            R.layout.item_playlist_header -> {
                viewHolder.setOnClickListener(R.id.lastAdded, this) { _, _, view ->
                    val mediaId = MediaId.Category(MediaIdCategory.PLAYLISTS, AutoPlaylist.LAST_ADDED.id.toString())
                    navigator.toDetailFragment(view.findActivity(), mediaId, view)
                }
                viewHolder.setOnClickListener(R.id.history, this) { _, _, view ->
                    val mediaId = MediaId.Category(MediaIdCategory.PLAYLISTS, AutoPlaylist.HISTORY.id.toString())
                    navigator.toDetailFragment(view.findActivity(), mediaId, view)
                }
            }
            R.layout.item_playlist,
            R.layout.item_playlist_podcast -> {
                viewHolder.setOnClickListener(this) { item, _, view ->
                    require(item is DisplayableAlbum)
                    navigator.toDetailFragment(view.findActivity(), item.mediaId.toDomain(), view)
                }
                viewHolder.setOnLongClickListener(this) { item, _, _ ->
                    navigator.toDialog(item.mediaId.toDomain(), viewHolder.itemView, viewHolder.itemView)
                }
                viewHolder.elevateSongOnTouch()
            }
        }
        // TODO elevate
    }

    override fun bind(holder: DataBoundViewHolder, item: DisplayableItem, position: Int) {
        holder.itemView.transitionName = "playlists ${item.mediaId}"

        when (item){
            is DisplayableAlbum -> bindAlbum(holder, item)
            is DisplayableHeader -> bindHeader(holder, item)
            is DisplayableNestedListPlaceholder -> throwNotHandled(item)
            is DisplayableTrack -> throwNotHandled(item)
        }.exhaustive
    }

    private fun bindAlbum(holder: DataBoundViewHolder, item: DisplayableAlbum){
        holder.itemView.apply {
            holder.imageView!!.loadAlbumImage(item.mediaId.toDomain())
            quickAction?.setId(item.mediaId)
            firstText.text = item.title
            secondText?.text = item.subtitle
            explicit?.isVisible = false
        }
    }

    private fun bindHeader(holder: DataBoundViewHolder, item: DisplayableHeader){
        if (holder.itemViewType == R.layout.item_home_header){
            holder.itemView.title.text = item.title
        }
    }

}