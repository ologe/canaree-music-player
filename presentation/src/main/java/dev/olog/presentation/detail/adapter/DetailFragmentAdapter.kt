package dev.olog.presentation.detail.adapter


import android.annotation.SuppressLint
import android.content.res.ColorStateList
import android.view.View
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import dev.olog.core.MediaId
import dev.olog.core.entity.AutoPlaylist
import dev.olog.media.MediaProvider
import dev.olog.presentation.R
import dev.olog.presentation.base.adapter.*
import dev.olog.presentation.base.drag.IDragListener
import dev.olog.presentation.base.drag.TouchableAdapter
import dev.olog.presentation.detail.DetailFragmentHeaders
import dev.olog.presentation.detail.DetailFragmentViewModel
import dev.olog.presentation.detail.DetailFragmentViewModel.Companion.NESTED_SPAN_COUNT
import dev.olog.presentation.detail.DetailSortDialog
import dev.olog.presentation.interfaces.SetupNestedList
import dev.olog.presentation.loadBigAlbumImage
import dev.olog.presentation.loadSongImage
import dev.olog.presentation.model.*
import dev.olog.presentation.navigator.Navigator
import dev.olog.presentation.tutorial.TutorialTapTarget
import dev.olog.presentation.utils.asHtml
import dev.olog.shared.android.extensions.colorAccent
import dev.olog.shared.android.extensions.textColorPrimary
import dev.olog.shared.android.extensions.toggleVisibility
import dev.olog.shared.exhaustive
import dev.olog.shared.swap
import kotlinx.android.synthetic.main.item_detail_biography.view.*
import kotlinx.android.synthetic.main.item_detail_header.view.*
import kotlinx.android.synthetic.main.item_detail_header.view.title
import kotlinx.android.synthetic.main.item_detail_header_albums.view.*
import kotlinx.android.synthetic.main.item_detail_header_all_song.view.*
import kotlinx.android.synthetic.main.item_detail_song.view.explicit
import kotlinx.android.synthetic.main.item_detail_song.view.firstText
import kotlinx.android.synthetic.main.item_detail_song.view.secondText
import kotlinx.android.synthetic.main.item_detail_song_most_played.view.index
import kotlinx.android.synthetic.main.item_detail_song_most_played.view.isPlaying
import kotlinx.android.synthetic.main.item_tab_podcast.view.*
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onEach

