package dev.olog.msc.data.repository

import android.provider.MediaStore
import com.squareup.sqlbrite3.BriteContentResolver
import com.squareup.sqlbrite3.SqlBrite
import dev.olog.core.entity.track.Album
import dev.olog.core.entity.track.Song
import dev.olog.msc.constants.AppConstants
import dev.olog.data.db.dao.AppDatabase
import dev.olog.msc.data.mapper.toAlbum
import dev.olog.msc.domain.gateway.AlbumGateway
import dev.olog.msc.domain.gateway.SongGateway
import dev.olog.shared.debounceFirst
import dev.olog.msc.utils.safeCompare
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
        )
                .debounceFirst()
                .lift(SqlBrite.Query.mapToOne { 0 })
                .switchMap { songGateway.getAll() }
                .map { songList ->
                    songList.asSequence()
                            .filter { it.album != AppConstants.UNKNOWN }
                            .distinctBy { it.albumId }
                            .map { song ->
                                song.toAlbum(songList.count { it.albumId == song.albumId })
                            }.sortedWith(Comparator { o1, o2 -> collator.safeCompare(o1.title, o2.title) })
                            .toList()
                }
    }

    private val cachedData = queryAllData()
            .replay(1)
            .refCount()

    override fun getAll(): Observable<List<Album>> {
        return cachedData
    }

    override fun getByParam(param: Long): Observable<Album> {
        return cachedData.map { it.first { it.id == param } }
    }

    @Suppress("PARAMETER_NAME_CHANGED_ON_OVERRIDE")
    override fun observeSongListByParam(albumId: Long): Observable<List<Song>> {
        return songGateway.getAll().map { it.filter { it.albumId == albumId } }
    }

    override fun observeByArtist(artistId: Long): Observable<List<Album>> {
        return getAll().map { it.filter { it.artistId == artistId } }
    }
}