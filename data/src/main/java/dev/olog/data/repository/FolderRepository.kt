package dev.olog.data.repository

import dev.olog.data.db.AppDatabase
import dev.olog.data.entity.FolderMostPlayedEntity
import dev.olog.domain.entity.Folder
import dev.olog.domain.entity.Song
import dev.olog.domain.gateway.FolderGateway
import dev.olog.domain.gateway.SongGateway
import dev.olog.domain.mapper.toFolder
import dev.olog.shared.MediaIdHelper
import io.reactivex.Completable
import io.reactivex.CompletableSource
import io.reactivex.Flowable
import io.reactivex.rxkotlin.toFlowable
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class FolderRepository @Inject constructor(
        private val songGateway: SongGateway,
        appDatabase: AppDatabase

): FolderGateway {

    private val mostPlayedDao = appDatabase.folderMostPlayedDao()

    private val listObservable : Flowable<List<Folder>> = songGateway.getAll()
                .flatMapSingle { it.toFlowable()
                        .distinct { it.folderPath }
                        .map(Song::toFolder)
                        .toList()
                }.distinctUntilChanged()
                .replay(1)
                .refCount()


    override fun getAll(): Flowable<List<Folder>> = listObservable

    override fun observeSongListByParam(param: String): Flowable<List<Song>> {
        return songGateway.getAll()
                .flatMapSingle { it.toFlowable()
                        .filter { it.folderPath == param }
                        .toList()
                }
    }

    override fun getByParam(param: String): Flowable<Folder> {
        return getAll().flatMapSingle { it.toFlowable()
                .filter { it.path == param }
                .firstOrError()
        }
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