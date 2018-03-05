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
import dev.olog.msc.domain.entity.LastFmArtist
import dev.olog.msc.domain.entity.LastFmTrack
import me.xdrop.fuzzywuzzy.FuzzySearch

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

fun LastFmArtistEntity.toDomain(): LastFmArtist {
    return LastFmArtist(
            this.id,
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

fun TrackInfo.toModel(id: Long): LastFmTrackEntity {
    val track = this.track
    val title = track.name
    val artist = track.artist.name
    val album = track.album.title
    val image = track.album.image.reversed().first { it.text.isNotBlank() }.text

    return LastFmTrackEntity(
            id,
            title ?: "",
            artist ?: "",
            album ?: "",
            image
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

fun AlbumInfo.toModel(id: Long): LastFmAlbumEntity {
    val album = this.album
    return LastFmAlbumEntity(
            id,
            album.name,
            album.artist,
            album.image.reversed().first { it.text.isNotBlank() }.text
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
            artist.image.reversed().first { it.text.isNotBlank() }.text
    )
}