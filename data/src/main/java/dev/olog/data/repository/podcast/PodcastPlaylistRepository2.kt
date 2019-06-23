package dev.olog.data.repository.podcast

import android.content.Context
import dev.olog.core.PlaylistConstants
import dev.olog.core.dagger.ApplicationContext
import dev.olog.core.entity.track.Playlist
import dev.olog.core.entity.track.Song
import dev.olog.core.gateway.Id
import dev.olog.core.gateway.PodcastPlaylistGateway2
import dev.olog.data.R
import dev.olog.data.db.dao.AppDatabase
import dev.olog.data.mapper.toDomain
import dev.olog.shared.assertBackground
import dev.olog.shared.assertBackgroundThread
import dev.olog.shared.mapListItem
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.reactive.flow.asFlow
import javax.inject.Inject

internal class PodcastPlaylistRepository2 @Inject constructor(
    @ApplicationContext context: Context,
    appDatabase: AppDatabase
) : PodcastPlaylistGateway2 {

    private val autoPlaylistTitles = context.resources.getStringArray(R.array.common_auto_playlists)
    private val podcastPlaylistDao = appDatabase.podcastPlaylistDao()

    override fun getAll(): List<Playlist> {
        assertBackgroundThread()
        val result = podcastPlaylistDao.getAllPlaylists()
        return result.map { it.toDomain() }
    }

    override fun observeAll(): Flow<List<Playlist>> {
        assertBackgroundThread()
        return podcastPlaylistDao.observeAllPlaylists()
            .distinctUntilChanged()
            .asFlow()
            .mapListItem { it.toDomain() }
            .assertBackground()
    }

    override fun getByParam(param: Id): Playlist? {
        assertBackgroundThread()
        return podcastPlaylistDao.getPlaylistById(param)?.toDomain()

    }

    override fun observeByParam(param: Id): Flow<Playlist?> {
        return podcastPlaylistDao.observePlaylistById(param)
            .distinctUntilChanged()
            .map { it.toDomain() }
            .asFlow()
            .assertBackground()
    }

    override fun getTrackListByParam(param: Id): List<Song> {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun observeTrackListByParam(param: Id): Flow<List<Song>> {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun getAllAutoPlaylists(): List<Playlist> {
        assertBackgroundThread()
        return listOf(
            createAutoPlaylist(PlaylistConstants.LAST_ADDED_ID, autoPlaylistTitles[0], 0),
            createAutoPlaylist(PlaylistConstants.FAVORITE_LIST_ID, autoPlaylistTitles[1], 0),
            createAutoPlaylist(PlaylistConstants.HISTORY_LIST_ID, autoPlaylistTitles[2], 0)
        )
    }

    private fun createAutoPlaylist(id: Long, title: String, listSize: Int) : Playlist {
        return Playlist(id, title, listSize, true)
    }
}