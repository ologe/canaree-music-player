package dev.olog.msc.presentation.search

import android.arch.lifecycle.Lifecycle
import android.databinding.ViewDataBinding
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.LinearSnapHelper
import android.support.v7.widget.RecyclerView
import dev.olog.msc.BR
import dev.olog.msc.R
import dev.olog.msc.dagger.qualifier.FragmentLifecycle
import dev.olog.msc.presentation.base.adapter.AbsAdapter
import dev.olog.msc.presentation.base.adapter.DataBoundViewHolder
import dev.olog.msc.presentation.base.music.service.MediaProvider
import dev.olog.msc.presentation.model.DisplayableItem
import dev.olog.msc.presentation.navigator.Navigator
import dev.olog.msc.utils.k.extension.elevateSongOnTouch
import dev.olog.msc.utils.k.extension.setOnClickListener
import dev.olog.msc.utils.k.extension.setOnLongClickListener
import javax.inject.Inject

class SearchFragmentAdapter @Inject constructor(
        @FragmentLifecycle lifecycle: Lifecycle,
        private val albumAdapter: SearchFragmentAlbumAdapter,
        private val artistAdapter: SearchFragmentArtistAdapter,
        private val folderAdapter: SearchFragmentFolderAdapter,
        private val playlistAdapter: SearchFragmentPlaylistAdapter,
        private val genreAdapter: SearchFragmentGenreAdapter,
        private val recycledViewPool: RecyclerView.RecycledViewPool,
        private val mediaProvider: MediaProvider,
        private val navigator: Navigator,
        private val viewModel: SearchFragmentViewModel

) : AbsAdapter<DisplayableItem>(lifecycle) {

    override fun initViewHolderListeners(viewHolder: DataBoundViewHolder, viewType: Int) {
        when (viewType){
            R.layout.item_search_albums_horizontal_list -> {
                val list = viewHolder.itemView as RecyclerView
                setupHorizontalList(list, albumAdapter)
            }
            R.layout.item_search_artists_horizontal_list -> {
                val list = viewHolder.itemView as RecyclerView
                setupHorizontalList(list, artistAdapter)
            }
            R.layout.item_search_folder_horizontal_list -> {
                val list = viewHolder.itemView as RecyclerView
                setupHorizontalList(list, folderAdapter)
            }
            R.layout.item_search_playlists_horizontal_list -> {
                val list = viewHolder.itemView as RecyclerView
                setupHorizontalList(list, playlistAdapter)
            }
            R.layout.item_search_genre_horizontal_list -> {
                val list = viewHolder.itemView as RecyclerView
                setupHorizontalList(list, genreAdapter)
            }
            R.layout.item_search_song -> {
                viewHolder.setOnClickListener(controller) { item, _, _ ->
                    mediaProvider.playFromMediaId(item.mediaId)
                    viewModel.insertToRecent(item.mediaId)

                }
                viewHolder.setOnLongClickListener(controller) { item ,_, _ ->
                    navigator.toDialog(item, viewHolder.itemView)
                }
                viewHolder.setOnClickListener(R.id.more, controller) { item, _, view ->
                    navigator.toDialog(item, view)
                }

            }
            R.layout.item_search_clear_recent -> {
                viewHolder.setOnClickListener(controller) { _, _, _ ->
                    viewModel.clearRecentSearches()
                }
            }
            R.layout.item_search_recent,
            R.layout.item_search_recent_album,
            R.layout.item_search_recent_artist -> {
                viewHolder.setOnClickListener(controller) { item, _, _  ->
                    if (item.isPlayable){
                        mediaProvider.playFromMediaId(item.mediaId)
                    } else {
                        navigator.toDetailFragment(item.mediaId)
                    }
                    viewModel.insertToRecent(item.mediaId)
                }
                viewHolder.setOnLongClickListener(controller) { item ,_, _ ->
                    navigator.toDialog(item, viewHolder.itemView)
                }
                viewHolder.setOnClickListener(R.id.clear, controller) { item, _, _ ->
                    viewModel.deleteFromRecent(item.mediaId)
                }
            }
        }
        when (viewType){
            R.layout.item_search_song,
            R.layout.item_search_recent,
            R.layout.item_search_recent_album,
            R.layout.item_search_recent_artist -> viewHolder.elevateSongOnTouch()
        }
    }

    private fun setupHorizontalList(list: RecyclerView, adapter: AbsAdapter<*>){
        val layoutManager = LinearLayoutManager(list.context,
                LinearLayoutManager.HORIZONTAL, false)
        list.layoutManager = layoutManager
        list.adapter = adapter
        list.setRecycledViewPool(recycledViewPool)
        list.setHasFixedSize(true)

        val snapHelper = LinearSnapHelper()
        snapHelper.attachToRecyclerView(list)
    }

    override fun bind(binding: ViewDataBinding, item: DisplayableItem, position: Int) {
        binding.setVariable(BR.item, item)
    }

    override fun canInteractWithViewHolder(viewType: Int): Boolean? {
        return viewType == R.layout.item_search_song ||
                viewType == R.layout.item_search_recent
    }

    override val onSwipeLeftAction = { position: Int ->
        val item = controller.getItem(position)
        mediaProvider.addToPlayNext(item.mediaId)
    }

}