package dev.olog.msc.api.last.fm

import dev.olog.msc.api.last.fm.annotation.Proxy
import dev.olog.msc.api.last.fm.model.SearchedImage
import dev.olog.msc.api.last.fm.model.SearchedTrack
import dev.olog.msc.api.last.fm.track.info.TrackInfo
import dev.olog.msc.api.last.fm.track.search.TrackSearch
import dev.olog.msc.domain.interactor.last.fm.GetLastFmTrackImageUseCase
import dev.olog.msc.domain.interactor.last.fm.GetLastFmTrackUseCase
import dev.olog.msc.domain.interactor.last.fm.InsertLastFmTrackImageUseCase
import dev.olog.msc.domain.interactor.last.fm.InsertLastFmTrackUseCase
import io.reactivex.Single
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class LastFmService @Inject constructor(
        @Proxy private val lastFm: RestLastFm,
        private val getLastFmTrackUseCase: GetLastFmTrackUseCase,
        private val getLastFmTrackImageUseCase: GetLastFmTrackImageUseCase,
        private val insertLastFmTrackUseCase: InsertLastFmTrackUseCase,
        private val insertLastFmTrackImageUseCase: InsertLastFmTrackImageUseCase
) {

    fun fetchSongInfo(id: Long, title: String, artist: String): Single<SearchedTrack> {
        val cached = getLastFmTrackUseCase.execute(id)

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

    fun fetchAlbumArt(id: Long, title: String, artist: String, album: String): Single<String> {
        val cached = getLastFmTrackImageUseCase.execute(id).map { it.image }

        val fetch = if (artist.isNotBlank() && album.isNotBlank()){
            lastFm.getAlbumInfo(album, artist)
        } else fetchSongInfo(id, title, artist)
                .flatMap { lastFm.getAlbumInfo(it.album, it.artist) }

        val fetchMap = fetch
                .map { it.album.image }
                .map { it.reversed().first { it.text.isNotBlank()  } }
                .map { it.text }
                // todo detect image not found
                // cache then return
                .flatMap { insertLastFmTrackImageUseCase.execute(SearchedImage(id, it)).toSingle { it } }


        return cached.onErrorResumeNext(fetchMap)
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