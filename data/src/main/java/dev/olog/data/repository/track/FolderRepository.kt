package dev.olog.data.repository.track

import android.content.ContentResolver
import android.content.Context
import android.database.Cursor
import android.provider.MediaStore
import dagger.hilt.android.qualifiers.ApplicationContext
import dev.olog.core.MediaId
import dev.olog.core.entity.track.Artist
import dev.olog.core.entity.track.Folder
import dev.olog.core.entity.track.Song
import dev.olog.core.gateway.base.Path
import dev.olog.core.gateway.track.FolderGateway
import dev.olog.core.gateway.track.SongGateway
import dev.olog.core.prefs.BlacklistPreferences
import dev.olog.core.prefs.SortPreferences
import dev.olog.core.schedulers.Schedulers
import dev.olog.data.db.dao.FolderMostPlayedDao
import dev.olog.data.db.entities.FolderMostPlayedEntity
import dev.olog.data.mapper.toArtist
import dev.olog.data.mapper.toSong
import dev.olog.data.queries.FolderQueries
import dev.olog.data.repository.BaseRepository
import dev.olog.data.repository.ContentUri
import dev.olog.data.utils.getString
import dev.olog.data.utils.queryAll
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.map
import java.io.File
import javax.inject.Inject

internal class FolderRepository @Inject constructor(
    @ApplicationContext context: Context,
    contentResolver: ContentResolver,
    sortPrefs: SortPreferences,
    blacklistPrefs: BlacklistPreferences,
    private val songGateway2: SongGateway,
    private val mostPlayedDao: FolderMostPlayedDao,
    schedulers: Schedulers
) : BaseRepository<Folder, Path>(context, contentResolver, schedulers), FolderGateway {

    private val queries = FolderQueries(contentResolver, blacklistPrefs, sortPrefs)

    init {
        firstQuery()
    }

    override fun registerMainContentUri(): ContentUri {
        return ContentUri(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI, true)
    }

    @Suppress("DEPRECATION")
    private fun extractFolders(cursor: Cursor): List<Folder> {
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

    override fun queryAll(): List<Folder> {
        val cursor = queries.getAll(false)
        return extractFolders(cursor)
    }

    override fun getByParam(param: Path): Folder? {
        return channel.replayCache.firstOrNull()?.find { it.path == param }
    }

    override fun getByHashCode(hashCode: Int): Folder? {
        return channel.replayCache.firstOrNull()?.find { it.path.hashCode() == hashCode }
    }

    override fun observeByParam(param: Path): Flow<Folder?> {
        return channel
            .map { list -> list.find { it.path == param } }
            .distinctUntilChanged()
    }

    override fun getTrackListByParam(param: Path): List<Song> {
        val cursor = queries.getSongList(param)
        return contentResolver.queryAll(cursor) { it.toSong() }
    }

    override fun observeTrackListByParam(param: Path): Flow<List<Song>> {
        val contentUri = ContentUri(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI, true)
        return observeByParamInternal(contentUri) { getTrackListByParam(param) }
    }

    override fun getAllBlacklistedIncluded(): List<Folder> {
        val cursor = queries.getAll(true)
        return extractFolders(cursor)
    }

    override fun observeMostPlayed(mediaId: MediaId): Flow<List<Song>> {
        val folderPath = mediaId.categoryValue
        return mostPlayedDao.getAll(folderPath, songGateway2)
            .distinctUntilChanged()
    }

    override suspend fun insertMostPlayed(mediaId: MediaId) {
        mostPlayedDao.insertOne(
            FolderMostPlayedEntity(
                0,
                mediaId.leaf!!,
                mediaId.categoryValue
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

    override fun observeRecentlyAdded(path: Path): Flow<List<Song>> {
        val contentUri = ContentUri(MediaStore.Audio.Artists.EXTERNAL_CONTENT_URI, true)
        return observeByParamInternal(contentUri) {
            val cursor = queries.getRecentlyAdded(path)
            contentResolver.queryAll(cursor) { it.toSong() }
        }
    }

    private fun extractArtists(cursor: Cursor): List<Artist> {
        return contentResolver.queryAll(cursor) { it.toArtist() }
            .groupBy { it.id }
            .map { (_, list) ->
                val artist = list[0]
                artist.withSongs(list.size)
            }
    }
}