package dev.olog.msc.data.repository.podcast

import android.provider.MediaStore
import com.squareup.sqlbrite3.BriteContentResolver
import com.squareup.sqlbrite3.SqlBrite
import dev.olog.msc.constants.AppConstants
import dev.olog.msc.data.db.AppDatabase
import dev.olog.msc.data.mapper.toArtist
import dev.olog.msc.data.mapper.toFakeArtist
import dev.olog.msc.domain.entity.Podcast
import dev.olog.msc.domain.entity.PodcastArtist
import dev.olog.msc.domain.gateway.PodcastArtistGateway
import dev.olog.msc.domain.gateway.PodcastGateway
import dev.olog.msc.domain.gateway.UsedImageGateway
import dev.olog.msc.utils.k.extension.debounceFirst
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
        private val collator: Collator,
        private val usedImageGateway: UsedImageGateway

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
                .map { updateImages(it) }

    }

    private fun updateImages(list: List<PodcastArtist>): List<PodcastArtist>{
        val allForArtists = usedImageGateway.getAllForArtists()
        if (allForArtists.isEmpty()){
            return list
        }
        return list.map { artist ->
            val image = allForArtists.firstOrNull { it.id == artist.id }?.image ?: artist.image
            artist.copy(image = image)
        }
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
        return if (AppConstants.useFakeData){
            song.toFakeArtist(songCount, albumCount)
        } else {
            song.toArtist(songCount, albumCount)
        }
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

    override fun getAllNewRequest(): Observable<List<PodcastArtist>> {
        return queryAllData()
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