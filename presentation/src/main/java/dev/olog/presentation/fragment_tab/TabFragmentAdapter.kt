package dev.olog.presentation.fragment_tab

import android.arch.lifecycle.Lifecycle
import android.databinding.ViewDataBinding
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import dagger.Lazy
import dev.olog.presentation.BR
import dev.olog.presentation.R
import dev.olog.presentation._base.BaseListAdapter
import dev.olog.presentation._base.DataBoundViewHolder
import dev.olog.presentation.dagger.FragmentLifecycle
import dev.olog.presentation.model.DisplayableItem
import dev.olog.presentation.navigation.Navigator
import dev.olog.presentation.service_music.MusicController
import dev.olog.presentation.utils.extension.setOnClickListener
import dev.olog.presentation.utils.extension.setOnLongClickListener
import dev.olog.shared.MediaIdHelper
import javax.inject.Inject

class TabFragmentAdapter @Inject constructor(
        @FragmentLifecycle lifecycle: Lifecycle,
        private val navigator: Navigator,
        private val source: Int,
        private val musicController: MusicController,
        private val viewModel: TabFragmentViewModel,
        private val lastPlayedArtistsAdapter: Lazy<TabLastPlayedArtistsAdapter>,
        private val lastPlayedAlbumsAdapter: Lazy<TabLastPlayedAlbumsAdapter>

) : BaseListAdapter<DisplayableItem>(lifecycle) {

    override fun initViewHolderListeners(viewHolder: DataBoundViewHolder<*>, viewType: Int) {
        when (viewType) {
            R.layout.item_shuffle -> {
                viewHolder.setOnClickListener(dataController) { _, _ ->
                    musicController.playShuffle(MediaIdHelper.MEDIA_ID_BY_ALL)
                }
            }
            R.layout.item_tab_album, R.layout.item_tab_song -> {
                viewHolder.setOnClickListener(dataController) { item, position ->
                    if (item.isPlayable){
                        musicController.playFromMediaId(item.mediaId)
                    } else {
                        navigator.toDetailFragment(item.mediaId, position)
                        val category = MediaIdHelper.extractCategory(item.mediaId)
                        when (category){
                            MediaIdHelper.MEDIA_ID_BY_ARTIST -> {
                                viewModel.insertArtistLastPlayed(item.mediaId)
                                        .subscribe({}, Throwable::printStackTrace)
                            }
                            MediaIdHelper.MEDIA_ID_BY_ALBUM -> {
                                viewModel.insertAlbumLastPlayed(item.mediaId)
                                        .subscribe({}, Throwable::printStackTrace)
                            }
                        }
                    }
                }
                viewHolder.setOnLongClickListener(dataController) { item, _ ->
                    navigator.toDialog(item, viewHolder.itemView)
                }
                viewHolder.setOnClickListener(R.id.more, dataController) { item, _, view ->
                    navigator.toDialog(item, view)
                }
            }
            R.layout.item_tab_last_played_album_horizontal_list -> {
                val view = viewHolder.itemView as RecyclerView
                setupHorizontalList(view, lastPlayedAlbumsAdapter.get())
            }
            R.layout.item_tab_last_played_artist_horizontal_list -> {
                val view = viewHolder.itemView as RecyclerView
                setupHorizontalList(view, lastPlayedArtistsAdapter.get())
            }
        }
    }

    private fun setupHorizontalList(list: RecyclerView, adapter: BaseListAdapter<*>){
        val layoutManager = LinearLayoutManager(list.context, LinearLayoutManager.HORIZONTAL, false)
        list.layoutManager = layoutManager
        list.adapter = adapter

//        val snapHelper = LinearSnapHelper()
//        snapHelper.attachToRecyclerView(list)
    }

    override fun bind(binding: ViewDataBinding, item: DisplayableItem, position: Int) {
        binding.setVariable(BR.item, item)
        binding.setVariable(BR.source, source)
        binding.setVariable(BR.position, position)
    }

    override fun getItemViewType(position: Int): Int = dataController[position].type

    override fun areItemsTheSame(oldItem: DisplayableItem, newItem: DisplayableItem): Boolean {
        return oldItem.mediaId == newItem.mediaId
    }



}