internal class DetailFragmentAdapter(
    private val mediaId: MediaId,
    private val setupNestedList: SetupNestedList,
    private val navigator: Navigator,
    private val mediaProvider: MediaProvider,
    private val viewModel: DetailFragmentViewModel,
    private val dragListener: IDragListener,
    private val afterImageLoad: () -> Unit
) : ObservableAdapter<DisplayableItem>(DiffCallbackDetailDisplayableItem),
    TouchableAdapter,
    CanShowIsPlaying by CanShowIsPlayingImpl() {

    private var podcastPositions = emptyMap<Long, Int>()

    private val headers by lazy { currentList.indexOfFirst { it is DisplayableTrack } }

    override fun initViewHolderListeners(viewHolder: DataBoundViewHolder, viewType: Int) {
        when (viewType) {

            R.layout.item_detail_list_most_played,
            R.layout.item_detail_list_recently_added,
            R.layout.item_detail_list_related_artists,
            R.layout.item_detail_list_albums -> {
                setupNestedList.setupNestedList(viewType, viewHolder.itemView as RecyclerView)
            }
            R.layout.item_detail_podcast -> {
                viewHolder.setOnClickListener(this) { item, _, _ ->
                    viewModel.detailSortDataUseCase(item.mediaId) {
                        mediaProvider.playFromMediaId(item.mediaId, viewModel.getFilter(), it)
                    }
                }
                viewHolder.setOnLongClickListener(this) { item, _, _ ->
                    navigator.toDialog(item.mediaId, viewHolder.itemView)
                }
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

    override fun onViewAttachedToWindow(holder: DataBoundViewHolder) {
        super.onViewAttachedToWindow(holder)

        val view = holder.itemView

        when (holder.itemViewType) {
            R.layout.item_detail_list_recently_added,
            R.layout.item_detail_list_most_played -> {
                val list = holder.itemView as RecyclerView
                val layoutManager = list.layoutManager as GridLayoutManager
                val adapter = list.adapter as ObservableAdapter<*>
                adapter.observeData
                    .onEach { updateNestedSpanCount(layoutManager, it.size) }
                    .launchIn(holder.lifecycleScope)
            }
            R.layout.item_detail_header_all_song -> {
                val sortText = holder.itemView.sort
                val sortImage = holder.itemView.sortImage

                viewModel.observeSorting()
                    .onEach { view.sortImage.update(it) }
                    .launchIn(holder.lifecycleScope)

                if (viewModel.showSortByTutorialIfNeverShown()) {
                    TutorialTapTarget.sortBy(sortText, sortImage)
                }
            }
            R.layout.item_detail_biography -> {
                viewModel.biography
                    .map { it?.asHtml() }
                    .onEach { view.biography.text = it }
                    .launchIn(holder.lifecycleScope)
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

    override fun onBindViewHolder(
        holder: DataBoundViewHolder,
        position: Int,
        payloads: MutableList<Any>
    ) {
        val item = getItem(position)

        val payload = payloads.filterIsInstance<List<String>>().firstOrNull()
        if (payload != null) {
            holder.itemView.apply {
                title.text = payload[0]
                subtitle.text = payload[1]
            }
        }
        val currentPayload = payloads.filterIsInstance<Boolean>().firstOrNull()
        if (currentPayload != null) {
            holder.itemView.isPlaying.animateVisibility(currentPayload)
            if (item is DisplayableTrack) {
                bindPodcastProgressBarTint(holder.itemView, item)
            }
        }

        val updatePodcastPosition = payloads.filterIsInstance<Unit>().firstOrNull()
        if (updatePodcastPosition != null && item is DisplayableTrack) {
            bindPodcast(holder.itemView, item)
        }

        if (payloads.isEmpty()) {
            super.onBindViewHolder(holder, position, payloads)
        }
    }

    override fun bind(holder: DataBoundViewHolder, item: DisplayableItem, position: Int) {
        when (item){
            is DisplayableTrack -> bindTrack(holder, item)
            is DisplayableHeader -> bindHeader(holder, item)
            is DisplayableNestedListPlaceholder -> {}
            is DisplayableAlbum -> {}
        }.exhaustive
    }

    private fun bindTrack(holder: DataBoundViewHolder, item: DisplayableTrack){
        holder.itemView.apply {
            isPlaying.toggleVisibility(item.mediaId == playingMediaId)

            holder.imageView?.loadSongImage(item.mediaId)
            firstText.text = item.title
            secondText?.text = item.subtitle
            explicit?.onItemChanged(item.title)

            bindPodcast(this, item)
            bindPodcastProgressBarTint(this, item)
        }
        when (holder.itemViewType){
            R.layout.item_detail_song_with_track,
            R.layout.item_detail_song_with_track_and_image -> {
                val trackNumber = if (item.idInPlaylist < 1){
                    "-"
                } else item.idInPlaylist.toString()
                holder.itemView.index.text = trackNumber
            }
        }
    }

    private fun bindHeader(holder: DataBoundViewHolder, item: DisplayableHeader){
        when (holder.itemViewType){
            R.layout.item_detail_image -> {
                holder.imageView!!.post { afterImageLoad() }
                holder.imageView!!.loadBigAlbumImage(mediaId)
                holder.itemView.title.text = item.title
                holder.itemView.subtitle.text = item.subtitle
            }
            R.layout.item_detail_song_footer,
            R.layout.item_detail_header,
            R.layout.item_detail_header_albums,
            R.layout.item_detail_header_recently_added,
            R.layout.item_detail_image -> {
                holder.itemView.apply {
                    title.text = item.title
                    subtitle?.text = item.subtitle
                    seeMore?.toggleVisibility(item.visible, true)
                }
            }
            R.layout.item_detail_header_all_song -> {
                holder.itemView.apply {
                    title.text = item.title
                    sort.text = item.subtitle
                }
            }
        }
    }

    @SuppressLint("SetTextI18n")
    private fun bindPodcast(view: View, item: DisplayableTrack) {
        val duration = item.duration.toInt()
        val progress = podcastPositions[item.mediaId.resolveId] ?: 0
        view.progressBar?.max = duration
        view.progressBar?.progress = progress

        val percentage = (progress.toFloat() / duration.toFloat() * 100f).toInt()
        view.percentage?.text = "$percentage%"
    }

    private fun bindPodcastProgressBarTint(view: View, item: DisplayableTrack) {
        val color = if (item.mediaId == playingMediaId) {
            view.context.colorAccent()
        } else {
            view.context.textColorPrimary()
        }
        view.progressBar?.progressTintList = ColorStateList.valueOf(color)
    }

    fun updatePodcastPositions(positions: Map<Long, Int>) {
        this.podcastPositions = positions
        for (index in currentList.indices) {
            notifyItemChanged(index, Unit)
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
        val realFrom = from - headers
        val realTo = to - headers
        currentList.swap(from, to) // TODO check if works
        notifyItemMoved(from, to)
        viewModel.addMove(realFrom, realTo)
    }

    override fun onSwipedRight(viewHolder: RecyclerView.ViewHolder) {
        val position = viewHolder.adapterPosition
        val item = getItem(position)
        currentList.removeAt(position) // TODO check if works
        notifyItemRemoved(position)
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