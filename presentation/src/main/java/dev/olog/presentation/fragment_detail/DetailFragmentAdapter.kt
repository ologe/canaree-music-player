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
import dev.olog.domain.SortArranging
import dev.olog.domain.entity.SortType
import dev.olog.presentation.R
import dev.olog.presentation._base.BaseListAdapter
import dev.olog.presentation._base.BaseMapAdapterDraggable
import dev.olog.presentation._base.DataBoundViewHolder
import dev.olog.presentation.dagger.FragmentLifecycle
import dev.olog.presentation.dialog_sort.DetailSortDialog
import dev.olog.presentation.model.DisplayableItem
import dev.olog.presentation.navigation.Navigator
import dev.olog.presentation.service_music.MusicController
import dev.olog.presentation.utils.extension.elevateAlbumOnTouch
import dev.olog.presentation.utils.extension.elevateSongOnTouch
import dev.olog.presentation.utils.extension.setOnClickListener
import dev.olog.presentation.utils.extension.setOnLongClickListener
import dev.olog.presentation.widgets.fastscroller.FastScrollerSectionIndexer
import dev.olog.shared.MediaIdHelper
import io.reactivex.BackpressureStrategy
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.rxkotlin.Flowables
import kotlinx.android.synthetic.main.item_detail_header_all_song.view.*
import javax.inject.Inject

class DetailFragmentAdapter @Inject constructor(
        @FragmentLifecycle lifecycle: Lifecycle,
        enums: Array<DetailFragmentDataType>,
        private val mediaId: String,
        private val recentSongsAdapter: DetailRecentlyAddedAdapter,
        private val mostPlayedAdapter: DetailMostPlayedAdapter,
        private val navigator: Navigator,
        private val musicController: MusicController,
        private val viewModel: DetailFragmentViewModel,
        private val recycledViewPool : RecyclerView.RecycledViewPool

) : BaseMapAdapterDraggable<DetailFragmentDataType, DisplayableItem>(lifecycle, enums),
        FastScrollerSectionIndexer {

    override fun initViewHolderListeners(viewHolder: DataBoundViewHolder<*>, viewType: Int){
        when (viewType) {
            R.layout.item_detail_item_info -> {
                viewHolder.setOnClickListener(R.id.more, dataController) { item ,_, view ->
                    navigator.toDialogDetailItem(item, view)
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

            R.layout.item_detail_most_played_list -> {
                val list = viewHolder.itemView as RecyclerView
                setupHorizontalList(list, mostPlayedAdapter)
            }
            R.layout.item_detail_recently_added_list -> {
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
            R.layout.item_detail_header_all_song -> {
                viewHolder.setOnClickListener(R.id.sort, dataController) { _, _, view ->
                    DetailSortDialog().show(view.context, view, mediaId, viewModel.getSortOrder().firstOrError()) { sortType ->
                        viewModel.updateSortType(sortType).subscribe()
                    }
                }
                viewHolder.setOnClickListener(R.id.sortImage, dataController) { _, _, _ ->
                    viewModel.getSortOrder()
                            .firstOrError()
                            .filter { it != SortType.CUSTOM }
                            .flatMapCompletable { viewModel.toggleSortArranging() }
                            .subscribe()
                }
            }
        }

        when (viewType){
            R.layout.item_detail_album -> viewHolder.elevateAlbumOnTouch()
            R.layout.item_detail_song,
            R.layout.item_detail_song_with_track,
            R.layout.item_detail_song_with_drag_handle,
            R.layout.item_detail_album_mini-> viewHolder.elevateSongOnTouch()
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
            R.layout.item_detail_most_played_list -> {
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
            R.layout.item_detail_recently_added_list -> {
                val list = holder.itemView as RecyclerView
                val layoutManager = list.layoutManager as GridLayoutManager
                recentSongsAdapter.onDataChanged()
                        .takeUntil(RxView.detaches(holder.itemView).toFlowable(BackpressureStrategy.LATEST))
                        .map { it.size }
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe({ size ->
                            layoutManager.spanCount = if (size < 5) size else 5
                        }, Throwable::printStackTrace)
            }
            R.layout.item_detail_header_all_song -> {
                val image = holder.itemView.sortImage

                Flowables.combineLatest(
                        viewModel.getSortOrder(),
                        viewModel.getSortArranging(), { sort, arranging ->
                    Pair(sort, arranging)

                }).takeUntil(RxView.detaches(holder.itemView).toFlowable(BackpressureStrategy.LATEST))
                        .subscribe({ (sort, arranging) ->
                            if (sort == SortType.CUSTOM){
                                image.setImageResource(R.drawable.vd_remove)
                            } else {
                                if (arranging == SortArranging.ASCENDING){
                                    image.setImageResource(R.drawable.vd_arrow_down)
                                } else {
                                    image.setImageResource(R.drawable.vd_arrow_up)
                                }
                            }

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
    }

    override fun isViewTypeDraggable(): Int = R.layout.item_detail_song_with_drag_handle

    override fun isSwipeEnabled(): Boolean = false

    override fun onItemMove(from: Int, to: Int) {
        super.onItemMove(from, to)
        val (list, realFrom) = dataController.getItemPositionWithListWithin(from)
        val (_, realTo) = dataController.getItemPositionWithListWithin(to)
        val headersCount = list.indexOfFirst { it.type == isViewTypeDraggable() }
        viewModel.moveItemInPlaylist(realFrom - headersCount, realTo - headersCount)
    }

    override fun getSectionText(position: Int): String? {
        val item = dataController[position]
        val itemType = item.type
        if (itemType == R.layout.item_detail_song ||
                itemType == R.layout.item_detail_song_with_drag_handle ||
                itemType == R.layout.item_detail_song_with_track) {
            return item.title[0].toString().toUpperCase()
        } else {
            return null
        }
    }
}