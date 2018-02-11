package dev.olog.msc.data.repository

import android.provider.MediaStore
import com.squareup.sqlbrite3.BriteContentResolver
import dev.olog.msc.constants.AppConstants
import dev.olog.msc.data.db.AppDatabase
import dev.olog.msc.data.mapper.toAlbum
import dev.olog.msc.data.mapper.toNotNeuralAlbum
import dev.olog.msc.domain.entity.Album
import dev.olog.msc.domain.entity.Song
import dev.olog.msc.domain.gateway.AlbumGateway
import dev.olog.msc.domain.gateway.SongGateway
import io.reactivex.BackpressureStrategy
import io.reactivex.Completable
import io.reactivex.Flowable
import io.reactivex.rxkotlin.Flowables
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AlbumRepository @Inject constructor(
        private val rxContentResolver: BriteContentResolver,
        private val songGateway: SongGateway,
        appDatabase: AppDatabase

) : AlbumGateway {

    companion object {
        private val MEDIA_STORE_URI = MediaStore.Audio.Albums.EXTERNAL_CONTENT_URI
    }

    private val lastPlayedDao = appDatabase.lastPlayedAlbumDao()

    private val songMap : MutableMap<Long, Flowable<List<Song>>> = mutableMapOf()

    private val contentProviderObserver : Flowable<List<Album>> = rxContentResolver
            .createQuery(
                    MEDIA_STORE_URI,
                    arrayOf("count(*)"),
                    null, null, null,
                    false
            ).mapToOne { 0 }
            .toFlowable(BackpressureStrategy.LATEST)
            .flatMap { songGateway.getAll() }
            .map { songList -> songList.asSequence()
                    .filter { it.album != AppConstants.UNKNOWN_ALBUM }
                    .distinctBy { it.albumId }
                    .map { song ->
                        val songs = songList.count { it.albumId == song.albumId }

                        song.toAlbum(songs)
                    }.sortedBy { it.title.toLowerCase() }
                    .toList()

            }
            .distinctUntilChanged()
            .replay(1)
            .refCount()

    override fun getAll(): Flowable<List<Album>> = contentProviderObserver

    @Suppress("PARAMETER_NAME_CHANGED_ON_OVERRIDE")
    override fun getByParam(albumId: Long): Flowable<Album> {
        return getAll().map { it.first { it.id == albumId } }
    }

    override fun getAllAlbumsForUtils(): Flowable<List<Album>> {
        return songGateway.getAllUnfiltered()
                .map { songList -> songList.asSequence()
                        .filter { it.album != AppConstants.UNKNOWN_ALBUM }
                        .distinctBy { it.albumId }
                        .map { it.toNotNeuralAlbum() }
                        .sortedBy { it.title.toLowerCase() }
                        .toList()
                }
    }

    @Suppress("PARAMETER_NAME_CHANGED_ON_OVERRIDE")
    override fun observeSongListByParam(albumId: Long): Flowable<List<Song>> {
        var flowable = songMap[albumId]

        if (flowable == null){
            flowable = songGateway.getAll().map {
                it.asSequence().filter { it.albumId == albumId }.toList()
            }.distinctUntilChanged()
                    .replay(1)
                    .refCount()

            songMap[albumId] = flowable
        }

        return flowable
    }

    override fun getLastPlayed(): Flowable<List<Album>> {
        return Flowables.combineLatest(getAll(), lastPlayedDao.getAll(), { all, lastPlayed ->
            if (all.size < 10) {
                listOf() // too few album to show recents
            } else {
                lastPlayed.asSequence()
                        .map { lastPlayedAlbumEntity -> all.firstOrNull { it.id == lastPlayedAlbumEntity.id } }
                        .filter { it != null }
                        .map { it!! }
                        .take(10)
                        .toList()
            }
        })
    }

    override fun addLastPlayed(item: Album): Completable {
        return lastPlayedDao.insertOne(item)
    }
}