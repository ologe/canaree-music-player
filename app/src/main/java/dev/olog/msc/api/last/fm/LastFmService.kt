package dev.olog.msc.api.last.fm

import dev.olog.msc.api.last.fm.model.SearchedSong
import dev.olog.msc.api.last.fm.track.info.TrackInfo
import dev.olog.msc.api.last.fm.track.search.TrackSearch
import dev.olog.msc.constants.AppConstants
import io.reactivex.Single
import me.xdrop.fuzzywuzzy.FuzzySearch
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class LastFmService @Inject constructor(
        private val lastFm: RestLastFm
) {

    fun fetchSongInfo(title: String, artist: String): Single<SearchedSong> {
        val notUnknownArtist = if (artist == AppConstants.UNKNOWN) "" else artist

        return lastFm.getTrackInfo(title, notUnknownArtist)
                .map { it.toSearchSong() }
                .onErrorResumeNext {
                    lastFm.searchTrack(title, notUnknownArtist)
                            .map { it.toSearchSong(notUnknownArtist) }
                            .flatMap { lastFm.getTrackInfo(it.title, it.artist) }
                            .map { it.toSearchSong() }
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

    private fun TrackSearch.toSearchSong(originalTitle: String): SearchedSong {
        val tracks = this.results.trackmatches.track
        val best = FuzzySearch.extractOne(originalTitle, tracks.map { it.name }).string
        val bestMatch = tracks.first { it.name == best }

        return SearchedSong(
                bestMatch.name ?: "",
                bestMatch.artist ?: "",
                ""
        )
    }

}