package dev.olog.msc.presentation.library.tab.adapters

import androidx.databinding.ViewDataBinding
import androidx.lifecycle.Lifecycle
import androidx.recyclerview.widget.RecyclerView
import dev.olog.core.MediaId
import dev.olog.msc.BR
import dev.olog.msc.R
import dev.olog.msc.presentation.base.music.service.MediaProvider
import dev.olog.msc.presentation.detail.sort.DetailSort
import dev.olog.msc.presentation.library.tab.TabFragmentViewModel
import dev.olog.presentation.navigator.Navigator
import dev.olog.msc.utils.k.extension.elevateAlbumOnTouch
import dev.olog.msc.utils.k.extension.elevateSongOnTouch
import dev.olog.presentation.base.DataBoundViewHolder
import dev.olog.presentation.base.ObservableAdapter
import dev.olog.presentation.base.setOnClickListener
import dev.olog.presentation.base.setOnLongClickListener
import dev.olog.presentation.interfaces.SetupNestedList
import dev.olog.presentation.model.DisplayableItem

internal class TabFragmentAdapter (
    lifecycle: Lifecycle,
    private val navigator: Navigator,
    private val mediaProvider: MediaProvider,
    private val viewModel: TabFragmentViewModel,
    private val setupNestedList: SetupNestedList

) : ObservableAdapter<DisplayableItem>(lifecycle) {

    override fun initViewHolderListeners(viewHolder: DataBoundViewHolder, viewType: Int) {
        when (viewType) {
            R.layout.item_tab_shuffle -> {
                viewHolder.setOnClickListener(this) { _, _, _ ->
                    mediaProvider.shuffle(MediaId.shuffleAllId())
                }
            }
            R.layout.item_tab_album,
            R.layout.item_tab_artist,
            R.layout.item_tab_auto_playlist,
            R.layout.item_tab_song,
            R.layout.item_tab_podcast -> {
                viewHolder.setOnClickListener(this) { item, _, _ ->
                    if (item.isPlayable && !item.mediaId.isPodcast){
                        val sort = viewModel.getAllTracksSortOrder()
                        mediaProvider.playFromMediaId(item.mediaId, DetailSort(sort.type, sort.arranging))
                    } else if (item.isPlayable){
                        mediaProvider.playFromMediaId(item.mediaId)
                    } else {
                        navigator.toDetailFragment(item.mediaId)
                    }
                }
                viewHolder.setOnLongClickListener(this) { item, _, _ ->
                    navigator.toDialog(item, viewHolder.itemView)
                }
                viewHolder.setOnClickListener(R.id.more, this) { item, _, view ->
                    navigator.toDialog(item, view)
                }
            }
            R.layout.item_tab_last_played_album_horizontal_list,
            R.layout.item_tab_last_played_artist_horizontal_list,
            R.layout.item_tab_new_album_horizontal_list,
            R.layout.item_tab_new_artist_horizontal_list -> {
                val view = viewHolder.itemView as RecyclerView
                setupNestedList.setupNestedList(viewType, view)
            }
        }

        when (viewType){
            R.layout.item_tab_album,
            R.layout.item_tab_artist,
            R.layout.item_tab_auto_playlist -> viewHolder.elevateAlbumOnTouch()
            R.layout.item_tab_song,
            R.layout.item_tab_podcast -> viewHolder.elevateSongOnTouch()
        }
    }

    override fun bind(binding: ViewDataBinding, item: DisplayableItem, position: Int) {
        binding.setVariable(BR.item, item)
    }
}
