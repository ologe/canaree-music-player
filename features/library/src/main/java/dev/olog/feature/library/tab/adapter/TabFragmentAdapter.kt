package dev.olog.feature.library.tab.adapter

import android.view.View
import androidx.core.view.isVisible
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import dev.olog.core.MediaId
import dev.olog.feature.base.adapter.*
import dev.olog.feature.library.R
import dev.olog.lib.media.MediaProvider
import dev.olog.lib.image.provider.ImageLoader
import dev.olog.feature.library.tab.TabFragmentViewModel
import dev.olog.feature.library.tab.model.TabFragmentModel
import dev.olog.navigation.Navigator
import dev.olog.shared.exhaustive
import kotlinx.android.synthetic.main.item_tab_album.*
import kotlinx.android.synthetic.main.item_tab_header.*
import kotlinx.android.synthetic.main.item_tab_podcast.*
import kotlinx.android.synthetic.main.item_tab_track.*
import kotlinx.android.synthetic.main.item_tab_track.cover
import kotlinx.android.synthetic.main.item_tab_track.firstText
import kotlinx.android.synthetic.main.item_tab_track.secondText

internal class TabFragmentAdapter(
    private val navigator: Navigator,
    private val mediaProvider: MediaProvider,
    private val viewModel: TabFragmentViewModel,
    private val setupNestedList: SetupNestedList

) : ObservableAdapter<TabFragmentModel>(TabFragmentModelDiff) {

    override fun getItemViewType(position: Int): Int = getItem(position).layoutType

    override fun initViewHolderListeners(viewHolder: LayoutContainerViewHolder, viewType: Int) {
        when (viewType) {
            R.layout.item_tab_shuffle -> {
                viewHolder.setOnClickListener(this) { _, _, _ ->
                    mediaProvider.shuffle(MediaId.shuffleId(), null)
                }
            }
            R.layout.item_tab_track,
            R.layout.item_tab_podcast -> {
                viewHolder.setOnClickListener(this) { item, _, _ ->
                    onItemClick(item)

                }
                viewHolder.setOnLongClickListener(this) { item, _, _ ->
                    onItemLongClick(item, viewHolder.itemView)
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
                    onItemLongClick(item, viewHolder.itemView)
                }
                viewHolder.elevateAlbumOnTouch()
            }
            R.layout.item_tab_last_played_album_list,
            R.layout.item_tab_last_played_artist_list,
            R.layout.item_tab_recently_added_album_list,
            R.layout.item_tab_recently_added_artist_list -> {
                val view = viewHolder.itemView as RecyclerView
                setupNestedList.setupNestedList(viewType, view)
            }
        }
    }

    private fun onItemClick(item: TabFragmentModel) = when (item) {
        is TabFragmentModel.Track -> {
            val sort = viewModel.getAllTracksSortOrder()
            mediaProvider.playFromMediaId(item.mediaId, null, sort)
        }
        is TabFragmentModel.Podcast -> mediaProvider.playFromMediaId(item.mediaId, null, null)
        is TabFragmentModel.Album -> navigator.toDetailFragment(item.mediaId)
        else -> error("invalid item=$item")
    }

    private fun onItemLongClick(item: TabFragmentModel, view: View) {
        val mediaId = when (item) {
            is TabFragmentModel.Track -> item.mediaId
            is TabFragmentModel.Podcast -> item.mediaId
            is TabFragmentModel.Album -> item.mediaId
            else -> null
        } ?: return
        navigator.toDialog(mediaId, view)
    }

    override fun bind(
        holder: LayoutContainerViewHolder,
        item: TabFragmentModel,
        position: Int
    ) = holder.bindView {
        when (item){
            is TabFragmentModel.Album -> bindAlbum(item)
            is TabFragmentModel.Track -> bindTrack(item)
            is TabFragmentModel.Podcast -> bindPodcast(item)
            is TabFragmentModel.Header -> bindHeader(item)
            is TabFragmentModel.Shuffle,
            is TabFragmentModel.RecentlyPlayedAlbumsList,
            is TabFragmentModel.RecentlyPlayedArtistList,
            is TabFragmentModel.RecentlyAddedAlbumsList,
            is TabFragmentModel.RecentlyAddedArtistList -> {}
        }.exhaustive
    }

    private fun LayoutContainerViewHolder.bindTrack(item: TabFragmentModel.Track){
        ImageLoader.loadSongImage(cover, item.mediaId)
        firstText.text = item.title
        secondText.text = item.subtitle
        explicit?.onItemChanged(item.title)
    }

    private fun LayoutContainerViewHolder.bindPodcast(item: TabFragmentModel.Podcast){
        ImageLoader.loadSongImage(imageView!!, item.mediaId)
        firstText.text = item.title
        secondText.text = item.subtitle

        duration.text = item.formattedDuration
    }

    private fun LayoutContainerViewHolder.bindAlbum(item: TabFragmentModel.Album){
        ImageLoader.loadAlbumImage(imageView!!, item.mediaId)
        quickAction?.setId(item.mediaId)
        firstText.text = item.title
        secondText?.text = item.subtitle
        explicit?.isVisible = false
    }

    private fun LayoutContainerViewHolder.bindHeader(item: TabFragmentModel.Header){
        title.text = item.title
    }

}

private object TabFragmentModelDiff : DiffUtil.ItemCallback<TabFragmentModel>() {

    override fun areItemsTheSame(oldItem: TabFragmentModel, newItem: TabFragmentModel): Boolean {
        return oldItem == newItem
    }

    override fun areContentsTheSame(oldItem: TabFragmentModel, newItem: TabFragmentModel): Boolean {
        return oldItem == newItem
    }
}
