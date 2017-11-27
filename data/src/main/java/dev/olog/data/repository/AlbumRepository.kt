package dev.olog.data.repository

import dev.olog.data.DataConstants
import dev.olog.domain.entity.Album
import dev.olog.domain.entity.Song
import dev.olog.domain.gateway.AlbumGateway
import dev.olog.domain.gateway.SongGateway
import dev.olog.domain.mapper.toAlbum
import io.reactivex.Completable
import io.reactivex.Flowable
import io.reactivex.rxkotlin.toFlowable
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AlbumRepository @Inject constructor(
        private val songGateway: SongGateway

) : AlbumGateway{

    private val listObservable : Flowable<List<Album>> = songGateway.getAll()
            .flatMapSingle { it.toFlowable()
                    .filter { it.album != DataConstants.UNKNOWN_ALBUM }
                    .distinct (Song::albumId)
                    .map(Song::toAlbum)
                    .toSortedList(compareBy { it.title.toLowerCase() })
            }.distinctUntilChanged()
            .replay(1)
            .refCount()

    override fun getAll(): Flowable<List<Album>> = listObservable

    override fun getByParam(param: Long): Flowable<Album> {
        return getAll().flatMapSingle { it.toFlowable()
                .filter { it.id == param }
                .firstOrError()
        }
    }

    override fun observeSongListByParam(param: Long): Flowable<List<Song>> {
        return songGateway.getAll()
                .flatMapSingle { it.toFlowable()
                        .filter { it.albumId == param }
                        .toList()
                }
    }

    override fun getLastPlayed(): Flowable<List<Album>> {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun addLastPlayed(item: Album): Completable {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }
}