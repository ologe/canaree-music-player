package dev.olog.presentation.fragment_detail

import android.arch.lifecycle.Lifecycle
import android.databinding.ViewDataBinding
import android.support.v7.widget.GridLayoutManager
import android.support.v7.widget.LinearSnapHelper
import android.support.v7.widget.RecyclerView
import android.view.View
import android.widget.TextView
import com.jakewharton.rxbinding2.view.RxView
import dev.olog.presentation.BR
import dev.olog.presentation.R
import dev.olog.presentation._base.BaseMapAdapter
import dev.olog.presentation._base.DataBoundViewHolder
import dev.olog.presentation.dagger.FragmentLifecycle
import dev.olog.presentation.model.DisplayableItem
import dev.olog.presentation.music_service.MusicController
import dev.olog.presentation.navigation.Navigator
import dev.olog.shared.MediaIdHelper
import io.reactivex.BackpressureStrategy
import io.reactivex.android.schedulers.AndroidSchedulers
import javax.inject.Inject

class DetailAdapter @Inject constructor(
        @FragmentLifecycle lifecycle: Lifecycle,
        enums: Array<DetailDataType>,
        private val view: DetailFragmentView,
        private val mediaId: String,
        private val listPosition: Int,
        private val recentSongsAdapter: DetailRecentlyAddedAdapter,
        private val mostPlayedAdapter: DetailMostPlayedAdapter,
        private val navigator: Navigator,
        private val musicController: MusicController,
        private val viewModel: DetailFragmentViewModel,
        private val recyclerViewPool : RecyclerView.RecycledViewPool

) : BaseMapAdapter<DetailDataType, DisplayableItem>(lifecycle, enums) {

    private val source = MediaIdHelper.mapCategoryToSource(mediaId)

    override fun initViewHolderListeners(viewHolder: DataBoundViewHolder<*>, viewType: Int){
        when (viewType) {
            R.layout.item_most_played_horizontal_list -> {
                val list = viewHolder.itemView as RecyclerView
                val layoutManager = GridLayoutManager(viewHolder.itemView.context,
                        5, GridLayoutManager.HORIZONTAL, false)
                layoutManager.isItemPrefetchEnabled = true
                layoutManager.initialPrefetchItemCount = 10
                list.layoutManager = layoutManager
                list.adapter = mostPlayedAdapter
                list.recycledViewPool = recyclerViewPool

                val snapHelper = LinearSnapHelper()
                snapHelper.attachToRecyclerView(list)
            }
            R.layout.item_recent_horizontal_list -> {
                val list = viewHolder.itemView as RecyclerView
                val layoutManager = GridLayoutManager(viewHolder.itemView.context,
                        5, GridLayoutManager.HORIZONTAL, false)
                layoutManager.isItemPrefetchEnabled = true
                layoutManager.initialPrefetchItemCount = 10
                list.layoutManager = layoutManager
                list.adapter = recentSongsAdapter
                list.recycledViewPool = recyclerViewPool

                val snapHelper = LinearSnapHelper()
                snapHelper.attachToRecyclerView(list)
            }

            R.layout.item_detail_song -> {
                viewHolder.itemView.setOnClickListener {
                    val position = viewHolder.adapterPosition
                    if (position != RecyclerView.NO_POSITION){
                        val item = dataController[position]
                        musicController.playFromMediaId(item.mediaId)
                        viewModel.addToMostPlayed(item.mediaId)
                                .subscribe()
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

            R.layout.item_detail_album,
            R.layout.item_detail_album_mini -> {
                viewHolder.itemView.setOnClickListener {
                    val position = viewHolder.adapterPosition
                    if (position != RecyclerView.NO_POSITION){
                        val item = dataController[position]
                        navigator.toDetailActivity(item.mediaId, position)
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

            }
            R.layout.item_detail_related_artist -> {
                viewHolder.itemView.setOnClickListener {
                    navigator.toRelatedArtists(mediaId)
                }
            }
            R.layout.item_shuffle_with_divider -> {
                viewHolder.itemView.setOnClickListener { musicController.playShuffle(mediaId) }
            }
            R.layout.item_header -> {
                val seeAll = viewHolder.itemView.findViewById<TextView>(R.id.seeAll)
                seeAll.setOnClickListener {
                    val position = viewHolder.adapterPosition
                    if (position != RecyclerView.NO_POSITION){
                        val item = dataController[position]
                        when (item.mediaId) {
                            DetailHeaders.RECENTLY_ADDED_ID -> navigator.toRecentlyAdded(mediaId)
                            DetailHeaders.ALBUMS_ID -> navigator.toAlbums(mediaId)
                        }
                    }
                }

            }
        }
    }

    override fun onViewAttachedToWindow(holder: DataBoundViewHolder<*>) {
        when (holder.itemViewType) {
            R.layout.item_most_played_horizontal_list -> {
                val list = holder.itemView as RecyclerView
                val layoutManager = list.layoutManager as GridLayoutManager
                mostPlayedAdapter.onDataChanged()
                        .takeUntil(RxView.detaches(holder.itemView).toFlowable(BackpressureStrategy.LATEST))
                        .map { it.size }
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe({ size ->
                            layoutManager.spanCount = if (size < 5) size else 5
                        }, Throwable::printStackTrace)
            }
        }
    }

    override fun bind(binding: ViewDataBinding, item: DisplayableItem, position: Int){
        binding.setVariable(BR.item, item)
        binding.setVariable(BR.source,  source)
        if (position == 0){
            binding.setVariable(BR.position, listPosition)
        } else{
            binding.setVariable(BR.position, position)
        }
    }

    override fun getItemViewType(position: Int): Int = dataController[position].type

    override fun areItemsTheSame(oldItem: DisplayableItem, newItem: DisplayableItem): Boolean {
        return oldItem.mediaId == newItem.mediaId
    }

    override fun afterDataChanged() {
        view.startTransition()
    }

}