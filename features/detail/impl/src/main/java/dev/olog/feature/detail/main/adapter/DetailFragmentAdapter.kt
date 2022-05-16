package dev.olog.feature.detail.main.adapter


import android.annotation.SuppressLint
import android.view.View
import androidx.core.text.parseAsHtml
import androidx.core.view.isVisible
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import dev.olog.core.MediaId
import dev.olog.core.entity.AutoPlaylist
import dev.olog.feature.detail.R
import dev.olog.feature.detail.main.DetailFragmentHeaders
import dev.olog.feature.detail.main.DetailFragmentViewModel
import dev.olog.feature.detail.main.DetailFragmentViewModel.Companion.NESTED_SPAN_COUNT
import dev.olog.image.provider.BindingsAdapter
import dev.olog.platform.adapter.DataBoundViewHolder
import dev.olog.platform.adapter.ObservableAdapter
import dev.olog.platform.adapter.SetupNestedList
import dev.olog.platform.adapter.drag.IDragListener
import dev.olog.platform.adapter.drag.TouchableAdapter
import dev.olog.platform.adapter.elevateSongOnTouch
import dev.olog.platform.adapter.setOnClickListener
import dev.olog.platform.adapter.setOnDragListener
import dev.olog.platform.adapter.setOnLongClickListener
import dev.olog.shared.extension.collectOnLifecycle
import dev.olog.shared.extension.exhaustive
import dev.olog.shared.extension.map
import dev.olog.ui.model.DisplayableAlbum
import dev.olog.ui.model.DisplayableHeader
import dev.olog.ui.model.DisplayableItem
import dev.olog.ui.model.DisplayableNestedListPlaceholder
import dev.olog.ui.model.DisplayableTrack
import kotlinx.android.synthetic.main.item_detail_biography.view.*
import kotlinx.android.synthetic.main.item_detail_header.view.*
import kotlinx.android.synthetic.main.item_detail_header.view.title
import kotlinx.android.synthetic.main.item_detail_header_albums.view.*
import kotlinx.android.synthetic.main.item_detail_header_all_song.view.*
import kotlinx.android.synthetic.main.item_detail_song.view.*
import kotlinx.android.synthetic.main.item_detail_song.view.explicit
import kotlinx.android.synthetic.main.item_detail_song.view.firstText
import kotlinx.android.synthetic.main.item_detail_song.view.secondText
import kotlinx.android.synthetic.main.item_detail_song_most_played.view.*

