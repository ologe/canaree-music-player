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
import dev.olog.data.spotify.db.SpotifyImageEntity
import dev.olog.data.spotify.db.SpotifyImagesDao
import dev.olog.data.spotify.entity.RemoteSpotifyAlbum
import dev.olog.data.spotify.entity.RemoteSpotifyArtist
import dev.olog.data.spotify.entity.RemoteSpotifyTrack
import dev.olog.data.spotify.service.SpotifyService
import dev.olog.shared.throwNotHandled
import me.xdrop.fuzzywuzzy.FuzzySearch
import java.text.SimpleDateFormat
import java.util.*
import javax.inject.Inject

internal class SpotifyGatewayImpl @Inject constructor(
    private val artistGateway: ArtistGateway,
    private val service: SpotifyService,
    private val imageDao: SpotifyImagesDao
) : SpotifyGateway {

    private val pattern = "yyyy-MM"
    private val dateFormatter = SimpleDateFormat(pattern, Locale.US)

    override suspend fun getArtistAlbums(
        artistMediaId: MediaId.Category,
        type: SpotifyAlbumType
    ): List<SpotifyAlbum> {
        val artist = artistGateway.getByParam(artistMediaId.categoryId.toLong())!!

        return findSpotifyArtistBestMatch(artist)
            .flatMap { service.getArtistAlbums(it.id, type.value) }
            .map { artistAlbums ->
                artistAlbums.items.map { it.toDomain() }
                    .distinctBy { it.title }
                    .sortedByDescending { it.releaseDate }
            }
            .fix(orDefault = emptyList())
            .also { albums ->
                imageDao.insertImages(albums.map { SpotifyImageEntity(it.uri, it.image) })
            }
    }

    override suspend fun getArtistTopTracks(artistMediaId: MediaId.Category): List<SpotifyTrack> {
        val artist = artistGateway.getByParam(artistMediaId.categoryId.toLong())!!

        return findSpotifyArtistBestMatch(artist)
            .flatMap { service.getArtistTopTracks(it.id) }
            .map {   topTracks ->
                topTracks.tracks.map { it.toDomain() }
            }
            .fix(orDefault = emptyList())
            .also { tracks ->
                imageDao.insertImages(tracks.map { SpotifyImageEntity(it.uri, it.image) })
            }
    }

    override fun getImage(spotifyUri: String): String? {
        return imageDao.getImage(spotifyUri)
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
            name = this.name,
            uri = this.uri,
            image = this.album.images.maxBy { it.height }!!.url
        )
    }

    private fun RemoteSpotifyAlbum.toDomain(): SpotifyAlbum {
        val date = dateFormatter.parse(this.release_date.take(pattern.length))!!.time
        return SpotifyAlbum(
            id = this.id,
            title = this.name,
            albumType = this.album_type.mapAlbumType(),
            image = this.images.maxBy { it.height }!!.url,
            songs = this.total_tracks,
            uri = this.uri,
            releaseDate = date
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
