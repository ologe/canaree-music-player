package dev.olog.msc.presentation.detail

import androidx.databinding.ViewDataBinding
import androidx.lifecycle.Lifecycle
import com.jakewharton.rxbinding2.view.RxView
import dev.olog.msc.BR
import dev.olog.msc.R
import dev.olog.msc.constants.PlaylistConstants
import dev.olog.core.entity.SortArranging
import dev.olog.core.entity.SortType
import dev.olog.msc.presentation.base.adapter.AbsAdapter
import dev.olog.msc.presentation.base.adapter.DataBoundViewHolder
import dev.olog.msc.presentation.base.music.service.MediaProvider
import dev.olog.msc.presentation.detail.DetailFragmentViewModel.Companion.NESTED_SPAN_COUNT
import dev.olog.msc.presentation.detail.sort.DetailSortDialog
import dev.olog.presentation.model.DisplayableItem
import dev.olog.msc.presentation.navigator.Navigator
import dev.olog.msc.presentation.tutorial.TutorialTapTarget
import dev.olog.core.MediaId
import dev.olog.msc.utils.k.extension.elevateSongOnTouch
import dev.olog.msc.utils.k.extension.setOnClickListener
import dev.olog.msc.utils.k.extension.setOnLongClickListener
import dev.olog.msc.utils.k.extension.setOnMoveListener
import io.reactivex.android.schedulers.AndroidSchedulers
import kotlinx.android.synthetic.main.item_detail_header_all_song.view.*

