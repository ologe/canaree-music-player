package dev.olog.msc.data.repository

import android.provider.MediaStore
import com.squareup.sqlbrite3.BriteContentResolver
import com.squareup.sqlbrite3.SqlBrite
import dev.olog.core.entity.track.Artist
import dev.olog.core.entity.track.Song
import dev.olog.presentation.AppConstants
import dev.olog.data.db.dao.AppDatabase
import dev.olog.msc.data.mapper.toArtist
import dev.olog.msc.domain.gateway.ArtistGateway
import dev.olog.msc.domain.gateway.SongGateway
import dev.olog.shared.debounceFirst
import dev.olog.msc.utils.safeCompare
import io.reactivex.Observable
import java.text.Collator
import javax.inject.Inject

private val MEDIA_STORE_URI = MediaStore.Audio.Artists.EXTERNAL_CONTENT_URI

class ArtistRepository @Inject constructor(
    private val rxContentResolver: BriteContentResolver,
    private val songGateway: SongGateway,
    appDatabase: AppDatabase,
    private val collator: Collator

) : ArtistGateway {

    private val lastPlayedDao = appDatabase.lastPlayedArtistDao()

    private fun queryAllData(): Observable<List<Artist>> {
        return rxContentResolver.createQuery(
                MEDIA_STORE_URI, arrayOf("count(*) as size"), null,
                null, " size ASC LIMIT 1", true
        )
                .debounceFirst()
                .lift(SqlBrite.Query.mapToOne { 0 })
                .switchMap { songGateway.getAll() }
                .map { mapToArtists(it) }

    }

    private fun mapToArtists(songList: List<Song>): List<Artist> {
        return songList.asSequence()
                .filter { it.artist != AppConstants.UNKNOWN }
                .distinctBy { it.artistId }
                .map { song ->
                    val albums = countAlbums(song.artistId, songList)
                    val songs = countTracks(song.artistId, songList)
                    mapSongToArtist(song, songs, albums)
                }.sortedWith(Comparator { o1, o2 -> collator.safeCompare(o1.name, o2.name) })
                .toList()
    }

    private fun mapSongToArtist(song: Song, songCount: Int, albumCount: Int): Artist {
        return song.toArtist(songCount, albumCount)
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

    override fun getByParam(param: Long): Observable<Artist> {
        return cachedData.map { it.first { it.id == param } }
    }

    @Suppress("PARAMETER_NAME_CHANGED_ON_OVERRIDE")
    override fun observeSongListByParam(artistId: Long): Observable<List<Song>> {
        return songGateway.getAll().map {
            it.asSequence().filter { it.artistId == artistId }.toList()
        }.distinctUntilChanged()
    }

}