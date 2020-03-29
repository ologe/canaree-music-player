package dev.olog.data.spotify.gateway

import dev.olog.core.MediaId
import dev.olog.core.entity.track.Album
import dev.olog.core.entity.track.Artist
import dev.olog.core.gateway.spotify.SpotifyGateway
import dev.olog.core.gateway.track.ArtistGateway
import dev.olog.data.shared.retrofit.fix
import dev.olog.data.shared.retrofit.flatMap
import dev.olog.data.shared.retrofit.map
import dev.olog.data.spotify.entity.SpotifyArtistAlbum
import dev.olog.data.spotify.service.SpotifyService
import me.xdrop.fuzzywuzzy.FuzzySearch
import javax.inject.Inject

internal class SpotifyGatewayImpl @Inject constructor(
    private val artistGateway: ArtistGateway,
    private val service: SpotifyService
) : SpotifyGateway {

    override suspend fun getArtistAlbums(artistMediaId: MediaId.Category): List<Album> {
        val artist = artistGateway.getByParam(artistMediaId.categoryId)!!

        return service.searchArtist(artist.name)
            .map { it.artists.items }
            .flatMap { artists ->
                val bestArtistIndex =
                    FuzzySearch.extractOne(artist.name, artists.map { it.name }).index
                val bestArtist = artists[bestArtistIndex]
                service.searchArtistAlbums(bestArtist.id)
            }.map { artistAlbums ->
                artistAlbums.items.map { it.toAlbum(artist) }
            }
            .fix(orDefault = emptyList())
    }

    private fun SpotifyArtistAlbum.toAlbum(artist: Artist): Album {
        return Album(
            id = System.currentTimeMillis(),
            artistId = artist.id,
            title = name,
            artist = artist.name,
            albumArtist = "",
            songs = total_tracks,
            hasSameNameAsFolder = false
        )
    }

}
