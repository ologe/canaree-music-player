package dev.olog.data.repository

import dev.olog.domain.entity.Folder
import dev.olog.domain.entity.Song
import dev.olog.domain.gateway.FolderGateway
import dev.olog.domain.gateway.SongGateway
import dev.olog.domain.mapper.toFolder
import io.reactivex.Flowable
import io.reactivex.rxkotlin.toFlowable
import java.io.File
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class FolderRepository @Inject constructor(
        private val songGateway: SongGateway

): FolderGateway {

    private val listObservable : Flowable<List<Folder>> = songGateway.getAll()
                .flatMapSingle { it.toFlowable()
                        .distinct { it.path.substring(0, it.path.lastIndexOf(File.separator)) }
                        .map(Song::toFolder)
                        .toList()
                }.distinctUntilChanged()
                .replay(1)
                .refCount()


    override fun getAll(): Flowable<List<Folder>> = listObservable

    override fun observeSongListByParam(param: String): Flowable<List<Song>> {
        return songGateway.getAll()
                .flatMapSingle { it.toFlowable()
                        .filter { it.path == param }
                        .toList()
                }
    }

    override fun getByParam(param: String): Flowable<Folder> {
        return getAll().flatMapSingle { it.toFlowable()
                .filter { it.path == param }
                .firstOrError()
        }
    }
}