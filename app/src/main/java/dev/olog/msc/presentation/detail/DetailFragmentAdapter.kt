package dev.olog.msc.presentation.detail

import android.arch.lifecycle.Lifecycle
import android.databinding.ViewDataBinding
import android.support.v7.widget.GridLayoutManager
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.LinearSnapHelper
import android.support.v7.widget.RecyclerView
import android.view.MotionEvent
import android.view.View
import com.jakewharton.rxbinding2.view.RxView
import dev.olog.msc.BR
import dev.olog.msc.R
import dev.olog.msc.constants.PlaylistConstants
import dev.olog.msc.dagger.qualifier.FragmentLifecycle
import dev.olog.msc.dagger.scope.PerFragment
import dev.olog.msc.domain.entity.SortArranging
import dev.olog.msc.domain.entity.SortType
import dev.olog.msc.presentation.base.adp.AbsAdapter
import dev.olog.msc.presentation.base.adp.DataBoundViewHolder
import dev.olog.msc.presentation.base.music.service.MediaProvider
import dev.olog.msc.presentation.detail.DetailFragmentViewModel.Companion.NESTED_SPAN_COUNT
import dev.olog.msc.presentation.detail.sort.DetailSortDialog
import dev.olog.msc.presentation.model.DisplayableItem
import dev.olog.msc.presentation.navigator.Navigator
import dev.olog.msc.utils.MediaId
import dev.olog.msc.utils.k.extension.elevateAlbumOnTouch
import dev.olog.msc.utils.k.extension.elevateSongOnTouch
import dev.olog.msc.utils.k.extension.setOnClickListener
import dev.olog.msc.utils.k.extension.setOnLongClickListener
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.rxkotlin.Observables
import kotlinx.android.synthetic.main.item_detail_header_all_song.view.*
import javax.inject.Inject

