package dev.olog.feature.library.tab

import android.content.Context
import dagger.hilt.android.qualifiers.ApplicationContext
import dev.olog.feature.library.R
import dev.olog.feature.library.tab.model.TabFragmentModel
import javax.inject.Inject

class TabFragmentHeaders @Inject constructor(
    @ApplicationContext context: Context,
) {

    private val resources = context.resources

    val allPlaylistHeader: TabFragmentModel = TabFragmentModel.Header(
        title = resources.getString(R.string.tab_all_playlists)
    )

    val autoPlaylistHeader: TabFragmentModel = TabFragmentModel.Header(
        title = resources.getString(R.string.tab_auto_playlists)
    )

    val shuffleHeader: TabFragmentModel = TabFragmentModel.Shuffle

    val allAlbumsHeader: TabFragmentModel = TabFragmentModel.Header(
        title = resources.getString(R.string.tab_all_albums)
    )

    val allArtistsHeader: TabFragmentModel = TabFragmentModel.Header(
        title = resources.getString(R.string.tab_all_artists)
    )

    val lastPlayedAlbumHeaders: List<TabFragmentModel> = listOf(
        TabFragmentModel.Header(
            title = resources.getString(R.string.tab_recent_played)
        ),
        TabFragmentModel.RecentlyPlayedAlbumsList
    )

    val lastPlayedArtistHeaders: List<TabFragmentModel> = listOf(
        TabFragmentModel.Header(
            title = resources.getString(R.string.tab_recent_played)
        ),
        TabFragmentModel.RecentlyPlayedArtistList
    )

    val recentlyAddedAlbumsHeaders: List<TabFragmentModel> = listOf(
        TabFragmentModel.Header(
            title = resources.getStringArray(R.array.tab_recently_added)[0]
        ),
        TabFragmentModel.RecentlyAddedAlbumsList
    )

    val recentlyAddedArtistsHeaders: List<TabFragmentModel> = listOf(
        TabFragmentModel.Header(
            title = resources.getStringArray(R.array.tab_recently_added)[1]
        ),
        TabFragmentModel.RecentlyAddedArtistList
    )

}