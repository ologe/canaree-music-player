package dev.olog.presentation.tab.adapter

import androidx.core.view.isVisible
import androidx.recyclerview.widget.RecyclerView
import dev.olog.core.MediaId
import dev.olog.feature.base.adapter.*
import dev.olog.lib.media.MediaProvider
import dev.olog.lib.image.provider.ImageLoader
import dev.olog.presentation.R
import dev.olog.presentation.base.adapter.*
import dev.olog.presentation.model.*
import dev.olog.presentation.navigator.NavigatorLegacy
import dev.olog.presentation.tab.TabFragmentViewModel
import dev.olog.shared.exhaustive
import kotlinx.android.synthetic.main.item_tab_album.*
import kotlinx.android.synthetic.main.item_tab_header.*
import kotlinx.android.synthetic.main.item_tab_podcast.*
import kotlinx.android.synthetic.main.item_tab_song.*
import kotlinx.android.synthetic.main.item_tab_song.firstText
import kotlinx.android.synthetic.main.item_tab_song.secondText

internal class TabFragmentAdapter(
    private val navigator: NavigatorLegacy,
    private val mediaProvider: MediaProvider,
    private val viewModel: TabFragmentViewModel,
    private val setupNestedList: SetupNestedList

) : ObservableAdapter<DisplayableItem>(DiffCallbackDisplayableItem) {

    override fun initViewHolderListeners(viewHolder: LayoutContainerViewHolder, viewType: Int) {
        when (viewType) {
            R.layout.item_tab_shuffle -> {
                viewHolder.setOnClickListener(this) { _, _, _ ->
                    mediaProvider.shuffle(MediaId.shuffleId(), null)
                }
            }
            R.layout.item_tab_song,
            R.layout.item_tab_podcast -> {
                viewHolder.setOnClickListener(this) { item, _, _ ->
                    onItemClick(item)

                }
                viewHolder.setOnLongClickListener(this) { item, _, _ ->
                    navigator.toDialog(item.mediaId, viewHolder.itemView)
                }
                viewHolder.elevateSongOnTouch()
            }
            R.layout.item_tab_album,
            R.layout.item_tab_artist,
            R.layout.item_tab_auto_playlist -> {
                viewHolder.setOnClickListener(this) { item, _, _ ->
                    onItemClick(item)
                }
                viewHolder.setOnLongClickListener(this) { item, _, _ ->
                    navigator.toDialog(item.mediaId, viewHolder.itemView)
                }
                viewHolder.elevateAlbumOnTouch()
            }
            R.layout.item_tab_last_played_album_horizontal_list,
            R.layout.item_tab_last_played_artist_horizontal_list,
            R.layout.item_tab_new_album_horizontal_list,
            R.layout.item_tab_new_artist_horizontal_list -> {
                val view = viewHolder.itemView as RecyclerView
                setupNestedList.setupNestedList(viewType, view)
            }
        }
    }

    private fun onItemClick(item: DisplayableItem){
        if (item is DisplayableTrack){
            val sort = viewModel.getAllTracksSortOrder(item.mediaId)
            mediaProvider.playFromMediaId(item.mediaId, null, sort)
        } else if (item is DisplayableAlbum){
            navigator.toDetailFragment(item.mediaId)
        }
    }

    override fun bind(
        holder: LayoutContainerViewHolder,
        item: DisplayableItem,
        position: Int
    ) = holder.bindView {
        when (item){
            is DisplayableTrack -> bindTrack(item)
            is DisplayableAlbum -> bindAlbum(item)
            is DisplayableHeader -> bindHeader(item)
            is DisplayableNestedListPlaceholder -> {}
        }.exhaustive
    }

    private fun LayoutContainerViewHolder.bindTrack(item: DisplayableTrack){
        ImageLoader.loadSongImage(imageView!!, item.mediaId)
        firstText.text = item.title
        secondText.text = item.subtitle
        duration?.let {
            val durationString = item.idInPlaylist.toString() + "m"
            it.text = durationString
        }
        explicit?.onItemChanged(item.title)
    }

    private fun LayoutContainerViewHolder.bindAlbum(item: DisplayableAlbum){
        ImageLoader.loadAlbumImage(imageView!!, item.mediaId)
        quickAction?.setId(item.mediaId)
        firstText.text = item.title
        secondText?.text = item.subtitle
        explicit?.isVisible = false
    }

    private fun LayoutContainerViewHolder.bindHeader(item: DisplayableHeader){
        if (itemViewType == R.layout.item_tab_header){
            title.text = item.title
        }
    }

}
