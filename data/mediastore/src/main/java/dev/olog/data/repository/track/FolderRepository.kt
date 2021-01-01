package dev.olog.data.repository.track

import android.content.Context
import android.database.Cursor
import android.provider.MediaStore
import dagger.hilt.android.qualifiers.ApplicationContext
import dev.olog.domain.mediaid.MediaId
import dev.olog.domain.entity.track.Artist
import dev.olog.domain.entity.track.Folder
import dev.olog.domain.entity.track.Track
import dev.olog.domain.gateway.base.Path
import dev.olog.domain.gateway.track.FolderGateway
import dev.olog.domain.gateway.track.SongGateway
import dev.olog.domain.prefs.BlacklistPreferences
import dev.olog.domain.prefs.SortPreferencesGateway
import dev.olog.domain.schedulers.Schedulers
import dev.olog.data.local.most.played.FolderMostPlayedDao
import dev.olog.data.local.most.played.FolderMostPlayedEntity
import dev.olog.data.mapper.toArtist
import dev.olog.data.mapper.toSong
import dev.olog.data.queries.FolderQueries
import dev.olog.data.repository.BaseRepository
import dev.olog.data.repository.ContentUri
import dev.olog.data.utils.getString
import dev.olog.data.utils.queryAll
import dev.olog.shared.value
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.map
import java.io.File
import javax.inject.Inject

internal class FolderRepository @Inject constructor(
    @ApplicationContext context: Context,
    sortPrefs: SortPreferencesGateway,
    blacklistPrefs: BlacklistPreferences,
    private val songGateway: SongGateway,
    private val mostPlayedDao: FolderMostPlayedDao,
    schedulers: Schedulers
) : BaseRepository<Folder, Path>(context, schedulers), FolderGateway {

    private val queries = FolderQueries(
        schedulers = schedulers,
        contentResolver = contentResolver,
        blacklistPrefs = blacklistPrefs,
        sortPrefs = sortPrefs
    )

    init {
        firstQuery()
    }

    override fun registerMainContentUri(): ContentUri {
        return ContentUri(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI, true)
    }

    @Suppress("DEPRECATION")
    private suspend fun extractFolders(cursor: Cursor): List<Folder> {
        val pathList = contentResolver.queryAll(cursor) {
            val data = it.getString(MediaStore.Audio.Media.DATA)
            data.substring(0, data.lastIndexOf(File.separator)) // path
        }
        return pathList.asSequence()
            .groupBy { it }
            .entries
            .map { (path, list) ->
                val dirName = path.substring(path.lastIndexOf(File.separator) + 1)
                Folder(
                    dirName.capitalize(),
                    path,
                    list.size
                )
            }.sortedBy { it.title }
    }

    override suspend fun queryAll(): List<Folder> {
        val cursor = queries.getAll(false)
        return extractFolders(cursor)
    }

    override suspend fun getByParam(param: Path): Folder? {
        return publisher.value?.find { it.path == param }
    }

    override suspend fun getByHashCode(hashCode: Int): Folder? {
        return getAll().find { it.path.hashCode() == hashCode }
    }

    override fun observeByParam(param: Path): Flow<Folder?> {
        return observeAll()
            .map { list -> list.find { it.path == param } }
            .distinctUntilChanged()
    }

    override suspend fun getTrackListByParam(param: Path): List<Track> {
        val cursor = queries.getSongList(param)
        return contentResolver.queryAll(cursor, Cursor::toSong)
    }

    override fun observeTrackListByParam(param: Path): Flow<List<Track>> {
        val contentUri = ContentUri(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI, true)
        return observeByParamInternal(contentUri) { getTrackListByParam(param) }
    }

    override suspend fun getAllBlacklistedIncluded(): List<Folder> {
        val cursor = queries.getAll(true)
        return extractFolders(cursor)
    }

    override fun observeMostPlayed(mediaId: MediaId): Flow<List<Track>> {
        val folderPath = mediaId.categoryValue
        return mostPlayedDao.observeAll(folderPath, songGateway)
            .distinctUntilChanged()
    }

    override suspend fun insertMostPlayed(mediaId: MediaId.Track) {
        mostPlayedDao.insertOne(
            FolderMostPlayedEntity(
                songId = mediaId.id,
                folderPath = mediaId.categoryValue
            )
        )
    }

    override fun observeSiblings(param: Path): Flow<List<Folder>> {
        return observeAll()
            .map { it.filter { it.path != param } }
            .distinctUntilChanged()
    }

    override fun observeRelatedArtists(params: Path): Flow<List<Artist>> {
        val contentUri = ContentUri(MediaStore.Audio.Artists.EXTERNAL_CONTENT_URI, true)
        return observeByParamInternal(contentUri) { extractArtists(queries.getRelatedArtists(params)) }
            .distinctUntilChanged()
    }

    override fun observeRecentlyAdded(param: Path): Flow<List<Track>> {
        val contentUri = ContentUri(MediaStore.Audio.Artists.EXTERNAL_CONTENT_URI, true)
        return observeByParamInternal(contentUri) {
            val cursor = queries.getRecentlyAdded(param)
            contentResolver.queryAll(cursor, Cursor::toSong)
        }
    }

    private suspend fun extractArtists(cursor: Cursor): List<Artist> {
        return contentResolver.queryAll(cursor, Cursor::toArtist)
            .groupBy { it.id }
            .map { (_, list) ->
                val artist = list[0]
                artist.withSongs(list.size)
            }
    }
}