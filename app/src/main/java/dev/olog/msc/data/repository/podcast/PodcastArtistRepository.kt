package dev.olog.msc.data.repository.podcast

import android.provider.MediaStore
import com.squareup.sqlbrite3.BriteContentResolver
import com.squareup.sqlbrite3.SqlBrite
import dev.olog.core.entity.podcast.Podcast
import dev.olog.core.entity.podcast.PodcastArtist
import dev.olog.msc.constants.AppConstants
import dev.olog.data.db.dao.AppDatabase
import dev.olog.msc.data.mapper.toArtist
import dev.olog.msc.domain.gateway.PodcastArtistGateway
import dev.olog.msc.domain.gateway.PodcastGateway
import dev.olog.shared.debounceFirst
import dev.olog.msc.utils.safeCompare
import io.reactivex.Completable
import io.reactivex.Observable
import io.reactivex.rxkotlin.Observables
import java.text.Collator
import javax.inject.Inject

private val MEDIA_STORE_URI = MediaStore.Audio.Artists.EXTERNAL_CONTENT_URI

class PodcastArtistRepository @Inject constructor(
    appDatabase: AppDatabase,
    private val rxContentResolver: BriteContentResolver,
    private val podcastGateway: PodcastGateway,
    private val collator: Collator

) : PodcastArtistGateway {

    private val lastPlayedDao = appDatabase.lastPlayedPodcastArtistDao()

    private fun queryAllData(): Observable<List<PodcastArtist>> {
        return rxContentResolver.createQuery(
                MEDIA_STORE_URI, arrayOf("count(*) as size"), null,
                null, " size ASC LIMIT 1", true
        )
                .debounceFirst()
                .lift(SqlBrite.Query.mapToOne { 0 })
                .switchMap { podcastGateway.getAll() }
                .map { mapToArtists(it) }

    }

    private fun mapToArtists(songList: List<Podcast>): List<PodcastArtist> {
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

    private fun mapSongToArtist(song: Podcast, songCount: Int, albumCount: Int): PodcastArtist {
        return song.toArtist(songCount, albumCount)
    }

    private fun countAlbums(artistId: Long, songList: List<Podcast>): Int {
        return songList.asSequence()
                .distinctBy { it.albumId }
                .filter { it.album != AppConstants.UNKNOWN }
                .count { it.artistId == artistId }
    }

    private fun countTracks(artistId: Long, songList: List<Podcast>): Int {
        return songList.count { it.artistId == artistId }
    }

    private val cachedData = queryAllData()
            .replay(1)
            .refCount()

    override fun getAll(): Observable<List<PodcastArtist>> {
        return cachedData
    }

    override fun getByParam(param: Long): Observable<PodcastArtist> {
        return cachedData.map { it.first { it.id == param } }
    }

    @Suppress("PARAMETER_NAME_CHANGED_ON_OVERRIDE")
    override fun observePodcastListByParam(artistId: Long): Observable<List<Podcast>> {
        return podcastGateway.getAll()
                .map { it.filter { it.artistId == artistId } }
    }

    override fun getLastPlayed(): Observable<List<PodcastArtist>> {
        return Observables.combineLatest(
                getAll(),
                lastPlayedDao.getAll().toObservable(),
                { all, lastPlayed ->

                    if (all.size < 5) {
                        listOf() // too few album to show recents
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