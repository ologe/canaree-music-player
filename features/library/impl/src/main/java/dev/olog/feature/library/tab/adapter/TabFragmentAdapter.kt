package dev.olog.feature.library.tab.adapter

import androidx.recyclerview.widget.RecyclerView
import dev.olog.core.MediaUri
import dev.olog.feature.base.adapter.*
import dev.olog.feature.base.adapter.media.MediaListItem
import dev.olog.feature.base.adapter.media.ShuffleAdapter

class TabFragmentAdapter(
    adapters: List<RecyclerView.Adapter<RecyclerView.ViewHolder>>
) : CustomConcatAdapter(
    concatConfig(),
    adapters
) {

    fun submitMain(category: MediaUri.Category, data: List<MediaListItem>) {
        val adapter = firstByType<TabFragmentMediaAdapter>()
        adapter.submitList(data)

        when (category) {
            MediaUri.Category.Track -> findByType<ShuffleAdapter>()?.show = data.isNotEmpty()
            MediaUri.Category.Playlist -> requireHeaderOf(adapter).show = data.isNotEmpty()
            else -> {}
        }
    }

    fun submitAutoPlaylist(data: List<MediaListItem>) {
        val adapter = firstByType<TabFragmentAutoPlaylistAdapter>()
        adapter.submitList(data)
    }

    fun submitRecent(added: List<MediaListItem>, played: List<MediaListItem>) {
        val addedAdapter = firstByType<TabFragmentRecentlyAddedAdapter>()
        addedAdapter.submitList(added)

        val playedAdapter = firstByType<TabFragmentRecentlyPlayedAdapter>()
        playedAdapter.submitList(played)

        val adapter = requireHeaderOf(firstByType<TabFragmentMediaAdapter>())
        adapter.show = added.isNotEmpty() || played.isNotEmpty()
    }

    fun indexOf(predicate: (MediaListItem) -> Boolean): Int {
        return -1 // TODO implement correctly
//        val above = delegate.adapters.dropLast(1).sumOf { it.itemCount }
//        return above + (delegate.adapters.last() as MediaListItemAdapter).indexOf(predicate)
    }

}