package dev.olog.presentation.detail.adapter


import android.annotation.SuppressLint
import android.text.Spanned
import androidx.core.text.parseAsHtml
import androidx.core.view.isVisible
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import dev.olog.core.MediaId
import dev.olog.core.entity.AutoPlaylist
import dev.olog.core.entity.sort.SortEntity
import dev.olog.media.MediaProvider
import dev.olog.presentation.BindingsAdapter
import dev.olog.presentation.R
import dev.olog.presentation.base.adapter.*
import dev.olog.presentation.base.drag.IDragListener
import dev.olog.presentation.base.drag.TouchableAdapter
import dev.olog.presentation.detail.DetailFragmentHeaders
import dev.olog.presentation.detail.DetailFragmentViewModel
import dev.olog.presentation.detail.DetailFragmentViewModel.Companion.NESTED_SPAN_COUNT
import dev.olog.presentation.detail.DetailSortDialog
import dev.olog.presentation.interfaces.SetupNestedList
import dev.olog.presentation.model.*
import dev.olog.presentation.navigator.Navigator
import dev.olog.presentation.tutorial.TutorialTapTarget
import dev.olog.shared.exhaustive
import dev.olog.shared.swapped
import kotlinx.android.synthetic.main.item_detail_biography.*
import kotlinx.android.synthetic.main.item_detail_header.*
import kotlinx.android.synthetic.main.item_detail_header.title
import kotlinx.android.synthetic.main.item_detail_header_albums.*
import kotlinx.android.synthetic.main.item_detail_header_all_song.*
import kotlinx.android.synthetic.main.item_detail_song.explicit
import kotlinx.android.synthetic.main.item_detail_song.firstText
import kotlinx.android.synthetic.main.item_detail_song.secondText
import kotlinx.android.synthetic.main.item_detail_song_most_played.*
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onEach

