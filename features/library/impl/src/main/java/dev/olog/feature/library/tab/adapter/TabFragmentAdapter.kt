package dev.olog.feature.library.tab.adapter

import androidx.recyclerview.widget.RecyclerView
import dev.olog.core.MediaUri
import dev.olog.feature.base.adapter.*
import dev.olog.feature.base.adapter.media.MediaListItem

class TabFragmentAdapter(
    adapters: List<RecyclerView.Adapter<RecyclerView.ViewHolder>>
) : CustomConcatAdapter(
    concatConfig(isolateViewTypes = false),
    adapters
) {

    fun submitMain(category: MediaUri.Category, data: List<MediaListItem>) {
        val adapter = delegate.firstByType<MediaListItemAdapter>()
        adapter.submitList(data)

        when (category) {
            MediaUri.Category.Track -> delegate.findByType<ShuffleAdapter>()?.show = data.isNotEmpty()
            MediaUri.Category.Playlist -> delegate.findByType<TextHeaderAdapter>()?.show = data.isNotEmpty()
            else -> {}
        }
    }

    fun submitAutoPlaylist(data: List<MediaListItem>) {
        val adapter = delegate.firstByType<TabFragmentAutoPlaylistAdapter>()
        adapter.submitList(data)
    }

    fun submitRecent(added: List<MediaListItem>, played: List<MediaListItem>) {
        val addedAdapter = delegate.firstByType<TabFragmentRecentlyAddedAdapter>()
        addedAdapter.submitList(added)

        val playedAdapter = delegate.firstByType<TabFragmentRecentlyPlayedAdapter>()
        playedAdapter.submitList(played)

        val adapter = delegate.firstByType<MediaListItemAdapter>()
        val allHeaderAdapter = delegate.requireHeaderOf(adapter)
        allHeaderAdapter.show = added.isNotEmpty() || played.isNotEmpty()
    }

    fun indexOf(predicate: (MediaListItem) -> Boolean): Int {
        return -1 // TODO implement correctly
//        val above = delegate.adapters.dropLast(1).sumOf { it.itemCount }
//        return above + (delegate.adapters.last() as MediaListItemAdapter).indexOf(predicate)
    }

}