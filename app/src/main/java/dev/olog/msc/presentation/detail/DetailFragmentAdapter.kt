package dev.olog.msc.presentation.detail

import android.arch.lifecycle.Lifecycle
import android.content.Context
import android.databinding.ViewDataBinding
import android.support.v7.widget.GridLayoutManager
import android.support.v7.widget.LinearSnapHelper
import android.support.v7.widget.RecyclerView
import android.view.MotionEvent
import android.view.View
import com.jakewharton.rxbinding2.view.RxView
import dev.olog.msc.BR
import dev.olog.msc.R
import dev.olog.msc.constants.PlaylistConstants
import dev.olog.msc.dagger.qualifier.ApplicationContext
import dev.olog.msc.dagger.qualifier.FragmentLifecycle
import dev.olog.msc.dagger.scope.PerFragment
import dev.olog.msc.domain.entity.SortArranging
import dev.olog.msc.domain.entity.SortType
import dev.olog.msc.presentation.base.adapter.BaseListAdapter
import dev.olog.msc.presentation.base.adapter.BaseMapAdapter
import dev.olog.msc.presentation.base.adapter.DataBoundViewHolder
import dev.olog.msc.presentation.base.adapter.TouchCallbackConfig
import dev.olog.msc.presentation.base.music.service.MediaProvider
import dev.olog.msc.presentation.detail.sort.DetailSortDialog
import dev.olog.msc.presentation.model.DisplayableItem
import dev.olog.msc.presentation.navigator.Navigator
import dev.olog.msc.utils.MediaId
import dev.olog.msc.utils.k.extension.elevateAlbumOnTouch
import dev.olog.msc.utils.k.extension.elevateSongOnTouch
import dev.olog.msc.utils.k.extension.setOnClickListener
import dev.olog.msc.utils.k.extension.setOnLongClickListener
import io.reactivex.BackpressureStrategy
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.rxkotlin.Observables
import kotlinx.android.synthetic.main.item_detail_header_all_song.view.*
import javax.inject.Inject

@PerFragment
class DetailFragmentAdapter @Inject constructor(
        @ApplicationContext context: Context,
        @FragmentLifecycle lifecycle: Lifecycle,
        enums: Array<DetailFragmentDataType>,
        private val mediaId: MediaId,
        private val recentSongsAdapter: DetailRecentlyAddedAdapter,
        private val mostPlayedAdapter: DetailMostPlayedAdapter,
        private val navigator: Navigator,
        private val mediaProvider: MediaProvider,
        private val viewModel: DetailFragmentViewModel,
        private val recycledViewPool : RecyclerView.RecycledViewPool

) : BaseMapAdapter<DetailFragmentDataType, DisplayableItem>(lifecycle, enums, context) {

    override fun initViewHolderListeners(viewHolder: DataBoundViewHolder<*>, viewType: Int){
        when (viewType) {
            R.layout.item_detail_item_info -> {
                viewHolder.setOnClickListener(R.id.more, dataController) { item ,_, view ->
                    navigator.toDialog(item, view)
                }
                if (mediaId.isAlbum){
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
                    viewModel.getDetailSortDataUseCase.execute(item.mediaId)
                            .subscribe({
                                mediaProvider.playFromMediaId(item.mediaId, it)
                            }, Throwable::printStackTrace)
                }
                viewHolder.setOnLongClickListener(dataController) { item, _ ->
                    navigator.toDialog(item, viewHolder.itemView)
                }
                viewHolder.setOnClickListener(R.id.more, dataController) { item, _, view ->
                    navigator.toDialog(item, view)
                }
                viewHolder.itemView.findViewById<View>(R.id.dragHandle)?.setOnTouchListener { _, event ->
                    if(event.actionMasked == MotionEvent.ACTION_DOWN) {
                        touchHelper()?.startDrag(viewHolder)
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
                viewHolder.setOnClickListener(R.id.clickableZone, dataController) { _, _, _ ->
                    navigator.toRelatedArtists(mediaId)
                }
            }
            R.layout.item_detail_shuffle -> {
                viewHolder.setOnClickListener(dataController) { _, _ ->
                    mediaProvider.shuffle(mediaId)
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
                    DetailSortDialog().show(view.context, view, mediaId, viewModel.observeSortOrder().firstOrError()) { sortType ->
                        viewModel.updateSortType(sortType).subscribe()
                    }
                }
                viewHolder.setOnClickListener(R.id.sortImage, dataController) { _, _, _ ->
                    viewModel.observeSortOrder()
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

                Observables.combineLatest(
                        viewModel.observeSortOrder(),
                        viewModel.getSortArranging(), { sort, arranging ->
                    Pair(sort, arranging)

                }).takeUntil(RxView.detaches(holder.itemView))
                        .observeOn(AndroidSchedulers.mainThread())
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

    private val hasDragBehavior = mediaId.isPlaylist &&
            !PlaylistConstants.isAutoPlaylist(mediaId.categoryValue.toLong())

    private val canSwipe = mediaId.isPlaylist &&
            !PlaylistConstants.isAutoPlaylist(mediaId.categoryValue.toLong())

    override val touchCallbackConfig: TouchCallbackConfig = if (hasDragBehavior) TouchCallbackConfig(
            true, canSwipe,
            draggableViewType = R.layout.item_detail_song_with_drag_handle,
            onDragAction = { from, to -> viewModel.moveItemInPlaylist(from, to) },
            onSwipeAction = { position ->
                viewModel.removeFromPlaylist(dataController[position].trackNumber.toLong())
                    .subscribe({}, Throwable::printStackTrace)
            }
    ) else super.touchCallbackConfig
}