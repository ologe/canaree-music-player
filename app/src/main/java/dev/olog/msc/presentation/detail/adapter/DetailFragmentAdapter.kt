package dev.olog.msc.presentation.detail.adapter

import androidx.databinding.ViewDataBinding
import androidx.lifecycle.Lifecycle
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.jakewharton.rxbinding2.view.RxView
import dev.olog.core.MediaId
import dev.olog.core.entity.AutoPlaylist
import dev.olog.core.entity.id
import dev.olog.core.entity.sort.SortArranging
import dev.olog.core.entity.sort.SortType
import dev.olog.media.MediaProvider
import dev.olog.msc.BR
import dev.olog.msc.R
import dev.olog.msc.presentation.detail.DetailFragmentHeaders
import dev.olog.msc.presentation.detail.DetailFragmentViewModel
import dev.olog.msc.presentation.detail.DetailFragmentViewModel.Companion.NESTED_SPAN_COUNT
import dev.olog.msc.presentation.detail.sort.DetailSortDialog
import dev.olog.msc.utils.k.extension.elevateSongOnTouch
import dev.olog.presentation.base.DataBoundViewHolder
import dev.olog.presentation.base.ObservableAdapter
import dev.olog.presentation.base.setOnClickListener
import dev.olog.presentation.base.setOnLongClickListener
import dev.olog.presentation.interfaces.SetupNestedList
import dev.olog.presentation.model.DisplayableItem
import dev.olog.presentation.navigator.Navigator
import dev.olog.presentation.tutorial.TutorialTapTarget
import dev.olog.shared.extensions.asLiveData
import dev.olog.shared.extensions.subscribe
import io.reactivex.android.schedulers.AndroidSchedulers
import kotlinx.android.synthetic.main.item_detail_header_all_song.view.*

