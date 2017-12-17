package dev.olog.data.repository

import dev.olog.data.DataConstants
import dev.olog.data.db.AppDatabase
import dev.olog.domain.entity.Album
import dev.olog.domain.entity.Song
import dev.olog.domain.gateway.AlbumGateway
import dev.olog.domain.gateway.SongGateway
import io.reactivex.Completable
import io.reactivex.Flowable
import io.reactivex.rxkotlin.toFlowable
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AlbumRepository @Inject constructor(
        songGateway: SongGateway,
        appDatabase: AppDatabase

) : AlbumGateway{

    private val lastPlayedDao = appDatabase.lastPlayedAlbumDao()

    private val dataMap : Flowable<MutableMap<Long, MutableList<Song>>> = songGateway.getAll()
            .flatMapSingle { it.toFlowable()
                    .filter { it.album != DataConstants.UNKNOWN_ALBUM }
                    .collectInto(mutableMapOf<Long, MutableList<Song>>(), { map, song ->
                        if (map.contains(song.albumId)){
                            map[song.albumId]!!.add(song)
                        } else {
                            map.put(song.albumId, mutableListOf(song))
                        }
                    }) }
            .distinctUntilChanged()
            .replay(1)
            .refCount()

    private val listObservable : Flowable<List<Album>> = dataMap.flatMapSingle { it.entries.toFlowable()
            .map {
                val song = it.value[0]
                Album(song.albumId, song.artistId, song.album, song.artist, song.image, it.value.size)
            }
            .toSortedList(compareBy { it.title.toLowerCase() })
    }.distinctUntilChanged()
            .replay(1)
            .refCount()

    override fun getAll(): Flowable<List<Album>> = listObservable

    override fun getByParam(param: Long): Flowable<Album> {
        return getAll().map { it.first { it.id == param } }
    }

    override fun observeSongListByParam(param: Long): Flowable<List<Song>> {
        return dataMap.map { it[param]!! }
    }

    override fun getLastPlayed(): Flowable<List<Album>> = lastPlayedDao.getAll()
            .map { it.sortedWith(compareByDescending { it.dateAdded }) }
            .flatMapSingle { it.toFlowable()
                    .map { Album(it.id, it.artistId, it.title, it.artist, it.image) }
                    .toList()
            }

    override fun addLastPlayed(item: Album): Completable {
        return lastPlayedDao.insertOne(item)
    }
}