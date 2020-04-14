package dev.olog.feature.library.home

import androidx.recyclerview.widget.RecyclerView
import dev.olog.core.extensions.findActivity
import dev.olog.domain.MediaId
import dev.olog.domain.MediaIdCategory
import dev.olog.domain.entity.AutoPlaylist
import dev.olog.feature.library.R
import dev.olog.feature.presentation.base.SetupNestedList
import dev.olog.feature.presentation.base.activity.HasBottomNavigation
import dev.olog.feature.presentation.base.adapter.DataBoundViewHolder
import dev.olog.feature.presentation.base.adapter.DiffCallbackDisplayableItem
import dev.olog.feature.presentation.base.adapter.ObservableAdapter
import dev.olog.feature.presentation.base.adapter.setOnClickListener
import dev.olog.feature.presentation.base.model.DisplayableHeader
import dev.olog.feature.presentation.base.model.DisplayableItem
import dev.olog.navigation.Navigator
import dev.olog.navigation.screens.BottomNavigationPage
import kotlinx.android.synthetic.main.item_tab_header.view.*

internal class HomeFragmentAdapter(
    private val navigator: Navigator,
    private val bottomNavigator: HasBottomNavigation,
    private val setupNestedList: SetupNestedList
) : ObservableAdapter<DisplayableItem>(DiffCallbackDisplayableItem) {

    override fun initViewHolderListeners(viewHolder: DataBoundViewHolder, viewType: Int) {
        when (viewType) {
            R.layout.item_tab_last_played_album_horizontal_list,
            R.layout.item_tab_new_album_horizontal_list -> {
                val view = viewHolder.itemView as RecyclerView
                setupNestedList.setupNestedList(viewType, view)
            }
            R.layout.item_home_header -> {
                viewHolder.setOnClickListener(R.id.tracks,this) { _, _, _ ->
                    bottomNavigator.navigate(BottomNavigationPage.LIBRARY)
                }
                viewHolder.setOnClickListener(R.id.playlists,this) { _, _, _ ->
                    bottomNavigator.navigate(BottomNavigationPage.PLAYLISTS)
                }
                viewHolder.setOnClickListener(R.id.albums,this) { _, _, view ->
                    navigator.toAlbums(view.findActivity())
                }
                viewHolder.setOnClickListener(R.id.artists,this) { _, _, view ->
                    navigator.toArtists(view.findActivity())
                }
                viewHolder.setOnClickListener(R.id.folders,this) { _, _, view ->
                    navigator.toFolders(view.findActivity())
                }
                viewHolder.setOnClickListener(R.id.genres,this) { _, _, view ->
                    navigator.toGenres(view.findActivity())
                }
                viewHolder.setOnClickListener(R.id.favorites,this) { _, _, view ->
                    val mediaId = MediaId.Category(MediaIdCategory.PLAYLISTS, AutoPlaylist.FAVORITE.id.toString())
                    navigator.toDetailFragment(view.findActivity(), mediaId, view)
                }
            }
        }
        // TODO elevate
    }

    override fun bind(holder: DataBoundViewHolder, item: DisplayableItem, position: Int) {
        when (item) {
            is DisplayableHeader -> bindHeader(holder, item)
        }
    }

    private fun bindHeader(holder: DataBoundViewHolder, item: DisplayableHeader){
        if (holder.itemViewType == R.layout.item_tab_header){
            holder.itemView.title.text = item.title
        }
    }

}