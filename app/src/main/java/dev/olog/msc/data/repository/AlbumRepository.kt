package dev.olog.msc.data.repository

import android.provider.MediaStore
import com.squareup.sqlbrite3.BriteContentResolver
import dev.olog.msc.constants.AppConstants
import dev.olog.msc.data.db.AppDatabase
import dev.olog.msc.data.mapper.toAlbum
import dev.olog.msc.domain.entity.Album
import dev.olog.msc.domain.entity.Song
import dev.olog.msc.domain.gateway.AlbumGateway
import dev.olog.msc.domain.gateway.SongGateway
import dev.olog.msc.utils.k.extension.crashlyticsLog
import dev.olog.msc.utils.k.extension.emitThenDebounce
import io.reactivex.Completable
import io.reactivex.Observable
import io.reactivex.rxkotlin.Observables
import java.text.Collator
import javax.inject.Inject

private val MEDIA_STORE_URI = MediaStore.Audio.Albums.EXTERNAL_CONTENT_URI

class AlbumRepository @Inject constructor(
        private val rxContentResolver: BriteContentResolver,
        private val songGateway: SongGateway,
        appDatabase: AppDatabase,
        private val collator: Collator

) : AlbumGateway {

    private val lastPlayedDao = appDatabase.lastPlayedAlbumDao()

    private fun queryAllData(): Observable<List<Album>> {
        return rxContentResolver.createQuery(
                MEDIA_STORE_URI, arrayOf("count(*) as size"), null,
                null, " size ASC LIMIT 1", true
        ).mapToOne { 0 }
                .flatMap { songGateway.getAll() }
                .map { songList ->
                    songList.asSequence()
                            .filter { it.album != AppConstants.UNKNOWN }
                            .distinctBy { it.albumId }
                            .map { song ->
                                song.toAlbum(songList.count { it.albumId == song.albumId })
                            }.sortedWith(Comparator { o1, o2 -> collator.compare(o1.title, o2.title) })
                            .toList()
                }
    }

    private val cachedData = queryAllData()
            .replay(1)
            .refCount()

    override fun getAll(): Observable<List<Album>> {
        return cachedData
    }

    override fun getAllNewRequest(): Observable<List<Album>> {
        return queryAllData()
    }

    override fun getByParam(param: Long): Observable<Album> {
        return cachedData.map { albums ->
            try {
                albums.first { it.id == param }
            } catch (ex: Exception){
                crashlyticsLog("searched album=$param, all albums id=${albums.map { it.id }}")
                throw ex
            }
        }
    }

    @Suppress("PARAMETER_NAME_CHANGED_ON_OVERRIDE")
    override fun observeSongListByParam(albumId: Long): Observable<List<Song>> {
        val observable = songGateway.getAll().map { it.filter { it.albumId == albumId } }

        return observable.emitThenDebounce()
    }

    override fun observeByArtist(artistId: Long): Observable<List<Album>> {
        return getAll().map { it.filter { it.artistId == artistId } }
    }

    override fun getLastPlayed(): Observable<List<Album>> {
        val observable = Observables.combineLatest(
                getAll(),
                lastPlayedDao.getAll().toObservable(),
                { all, lastPlayed ->

            if (all.size < 10) {
                listOf() // too few album to show recents
            } else {
                lastPlayed.asSequence()
                        .mapNotNull { last -> all.firstOrNull { it.id == last.id } }
                        .take(10)
                        .toList()
            }
        })
        return observable.emitThenDebounce()
    }

    override fun addLastPlayed(id: Long): Completable {
        return lastPlayedDao.insertOne(id)
    }
}