internal class DetailFragmentAdapter(
    private val mediaId: MediaId,
    private val setupNestedList: SetupNestedList,
    private val navigator: Navigator,
    private val mediaProvider: MediaProvider,
    private val viewModel: DetailFragmentViewModel,
    private val dragListener: IDragListener
) : ObservableAdapter<DisplayableItem>(DiffCallbackDetailDisplayableItem),
    TouchableAdapter {

    private val headersIndex: Int
        get() = currentList.indexOfFirst { it is DisplayableTrack }

    override fun initViewHolderListeners(viewHolder: LayoutContainerViewHolder, viewType: Int) {
        when (viewType) {

            R.layout.item_detail_list_most_played,
            R.layout.item_detail_list_recently_added,
            R.layout.item_detail_list_related_artists,
            R.layout.item_detail_list_albums -> {
                setupNestedList.setupNestedList(viewType, viewHolder.itemView as RecyclerView)
            }

            R.layout.item_detail_song,
            R.layout.item_detail_song_with_track,
            R.layout.item_detail_song_with_drag_handle,
            R.layout.item_detail_song_with_track_and_image -> {
                viewHolder.setOnClickListener(this) { item, _, _ ->
                    viewModel.detailSortDataUseCase(item.mediaId) {
                        mediaProvider.playFromMediaId(item.mediaId, viewModel.getFilter(), it)
                    }
                }
                viewHolder.setOnLongClickListener(this) { item, _, _ ->
                    navigator.toDialog(item.mediaId, viewHolder.itemView)
                }
                viewHolder.setOnClickListener(R.id.more, this) { item, _, view ->
                    navigator.toDialog(item.mediaId, view)
                }

                viewHolder.setOnDragListener(R.id.dragHandle, dragListener)
            }
            R.layout.item_detail_shuffle -> {
                viewHolder.setOnClickListener(this) { _, _, _ ->
                    mediaProvider.shuffle(mediaId, viewModel.getFilter())
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
                        DetailFragmentHeaders.RELATED_ARTISTS_SEE_ALL -> navigator.toRelatedArtists(
                            mediaId
                        )
                    }
                }
            }

            R.layout.item_detail_header_all_song -> {
                viewHolder.setOnClickListener(R.id.sort, this) { _, _, view ->
                    viewModel.observeSortOrder { currentSortType ->
                        DetailSortDialog().show(view, mediaId, currentSortType) { newSortType ->
                            viewModel.updateSortOrder(newSortType)
                        }
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

    override fun onViewAttachedToWindow(holder: LayoutContainerViewHolder) {
        super.onViewAttachedToWindow(holder)

        when (holder.itemViewType) {
            R.layout.item_detail_list_recently_added,
            R.layout.item_detail_list_most_played -> {
                val list = holder.itemView as RecyclerView
                val layoutManager = list.layoutManager as GridLayoutManager
                val adapter = list.adapter as ObservableAdapter<*>
                adapter.observeData()
                    .onEach { updateNestedSpanCount(layoutManager, it.size) }
                    .launchIn(holder.coroutineScope)
            }
            R.layout.item_detail_header_all_song -> {
                viewModel.observeSorting()
                    .onEach { holder.bindSorting(it) }
                    .launchIn(holder.coroutineScope)

                if (viewModel.showSortByTutorialIfNeverShown()) {
                    holder.bindTutorial()
                }
            }
            R.layout.item_detail_biography -> {
                viewModel.observeBiography()
                    .map { it.parseAsHtml() }
                    .onEach { holder.bindBiography(it) }
                    .launchIn(holder.coroutineScope)
            }
        }
    }

    private fun LayoutContainerViewHolder.bindBiography(
        biographyText: Spanned
    ) = bindView {
        biography.text = biographyText
    }

    private fun LayoutContainerViewHolder.bindSorting(
        sort: SortEntity,
    ) = bindView {
        sortImage.update(sort)
    }

    private fun LayoutContainerViewHolder.bindTutorial(

    ) = bindView {
        TutorialTapTarget.sortBy(sort, sortImage)
    }

    private fun updateNestedSpanCount(layoutManager: GridLayoutManager, size: Int) {
        layoutManager.spanCount = when {
            size == 0 -> 1
            size < NESTED_SPAN_COUNT -> size
            else -> NESTED_SPAN_COUNT
        }
    }

    override fun onBindViewHolder(
        holder: LayoutContainerViewHolder,
        position: Int,
        payloads: MutableList<Any>
    ) = holder.bindView {
        if (payloads.isNotEmpty()){
            val payload = payloads[0] as List<String>
            title.text = payload[0]
            subtitle.text = payload[1]
            return@bindView
        }
        super.onBindViewHolder(holder, position, payloads)
    }

    override fun bind(
        holder: LayoutContainerViewHolder,
        item: DisplayableItem,
        position: Int
    ) {
        when (item){
            is DisplayableTrack -> bindTrack(holder, item)
            is DisplayableHeader -> bindHeader(holder, item)
            is DisplayableNestedListPlaceholder -> {}
            is DisplayableAlbum -> {}
        }.exhaustive
    }

    private fun bindTrack(
        holder: LayoutContainerViewHolder,
        item: DisplayableTrack
    ) = holder.bindView {

        if (imageView != null) {
            BindingsAdapter.loadSongImage(imageView, item.mediaId)
        }
        firstText.text = item.title
        secondText?.text = item.subtitle
        explicit.onItemChanged(item.title)

        when (itemViewType){
            R.layout.item_detail_song_with_track,
            R.layout.item_detail_song_with_track_and_image -> {
                val trackNumber = if (item.idInPlaylist < 1){
                    "-"
                } else {
                    item.idInPlaylist.toString()
                }
                index.text = trackNumber
            }
        }
    }

    private fun bindHeader(
        holder: LayoutContainerViewHolder,
        item: DisplayableHeader
    ) = holder.bindView {
        when (itemViewType){
            R.layout.item_detail_image -> {
                BindingsAdapter.loadBigAlbumImage(imageView!!, mediaId)
                title.text = item.title
                subtitle.text = item.subtitle
            }
            R.layout.item_detail_song_footer,
            R.layout.item_detail_header,
            R.layout.item_detail_header_albums,
            R.layout.item_detail_header_recently_added,
            R.layout.item_detail_image -> {
                title.text = item.title
                subtitle?.text = item.subtitle
                seeMore?.isVisible = item.visible
            }
            R.layout.item_detail_header_all_song -> {
                title.text = item.title
                sort.text = item.subtitle
            }
        }
    }

    val canSwipeRight: Boolean
        get() {
            if (mediaId.isPlaylist || mediaId.isPodcastPlaylist) {
                val playlistId = mediaId.resolveId
                return playlistId != AutoPlaylist.LAST_ADDED.id || !AutoPlaylist.isAutoPlaylist(
                    playlistId
                )
            }
            return false
        }

    override fun canInteractWithViewHolder(viewType: Int): Boolean {
        return viewType == R.layout.item_detail_song ||
                viewType == R.layout.item_detail_song_with_drag_handle ||
                viewType == R.layout.item_detail_song_with_track ||
                viewType == R.layout.item_detail_song_with_track_and_image
    }

    override fun onClearView() {
        viewModel.processMove()
    }

    override fun onMoved(from: Int, to: Int) {
        val realFrom = from - headersIndex
        val realTo = to - headersIndex
        viewModel.addMove(realFrom, realTo)

        submitList(currentList.swapped(from, to))
    }

    override fun onSwipedRight(viewHolder: RecyclerView.ViewHolder) {
        val position = viewHolder.adapterPosition
        val item = getItem(position)

        val newList = currentList.toMutableList()
        newList.removeAt(position)
        submitList(newList)
        viewModel.removeFromPlaylist(item)
    }

    override fun afterSwipeRight(viewHolder: RecyclerView.ViewHolder) {

    }

    override fun onSwipedLeft(viewHolder: RecyclerView.ViewHolder) {
        val item = getItem(viewHolder.adapterPosition)
        mediaProvider.addToPlayNext(item.mediaId)
    }

    override fun afterSwipeLeft(viewHolder: RecyclerView.ViewHolder) {
        notifyItemChanged(viewHolder.adapterPosition)
    }


}

object DiffCallbackDetailDisplayableItem : DiffUtil.ItemCallback<DisplayableItem>() {

    override fun areItemsTheSame(oldItem: DisplayableItem, newItem: DisplayableItem): Boolean {
        return oldItem.mediaId == newItem.mediaId
    }

    @SuppressLint("DiffUtilEquals")
    override fun areContentsTheSame(oldItem: DisplayableItem, newItem: DisplayableItem): Boolean {
        return oldItem == newItem
    }

    override fun getChangePayload(oldItem: DisplayableItem, newItem: DisplayableItem): Any? {
        if (newItem.type == R.layout.item_detail_image){
            require(newItem is DisplayableHeader)
            return listOf(
                newItem.title,
                newItem.subtitle
            )
        }
        return null
    }
}