class DetailFragmentAdapter (
    lifecycle: Lifecycle,
    private val mediaId: MediaId,
    private val recentlyAddedAdapter: DetailRecentlyAddedAdapter,
    private val mostPlayedAdapter: DetailMostPlayedAdapter,
    private val relatedArtistsAdapter: DetailRelatedArtistsAdapter,
    private val albumsAdapter: DetailAlbumsAdapter,
    private val navigator: Navigator,
    private val mediaProvider: MediaProvider,
    private val viewModel: DetailFragmentViewModel,
    private val recycledViewPool : androidx.recyclerview.widget.RecyclerView.RecycledViewPool

) : AbsAdapter<DisplayableItem>(lifecycle) {

    override fun initViewHolderListeners(viewHolder: DataBoundViewHolder, viewType: Int){
        when (viewType) {

            R.layout.item_detail_most_played_list -> {
                val list = viewHolder.itemView as androidx.recyclerview.widget.RecyclerView
                setupHorizontalListAsGrid(list, mostPlayedAdapter)
            }
            R.layout.item_detail_recently_added_list -> {
                val list = viewHolder.itemView as androidx.recyclerview.widget.RecyclerView
                setupHorizontalListAsGrid(list, recentlyAddedAdapter)
            }
            R.layout.item_detail_related_artists_list -> {
                val list = viewHolder.itemView as androidx.recyclerview.widget.RecyclerView
                setupHorizontalListAsList(list, relatedArtistsAdapter)
            }
            R.layout.item_detail_albums_list -> {
                val list = viewHolder.itemView as androidx.recyclerview.widget.RecyclerView
                setupHorizontalListAsList(list, albumsAdapter)
            }
            R.layout.item_detail_song,
            R.layout.item_detail_song_with_track,
            R.layout.item_detail_song_with_drag_handle,
            R.layout.item_detail_song_with_track_and_image -> {
                viewHolder.setOnClickListener(controller) { item, _, _ ->
                    viewModel.detailSortDataUseCase(item.mediaId) {
                        mediaProvider.playFromMediaId(item.mediaId, it)
                    }
                }
                viewHolder.setOnLongClickListener(controller) { item, _, _ ->
                    navigator.toDialog(item, viewHolder.itemView)
                }
                viewHolder.setOnClickListener(R.id.more, controller) { item, _, view ->
                    navigator.toDialog(item, view)
                }
                viewHolder.setOnMoveListener(controller, touchHelper)
            }
            R.layout.item_detail_shuffle -> {
                viewHolder.setOnClickListener(controller) { _, _, _ ->
                    mediaProvider.shuffle(mediaId)
                }
            }

            R.layout.item_detail_header_recently_added -> {
                viewHolder.setOnClickListener(R.id.seeMore, controller) { _, _, _ ->
                    navigator.toRecentlyAdded(mediaId)
                }
            }
            R.layout.item_detail_header -> {

                viewHolder.setOnClickListener(R.id.seeMore, controller) { item, _, _ ->
                    when (item.mediaId) {
                        DetailFragmentHeaders.RELATED_ARTISTS_SEE_ALL -> navigator.toRelatedArtists(mediaId)
                    }
                }
            }

            R.layout.item_detail_header_all_song -> {
                viewHolder.setOnClickListener(R.id.sort, controller) { _, _, view ->
                    viewModel.observeSortOrder {
                        DetailSortDialog().show(view.context, view, mediaId, it, viewModel::updateSortOrder)
                    }
                }
                viewHolder.setOnClickListener(R.id.sortImage, controller) { _, _, _ ->
                    viewModel.toggleSortArranging()
                }
            }
        }

        when (viewType){
            R.layout.item_detail_song,
            R.layout.item_detail_song_with_track,
            R.layout.item_detail_song_with_drag_handle -> viewHolder.elevateSongOnTouch()
        }
    }

    private fun setupHorizontalListAsGrid(list: androidx.recyclerview.widget.RecyclerView, adapter: AbsAdapter<*>){
        val layoutManager = androidx.recyclerview.widget.GridLayoutManager(list.context,
                NESTED_SPAN_COUNT, androidx.recyclerview.widget.GridLayoutManager.HORIZONTAL, false)
        layoutManager.isItemPrefetchEnabled = true
        layoutManager.initialPrefetchItemCount = NESTED_SPAN_COUNT
        list.layoutManager = layoutManager
        list.adapter = adapter
        list.setRecycledViewPool(recycledViewPool)

        val snapHelper = androidx.recyclerview.widget.LinearSnapHelper()
        snapHelper.attachToRecyclerView(list)
    }

    private fun setupHorizontalListAsList(list: androidx.recyclerview.widget.RecyclerView, adapter: AbsAdapter<*>){
        val layoutManager = androidx.recyclerview.widget.LinearLayoutManager(list.context, androidx.recyclerview.widget.LinearLayoutManager.HORIZONTAL, false)
        layoutManager.isItemPrefetchEnabled = true
        layoutManager.initialPrefetchItemCount = NESTED_SPAN_COUNT
        list.layoutManager = layoutManager
        list.adapter = adapter
        list.setRecycledViewPool(recycledViewPool)
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
                val list = holder.itemView as androidx.recyclerview.widget.RecyclerView
                val layoutManager = list.layoutManager as androidx.recyclerview.widget.GridLayoutManager
                mostPlayedAdapter.setAfterDataChanged({
                    updateNestedSpanCount(layoutManager, it.size)
                }, false)
            }
            R.layout.item_detail_recently_added_list -> {
                val list = holder.itemView as androidx.recyclerview.widget.RecyclerView
                val layoutManager = list.layoutManager as androidx.recyclerview.widget.GridLayoutManager
                recentlyAddedAdapter.setAfterDataChanged({
                    updateNestedSpanCount(layoutManager, it.size)
                }, false)
            }
            R.layout.item_detail_header_all_song -> {
                val sortText = holder.itemView.sort
                val sortImage = holder.itemView.sortImage

                viewModel.observeSorting()
                        .takeUntil(RxView.detaches(holder.itemView))
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe({ (sort, arranging) ->
                            if (sort == SortType.CUSTOM){
                                sortImage.setImageResource(R.drawable.vd_remove)
                            } else {
                                if (arranging == SortArranging.ASCENDING){
                                    sortImage.setImageResource(R.drawable.vd_arrow_down)
                                } else {
                                    sortImage.setImageResource(R.drawable.vd_arrow_up)
                                }
                            }

                        }, Throwable::printStackTrace)

                viewModel.showSortByTutorialIfNeverShown()
                        .subscribe({ TutorialTapTarget.sortBy(sortText, sortImage) }, {})
            }
        }
    }

    private fun updateNestedSpanCount(layoutManager: androidx.recyclerview.widget.GridLayoutManager, size: Int){
        layoutManager.spanCount = when {
            size == 0 -> 1
            size < NESTED_SPAN_COUNT -> size
            else -> NESTED_SPAN_COUNT
        }
    }

    override fun bind(binding: ViewDataBinding, item: DisplayableItem, position: Int){
        binding.setVariable(BR.item, item)
    }

    val canSwipeRight : Boolean
        get() {
            if (mediaId.isPlaylist){
                val playlistId = mediaId.resolveId
                return playlistId != PlaylistConstants.LAST_ADDED_ID || !PlaylistConstants.isAutoPlaylist(playlistId)
            }
//            if (mediaId.isPodcastPlaylist){
//                val playlistId = mediaId.resolveId
//                return playlistId != PlaylistConstants.PODCAST_LAST_ADDED_ID || !PlaylistConstants.isPodcastAutoPlaylist(playlistId)
//            }
            return false
        }

    override val onDragAction = { from: Int, to: Int -> viewModel.moveItemInPlaylist(from, to) }

    override fun onSwipedRight(position: Int) {
        onSwipeRightAction.invoke(position)
        controller.remove(position)
        notifyItemRemoved(position)
    }

    override val onSwipeRightAction = { position: Int ->
        controller.getItem(position)?.let { viewModel.removeFromPlaylist(it) } ?: Any()
    }

    override val onSwipeLeftAction = { position: Int ->
        controller.getItem(position)?.let { mediaProvider.addToPlayNext(it.mediaId) } ?: Any()
    }

    override fun canInteractWithViewHolder(viewType: Int): Boolean? {
        if (mediaId.isPodcastPlaylist){
            return false
        }
        return viewType == R.layout.item_detail_song ||
                viewType == R.layout.item_detail_song_with_drag_handle ||
                viewType == R.layout.item_detail_song_with_track ||
                viewType == R.layout.item_detail_song_with_track_and_image
    }
}