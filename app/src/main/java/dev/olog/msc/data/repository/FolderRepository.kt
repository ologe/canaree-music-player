package dev.olog.msc.data.repository

import android.content.Context
import android.provider.MediaStore
import dev.olog.msc.dagger.qualifier.ApplicationContext
import dev.olog.msc.data.db.AppDatabase
import dev.olog.msc.data.entity.FolderMostPlayedEntity
import dev.olog.msc.data.mapper.toFolder
import dev.olog.msc.domain.entity.Folder
import dev.olog.msc.domain.entity.Song
import dev.olog.msc.domain.gateway.FolderGateway
import dev.olog.msc.domain.gateway.SongGateway
import dev.olog.msc.utils.MediaId
import dev.olog.msc.utils.k.extension.emitThenDebounce
import io.reactivex.Completable
import io.reactivex.CompletableSource
import io.reactivex.Observable
import javax.inject.Inject

private val MEDIA_STORE_URI = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI

class FolderRepository @Inject constructor(
        @ApplicationContext private val context: Context,
        private val songGateway: SongGateway,
        appDatabase: AppDatabase

): FolderGateway {

    private val mostPlayedDao = appDatabase.folderMostPlayedDao()

    private fun queryAllData(): Observable<List<Folder>> {
        return songGateway.getAll()
                .map(this::mapToFolderList)
    }

    private val cachedData = queryAllData()
            .replay(1)
            .refCount()

    override fun getAll(): Observable<List<Folder>> {
        return cachedData
    }

    override fun getAllNewRequest(): Observable<List<Folder>> {
        return queryAllData()
    }

    override fun getByParam(param: String): Observable<Folder> {
        return cachedData.map { it.first { it.path == param } }
    }

    @Suppress("PARAMETER_NAME_CHANGED_ON_OVERRIDE")
    override fun observeSongListByParam(path: String): Observable<List<Song>> {
        val observable = songGateway.getAll().map {
            it.asSequence().filter { it.folderPath == path}.toList()
        }.distinctUntilChanged()

        return observable.emitThenDebounce()
    }

    override fun getMostPlayed(mediaId: MediaId): Observable<List<Song>> {
        val folderPath = mediaId.categoryValue
        val observable = mostPlayedDao.getAll(folderPath, songGateway.getAll())

        return observable.emitThenDebounce()
    }

    override fun insertMostPlayed(mediaId: MediaId): Completable {
        val songId = mediaId.leaf!!
        return songGateway.getByParam(songId)
                .firstOrError()
                .flatMapCompletable { song ->
                    CompletableSource { mostPlayedDao.insertOne(FolderMostPlayedEntity(0, song.id, song.folderPath)) }
                }
    }

    override fun getAllUnfiltered(): Observable<List<Folder>> {
        return songGateway.getAllUnfiltered()
                .map(this::mapToFolderList)
    }

    private fun mapToFolderList(songList: List<Song>): List<Folder> {
        return songList.asSequence()
                .distinctBy { it.folderPath }
                .map { song ->
                    song.toFolder(context,
                            songList.count { it.folderPath == song.folderPath }) // count song for all folder
                }.sortedBy { it.title }
                .toList()
    }

}