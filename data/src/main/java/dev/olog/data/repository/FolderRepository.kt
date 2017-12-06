package dev.olog.data.repository

import dev.olog.data.db.AppDatabase
import dev.olog.data.entity.FolderMostPlayedEntity
import dev.olog.domain.entity.Folder
import dev.olog.domain.entity.Song
import dev.olog.domain.gateway.FolderGateway
import dev.olog.domain.gateway.SongGateway
import dev.olog.shared.MediaIdHelper
import io.reactivex.Completable
import io.reactivex.CompletableSource
import io.reactivex.Flowable
import io.reactivex.rxkotlin.toFlowable
import java.io.File
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class FolderRepository @Inject constructor(
        private val songGateway: SongGateway,
        appDatabase: AppDatabase

): FolderGateway {

    private val mostPlayedDao = appDatabase.folderMostPlayedDao()

    private val dataMap : Flowable<MutableMap<String, MutableList<Song>>> = songGateway.getAll()
            .flatMapSingle { it.toFlowable().collectInto(mutableMapOf<String, MutableList<Song>>(), { map, song ->
                if (map.contains(song.folderPath)){
                    map[song.folderPath]!!.add(song)
                } else {
                    map.put(song.folderPath, mutableListOf(song))
                }
            })
            }.distinctUntilChanged()
            .replay(1)
            .refCount()

    private val listObservable : Flowable<List<Folder>> = dataMap.flatMapSingle { it.entries.toFlowable()
                .map {
                    Folder(it.key.substring(it.key.lastIndexOf(File.separator) + 1),
                            it.key, it.value.size)
                }.toSortedList(compareBy { it.title.toLowerCase() })
            }.distinctUntilChanged()
            .replay(1)
            .refCount()

    override fun getAll(): Flowable<List<Folder>> = listObservable

    override fun observeSongListByParam(param: String): Flowable<List<Song>> {
        return dataMap.map { it[param]!! }
    }

    override fun getByParam(param: String): Flowable<Folder> {
        return getAll().map { it.first { it.path == param } }
    }

    override fun getMostPlayed(param: String): Flowable<List<Song>> {

        return mostPlayedDao.getAll(MediaIdHelper.extractCategoryValue(param), songGateway.getAll())
    }

    override fun insertMostPlayed(mediaId: String): Completable {
        return songGateway.getByParam(MediaIdHelper.extractLeaf(mediaId).toLong())
                .flatMapCompletable { song ->
                    CompletableSource { mostPlayedDao.insertOne(FolderMostPlayedEntity(0, song.id, song.folderPath)) }
                }
    }
}