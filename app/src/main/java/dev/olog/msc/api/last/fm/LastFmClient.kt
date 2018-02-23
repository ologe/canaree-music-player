package dev.olog.msc.api.last.fm

import android.accounts.NetworkErrorException
import android.arch.persistence.room.EmptyResultSetException
import android.net.ConnectivityManager
import dev.olog.msc.api.last.fm.annotation.Proxy
import dev.olog.msc.api.last.fm.model.SearchedImage
import dev.olog.msc.api.last.fm.model.SearchedTrack
import dev.olog.msc.api.last.fm.track.info.TrackInfo
import dev.olog.msc.api.last.fm.track.search.TrackSearch
import dev.olog.msc.domain.interactor.last.fm.*
import dev.olog.msc.utils.k.extension.isNetworkAvailable
import io.reactivex.Single
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class LastFmClient @Inject constructor(
        @Proxy private val lastFm: LastFmService,
        private val connectivityManager: ConnectivityManager,
        private val getLastFmTrackUseCase: GetLastFmTrackUseCase,
        private val getLastFmTrackImageUseCase: GetLastFmTrackImageUseCase,
        private val getLastFmArtistImageUseCase: GetLastFmArtistImageUseCase,
        private val insertLastFmTrackUseCase: InsertLastFmTrackUseCase,
        private val insertLastFmTrackImageUseCase: InsertLastFmTrackImageUseCase,
        private val insertLastFmArtistImageUseCase: InsertLastFmArtistImageUseCase
) {

    /**
     * @throws NetworkErrorException when there is no internet connection
     *      and no cache is found
     */
    fun fetchSongInfo(id: Long, title: String, artist: String): Single<SearchedTrack> {
        val cached = getLastFmTrackUseCase.execute(id)

        if (!connectivityManager.isNetworkAvailable()){
            return cached.onErrorResumeNext {
                if(it is EmptyResultSetException){
                    Single.error(NetworkErrorException())
                } else Single.error(it)
            }
        }

        val fetch =  lastFm.getTrackInfo(title, artist)
                .map { it.toSearchSong(id) }
                .onErrorResumeNext { lastFm.searchTrack(title, artist)
                        .map { it.toSearchSong(id) }
                        .flatMap { result -> lastFm.getTrackInfo(result.title, result.artist)
                                .map { it.toSearchSong(id) }
                                .onErrorReturn { result }
                        }
                }
                // cache and return
                .flatMap { insertLastFmTrackUseCase.execute(it).toSingle { it } }

        return cached.onErrorResumeNext(fetch)
    }

    /**
     * @throws NetworkErrorException when there is no internet connection
     *      and no cache is found
     */
    fun fetchAlbumArt(id: Long, title: String, artist: String, album: String): Single<String> {
        val cached = getLastFmTrackImageUseCase.execute(id).map { it.image }

        if (!connectivityManager.isNetworkAvailable()) {
            return cached.onErrorResumeNext {
                if(it is EmptyResultSetException){
                    Single.error(NetworkErrorException())
                } else Single.error(it)
            }
        }

        val fetch = if (artist.isNotBlank() && album.isNotBlank()){
            lastFm.getAlbumInfo(album, artist)
        } else fetchSongInfo(id, title, artist)
                .flatMap { lastFm.getAlbumInfo(it.album, it.artist) }

        val fetchMap = fetch
                .onErrorResumeNext {
                    if (it is NullPointerException){
                        insertLastFmTrackImageUseCase.execute(SearchedImage(id, ""))
                                .andThen(Single.error(it))
                    } else {
                        Single.error(it)
                    }
                }
                .map { it.album.image }
                .map { it.reversed().first { it.text.isNotBlank()  } }
                .map { it.text }
                // todo detect image not found
                // cache then return
                .flatMap { insertLastFmTrackImageUseCase.execute(SearchedImage(id, it)).toSingle { it } }


        return cached.onErrorResumeNext(fetchMap)
    }

    /**
     * @return true if a new image is downloaded
     */
    fun fetchArtistArt(artistId: Long, name: String): Single<Boolean> {
        val isConnected = connectivityManager.isNetworkAvailable()

        val fetch = lastFm.getArtistInfo(name)
                .map { it.artist.image }
                .map { it.reversed().first { it.text.isNotBlank() } }
                .map { it.text }
                .map { true }
                .onErrorResumeNext {
                    if (it is NullPointerException){
                        insertLastFmArtistImageUseCase
                                .execute(SearchedImage(artistId, ""))
                                .toSingle { false }
                    } else {
                        Single.just(false)
                    }
                }

        if (isConnected){
            return getLastFmArtistImageUseCase.execute(artistId)
                    .map { it.image }
                    .map { false }
                    .onErrorResumeNext {
                        if (it is EmptyResultSetException){
                            fetch
                        } else Single.just(false)
                    }
        }
        return Single.just(false)
    }

    private fun TrackInfo.toSearchSong(id: Long): SearchedTrack {
        val track = this.track
        val title = track.name
        val artist = track.artist.name
        val album = track.album.title

        return SearchedTrack(
                id,
                title ?: "",
                artist ?: "",
                album ?: ""
        )
    }

    private fun TrackSearch.toSearchSong(id: Long): SearchedTrack {
        val track = this.results.trackmatches.track[0]

        return SearchedTrack(
                id,
                track.name ?: "",
                track.artist ?: "",
                ""
        )
    }

}