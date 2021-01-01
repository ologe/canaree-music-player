package dev.olog.feature.search.adapter

import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import dev.olog.domain.mediaid.MediaId
import dev.olog.feature.base.adapter.*
import dev.olog.feature.base.adapter.drag.TouchableAdapter
import dev.olog.feature.search.R
import dev.olog.feature.search.SearchFragmentViewModel
import dev.olog.feature.search.model.SearchFragmentModel
import dev.olog.lib.image.provider.ImageLoader
import dev.olog.lib.media.MediaProvider
import dev.olog.navigation.Navigator
import dev.olog.shared.exhaustive
import kotlinx.android.synthetic.main.item_search_header.*
import kotlinx.android.synthetic.main.item_search_recent.*

internal class SearchFragmentAdapter(
    private val setupNestedList: SetupNestedList,
    private val mediaProvider: MediaProvider,
    private val navigator: Navigator,
    private val viewModel: SearchFragmentViewModel
) : ObservableAdapter<SearchFragmentModel>(SearchFragmentModelDiff),
    TouchableAdapter {

    override fun getItemViewType(position: Int): Int = getItem(position).layoutType

    override fun initViewHolderListeners(viewHolder: LayoutContainerViewHolder, viewType: Int) {
        when (viewType) {
            R.layout.item_search_list_albums,
            R.layout.item_search_list_artists,
            R.layout.item_search_list_folders,
            R.layout.item_search_list_playlists,
            R.layout.item_search_list_genres -> {
                val list = viewHolder.itemView as RecyclerView
                setupNestedList.setupNestedList(viewType, list)
            }
            R.layout.item_search_song -> {
                viewHolder.setOnClickListener(this) { item, _, _ ->
                    require(item is SearchFragmentModel.Track)
                    mediaProvider.playFromMediaId(item.mediaId, null, null)
                    viewModel.insertToRecent(item.mediaId)

                }
                viewHolder.setOnLongClickListener(this) { item, _, _ ->
                    require(item is SearchFragmentModel.Track)
                    navigator.toDialog(item.mediaId, viewHolder.itemView)
                }
                viewHolder.setOnClickListener(R.id.more, this) { item, _, view ->
                    require(item is SearchFragmentModel.Track)
                    navigator.toDialog(item.mediaId, view)
                }

            }
            R.layout.item_search_clear_recent -> {
                viewHolder.setOnClickListener(this) { _, _, _ ->
                    viewModel.clearRecentSearches()
                }
            }
            R.layout.item_search_recent -> {
                viewHolder.setOnClickListener(this) { item, _, _ ->
                    require(item is SearchFragmentModel.RecentTrack)
                    mediaProvider.playFromMediaId(item.mediaId, null, null)
                }
                viewHolder.setOnLongClickListener(this) { item, _, _ ->
                    require(item is SearchFragmentModel.RecentTrack)
                    navigator.toDialog(item.mediaId, viewHolder.itemView)
                }
            }
            R.layout.item_search_recent_album,
            R.layout.item_search_recent_artist -> {
                viewHolder.setOnClickListener(this) { item, _, _ ->
                    require(item is SearchFragmentModel.RecentAlbum)
                    navigator.toDetailFragment(item.mediaId)
                }
                viewHolder.setOnLongClickListener(this) { item, _, _ ->
                    require(item is SearchFragmentModel.RecentAlbum)
                    navigator.toDialog(item.mediaId, viewHolder.itemView)
                }
                viewHolder.setOnClickListener(R.id.clear, this) { item, _, _ ->
                    require(item is SearchFragmentModel.RecentAlbum)
                    viewModel.deleteFromRecent(item.mediaId)
                }
            }
        }
        when (viewType) {
            R.layout.item_search_song,
            R.layout.item_search_recent,
            R.layout.item_search_recent_album,
            R.layout.item_search_recent_artist -> viewHolder.elevateSongOnTouch()
        }
    }

    override fun bind(
        holder: LayoutContainerViewHolder,
        item: SearchFragmentModel,
        position: Int
    ) = holder.bindView {
        when (item) {
            is SearchFragmentModel.Track -> bindTrack(
                mediaId = item.mediaId,
                title = item.title,
                subtitle = item.subtitle
            )
            is SearchFragmentModel.Album -> bindAlbum(
                mediaId = item.mediaId,
                title = item.title,
                subtitle = item.subtitle
            )
            is SearchFragmentModel.Header -> bindHeader(
                title = item.title,
                subtitle = item.subtitle
            )
            is SearchFragmentModel.RecentTrack -> bindTrack(
                mediaId = item.mediaId,
                title = item.title,
                subtitle = item.subtitle
            )
            is SearchFragmentModel.RecentAlbum -> bindAlbum(
                mediaId = item.mediaId,
                title = item.title,
                subtitle = item.subtitle
            )
            is SearchFragmentModel.RecentHeader,
            is SearchFragmentModel.ClearRecent,
            is SearchFragmentModel.FoldersList,
            is SearchFragmentModel.AlbumsList,
            is SearchFragmentModel.ArtistsList,
            is SearchFragmentModel.PlaylistList,
            is SearchFragmentModel.GenreList -> {}
        }.exhaustive
    }

    private fun LayoutContainerViewHolder.bindTrack(
        mediaId: MediaId,
        title: String,
        subtitle: String?,
    ) {
        ImageLoader.loadSongImage(cover, mediaId)
        firstText.text = title
        secondText.text = subtitle
        explicit.onItemChanged(title)
    }

    private fun LayoutContainerViewHolder.bindAlbum(
        mediaId: MediaId,
        title: String,
        subtitle: String?,
    ) {
        ImageLoader.loadAlbumImage(cover, mediaId)
        firstText.text = title
        secondText.text = subtitle
    }

    private fun LayoutContainerViewHolder.bindHeader(
        title: String,
        subtitle: String?
    ) {
        this.title.text = title
        this.subtitle.text = subtitle
    }

    override fun canInteractWithViewHolder(viewType: Int): Boolean {
        return viewType == R.layout.item_search_song ||
                viewType == R.layout.item_search_recent
    }

    override fun onSwipedLeft(viewHolder: RecyclerView.ViewHolder) {
        val position = viewHolder.adapterPosition
        val item = getItem(position) as? SearchFragmentModel.Track ?: return
        mediaProvider.addToPlayNext(item.mediaId)
    }

    override fun afterSwipeLeft(viewHolder: RecyclerView.ViewHolder) {
        notifyItemChanged(viewHolder.adapterPosition)
    }

}

private object SearchFragmentModelDiff : DiffUtil.ItemCallback<SearchFragmentModel>() {
    override fun areItemsTheSame(
        oldItem: SearchFragmentModel,
        newItem: SearchFragmentModel
    ): Boolean {
        return oldItem == newItem
    }

    override fun areContentsTheSame(
        oldItem: SearchFragmentModel,
        newItem: SearchFragmentModel
    ): Boolean {
        return oldItem == newItem
    }
}