package dev.olog.msc.api.last.fm

import dev.olog.msc.api.last.fm.album.info.AlbumInfo
import dev.olog.msc.api.last.fm.model.SearchedSong
import dev.olog.msc.api.last.fm.track.info.TrackInfo
import dev.olog.msc.api.last.fm.track.search.TrackSearch
import io.reactivex.Single
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class LastFmService @Inject constructor(
        private val lastFm: RestLastFm
) {

    private fun getTrackInfo(track: String, artist: String): Single<TrackInfo> {
        return lastFm.getTrackInfo(UTF8NormalizedEntity(track).value, UTF8NormalizedEntity(artist).value)
    }

    private fun searchTrack(track: String, artist: String): Single<TrackSearch> {
        return lastFm.searchTrack(UTF8NormalizedEntity(track).value, UTF8NormalizedEntity(artist).value)
    }

    private fun getAlbumInfo(album: String, artist: String): Single<AlbumInfo> {
        return lastFm.getAlbumInfo(UTF8NormalizedEntity(album).value, UTF8NormalizedEntity(artist).value)
    }

    fun fetchSongInfo(title: String, artist: String): Single<SearchedSong> {
        return getTrackInfo(title, artist)
                .map { it.toSearchSong() }
                .onErrorResumeNext { searchTrack(title, artist)
                            .map { it.toSearchSong() }
                            .flatMap { result -> getTrackInfo(result.title, result.artist)
                                        .map { it.toSearchSong() }
                                        .onErrorReturn { result }
                            }
                }
    }

    fun fetchAlbumArt(title: String, artist: String, album: String): Single<String> {
        val albums = if (artist.isNotBlank() && album.isNotBlank()){
            getAlbumInfo(album, artist)
        } else fetchSongInfo(title, artist)
                .flatMap { getAlbumInfo(it.album, it.artist) }

        return albums.map { it.album.image }
                .map { it.reversed().first { it.text.isNotBlank()  } }
                .map { it.text }
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

}