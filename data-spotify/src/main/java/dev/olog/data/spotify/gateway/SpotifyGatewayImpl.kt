package dev.olog.data.spotify.gateway

import dev.olog.core.MediaId
import dev.olog.core.entity.spotify.SpotifyAlbum
import dev.olog.core.entity.spotify.SpotifyAlbumType
import dev.olog.core.entity.spotify.SpotifyTrack
import dev.olog.core.entity.track.Artist
import dev.olog.core.gateway.spotify.SpotifyGateway
import dev.olog.core.gateway.track.ArtistGateway
import dev.olog.data.shared.retrofit.IoResult
import dev.olog.data.shared.retrofit.fix
import dev.olog.data.shared.retrofit.flatMap
import dev.olog.data.shared.retrofit.map
import dev.olog.data.spotify.entity.RemoteSpotifyAlbum
import dev.olog.data.spotify.entity.RemoteSpotifyArtist
import dev.olog.data.spotify.entity.RemoteSpotifyTrack
import dev.olog.data.spotify.service.SpotifyService
import dev.olog.shared.throwNotHandled
import me.xdrop.fuzzywuzzy.FuzzySearch
import javax.inject.Inject

internal class SpotifyGatewayImpl @Inject constructor(
    private val artistGateway: ArtistGateway,
    private val service: SpotifyService
) : SpotifyGateway {

    override suspend fun getArtistAlbums(artistMediaId: MediaId.Category): List<SpotifyAlbum> {
        val artist = artistGateway.getByParam(artistMediaId.categoryId.toLong())!!

        return findSpotifyArtistBestMatch(artist)
            .flatMap { service.getArtistAlbums(it.id) }
            .map { artistAlbums ->
                artistAlbums.items.map { it.toDomain() }
            }
            .fix(orDefault = emptyList())
    }

    override suspend fun getArtistTopTracks(artistMediaId: MediaId.Category): List<SpotifyTrack> {
        val artist = artistGateway.getByParam(artistMediaId.categoryId.toLong())!!

        return findSpotifyArtistBestMatch(artist)
            .flatMap { service.getArtistTopTracks(it.id) }
            .map {   topTracks ->
                topTracks.tracks.map { it.toDomain() }
            }
            .fix(orDefault = emptyList())
    }

    private suspend fun findSpotifyArtistBestMatch(artist: Artist): IoResult<RemoteSpotifyArtist> {
        return service.searchArtist(artist.name)
            .map { it.artists.items }
            .map { artists ->
                val bestArtistIndex =
                    FuzzySearch.extractOne(artist.name, artists.map { it.name }).index
                artists[bestArtistIndex]
            }
    }

    private fun RemoteSpotifyTrack.toDomain(): SpotifyTrack {
        return SpotifyTrack(
            id = this.id,
            name = this.name
        )
    }

    private fun RemoteSpotifyAlbum.toDomain(): SpotifyAlbum {
        return SpotifyAlbum(
            id = this.id,
            name = this.name,
            albumType = this.album_type.mapAlbumType(),
            image = this.images.maxBy { it.height }!!.url,
            tracks = this.total_tracks
        )
    }

    private fun String.mapAlbumType(): SpotifyAlbumType {
        return when (this){
            "album" -> SpotifyAlbumType.ALBUM
            "single" -> SpotifyAlbumType.SINGLE
            "appears_on" -> SpotifyAlbumType.APPEARS_ON
            "compilation" -> SpotifyAlbumType.COMPILATION
            else -> throwNotHandled(this)
        }
    }

}
