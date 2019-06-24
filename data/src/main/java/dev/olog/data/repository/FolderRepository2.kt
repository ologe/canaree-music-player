package dev.olog.data.repository

import android.content.Context
import android.database.Cursor
import android.provider.MediaStore
import android.util.Log
import dev.olog.core.MediaId
import dev.olog.core.dagger.ApplicationContext
import dev.olog.core.entity.track.Artist
import dev.olog.core.entity.track.Folder
import dev.olog.core.entity.track.Song
import dev.olog.core.gateway.FolderGateway2
import dev.olog.core.gateway.Path
import dev.olog.core.gateway.SongGateway2
import dev.olog.core.prefs.BlacklistPreferences
import dev.olog.core.prefs.SortPreferences
import dev.olog.data.db.dao.AppDatabase
import dev.olog.data.db.entities.FolderMostPlayedEntity
import dev.olog.data.mapper.toArtist
import dev.olog.data.queries.FolderQueries
import dev.olog.data.utils.getString
import dev.olog.data.utils.queryAll
import dev.olog.shared.assertBackground
import dev.olog.shared.assertBackgroundThread
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.asFlow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map
import java.io.File
import javax.inject.Inject

internal class FolderRepository2 @Inject constructor(
        @ApplicationContext context: Context,
        appDatabase: AppDatabase,
        sortPrefs: SortPreferences,
        blacklistPrefs: BlacklistPreferences,
        private val songGateway2: SongGateway2
) : BaseRepository<Folder, Path>(context), FolderGateway2 {

    private val queries = FolderQueries(contentResolver, blacklistPrefs, sortPrefs)
    private val mostPlayedDao = appDatabase.folderMostPlayedDao()

    override fun registerMainContentUri(): ContentUri {
        return ContentUri(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI, true)
    }

    private fun extractFolders(cursor: Cursor): List<Folder> {
        assertBackgroundThread()
        val pathList = context.contentResolver.queryAll(cursor) {
            val data = it.getString(MediaStore.Audio.AudioColumns.DATA)
            data.substring(1, data.lastIndexOf(File.separator)) // path
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
        assertBackgroundThread()
        val cursor = queries.getAll(false)
        return extractFolders(cursor)
    }

    override fun getByParam(param: Path): Folder? {
        assertBackgroundThread()
        return channel.valueOrNull?.find { it.path == param }
    }

    override fun observeByParam(param: Path): Flow<Folder?> {
        return channel.asFlow().map { list -> list.find { it.path == param } }
                .assertBackground()
    }

    override fun getTrackListByParam(param: Path): List<Song> {
        return listOf()
    }

    override fun observeTrackListByParam(param: Path): Flow<List<Song>> {
        return flow { }
    }

    override fun getAllBlacklistedIncluded(): List<Folder> {
        assertBackgroundThread()
        val cursor = queries.getAll(true)
        return extractFolders(cursor)
    }

    override fun observeMostPlayed(mediaId: MediaId): Flow<List<Song>> {
        val folderPath = mediaId.categoryValue
        return mostPlayedDao.getAll(folderPath, songGateway2)
                .assertBackground()
    }

    override suspend fun insertMostPlayed(mediaId: MediaId) {
        assertBackgroundThread()
        songGateway2.getByParam(mediaId.leaf!!)?.let { item ->
            mostPlayedDao.insertOne(FolderMostPlayedEntity(
                    0,
                    item.id,
                    item.folderPath
            ))
        } ?: Log.w("FolderRepo", "song not found=$mediaId")
    }

    override fun observeSiblings(path: Path): Flow<List<Folder>> {
        return observeAll().map { it.filter { it.path != path } }
    }

    override fun observeRelatedArtists(params: Path): Flow<List<Artist>> {
        assertBackgroundThread()
        val cursor = queries.getRelatedArtists(params)
        return observeByParamInternal(ContentUri(MediaStore.Audio.Artists.EXTERNAL_CONTENT_URI, true)) {
            extractArtists(cursor)
        }
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