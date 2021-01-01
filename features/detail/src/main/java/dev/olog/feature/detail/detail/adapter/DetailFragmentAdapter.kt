package dev.olog.feature.detail.detail.adapter


import android.text.Spanned
import androidx.core.text.parseAsHtml
import androidx.core.view.isVisible
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import dev.olog.core.mediaid.MediaId
import dev.olog.core.entity.AutoPlaylist
import dev.olog.core.entity.sort.SortEntity
import dev.olog.feature.base.adapter.*
import dev.olog.feature.detail.R
import dev.olog.lib.media.MediaProvider
import dev.olog.lib.image.provider.ImageLoader
import dev.olog.feature.base.adapter.drag.IDragListener
import dev.olog.feature.base.adapter.drag.TouchableAdapter
import dev.olog.feature.detail.detail.DetailFragmentViewModel
import dev.olog.feature.detail.detail.DetailFragmentViewModel.Companion.NESTED_SPAN_COUNT
import dev.olog.feature.detail.detail.DetailSortDialog
import dev.olog.feature.detail.detail.DetailTutorial
import dev.olog.feature.detail.detail.model.DetailFragmentModel
import dev.olog.navigation.Navigator
import dev.olog.shared.exhaustive
import dev.olog.shared.swapped
import kotlinx.android.synthetic.main.item_detail_biography.*
import kotlinx.android.synthetic.main.item_detail_header.*
import kotlinx.android.synthetic.main.item_detail_header.title
import kotlinx.android.synthetic.main.item_detail_header_albums.*
import kotlinx.android.synthetic.main.item_detail_header_all_song.*
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
) : ObservableAdapter<DetailFragmentModel>(DetailFragmentModelDiff),
    TouchableAdapter {

    private val headersIndex: Int
        get() = currentList.indexOfFirst { it.isPlayable }

    override fun getItemViewType(position: Int): Int = getItem(position).layoutType

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
                    val playable = item as? DetailFragmentModel.Playable ?: return@setOnClickListener
                    viewModel.detailSortDataUseCase(playable.mediaId) {
                        mediaProvider.playFromMediaId(playable.mediaId, viewModel.getFilter(), it)
                    }
                }
                viewHolder.setOnLongClickListener(this) { item, _, _ ->
                    val playable = item as? DetailFragmentModel.Playable ?: return@setOnLongClickListener
                    navigator.toDialog(playable.mediaId, viewHolder.itemView)
                }
                viewHolder.setOnClickListener(R.id.more, this) { item, _, view ->
                    val playable = item as? DetailFragmentModel.Playable ?: return@setOnClickListener
                    navigator.toDialog(playable.mediaId, view)
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
                    if (item is DetailFragmentModel.RelatedArtistHeader) {
                        navigator.toRelatedArtists(mediaId)
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
        DetailTutorial.sortBy(sort, sortImage)
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
        item: DetailFragmentModel,
        position: Int
    ) {
        when (item){
            is DetailFragmentModel.Track -> bindTrack(
                holder = holder,
                mediaId = item.mediaId,
                title = item.title,
                subtitle = item.subtitle,
                position = item.trackNumber,
            )
            is DetailFragmentModel.PlaylistTrack -> bindTrack(
                holder = holder,
                mediaId = item.mediaId,
                title = item.title,
                subtitle = item.subtitle,
                position = item.idInPlaylist.toInt(),
            )
            is DetailFragmentModel.RelatedArtistHeader -> bindHeader(
                holder = holder,
                title = item.title,
                subtitle = null,
                showSeeAll = item.showSeeAll,
            )
            is DetailFragmentModel.AlbumsHeader -> bindHeader(
                holder = holder,
                title = item.title,
                subtitle = null,
                showSeeAll = null
            )
            is DetailFragmentModel.AllTracksHeader -> bindHeader(
                holder = holder,
                title = item.title,
                subtitle = item.subtitle,
                showSeeAll = null,
            )
            is DetailFragmentModel.MostPlayedHeader -> bindHeader(
                holder = holder,
                title = item.title,
                subtitle = null,
                showSeeAll = null,
            )
            is DetailFragmentModel.RecentlyAddedHeader -> bindHeader(
                holder = holder,
                title = item.title,
                subtitle = item.subtitle,
                showSeeAll = item.showSeeAll
            )
            is DetailFragmentModel.MainHeader -> bindHeader(
                holder = holder,
                title = item.title,
                subtitle = item.subtitle,
                showSeeAll = null,
            )
            is DetailFragmentModel.Biography,
            DetailFragmentModel.MostPlayedList,
            DetailFragmentModel.RelatedArtistList,
            DetailFragmentModel.RecentlyAddedList,
            DetailFragmentModel.AlbumsList,
            DetailFragmentModel.Shuffle,
            DetailFragmentModel.EmptyState -> {}
            is DetailFragmentModel.Duration -> bindHeader(
                holder = holder,
                title = item.content,
                subtitle = null,
                showSeeAll = null
            )
        }.exhaustive
    }

    private fun bindTrack(
        holder: LayoutContainerViewHolder,
        mediaId: MediaId,
        title: String,
        subtitle: String,
        position: Int, // TODO not sure about the name
    ) = holder.bindView {

        if (imageView != null) {
            ImageLoader.loadSongImage(imageView!!, mediaId)
        }
        firstText.text = title
        secondText?.text = subtitle
        explicit.onItemChanged(title)

        when (itemViewType){ // TODO separate
            R.layout.item_detail_song_with_track,
            R.layout.item_detail_song_with_track_and_image -> {
                val trackNumber = if (position < 1){
                    "-"
                } else {
                    position.toString()
                }
                index.text = trackNumber
            }
        }
    }

    private fun bindHeader(
        holder: LayoutContainerViewHolder,
        title: String,
        subtitle: String?,
        showSeeAll: Boolean?,
    ) = holder.bindView {
        when (itemViewType){
            R.layout.item_detail_image -> {
                ImageLoader.loadBigAlbumImage(imageView!!, mediaId)
                this.title.text = title
                this.subtitle.text = subtitle
            }
            R.layout.item_detail_song_footer,
            R.layout.item_detail_header,
            R.layout.item_detail_header_albums,
            R.layout.item_detail_header_recently_added,
            R.layout.item_detail_image -> {
                this.title.text = title
                this.subtitle?.text = subtitle
                this.seeMore?.isVisible = showSeeAll ?: false
            }
            R.layout.item_detail_header_all_song -> {
                this.title.text = title
                this.sort.text = subtitle
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
        // TODO is this correct
        val item = getItem(position) as? DetailFragmentModel.PlaylistTrack ?: return

        val newList = currentList.toMutableList()
        newList.removeAt(position)
        submitList(newList)
        viewModel.removeFromPlaylist(item)
    }

    override fun afterSwipeRight(viewHolder: RecyclerView.ViewHolder) {

    }

    override fun onSwipedLeft(viewHolder: RecyclerView.ViewHolder) {
        val item = getItem(viewHolder.adapterPosition) as? DetailFragmentModel.Playable ?: return
        mediaProvider.addToPlayNext(item.mediaId)
    }

    override fun afterSwipeLeft(viewHolder: RecyclerView.ViewHolder) {
        notifyItemChanged(viewHolder.adapterPosition)
    }


}

private object DetailFragmentModelDiff : DiffUtil.ItemCallback<DetailFragmentModel>() {

    override fun areItemsTheSame(oldItem: DetailFragmentModel, newItem: DetailFragmentModel): Boolean {
        return oldItem == newItem // TODO not sure
    }

    override fun areContentsTheSame(oldItem: DetailFragmentModel, newItem: DetailFragmentModel): Boolean {
        return oldItem == newItem
    }

    override fun getChangePayload(oldItem: DetailFragmentModel, newItem: DetailFragmentModel): Any? {
        if (newItem is DetailFragmentModel.MainHeader){
            return listOf(
                newItem.title,
                newItem.subtitle
            )
        }
        return null
    }
}