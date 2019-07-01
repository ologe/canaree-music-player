package dev.olog.data.repository.track

import android.content.Context
import android.database.Cursor
import android.provider.MediaStore
import android.util.Log
import dev.olog.core.MediaId
import dev.olog.core.dagger.ApplicationContext
import dev.olog.core.entity.AutoPlaylist
import dev.olog.core.entity.id
import dev.olog.core.entity.track.Artist
import dev.olog.core.entity.track.Playlist
import dev.olog.core.entity.track.Song
import dev.olog.core.gateway.Id
import dev.olog.core.gateway.PlaylistGateway
import dev.olog.core.gateway.PlaylistOperations
import dev.olog.core.gateway.SongGateway
import dev.olog.core.prefs.BlacklistPreferences
import dev.olog.core.prefs.SortPreferences
import dev.olog.data.R
import dev.olog.data.db.dao.AppDatabase
import dev.olog.data.db.entities.PlaylistMostPlayedEntity
import dev.olog.data.mapper.toArtist
import dev.olog.data.mapper.toPlaylist
import dev.olog.data.mapper.toSong
import dev.olog.data.queries.PlaylistQueries
import dev.olog.data.repository.BaseRepository
import dev.olog.data.repository.ContentUri
import dev.olog.data.repository.PlaylistRepositoryHelper
import dev.olog.data.utils.queryAll
import dev.olog.data.utils.queryCountRow
import dev.olog.shared.extensions.assertBackground
import dev.olog.shared.utils.assertBackgroundThread
import kotlinx.coroutines.flow.*
import javax.inject.Inject

internal class PlaylistRepository @Inject constructor(
    @ApplicationContext context: Context,
    sortPrefs: SortPreferences,
    blacklistPrefs: BlacklistPreferences,
    appDatabase: AppDatabase,
    private val songGateway2: SongGateway,
    private val helper: PlaylistRepositoryHelper
) : BaseRepository<Playlist, Id>(context), PlaylistGateway, PlaylistOperations by helper {

    private val autoPlaylistTitles = context.resources.getStringArray(R.array.common_auto_playlists)
    private val queries = PlaylistQueries(contentResolver, blacklistPrefs, sortPrefs)
    private val mostPlayedDao = appDatabase.playlistMostPlayedDao()

    override fun registerMainContentUri(): ContentUri {
        return ContentUri(MediaStore.Audio.Playlists.EXTERNAL_CONTENT_URI, true)
    }

    override fun queryAll(): List<Playlist> {
        assertBackgroundThread()
        val cursor = queries.getAll()
        val playlists = contentResolver.queryAll(cursor) { it.toPlaylist() }
        return playlists.map { playlist ->
            // playerAppearance the size for every playlist
            val sizeQueryCursor = queries.countPlaylistSize(playlist.id)
            val sizeQuery = contentResolver.queryCountRow(sizeQueryCursor)
            playlist.copy(size = sizeQuery)
        }
    }

    override fun getByParam(param: Id): Playlist? {
        assertBackgroundThread()
        return channel.valueOrNull?.find { it.id == param }
    }

    override fun observeByParam(param: Id): Flow<Playlist?> {
        return channel.asFlow()
            .map { it.find { it.id == param } }
            .distinctUntilChanged()
            .assertBackground()
    }

    override fun getTrackListByParam(param: Id): List<Song> {
        assertBackgroundThread()
        val cursor = queries.getSongList(param)
        return contentResolver.queryAll(cursor) { it.toSong() }
    }

    override fun observeTrackListByParam(param: Id): Flow<List<Song>> {
        val uri = MediaStore.Audio.Playlists.Members.getContentUri("external", param)
        val contentUri = ContentUri(uri, true)
        return observeByParamInternal(contentUri) { getTrackListByParam(param) }
            .assertBackground()
    }

    override fun getAllAutoPlaylists(): List<Playlist> {
        assertBackgroundThread()
        return listOf(
            createAutoPlaylist(AutoPlaylist.LAST_ADDED.id, autoPlaylistTitles[0], 0),
            createAutoPlaylist(AutoPlaylist.FAVORITE.id, autoPlaylistTitles[1], 0),
            createAutoPlaylist(AutoPlaylist.HISTORY.id, autoPlaylistTitles[2], 0)
        )
    }

    private fun createAutoPlaylist(id: Long, title: String, listSize: Int): Playlist {
        return Playlist(id, title, listSize, false)
    }

    override fun observeMostPlayed(mediaId: MediaId): Flow<List<Song>> {
        val folderPath = mediaId.categoryId
        return mostPlayedDao.getAll(folderPath, songGateway2)
            .distinctUntilChanged()
            .assertBackground()
    }

    override suspend fun insertMostPlayed(mediaId: MediaId) {
        assertBackgroundThread()
        mostPlayedDao.insertOne(
            PlaylistMostPlayedEntity(
                0,
                mediaId.leaf!!,
                mediaId.categoryId
            )
        )
    }

    override fun observeSiblings(id: Id): Flow<List<Playlist>> {
        return observeAll()
            .map { it.filter { it.id != id } }
            .distinctUntilChanged()
            .assertBackground()
    }

    override fun observeRelatedArtists(params: Id): Flow<List<Artist>> {
        val contentUri = ContentUri(MediaStore.Audio.Artists.EXTERNAL_CONTENT_URI, true)
        return observeByParamInternal(contentUri) { extractArtists(queries.getRelatedArtists(params)) }
            .distinctUntilChanged()
            .assertBackground()
    }

    private fun extractArtists(cursor: Cursor): List<Artist> {
        assertBackgroundThread()
        return context.contentResolver.queryAll(cursor) { it.toArtist() }
            .groupBy { it.id }
            .map { (_, list) ->
                val artist = list[0]
                artist.copy(songs = list.size)
            }
    }
}