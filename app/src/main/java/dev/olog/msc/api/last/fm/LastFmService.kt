package dev.olog.msc.api.last.fm

import dev.olog.msc.api.last.fm.model.SearchedSong
import dev.olog.msc.api.last.fm.track.info.TrackInfo
import dev.olog.msc.api.last.fm.track.search.TrackSearch
import dev.olog.msc.constants.AppConstants
import io.reactivex.Single
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class LastFmService @Inject constructor(
        private val lastFm: RestLastFm
) {

    fun fetchSongInfo(title: String, artist: String): Single<SearchedSong> {
        val normalizeTitle = NormalizedEntity(title)
        val normalizeArtist = NormalizedEntity(if (artist == AppConstants.UNKNOWN) "" else artist)

        return lastFm.getTrackInfo(normalizeTitle.value, normalizeArtist.value)
                .map { it.toSearchSong() }
                .onErrorResumeNext {
                    lastFm.searchTrack(normalizeTitle.value, normalizeArtist.value)
                            .map { it.toSearchSong() }
                            .flatMap { result ->
                                Single.just(result.normalize())
                                        .flatMap { lastFm.getTrackInfo(it.title, it.artist)
                                                .map { it.toSearchSong() }
                                        }.onErrorReturn { result }
                            }
                }
    }

    private fun TrackInfo.toSearchSong(): SearchedSong {
        val track = this.track
        val title = track.name
        val artist = track.artist.name
        val album = track.album.title

        return SearchedSong(
                title ?: "",
                artist ?: "",
                album ?: ""
        )
    }

    private fun TrackSearch.toSearchSong(): SearchedSong {
        val track = this.results.trackmatches.track[0]

        return SearchedSong(
                track.name ?: "",
                track.artist ?: "",
                ""
        )
    }

    private fun SearchedSong.normalize(): SearchedSong {
        return SearchedSong(
                NormalizedEntity(this.title).value,
                NormalizedEntity(this.artist).value,
                NormalizedEntity(this.album).value
        )
    }

}