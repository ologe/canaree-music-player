package dev.olog.msc.data.repository

import android.provider.MediaStore
import com.squareup.sqlbrite3.BriteContentResolver
import com.squareup.sqlbrite3.SqlBrite
import dev.olog.msc.constants.AppConstants
import dev.olog.msc.data.db.AppDatabase
import dev.olog.msc.data.mapper.toArtist
import dev.olog.msc.data.mapper.toFakeArtist
import dev.olog.msc.domain.entity.Artist
import dev.olog.msc.domain.entity.Song
import dev.olog.msc.domain.gateway.ArtistGateway
import dev.olog.msc.domain.gateway.SongGateway
import dev.olog.msc.domain.gateway.UsedImageGateway
import dev.olog.msc.onlyWithStoragePermission
import dev.olog.msc.utils.k.extension.debounceFirst
import io.reactivex.Completable
import io.reactivex.Observable
import io.reactivex.rxkotlin.Observables
import java.text.Collator
import javax.inject.Inject

private val MEDIA_STORE_URI = MediaStore.Audio.Artists.EXTERNAL_CONTENT_URI

class ArtistRepository @Inject constructor(
        private val rxContentResolver: BriteContentResolver,
        private val songGateway: SongGateway,
        appDatabase: AppDatabase,
        private val collator: Collator,
        private val usedImageGateway: UsedImageGateway

) : ArtistGateway {

    private val lastPlayedDao = appDatabase.lastPlayedArtistDao()

    private fun queryAllData(): Observable<List<Artist>> {
        return rxContentResolver.createQuery(
                MEDIA_STORE_URI, arrayOf("count(*) as size"), null,
                null, " size ASC LIMIT 1", true
        ).onlyWithStoragePermission()
                .debounceFirst()
                .lift(SqlBrite.Query.mapToOne { 0 })
                .switchMap { songGateway.getAll() }
                .map { mapToArtists(it) }
                .map { updateImages(it) }

    }

    private fun updateImages(list: List<Artist>): List<Artist>{
        val allForArtists = usedImageGateway.getAllForArtists()
        if (allForArtists.isEmpty()){
            return list
        }
        return list.map { artist ->
            val image = allForArtists.firstOrNull { it.id == artist.id }?.image ?: artist.image
            artist.copy(image = image)
        }
    }

    private fun mapToArtists(songList: List<Song>): List<Artist> {
        return songList.asSequence()
                .filter { it.artist != AppConstants.UNKNOWN }
                .distinctBy { it.artistId }
                .map { song ->
                    val albums = countAlbums(song.artistId, songList)
                    val songs = countTracks(song.artistId, songList)
                    mapSongToArtist(song, songs, albums)
                }.sortedWith(Comparator { o1, o2 -> collator.compare(o1.name, o2.name) })
                .toList()
    }

    private fun mapSongToArtist(song: Song, songCount: Int, albumCount: Int): Artist {
        return if (AppConstants.useFakeData){
            song.toFakeArtist(songCount, albumCount)
        } else {
            song.toArtist(songCount, albumCount)
        }
    }

    private fun countAlbums(artistId: Long, songList: List<Song>): Int {
        return songList.asSequence()
                .distinctBy { it.albumId }
                .filter { it.album != AppConstants.UNKNOWN }
                .count { it.artistId == artistId }
    }

    private fun countTracks(artistId: Long, songList: List<Song>): Int {
        return songList.count { it.artistId == artistId }
    }

    private val cachedData = queryAllData()
            .replay(1)
            .refCount()

    override fun getAll(): Observable<List<Artist>> {
        return cachedData
    }

    override fun getAllNewRequest(): Observable<List<Artist>> {
        return queryAllData()
    }

    override fun getByParam(param: Long): Observable<Artist> {
        return cachedData.map { it.first { it.id == param } }
    }

    @Suppress("PARAMETER_NAME_CHANGED_ON_OVERRIDE")
    override fun observeSongListByParam(artistId: Long): Observable<List<Song>> {
        return songGateway.getAll().map {
            it.asSequence().filter { it.artistId == artistId }.toList()
        }.distinctUntilChanged()
    }

    override fun getLastPlayed(): Observable<List<Artist>> {
        return Observables.combineLatest(
                getAll(),
                lastPlayedDao.getAll().toObservable(),
                { all, lastPlayed ->

            if (all.size < 5) {
                listOf()
            } else {
                lastPlayed.asSequence()
                        .mapNotNull { last -> all.firstOrNull { it.id == last.id } }
                        .take(5)
                        .toList()
            }
        })
    }

    override fun addLastPlayed(id: Long): Completable {
        return lastPlayedDao.insertOne(id)
    }

}