internal class DetailFragmentAdapter(
        lifecycle: Lifecycle,
        private val mediaId: MediaId,
        private val setupNestedList: SetupNestedList,
        private val navigator: Navigator,
        private val mediaProvider: MediaProvider,
        private val viewModel: DetailFragmentViewModel

) : ObservableAdapter<DisplayableItem>(lifecycle) {

    override fun initViewHolderListeners(viewHolder: DataBoundViewHolder, viewType: Int) {
        when (viewType) {

            R.layout.item_detail_most_played_list,
            R.layout.item_detail_recently_added_list,
            R.layout.item_detail_related_artists_list,
            R.layout.item_detail_albums_list -> {
                setupNestedList.setupNestedList(viewType, viewHolder.itemView as RecyclerView)
            }

            R.layout.item_detail_song,
            R.layout.item_detail_song_with_track,
            R.layout.item_detail_song_with_drag_handle,
            R.layout.item_detail_song_with_track_and_image -> {
                viewHolder.setOnClickListener(this) { item, _, _ ->
                    viewModel.detailSortDataUseCase(item.mediaId) {
                        mediaProvider.playFromMediaId(item.mediaId, it)
                    }
                }
                viewHolder.setOnLongClickListener(this) { item, _, _ ->
                    navigator.toDialog(item, viewHolder.itemView)
                }
                viewHolder.setOnClickListener(R.id.more, this) { item, _, view ->
                    navigator.toDialog(item, view)
                }
//                viewHolder.setOnMoveListener(controller, touchHelper) TODo
            }
            R.layout.item_detail_shuffle -> {
                viewHolder.setOnClickListener(this) { _, _, _ ->
                    mediaProvider.shuffle(mediaId)
                }
            }

            R.layout.item_detail_header_recently_added -> {
                viewHolder.setOnClickListener(R.id.seeMore, this) { _, _, _ ->
                    navigator.toRecentlyAdded(mediaId)
                }
            }
            R.layout.item_detail_header -> {

                viewHolder.setOnClickListener(R.id.seeMore, this) { item, _, _ ->
                    when (item.mediaId) {
                        DetailFragmentHeaders.RELATED_ARTISTS_SEE_ALL -> navigator.toRelatedArtists(mediaId)
                    }
                }
            }

            R.layout.item_detail_header_all_song -> {
                viewHolder.setOnClickListener(R.id.sort, this) { _, _, view ->
                    viewModel.observeSortOrder {
                        DetailSortDialog().show(view.context, view, mediaId, it, viewModel::updateSortOrder)
                    }
                }
                viewHolder.setOnClickListener(R.id.sortImage, this) { _, _, _ ->
                    viewModel.toggleSortArranging()
                }
            }
        }

        when (viewType) {
            R.layout.item_detail_song,
            R.layout.item_detail_song_with_track,
            R.layout.item_detail_song_with_drag_handle -> viewHolder.elevateSongOnTouch()
        }
    }

    override fun onViewAttachedToWindow(holder: DataBoundViewHolder) {
        super.onViewAttachedToWindow(holder)
        when (holder.itemViewType) {
            R.layout.item_detail_recently_added_list,
            R.layout.item_detail_most_played_list -> {
                val list = holder.itemView as RecyclerView
                val layoutManager = list.layoutManager as GridLayoutManager
                val adapter = list.adapter as ObservableAdapter<*>
                adapter.observeData(false)
                        .asLiveData()
                        .subscribe(holder) { updateNestedSpanCount(layoutManager, it.size) }
            }
            R.layout.item_detail_header_all_song -> {
                val sortText = holder.itemView.sort
                val sortImage = holder.itemView.sortImage

                viewModel.observeSorting()
                        .takeUntil(RxView.detaches(holder.itemView))
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe({ (sort, arranging) ->
                            if (sort == SortType.CUSTOM) {
                                sortImage.setImageResource(R.drawable.vd_remove)
                            } else {
                                if (arranging == SortArranging.ASCENDING) {
                                    sortImage.setImageResource(R.drawable.vd_arrow_down)
                                } else {
                                    sortImage.setImageResource(R.drawable.vd_arrow_up)
                                }
                            }

                        }, Throwable::printStackTrace)

                viewModel.showSortByTutorialIfNeverShown()
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe({ TutorialTapTarget.sortBy(sortText, sortImage) }, {})
            }
        }
    }

    private fun updateNestedSpanCount(layoutManager: GridLayoutManager, size: Int) {
        layoutManager.spanCount = when {
            size == 0 -> 1
            size < NESTED_SPAN_COUNT -> size
            else -> NESTED_SPAN_COUNT
        }
    }

    override fun bind(binding: ViewDataBinding, item: DisplayableItem, position: Int) {
        binding.setVariable(BR.item, item)
    }

    val canSwipeRight: Boolean
        get() {
            if (mediaId.isPlaylist) {
                val playlistId = mediaId.resolveId
                return playlistId != AutoPlaylist.LAST_ADDED.id || !AutoPlaylist.isAutoPlaylist(playlistId)
            }
            return false
        }

//    override val onDragAction = { from: Int, to: Int -> viewModel.moveItemInPlaylist(from, to) }
//
//    override fun onSwipedRight(position: Int) {
//        onSwipeRightAction.invoke(position)
//        controller.remove(position)
//        notifyItemRemoved(position)
//    }
//
//    override val onSwipeRightAction = { position: Int ->
//        controller.getItem(position)?.let { viewModel.removeFromPlaylist(it) } ?: Any()
//    }
//
//    override val onSwipeLeftAction = { position: Int ->
//        controller.getItem(position)?.let { mediaProvider.addToPlayNext(it.mediaId) } ?: Any()
//    }
//
//    override fun canInteractWithViewHolder(viewType: Int): Boolean? {
//        if (mediaId.isPodcastPlaylist) {
//            return false
//        }
//        return viewType == R.layout.item_detail_song ||
//                viewType == R.layout.item_detail_song_with_drag_handle ||
//                viewType == R.layout.item_detail_song_with_track ||
//                viewType == R.layout.item_detail_song_with_track_and_image
//    }
}