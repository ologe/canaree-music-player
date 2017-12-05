package dev.olog.presentation.fragment_search

import android.arch.lifecycle.Lifecycle
import android.databinding.ViewDataBinding
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.LinearSnapHelper
import android.support.v7.widget.RecyclerView
import android.view.View
import dev.olog.presentation.BR
import dev.olog.presentation.R
import dev.olog.presentation._base.BaseMapAdapter
import dev.olog.presentation._base.DataBoundViewHolder
import dev.olog.presentation.activity_main.TabViewPagerAdapter
import dev.olog.presentation.dagger.FragmentLifecycle
import dev.olog.presentation.model.DisplayableItem
import dev.olog.presentation.navigation.Navigator
import dev.olog.presentation.service_music.MusicController
import javax.inject.Inject

class SearchFragmentAdapter @Inject constructor(
        @FragmentLifecycle lifecycle: Lifecycle,
        enums : Array<SearchType>,
        private val albumAdapter: SearchFragmentAlbumAdapter,
        private val artistAdapter: SearchFragmentArtistAdapter,
        private val recycledViewPool: RecyclerView.RecycledViewPool,
        private val musicController: MusicController,
        private val navigator: Navigator

) : BaseMapAdapter<SearchType, DisplayableItem>(lifecycle, enums) {

    override fun initViewHolderListeners(viewHolder: DataBoundViewHolder<*>, viewType: Int) {
        when (viewType){
            R.layout.item_search_albums_horizontal_list -> {
                val list = viewHolder.itemView as RecyclerView
                val layoutManager = LinearLayoutManager(viewHolder.itemView.context,
                        LinearLayoutManager.HORIZONTAL, false)
                layoutManager.isItemPrefetchEnabled = true
                list.layoutManager = layoutManager
                list.adapter = albumAdapter
                list.recycledViewPool = recycledViewPool

                val snapHelper = LinearSnapHelper()
                snapHelper.attachToRecyclerView(list)
            }
            R.layout.item_search_artists_horizontal_list -> {
                val list = viewHolder.itemView as RecyclerView
                val layoutManager = LinearLayoutManager(viewHolder.itemView.context,
                        LinearLayoutManager.HORIZONTAL, false)
                layoutManager.isItemPrefetchEnabled = true
                list.layoutManager = layoutManager
                list.adapter = artistAdapter
                list.recycledViewPool = recycledViewPool

                val snapHelper = LinearSnapHelper()
                snapHelper.attachToRecyclerView(list)
            }
            R.layout.item_tab_song -> {
                viewHolder.itemView.setOnClickListener {
                    val position = viewHolder.adapterPosition
                    if (position != RecyclerView.NO_POSITION){
                        val item = dataController[position]
                        musicController.playFromMediaId(item.mediaId)
                    }
                }
                viewHolder.itemView.setOnLongClickListener {
                    val position = viewHolder.adapterPosition
                    if (position != RecyclerView.NO_POSITION){
                        val item = dataController[position]
                        navigator.toDialog(item, viewHolder.itemView)
                    }
                    true
                }
                viewHolder.itemView.findViewById<View>(R.id.more).setOnClickListener { view ->
                    val position = viewHolder.adapterPosition
                    if (position != RecyclerView.NO_POSITION){
                        val item = dataController[position]
                        navigator.toDialog(item, view)
                    }
                }
            }

        }
    }

    override fun bind(binding: ViewDataBinding, item: DisplayableItem, position: Int) {
        binding.setVariable(BR.item, item)
        binding.setVariable(BR.source,  TabViewPagerAdapter.SONG)
        binding.setVariable(BR.position, position)
    }

    override fun getItemViewType(position: Int): Int = dataController[position].type

    override fun areItemsTheSame(oldItem: DisplayableItem, newItem: DisplayableItem): Boolean {
        return oldItem.mediaId == newItem.mediaId
    }
}