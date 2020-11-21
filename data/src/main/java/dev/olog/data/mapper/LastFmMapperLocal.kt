package dev.olog.data.mapper

import dev.olog.core.entity.LastFmAlbum
import dev.olog.core.entity.LastFmArtist
import dev.olog.core.entity.LastFmTrack
import dev.olog.data.db.entities.LastFmAlbumEntity
import dev.olog.data.db.entities.LastFmArtistEntity
import dev.olog.data.db.entities.LastFmTrackEntity
import java.text.SimpleDateFormat
import java.util.*

private val formatter = SimpleDateFormat("yyyy-MM-dd", Locale.US).apply {
    timeZone = TimeZone.getTimeZone("UTC")
}

private fun millisToFormattedDate(value: Long): String {
    return formatter.format(Date(value))

}

internal fun LastFmTrackEntity.toDomain(): LastFmTrack {
    return LastFmTrack(
        this.id,
        this.title,
        this.artist,
        this.album,
        this.image,
        this.mbid,
        this.artistMbid,
        this.albumMbid
    )
}

internal fun LastFmAlbumEntity.toDomain(): LastFmAlbum {
    return LastFmAlbum(
        this.id,
        this.title,
        this.artist,
        this.image,
        this.mbid,
        this.wiki
    )
}



internal fun LastFmTrack.toModel(): LastFmTrackEntity {
    return LastFmTrackEntity(
        this.id,
        this.title,
        this.artist,
        this.album,
        this.image,
        millisToFormattedDate(System.currentTimeMillis()),
        this.mbid,
        this.artistMbid,
        this.albumMbid
    )
}



internal fun LastFmAlbum.toModel(): LastFmAlbumEntity {
    return LastFmAlbumEntity(
        this.id,
        this.title,
        this.artist,
        this.image,
        millisToFormattedDate(System.currentTimeMillis()),
        this.mbid,
        this.wiki
    )
}

internal fun LastFmArtistEntity.toDomain(): LastFmArtist {
    return LastFmArtist(
        this.id,
        this.image,
        this.mbid,
        this.wiki
    )
}

internal fun LastFmArtist.toModel() : LastFmArtistEntity{
    return LastFmArtistEntity(
        this.id,
        this.image,
        millisToFormattedDate(System.currentTimeMillis()),
        this.mbid,
        this.wiki
    )
}

object LastFmNulls {

    internal fun createNullTrack(trackId: Long): LastFmTrackEntity {
        return LastFmTrackEntity(
            trackId,
            "",
            "",
            "",
            "",
            millisToFormattedDate(System.currentTimeMillis()),
            "",
            "",
            ""
        )
    }

    internal fun createNullArtist(artistId: Long): LastFmArtistEntity {
        return LastFmArtistEntity(
            artistId,
            "",
            millisToFormattedDate(System.currentTimeMillis()),
            "",
            ""
        )
    }

    internal fun createNullAlbum(albumId: Long): LastFmAlbumEntity {
        return LastFmAlbumEntity(
            albumId,
            "",
            "",
            "",
            millisToFormattedDate(System.currentTimeMillis()),
            "",
            ""
        )
    }

}