internal class DetailFragmentAdapter(
    private val mediaId: MediaId,
    private val onShuffleClick: () -> Unit,
    private val onRecentlyAddedHeaderClick: () -> Unit,
    private val onRelatedArtistsHeaderClick: () -> Unit,
    private val onItemClick: (MediaId) -> Unit,
    private val onItemLongClick: (View, MediaId) -> Unit,
    private val onSortTypeClick: (View) -> Unit,
    private val onSortDirectionClick: () -> Unit,
    private val onShowSortTutorial: (text: View, image: View) -> Unit,
    private val onSwipeRight: (DisplayableItem) -> Unit,
    private val onSwipeLeft: (MediaId) -> Unit,
    private val onSwipeDone: () -> Unit,
    private val onItemMove: (from: Int, to: Int) -> Unit,
    private val viewModel: DetailFragmentViewModel, // todo refactor
    private val setupNestedList: SetupNestedList,
    private val dragListener: IDragListener
) : ObservableAdapter<DisplayableItem>(
    DiffCallbackDetailDisplayableItem
), TouchableAdapter {

    private val headers by lazy { indexOf { it is DisplayableTrack } }

    override fun initViewHolderListeners(viewHolder: DataBoundViewHolder, viewType: Int) {
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
                    onItemClick(item.mediaId)
                }
                viewHolder.setOnLongClickListener(this) { item, _, _ ->
                    onItemLongClick(viewHolder.itemView, item.mediaId)
                }
                viewHolder.setOnClickListener(R.id.more, this) { item, _, view ->
                    onItemLongClick(view, item.mediaId)
                }

                viewHolder.setOnDragListener(R.id.dragHandle, dragListener)
            }
            R.layout.item_detail_shuffle -> {
                viewHolder.setOnClickListener(this) { _, _, _ ->
                    onShuffleClick()
                }
            }

            R.layout.item_detail_header_recently_added -> {
                viewHolder.setOnClickListener(R.id.seeMore, this) { _, _, _ ->
                    onRecentlyAddedHeaderClick()
                }
            }
            R.layout.item_detail_header -> {

                viewHolder.setOnClickListener(R.id.seeMore, this) { item, _, _ ->
                    when (item.mediaId) {
                        DetailFragmentHeaders.RELATED_ARTISTS_SEE_ALL -> {
                            onRelatedArtistsHeaderClick()
                        }
                    }
                }
            }

            R.layout.item_detail_header_all_song -> {
                viewHolder.setOnClickListener(R.id.sort, this) { _, _, view ->
                    onSortTypeClick(view)

                }
                viewHolder.setOnClickListener(R.id.sortImage, this) { _, _, _ ->
                    onSortDirectionClick()
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
                adapter.observeChanges()
                    .collectOnLifecycle(holder) { updateNestedSpanCount(layoutManager, it.size) }
            }
            R.layout.item_detail_header_all_song -> {
                val sortText = holder.itemView.sort
                val sortImage = holder.itemView.sortImage

                viewModel.observeSorting()
                    .collectOnLifecycle(holder) {
                        view.sortImage.update(it)
                    }

                onShowSortTutorial(sortText, sortImage)
            }
            R.layout.item_detail_biography -> {
                viewModel.observeBiography()
                    .map { it?.parseAsHtml() }
                    .observe(holder) { view.biography.text = it }
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
        if (payloads.isNotEmpty()){
            val payload = payloads[0] as List<String>
            holder.itemView.apply {
                title.text = payload[0]
                subtitle.text = payload[1]
            }
            return
        }
        super.onBindViewHolder(holder, position, payloads)
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
            holder.imageView?.let {
                BindingsAdapter.loadSongImage(it, item.mediaId)
            }
            firstText.text = item.title
            secondText?.text = item.subtitle
            explicit.onItemChanged(item.title)
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
                BindingsAdapter.loadBigAlbumImage(holder.imageView!!, mediaId)
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
                    seeMore?.isVisible = item.visible
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

    override fun canInteractWithViewHolder(viewHolder: RecyclerView.ViewHolder): Boolean {
        val viewType = viewHolder.itemViewType
        return viewType == R.layout.item_detail_song ||
                viewType == R.layout.item_detail_song_with_drag_handle ||
                viewType == R.layout.item_detail_song_with_track ||
                viewType == R.layout.item_detail_song_with_track_and_image
    }

    override fun onClearView() {
        onSwipeDone()
    }

    override fun onMoved(from: Int, to: Int) {
        val realFrom = from - headers
        val realTo = to - headers
        swap(from, to)
        onItemMove(realFrom, realTo)
    }

    override fun onSwipedRight(viewHolder: RecyclerView.ViewHolder) {
        val position = viewHolder.adapterPosition
        val item = getItem(position)
        removeAt(position)
        onSwipeRight(item)
    }

    override fun afterSwipeRight(viewHolder: RecyclerView.ViewHolder) {

    }

    override fun onSwipedLeft(viewHolder: RecyclerView.ViewHolder) {
        val item = getItem(viewHolder.adapterPosition)
        onSwipeLeft(item.mediaId)
    }

    override fun afterSwipeLeft(viewHolder: RecyclerView.ViewHolder) {
        notifyItemChanged(viewHolder.adapterPosition)
    }

    override fun contentViewFor(holder: RecyclerView.ViewHolder): View {
        return holder.itemView.content
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