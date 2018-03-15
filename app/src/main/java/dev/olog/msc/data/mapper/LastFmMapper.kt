package dev.olog.msc.data.mapper

import dev.olog.msc.api.last.fm.album.info.AlbumInfo
import dev.olog.msc.api.last.fm.album.search.AlbumSearch
import dev.olog.msc.api.last.fm.artist.info.ArtistInfo
import dev.olog.msc.api.last.fm.track.info.TrackInfo
import dev.olog.msc.api.last.fm.track.search.TrackSearch
import dev.olog.msc.data.entity.LastFmAlbumEntity
import dev.olog.msc.data.entity.LastFmArtistEntity
import dev.olog.msc.data.entity.LastFmTrackEntity
import dev.olog.msc.domain.entity.LastFmAlbum
import dev.olog.msc.domain.entity.LastFmTrack
import me.xdrop.fuzzywuzzy.FuzzySearch
import java.text.SimpleDateFormat
import java.util.*

private val formatter = SimpleDateFormat("yyyy-MM-dd", Locale.US)

private fun millisToFormattedDate(value: Long): String {
    return formatter.format(Date(value))

}

fun LastFmTrackEntity.toDomain(): LastFmTrack {
    return LastFmTrack(
            this.id,
            this.title,
            this.artist,
            this.album,
            this.image
    )
}

fun LastFmAlbumEntity.toDomain(): LastFmAlbum {
    return LastFmAlbum(
            this.id,
            this.title,
            this.artist,
            this.image
    )
}

fun TrackInfo.toDomain(id: Long): LastFmTrack {
    val track = this.track
    val title = track.name
    val artist = track.artist.name
    val album = track.album.title
    val image = track.album.image.reversed().first { it.text.isNotBlank() }.text

    return LastFmTrack(
            id,
            title ?: "",
            artist ?: "",
            album ?: "",
            image
    )
}

fun LastFmTrack.toModel(title: String, artist: String, album: String): LastFmTrackEntity {
    return LastFmTrackEntity(
            this.id,
            title,
            artist,
            album,
            this.image,
            millisToFormattedDate(System.currentTimeMillis())
    )
}


fun TrackSearch.toDomain(id: Long): LastFmTrack {
    val track = this.results.trackmatches.track[0]

    return LastFmTrack(
            id,
            track.name ?: "",
            track.artist ?: "",
            "",
            ""
    )
}

fun AlbumInfo.toDomain(id: Long): LastFmAlbum {
    val album = this.album
    return LastFmAlbum(
            id,
            album.name,
            album.artist,
            album.image.reversed().first { it.text.isNotBlank() }.text
    )
}

fun LastFmAlbum.toModel(title: String, artist: String): LastFmAlbumEntity {
    return LastFmAlbumEntity(
            this.id,
            title,
            artist,
            this.image,
            millisToFormattedDate(System.currentTimeMillis())
    )
}

fun AlbumSearch.toDomain(id: Long, originalArtist: String): LastFmAlbum {
    val results = this.results.albummatches.album
    val bestArtist = FuzzySearch.extractOne(originalArtist, results.map { it.artist }).string
    val best = results.first { it.artist == bestArtist }

    return LastFmAlbum(
            id,
            best.name,
            best.artist,
            best.image.reversed().first { it.text.isNotBlank() }.text
    )
}

fun ArtistInfo.toModel(id: Long): LastFmArtistEntity {
    val artist = this.artist
    return LastFmArtistEntity(
            id,
            artist.image.reversed().first { it.text.isNotBlank() }.text,
            millisToFormattedDate(System.currentTimeMillis())
    )
}

object LastFmNulls {

    fun createNullArtist(artistId: Long): LastFmArtistEntity {
        return LastFmArtistEntity(
                artistId,
                "",
                millisToFormattedDate(System.currentTimeMillis())
        )
    }

    fun createNullTrack(trackId: Long): LastFmTrackEntity {
        return LastFmTrackEntity(
                trackId,
                "",
                "",
                "",
                "",
                millisToFormattedDate(System.currentTimeMillis())
        )
    }

    fun createNullAlbum(albumId: Long): LastFmAlbumEntity {
        return LastFmAlbumEntity(
                albumId,
                "",
                "",
                "",
                millisToFormattedDate(System.currentTimeMillis())
        )
    }

}
