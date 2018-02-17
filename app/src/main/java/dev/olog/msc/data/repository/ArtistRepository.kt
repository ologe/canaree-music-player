package dev.olog.msc.data.repository

import android.content.ContentResolver
import android.content.Context
import android.provider.MediaStore
import com.squareup.sqlbrite3.BriteContentResolver
import dev.olog.msc.constants.AppConstants
import dev.olog.msc.dagger.qualifier.ApplicationContext
import dev.olog.msc.data.db.AppDatabase
import dev.olog.msc.data.mapper.toArtist
import dev.olog.msc.domain.entity.Artist
import dev.olog.msc.domain.entity.Song
import dev.olog.msc.domain.gateway.ArtistGateway
import dev.olog.msc.domain.gateway.SongGateway
import dev.olog.msc.utils.k.extension.emitThenDebounce
import io.reactivex.Completable
import io.reactivex.Observable
import io.reactivex.rxkotlin.Observables
import javax.inject.Inject

private val MEDIA_STORE_URI = MediaStore.Audio.Artists.EXTERNAL_CONTENT_URI

class ArtistRepository @Inject constructor(
        @ApplicationContext private val context: Context,
        private val contentResolver: ContentResolver,
        private val rxContentResolver: BriteContentResolver,
        private val songGateway: SongGateway,
        appDatabase: AppDatabase

) : BaseRepository<Artist, Long>(), ArtistGateway {

    private val lastPlayedDao = appDatabase.lastPlayedArtistDao()

    override fun queryAllData(): Observable<List<Artist>> {
        return rxContentResolver.createQuery(
                MEDIA_STORE_URI, arrayOf("count(*)"), null,
                null, null, false
        ).mapToOne { 0 }
                .flatMap { songGateway.getAll() }
                .map { songList ->
                    songList.asSequence()
                            .filter { it.artist != AppConstants.UNKNOWN }
                            .distinctBy { it.artistId }
                            .map { song ->
                                val albums = songList.asSequence()
                                        .distinctBy { it.albumId }
                                        .count { it.artistId == song.artistId }
                                val songs = songList.count { it.artistId == song.artistId }

                                song.toArtist(context, songs, albums)
                            }.sortedBy { it.name.toLowerCase() }
                            .toList()
                }
    }



    override fun getByParamImpl(list: List<Artist>, param: Long): Artist {
        return list.first { it.id == param }
    }

    @Suppress("PARAMETER_NAME_CHANGED_ON_OVERRIDE")
    override fun observeSongListByParam(artistId: Long): Observable<List<Song>> {
        val observable = songGateway.getAll().map {
            it.asSequence().filter { it.artistId == artistId }.toList()
        }.distinctUntilChanged()

        return observable.emitThenDebounce()
    }

    override fun getLastPlayed(): Observable<List<Artist>> {
        val observable = Observables.combineLatest(
                getAll(),
                lastPlayedDao.getAll().toObservable(),
                { all, lastPlayed ->

            if (all.size < 10) {
                listOf()
            } else {
                lastPlayed.asSequence()
                        .mapNotNull { lastPlayedArtistEntity -> all.firstOrNull { it.id == lastPlayedArtistEntity.id } }
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