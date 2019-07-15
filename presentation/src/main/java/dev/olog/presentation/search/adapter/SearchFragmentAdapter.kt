package dev.olog.presentation.search.adapter

import androidx.databinding.ViewDataBinding
import androidx.lifecycle.Lifecycle
import androidx.recyclerview.widget.RecyclerView
import dev.olog.media.MediaProvider
import dev.olog.presentation.BR
import dev.olog.presentation.R
import dev.olog.presentation.base.adapter.*
import dev.olog.presentation.base.drag.TouchableAdapter
import dev.olog.presentation.interfaces.SetupNestedList
import dev.olog.presentation.model.DisplayableItem
import dev.olog.presentation.model.DisplayableTrack
import dev.olog.presentation.navigator.Navigator
import dev.olog.presentation.search.SearchFragmentViewModel

class SearchFragmentAdapter(
    lifecycle: Lifecycle,
    private val setupNestedList: SetupNestedList,
    private val mediaProvider: MediaProvider,
    private val navigator: Navigator,
    private val viewModel: SearchFragmentViewModel

) : ObservableAdapter<DisplayableItem>(
    lifecycle,
    DiffCallbackDisplayableItem
), TouchableAdapter {

    override fun initViewHolderListeners(viewHolder: DataBoundViewHolder, viewType: Int) {
        when (viewType) {
            R.layout.item_search_list_albums,
            R.layout.item_search_list_artists,
            R.layout.item_search_list_folder,
            R.layout.item_search_list_playlists,
            R.layout.item_search_list_genre -> {
                val list = viewHolder.itemView as RecyclerView
                setupNestedList.setupNestedList(viewType, list)
            }
            R.layout.item_search_song -> {
                viewHolder.setOnClickListener(this) { item, _, _ ->
                    mediaProvider.playFromMediaId(item.mediaId)
                    viewModel.insertToRecent(item.mediaId)

                }
                viewHolder.setOnLongClickListener(this) { item, _, _ ->
                    navigator.toDialog(item.mediaId, viewHolder.itemView)
                }
                viewHolder.setOnClickListener(R.id.more, this) { item, _, view ->
                    navigator.toDialog(item.mediaId, view)
                }

            }
            R.layout.item_search_clear_recent -> {
                viewHolder.setOnClickListener(this) { _, _, _ ->
                    viewModel.clearRecentSearches()
                }
            }
            R.layout.item_search_recent,
            R.layout.item_search_recent_album,
            R.layout.item_search_recent_artist -> {
                viewHolder.setOnClickListener(this) { item, _, _ ->
                    if (item is DisplayableTrack) {
                        mediaProvider.playFromMediaId(item.mediaId)
                    } else {
                        navigator.toDetailFragment(item.mediaId)
                    }
                    viewModel.insertToRecent(item.mediaId)
                }
                viewHolder.setOnLongClickListener(this) { item, _, _ ->
                    navigator.toDialog(item.mediaId, viewHolder.itemView)
                }
                viewHolder.setOnClickListener(R.id.clear, this) { item, _, _ ->
                    viewModel.deleteFromRecent(item.mediaId)
                }
            }
        }
        when (viewType) {
            R.layout.item_search_song,
            R.layout.item_search_recent,
            R.layout.item_search_recent_album,
            R.layout.item_search_recent_artist -> viewHolder.elevateSongOnTouch()
        }
    }

    override fun bind(binding: ViewDataBinding, item: DisplayableItem, position: Int) {
        binding.setVariable(BR.item, item)
    }

    override fun canInteractWithViewHolder(viewType: Int): Boolean {
        return viewType == R.layout.item_search_song ||
                viewType == R.layout.item_search_recent
    }

    override fun onSwipedLeft(viewHolder: RecyclerView.ViewHolder) {
        val position = viewHolder.adapterPosition
        val item = getItem(position)!!
        mediaProvider.addToPlayNext(item.mediaId)
    }

    override fun afterSwipeLeft(viewHolder: RecyclerView.ViewHolder) {
        notifyItemChanged(viewHolder.adapterPosition)
    }

}