@PerFragment
class DetailFragmentAdapter @Inject constructor(
        @FragmentLifecycle lifecycle: Lifecycle,
        private val mediaId: MediaId,
        private val recentlyAddedAdapter: DetailRecentlyAddedAdapter,
        private val mostPlayedAdapter: DetailMostPlayedAdapter,
        private val relatedArtistsAdapter: DetailRelatedArtistsAdapter,
        private val navigator: Navigator,
        private val mediaProvider: MediaProvider,
        private val viewModel: DetailFragmentViewModel,
        private val recycledViewPool : RecyclerView.RecycledViewPool

) : AbsAdapter<DisplayableItem>(lifecycle) {

    override fun initViewHolderListeners(viewHolder: DataBoundViewHolder, viewType: Int){
        when (viewType) {
            R.layout.item_detail_item_info -> {
                if (mediaId.isAlbum){
                    viewHolder.setOnClickListener(R.id.clickableArtist, controller) { item, _, _ ->
                        viewModel.artistMediaId()
                                .subscribe({ artistMediaId ->
                                    navigator.toDetailFragment(artistMediaId)
                                }, Throwable::printStackTrace)

                    }
                }
            }

            R.layout.item_detail_most_played_list -> {
                val list = viewHolder.itemView as RecyclerView
                setupHorizontalListAsGrid(list, mostPlayedAdapter)
            }
            R.layout.item_detail_recently_added_list -> {
                val list = viewHolder.itemView as RecyclerView
                setupHorizontalListAsGrid(list, recentlyAddedAdapter)
            }
            R.layout.item_detail_related_artists_list -> {
                val list = viewHolder.itemView as RecyclerView
                setupHorizontalListAsList(list, relatedArtistsAdapter)
            }

            R.layout.item_detail_song,
            R.layout.item_detail_song_with_track,
            R.layout.item_detail_song_with_drag_handle -> {
                viewHolder.setOnClickListener(controller) { item, _, _ ->
                    viewModel.getDetailSortDataUseCase.execute(item.mediaId)
                            .subscribe({
                                mediaProvider.playFromMediaId(item.mediaId, it)
                            }, Throwable::printStackTrace)
                }
                viewHolder.setOnLongClickListener(controller) { item, _, _ ->
                    navigator.toDialog(item, viewHolder.itemView)
                }
                viewHolder.setOnClickListener(R.id.more, controller) { item, _, view ->
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
                viewHolder.setOnClickListener(controller) { item, _,_ ->
                    navigator.toDetailFragment(item.mediaId)
                }
                viewHolder.setOnLongClickListener(controller) { item, _, _ ->
                    navigator.toDialog(item, viewHolder.itemView)
                }

            }

            R.layout.item_detail_shuffle -> {
                viewHolder.setOnClickListener(controller) { _, _, _ ->
                    mediaProvider.shuffle(mediaId)
                }
            }

            R.layout.item_detail_see_all -> {
                viewHolder.setOnClickListener(controller) { item, _, _ ->
                    when (item.mediaId){
                        DetailFragmentHeaders.RECENTLY_ADDED_SEE_ALL -> navigator.toRecentlyAdded(mediaId)
                        DetailFragmentHeaders.RELATED_ARTISTS_SEE_ALL -> navigator.toRelatedArtists(mediaId)
                    }
                }
            }

            R.layout.item_detail_header_all_song -> {
                viewHolder.setOnClickListener(R.id.sort, controller) { _, _, view ->
                    viewModel.observeSortOrder().firstOrError()
                            .observeOn(AndroidSchedulers.mainThread())
                            .subscribe({
                                DetailSortDialog().show(view.context, view, mediaId, it) { sortType ->
                                    viewModel.updateSortType(sortType).subscribe({}, Throwable::printStackTrace)
                                }
                            }, Throwable::printStackTrace)
                }
                viewHolder.setOnClickListener(R.id.sortImage, controller) { _, _, _ ->
                    viewModel.observeSortOrder()
                            .firstOrError()
                            .filter { it != SortType.CUSTOM }
                            .flatMapCompletable { viewModel.toggleSortArranging() }
                            .subscribe({}, Throwable::printStackTrace)
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

    private fun setupHorizontalListAsGrid(list: RecyclerView, adapter: AbsAdapter<*>){
        val layoutManager = GridLayoutManager(list.context,
                NESTED_SPAN_COUNT, GridLayoutManager.HORIZONTAL, false)
        layoutManager.isItemPrefetchEnabled = true
        layoutManager.initialPrefetchItemCount = NESTED_SPAN_COUNT
        list.layoutManager = layoutManager
        list.adapter = adapter
        list.recycledViewPool = recycledViewPool

        val snapHelper = LinearSnapHelper()
        snapHelper.attachToRecyclerView(list)
    }

    private fun setupHorizontalListAsList(list: RecyclerView, adapter: AbsAdapter<*>){
        val layoutManager = LinearLayoutManager(list.context, LinearLayoutManager.HORIZONTAL, false)
        layoutManager.isItemPrefetchEnabled = true
        layoutManager.initialPrefetchItemCount = NESTED_SPAN_COUNT
        list.layoutManager = layoutManager
        list.adapter = adapter
        list.recycledViewPool = recycledViewPool
    }

    override fun onViewDetachedFromWindow(holder: DataBoundViewHolder) {
        when (holder.itemViewType){
            R.layout.item_detail_most_played_list -> {
                mostPlayedAdapter.setAfterDataChanged(null)
            }
            R.layout.item_detail_recently_added_list -> {
                recentlyAddedAdapter.setAfterDataChanged(null)
            }
        }
    }

    override fun onViewAttachedToWindow(holder: DataBoundViewHolder) {
        when (holder.itemViewType) {
            R.layout.item_detail_most_played_list -> {
                val list = holder.itemView as RecyclerView
                val layoutManager = list.layoutManager as GridLayoutManager
                mostPlayedAdapter.setAfterDataChanged({
                    updateNestedSpanCount(layoutManager, it.size)
                }, false)
            }
            R.layout.item_detail_recently_added_list -> {
                val list = holder.itemView as RecyclerView
                val layoutManager = list.layoutManager as GridLayoutManager
                recentlyAddedAdapter.setAfterDataChanged({
                    updateNestedSpanCount(layoutManager, it.size)
                }, false)
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

    private fun updateNestedSpanCount(layoutManager: GridLayoutManager, size: Int){
        layoutManager.spanCount = when {
            size == 0 -> 1
            size < NESTED_SPAN_COUNT -> size
            else -> NESTED_SPAN_COUNT
        }
    }

    override fun bind(binding: ViewDataBinding, item: DisplayableItem, position: Int){
        binding.setVariable(BR.item, item)
    }

    val hasTouchBehavior = mediaId.isPlaylist &&
            !PlaylistConstants.isAutoPlaylist(mediaId.categoryValue.toLong())

    override val onDragAction = { from: Int, to: Int -> viewModel.moveItemInPlaylist(from, to) }

    override val onSwipeAction = { position: Int ->
        viewModel.removeFromPlaylist(controller.getItem(position).trackNumber.toLong())
                .subscribe({}, Throwable::printStackTrace)
    }

    override fun canInteractWithViewHolder(viewHolder: RecyclerView.ViewHolder): Boolean? {
        return hasTouchBehavior && viewHolder.itemViewType == R.layout.item_detail_song_with_drag_handle
    }
}