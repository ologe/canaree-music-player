package dev.olog.presentation.fragment_detail

import android.arch.lifecycle.Lifecycle
import android.databinding.ViewDataBinding
import android.support.v7.widget.GridLayoutManager
import android.support.v7.widget.LinearSnapHelper
import android.support.v7.widget.RecyclerView
import android.view.MotionEvent
import android.view.View
import com.android.databinding.library.baseAdapters.BR
import com.jakewharton.rxbinding2.view.RxView
import dev.olog.presentation.R
import dev.olog.presentation._base.BaseListAdapter
import dev.olog.presentation._base.BaseMapAdapterDraggable
import dev.olog.presentation._base.DataBoundViewHolder
import dev.olog.presentation.dagger.FragmentLifecycle
import dev.olog.presentation.model.DisplayableItem
import dev.olog.presentation.navigation.Navigator
import dev.olog.presentation.service_music.MusicController
import dev.olog.presentation.utils.extension.setOnClickListener
import dev.olog.presentation.utils.extension.setOnLongClickListener
import dev.olog.shared.MediaIdHelper
import io.reactivex.BackpressureStrategy
import io.reactivex.android.schedulers.AndroidSchedulers
import javax.inject.Inject

class DetailFragmentAdapter @Inject constructor(
        @FragmentLifecycle lifecycle: Lifecycle,
        enums: Array<DetailFragmentDataType>,
        private val view: DetailFragmentView,
        private val mediaId: String,
        private val recentSongsAdapter: DetailFragmentRecentlyAddedAdapter,
        private val mostPlayedAdapter: DetailFragmentMostPlayedAdapter,
        private val navigator: Navigator,
        private val musicController: MusicController,
        private val viewModel: DetailFragmentViewModel,
        private val recycledViewPool : RecyclerView.RecycledViewPool

) : BaseMapAdapterDraggable<DetailFragmentDataType, DisplayableItem>(lifecycle, enums) {

    override fun initViewHolderListeners(viewHolder: DataBoundViewHolder<*>, viewType: Int){
        when (viewType) {
            R.layout.item_detail_info_image -> {
                viewHolder.setOnClickListener(R.id.more, dataController) { item ,_, view ->
                    navigator.toDialog(item, view)
                }
                if (MediaIdHelper.extractCategory(mediaId) == MediaIdHelper.MEDIA_ID_BY_ALBUM){
                    viewHolder.setOnClickListener(R.id.clickableArtist, dataController) { item, _, _ ->
                        viewModel.artistMediaId(item.mediaId)
                                .subscribe({ artistMediaId ->
                                    navigator.toDetailFragment(artistMediaId)
                                }, Throwable::printStackTrace)

                    }
                }
            }

            R.layout.item_detail_most_played_horizontal_list -> {
                val list = viewHolder.itemView as RecyclerView
                setupHorizontalList(list, mostPlayedAdapter)
            }
            R.layout.item_detail_recent_horizontal_list -> {
                val list = viewHolder.itemView as RecyclerView
                setupHorizontalList(list, recentSongsAdapter)
            }

            R.layout.item_detail_song,
            R.layout.item_detail_song_with_track,
            R.layout.item_detail_song_with_drag_handle -> {
                viewHolder.setOnClickListener(dataController) { item, _ ->
                    musicController.playFromMediaId(item.mediaId)
                }
                viewHolder.setOnLongClickListener(dataController) { item, _ ->
                    navigator.toDialog(item, viewHolder.itemView)
                }
                viewHolder.setOnClickListener(R.id.more, dataController) { item, _, view ->
                    navigator.toDialog(item, view)
                }
                viewHolder.itemView.findViewById<View>(R.id.dragHandle)?.setOnTouchListener { _, event ->
                    if(event.actionMasked == MotionEvent.ACTION_DOWN) {
                        touchHelper?.startDrag(viewHolder)
                        true
                    } else false
                }
            }
            R.layout.item_detail_album,
            R.layout.item_detail_album_mini -> {
                viewHolder.setOnClickListener(dataController) { item, _ ->
                    navigator.toDetailFragment(item.mediaId)
                }
                viewHolder.setOnLongClickListener(dataController) { item, _ ->
                    navigator.toDialog(item, viewHolder.itemView)
                }

            }
            R.layout.item_detail_related_artist -> {
                viewHolder.setOnClickListener(dataController) { _, _ ->
                    navigator.toRelatedArtists(mediaId)
                }
            }
            R.layout.item_detail_shuffle -> {
                viewHolder.setOnClickListener(dataController) { _, _ ->
                    musicController.playShuffle(mediaId)
                }
            }
            R.layout.item_detail_header -> {
                viewHolder.setOnClickListener(R.id.seeAll, dataController) { item, _, _ ->
                    when (item.mediaId) {
                        DetailFragmentHeaders.RECENTLY_ADDED_ID -> navigator.toRecentlyAdded(mediaId)
                        DetailFragmentHeaders.ALBUMS_ID -> navigator.toAlbums(mediaId)
                    }
                }
            }
        }
    }

    private fun setupHorizontalList(list: RecyclerView, adapter: BaseListAdapter<*>){
        val layoutManager = GridLayoutManager(list.context,
                5, GridLayoutManager.HORIZONTAL, false)
        layoutManager.isItemPrefetchEnabled = true
        layoutManager.initialPrefetchItemCount = 10
        list.layoutManager = layoutManager
        list.adapter = adapter
        list.recycledViewPool = recycledViewPool

        val snapHelper = LinearSnapHelper()
        snapHelper.attachToRecyclerView(list)
    }

    override fun onViewAttachedToWindow(holder: DataBoundViewHolder<*>) {
        when (holder.itemViewType) {
            R.layout.item_detail_most_played_horizontal_list -> {
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
    }

    override fun getItemViewType(position: Int): Int = dataController[position].type

    override fun areItemsTheSame(oldItem: DisplayableItem, newItem: DisplayableItem): Boolean {
        return oldItem.mediaId == newItem.mediaId
    }

    override fun afterDataChanged() {
        view.startTransition()
    }

    override fun isViewTypeDraggable(): Int = R.layout.item_detail_song_with_drag_handle

    override fun isSwipeEnabled(): Boolean = false
}