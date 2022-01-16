package dev.olog.feature.library.tab.adapter

import android.view.View
import androidx.fragment.app.FragmentActivity
import androidx.recyclerview.widget.RecyclerView
import dev.olog.core.MediaStoreType
import dev.olog.core.MediaUri
import dev.olog.feature.base.adapter.MediaListItemAdapter
import dev.olog.feature.base.adapter.ShuffleAdapter
import dev.olog.feature.base.adapter.TextHeaderAdapter
import dev.olog.feature.base.adapter.media.ItemDirection
import dev.olog.feature.detail.FeatureDetailNavigator
import dev.olog.feature.dialogs.FeatureDialogsNavigator
import dev.olog.media.MediaProvider
import dev.olog.shared.exhaustive
import localization.R
import javax.inject.Inject

class TabFragmentAdapterFactory @Inject constructor(
    private val activity: FragmentActivity,
    private val mediaProvider: MediaProvider,
    private val detailNavigator: FeatureDetailNavigator,
    private val dialogNavigator: FeatureDialogsNavigator,
) {

    fun create(
        category: MediaUri.Category,
        type: MediaStoreType,
    ): TabFragmentAdapter {
        val concatAdapter = when (category) {
            MediaUri.Category.Folder -> folderAdapter()
            MediaUri.Category.Playlist -> playlistAdapter()
            MediaUri.Category.Track -> trackAdapter(type)
            MediaUri.Category.Author -> authorAdapter(type)
            MediaUri.Category.Collection -> collectionAdapter(type)
            MediaUri.Category.Genre -> genreAdapter()
        }
        return TabFragmentAdapter(concatAdapter)
    }

    private fun folderAdapter(): List<RecyclerView.Adapter<RecyclerView.ViewHolder>> {
        return listOf(
            MediaListItemAdapter(
                onItemClick = ::onItemClick,
                onItemLongClick = ::onItemLongClick,
            )
        )
    }

    private fun playlistAdapter(): List<RecyclerView.Adapter<RecyclerView.ViewHolder>> {
        return listOf(
            TabFragmentAutoPlaylistAdapter(
                context = activity,
                onItemClick = ::onItemClick,
                onItemLongClick = ::onItemLongClick,
            ),
            TextHeaderAdapter(activity.getString(R.string.tab_all_playlists)),
            MediaListItemAdapter(
                onItemClick = ::onItemClick,
                onItemLongClick = ::onItemLongClick,
                direction = ItemDirection.Horizontal(activity),
            )
        )
    }
    
    private fun trackAdapter(type: MediaStoreType): List<RecyclerView.Adapter<RecyclerView.ViewHolder>> {
        val result = mutableListOf<RecyclerView.Adapter<RecyclerView.ViewHolder>>()

        when (type) {
            MediaStoreType.Song -> {
                result += ShuffleAdapter { mediaProvider.playFromMediaId(MediaUri.ShuffleAll) }
                result += MediaListItemAdapter(
                    onItemClick = ::onItemClick,
                    onItemLongClick = ::onItemLongClick,
                )
            }
            MediaStoreType.Podcast -> {
                result += MediaListItemAdapter(
                    onItemClick = ::onItemClick,
                    onItemLongClick = ::onItemLongClick,
                )
            }
        }.exhaustive

        return result
    }

    private fun authorAdapter(type: MediaStoreType): List<RecyclerView.Adapter<RecyclerView.ViewHolder>> {
        val result = mutableListOf<RecyclerView.Adapter<RecyclerView.ViewHolder>>()

        result += TabFragmentRecentlyAddedAdapter(
            context = activity,
            onItemClick = ::onItemClick,
            onItemLongClick = ::onItemLongClick,
        )

        result += TabFragmentRecentlyPlayedAdapter(
            context = activity,
            onItemClick = ::onItemClick,
            onItemLongClick = ::onItemLongClick,
        )

        result += TextHeaderAdapter(activity.getString(R.string.tab_all_artists)) // TODO podcast localisation
        result += MediaListItemAdapter(
            onItemClick = ::onItemClick,
            onItemLongClick = ::onItemLongClick,
        )

        return result
    }

    private fun collectionAdapter(type: MediaStoreType): List<RecyclerView.Adapter<RecyclerView.ViewHolder>> {
        val result = mutableListOf<RecyclerView.Adapter<RecyclerView.ViewHolder>>()

        result += TabFragmentRecentlyAddedAdapter(
            context = activity,
            onItemClick = ::onItemClick,
            onItemLongClick = ::onItemLongClick,
        )

        result += TabFragmentRecentlyPlayedAdapter(
            context = activity,
            onItemClick = ::onItemClick,
            onItemLongClick = ::onItemLongClick,
        )

        result += TextHeaderAdapter(activity.getString(R.string.tab_all_albums)) // TODO podcast localisation
        result += MediaListItemAdapter(
            onItemClick = ::onItemClick,
            onItemLongClick = ::onItemLongClick,
        )

        return result
    }

    private fun genreAdapter(): List<RecyclerView.Adapter<RecyclerView.ViewHolder>> {
        return listOf(
            MediaListItemAdapter(
                onItemClick = ::onItemClick,
                onItemLongClick = ::onItemLongClick,
            )
        )
    }

    private fun onItemClick(uri: MediaUri) {
        if (uri.category == MediaUri.Category.Track) {
            mediaProvider.playFromMediaId(uri)
        } else {
            detailNavigator.toDetailFragment(activity, uri)
        }
    }

    private fun onItemLongClick(uri: MediaUri, view: View) {
        dialogNavigator.toDialog(activity, uri, view)
    }

}