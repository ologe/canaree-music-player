package dev.olog.msc.data.repository

import android.content.Context
import dev.olog.msc.dagger.qualifier.ApplicationContext
import dev.olog.msc.data.db.AppDatabase
import dev.olog.msc.data.entity.FolderMostPlayedEntity
import dev.olog.msc.data.mapper.toFolder
import dev.olog.core.entity.Folder
import dev.olog.core.entity.Song
import dev.olog.msc.domain.gateway.FolderGateway
import dev.olog.msc.domain.gateway.SongGateway
import dev.olog.core.MediaId
import dev.olog.msc.utils.safeCompare
import io.reactivex.Completable
import io.reactivex.CompletableSource
import io.reactivex.Observable
import java.text.Collator
import javax.inject.Inject

class FolderRepository @Inject constructor(
        @ApplicationContext private val context: Context,
        private val songGateway: SongGateway,
        appDatabase: AppDatabase,
        private val collator: Collator

): FolderGateway {

    private val mostPlayedDao = appDatabase.folderMostPlayedDao()

    private fun queryAllData(): Observable<List<Folder>> {
        return songGateway.getAll()
                .map(this::mapToFolderList)
                .map { it.filter { it.title.isNotBlank() } }
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
        return cachedData.map { list -> list.first { it.path == param } }
    }

    @Suppress("PARAMETER_NAME_CHANGED_ON_OVERRIDE")
    override fun observeSongListByParam(path: String): Observable<List<Song>> {
        return songGateway.getAll().map { list ->
            list.asSequence().filter { it.folderPath == path}.toList()
        }.distinctUntilChanged()
    }

    override fun getMostPlayed(mediaId: MediaId): Observable<List<Song>> {
        val folderPath = mediaId.categoryValue
        return mostPlayedDao.getAll(folderPath, songGateway.getAll())
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
                }.sortedWith(Comparator { o1, o2 -> collator.safeCompare(o1.title, o2.title) })
                .toList()
    }

}