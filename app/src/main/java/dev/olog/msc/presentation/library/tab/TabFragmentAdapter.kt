package dev.olog.msc.presentation.library.tab

import android.view.View
import androidx.databinding.ViewDataBinding
import androidx.lifecycle.Lifecycle
import androidx.recyclerview.widget.LinearLayoutManager
import dagger.Lazy
import dev.olog.msc.BR
import dev.olog.msc.R
import dev.olog.msc.dagger.qualifier.FragmentLifecycle
import dev.olog.msc.dagger.scope.PerFragment
import dev.olog.msc.presentation.base.adapter.AbsAdapter
import dev.olog.msc.presentation.base.adapter.DataBoundViewHolder
import dev.olog.msc.presentation.base.music.service.MediaProvider
import dev.olog.msc.presentation.detail.sort.DetailSort
import dev.olog.msc.presentation.model.DisplayableItem
import dev.olog.msc.presentation.navigator.Navigator
import dev.olog.msc.utils.MediaId
import dev.olog.msc.utils.k.extension.elevateAlbumOnTouch
import dev.olog.msc.utils.k.extension.elevateSongOnTouch
import dev.olog.msc.utils.k.extension.setOnClickListener
import dev.olog.msc.utils.k.extension.setOnLongClickListener
import javax.inject.Inject

@PerFragment
class TabFragmentAdapter @Inject constructor(
        @FragmentLifecycle lifecycle: Lifecycle,
        private val navigator: Navigator,
        private val mediaProvider: MediaProvider,
        private val lastPlayedArtistsAdapter: Lazy<TabFragmentLastPlayedArtistsAdapter>,
        private val lastPlayedAlbumsAdapter: Lazy<TabFragmentLastPlayedAlbumsAdapter>,
        private val newAlbumsAdapter : Lazy<TabFragmentNewAlbumsAdapter>,
        private val newArtistsAdapter : Lazy<TabFragmentNewArtistsAdapter>,
        private val viewModel: TabFragmentViewModel

) : AbsAdapter<DisplayableItem>(lifecycle) {

    override fun initViewHolderListeners(viewHolder: DataBoundViewHolder, viewType: Int) {
        when (viewType) {
            R.layout.item_tab_shuffle -> {
                viewHolder.setOnClickListener(controller) { _, _, _ ->
                    mediaProvider.shuffle(MediaId.shuffleAllId())
                }
            }
            R.layout.item_tab_album,
            R.layout.item_tab_artist,
            R.layout.item_tab_auto_playlist,
            R.layout.item_tab_song,
            R.layout.item_tab_podcast -> {
                viewHolder.setOnClickListener(controller) { item, _, _ ->
                    if (item.isPlayable && !item.mediaId.isPodcast){
                        val sort = viewModel.getAllTracksSortOrder()
                        mediaProvider.playFromMediaId(item.mediaId, DetailSort(sort.type, sort.arranging))
                    } else if (item.isPlayable){
                        mediaProvider.playFromMediaId(item.mediaId)
                    } else {
                        navigator.toDetailFragment(item.mediaId)
                    }
                }
                viewHolder.setOnLongClickListener(controller) { item, _, _ ->
                    navigator.toDialog(item, viewHolder.itemView)
                }
                viewHolder.setOnClickListener(R.id.more, controller) { item, _, view ->
                    navigator.toDialog(item, view)
                }
            }
            R.layout.item_tab_last_played_album_horizontal_list -> {
                val view = viewHolder.itemView as androidx.recyclerview.widget.RecyclerView
                setupHorizontalList(view, lastPlayedAlbumsAdapter.get())
            }
            R.layout.item_tab_last_played_artist_horizontal_list -> {
                val view = viewHolder.itemView as androidx.recyclerview.widget.RecyclerView
                setupHorizontalList(view, lastPlayedArtistsAdapter.get())
            }
            R.layout.item_tab_new_album_horizontal_list-> {
                val view = viewHolder.itemView as androidx.recyclerview.widget.RecyclerView
                setupHorizontalList(view, newAlbumsAdapter.get())
            }
            R.layout.item_tab_new_artist_horizontal_list-> {
                val view = viewHolder.itemView as androidx.recyclerview.widget.RecyclerView
                setupHorizontalList(view, newArtistsAdapter.get())
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

    private fun setupHorizontalList(list: androidx.recyclerview.widget.RecyclerView, adapter: AbsAdapter<*>){
        val layoutManager = androidx.recyclerview.widget.LinearLayoutManager(list.context, androidx.recyclerview.widget.LinearLayoutManager.HORIZONTAL, false)
        list.layoutManager = layoutManager
        list.adapter = adapter
    }

    override fun bind(binding: ViewDataBinding, item: DisplayableItem, position: Int) {
        binding.setVariable(BR.item, item)
    }

    override fun onViewRecycled(holder: DataBoundViewHolder) {
        holder.itemView.findViewById<View>(R.id.cover)?.let {
            GlideApp.with(holder.itemView).clear(it)
        }
        super.onViewRecycled(holder)
    }
}
