package dev.olog.presentation.tab.adapter

import androidx.databinding.ViewDataBinding
import androidx.lifecycle.Lifecycle
import androidx.recyclerview.widget.RecyclerView
import dev.olog.core.MediaId
import dev.olog.media.MediaProvider
import dev.olog.presentation.BR
import dev.olog.presentation.R
import dev.olog.presentation.base.adapter.*
import dev.olog.presentation.interfaces.SetupNestedList
import dev.olog.presentation.model.DisplayableItem
import dev.olog.presentation.navigator.Navigator
import dev.olog.presentation.tab.TabFragmentViewModel

internal class TabFragmentAdapter(
    lifecycle: Lifecycle,
    private val navigator: Navigator,
    private val mediaProvider: MediaProvider,
    private val viewModel: TabFragmentViewModel,
    private val setupNestedList: SetupNestedList

) : ObservableAdapter<DisplayableItem>(
    lifecycle,
    DiffCallbackDisplayableItem
) {

    override fun initViewHolderListeners(viewHolder: DataBoundViewHolder, viewType: Int) {
        when (viewType) {
            R.layout.item_tab_shuffle -> {
                viewHolder.setOnClickListener(this) { _, _, _ ->
                    mediaProvider.shuffle(MediaId.shuffleId(), null)
                }
            }
            R.layout.item_tab_song,
            R.layout.item_tab_podcast -> {
                viewHolder.setOnClickListener(this) { item, _, _ ->
                    val sort = viewModel.getAllTracksSortOrder(item.mediaId)
                    mediaProvider.playFromMediaId(item.mediaId, null, sort)
                }
                viewHolder.setOnLongClickListener(this) { item, _, _ ->
                    navigator.toDialog(item.mediaId, viewHolder.itemView)
                }
                viewHolder.elevateSongOnTouch()
            }
            R.layout.item_tab_album,
            R.layout.item_tab_artist,
            R.layout.item_tab_auto_playlist -> {
                viewHolder.setOnClickListener(this) { item, _, _ ->
                    navigator.toDetailFragment(item.mediaId)
                }
                viewHolder.setOnLongClickListener(this) { item, _, _ ->
                    navigator.toDialog(item.mediaId, viewHolder.itemView)
                }
                viewHolder.elevateAlbumOnTouch()
            }
            R.layout.item_tab_last_played_album_horizontal_list,
            R.layout.item_tab_last_played_artist_horizontal_list,
            R.layout.item_tab_new_album_horizontal_list,
            R.layout.item_tab_new_artist_horizontal_list -> {
                val view = viewHolder.itemView as RecyclerView
                setupNestedList.setupNestedList(viewType, view)
            }
        }
    }

    override fun bind(binding: ViewDataBinding, item: DisplayableItem, position: Int) {
        binding.setVariable(BR.item, item)
    }
}
