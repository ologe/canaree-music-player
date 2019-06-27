package dev.olog.msc.presentation.search.adapter

import androidx.databinding.ViewDataBinding
import androidx.lifecycle.Lifecycle
import androidx.recyclerview.widget.RecyclerView
import dev.olog.media.MediaProvider
import dev.olog.msc.BR
import dev.olog.msc.R
import dev.olog.msc.presentation.search.SearchFragmentViewModel
import dev.olog.msc.utils.k.extension.elevateSongOnTouch
import dev.olog.presentation.base.*
import dev.olog.presentation.interfaces.SetupNestedList
import dev.olog.presentation.model.DisplayableItem
import dev.olog.presentation.navigator.Navigator

class SearchFragmentAdapter (
    lifecycle: Lifecycle,
    private val setupNestedList: SetupNestedList,
    private val mediaProvider: MediaProvider,
    private val navigator: Navigator,
    private val viewModel: SearchFragmentViewModel

) : ObservableAdapter<DisplayableItem>(lifecycle, DiffCallbackDisplayableItem) {

    override fun initViewHolderListeners(viewHolder: DataBoundViewHolder, viewType: Int) {
        when (viewType){
            R.layout.item_search_albums_horizontal_list,
            R.layout.item_search_artists_horizontal_list,
            R.layout.item_search_folder_horizontal_list,
            R.layout.item_search_playlists_horizontal_list,
            R.layout.item_search_genre_horizontal_list -> {
                val list = viewHolder.itemView as RecyclerView
                setupNestedList.setupNestedList(viewType, list)
            }
            R.layout.item_search_song -> {
                viewHolder.setOnClickListener(this) { item, _, _ ->
                    mediaProvider.playFromMediaId(item.mediaId)
                    viewModel.insertToRecent(item.mediaId)

                }
                viewHolder.setOnLongClickListener(this) { item ,_, _ ->
                    navigator.toDialog(item, viewHolder.itemView)
                }
                viewHolder.setOnClickListener(R.id.more, this) { item, _, view ->
                    navigator.toDialog(item, view)
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
                viewHolder.setOnClickListener(this) { item, _, _  ->
                    if (item.isPlayable){
                        mediaProvider.playFromMediaId(item.mediaId)
                    } else {
                        navigator.toDetailFragment(item.mediaId)
                    }
                    viewModel.insertToRecent(item.mediaId)
                }
                viewHolder.setOnLongClickListener(this) { item ,_, _ ->
                    navigator.toDialog(item, viewHolder.itemView)
                }
                viewHolder.setOnClickListener(R.id.clear, this) { item, _, _ ->
                    viewModel.deleteFromRecent(item.mediaId)
                }
            }
        }
        when (viewType){
            R.layout.item_search_song,
            R.layout.item_search_recent,
            R.layout.item_search_recent_album,
            R.layout.item_search_recent_artist -> viewHolder.elevateSongOnTouch()
        }
    }

    override fun bind(binding: ViewDataBinding, item: DisplayableItem, position: Int) {
        binding.setVariable(BR.item, item)
    }

//    override fun canInteractWithViewHolder(viewType: Int): Boolean? { TODO
//        return viewType == R.layout.item_search_song ||
//                viewType == R.layout.item_search_recent
//    }
//
//    override val onSwipeLeftAction = { position: Int ->
//        controller.getItem(position)?.let { mediaProvider.addToPlayNext(it.mediaId) } ?: Any()
//    }

}