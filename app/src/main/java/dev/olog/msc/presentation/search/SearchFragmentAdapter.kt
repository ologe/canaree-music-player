package dev.olog.msc.presentation.search

import android.arch.lifecycle.Lifecycle
import android.databinding.ViewDataBinding
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.LinearSnapHelper
import android.support.v7.widget.RecyclerView
import dev.olog.msc.BR
import dev.olog.msc.R
import dev.olog.msc.dagger.FragmentLifecycle
import dev.olog.msc.presentation.MusicController
import dev.olog.msc.presentation.base.adapter.BaseListAdapter
import dev.olog.msc.presentation.base.adapter.BaseMapAdapter
import dev.olog.msc.presentation.base.adapter.DataBoundViewHolder
import dev.olog.msc.presentation.model.DisplayableItem
import dev.olog.msc.presentation.navigator.Navigator
import dev.olog.msc.utils.k.extension.elevateSongOnTouch
import dev.olog.msc.utils.k.extension.setOnClickListener
import dev.olog.msc.utils.k.extension.setOnLongClickListener
import javax.inject.Inject

class SearchFragmentAdapter @Inject constructor(
        @FragmentLifecycle lifecycle: Lifecycle,
        enums : Array<SearchFragmentType>,
        private val albumAdapter: SearchFragmentAlbumAdapter,
        private val artistAdapter: SearchFragmentArtistAdapter,
        private val recycledViewPool: RecyclerView.RecycledViewPool,
        private val musicController: MusicController,
        private val navigator: Navigator,
        private val viewModel: SearchFragmentViewModel

) : BaseMapAdapter<SearchFragmentType, DisplayableItem>(lifecycle, enums) {

    override fun initViewHolderListeners(viewHolder: DataBoundViewHolder<*>, viewType: Int) {
        when (viewType){
            R.layout.item_search_albums_horizontal_list -> {
                val list = viewHolder.itemView as RecyclerView
                setupHorizontalList(list, albumAdapter)
            }
            R.layout.item_search_artists_horizontal_list -> {
                val list = viewHolder.itemView as RecyclerView
                setupHorizontalList(list, artistAdapter)
            }
            R.layout.item_search_song -> {
                viewHolder.setOnClickListener(dataController) { item, _ ->
                    musicController.playFromMediaId(item.mediaId)
                    viewModel.insertSongToRecent(item.mediaId)
                            .subscribe({}, Throwable::printStackTrace)

                }
                viewHolder.setOnLongClickListener(dataController) { item ,_ ->
                    navigator.toDialog(item, viewHolder.itemView)
                }
                viewHolder.setOnClickListener(R.id.more, dataController) { item, _, view ->
                    navigator.toDialog(item, view)
                }

            }
            R.layout.item_search_clear_recent -> {
                viewHolder.setOnClickListener(dataController) { _, _ ->
                    viewModel.clearRecentSearches()
                            .subscribe({}, Throwable::printStackTrace)
                }
            }
            R.layout.item_search_recent -> {
                viewHolder.setOnClickListener(dataController) { item, _  ->
                    if (item.isPlayable){
                        musicController.playFromMediaId(item.mediaId)
                    } else {
                        navigator.toDetailFragment(item.mediaId)
                    }
                }
                viewHolder.setOnLongClickListener(dataController) { item ,_ ->
                    navigator.toDialog(item, viewHolder.itemView)
                }
                viewHolder.setOnClickListener(R.id.clear, dataController) { item, _, _ ->
                    viewModel.deleteFromRecent(item.mediaId)
                            .subscribe({}, Throwable::printStackTrace)
                }
            }
        }
        when (viewType){
            R.layout.item_search_song,
            R.layout.item_search_recent -> viewHolder.elevateSongOnTouch()
        }
    }

    private fun setupHorizontalList(list: RecyclerView, adapter: BaseListAdapter<*>){
        val layoutManager = LinearLayoutManager(list.context,
                LinearLayoutManager.HORIZONTAL, false)
        list.layoutManager = layoutManager
        list.adapter = adapter
        list.recycledViewPool = recycledViewPool

        val snapHelper = LinearSnapHelper()
        snapHelper.attachToRecyclerView(list)
    }

    override fun bind(binding: ViewDataBinding, item: DisplayableItem, position: Int) {
        binding.setVariable(BR.item, item)
    }

}