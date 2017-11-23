package dev.olog.data.repository

import dev.olog.data.DataConstants
import dev.olog.domain.entity.Artist
import dev.olog.domain.entity.Song
import dev.olog.domain.gateway.ArtistGateway
import dev.olog.domain.gateway.SongGateway
import dev.olog.domain.mapper.toArtist
import io.reactivex.Completable
import io.reactivex.Flowable
import io.reactivex.rxkotlin.toFlowable
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ArtistRepository @Inject constructor(
        private val songGateway: SongGateway
) :ArtistGateway{


    private val listObservable : Flowable<List<Artist>> = songGateway.getAll()
            .flatMapSingle { it.toFlowable()
                    .filter { it.album != DataConstants.UNKNOWN_ARTIST }
                    .distinct (Song::artistId)
                    .map(Song::toArtist)
                    .toList()
            }.distinctUntilChanged()
            .replay(1)
            .refCount()

    override fun getAll(): Flowable<List<Artist>> = listObservable

    override fun getByParam(param: Long): Flowable<Artist> {
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

    override fun getLastPlayed(): Flowable<List<Artist>> {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun addLastPlayed(item: Artist): Completable {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

}