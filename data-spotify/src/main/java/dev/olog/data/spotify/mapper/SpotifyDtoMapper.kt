package dev.olog.data.spotify.mapper

import android.provider.MediaStore
import dev.olog.data.spotify.dto.RemoteSpotifyAlbum
import dev.olog.data.spotify.dto.RemoteSpotifyTrack
import dev.olog.data.spotify.dto.RemoteSpotifyTrackAudioFeature
import dev.olog.data.spotify.entity.SpotifyTrackAudioFeatureEntity
import dev.olog.data.spotify.entity.SpotifyTrackEntity
import dev.olog.domain.entity.spotify.SpotifyAlbum
import dev.olog.domain.entity.spotify.SpotifyTrack
import dev.olog.domain.entity.track.Song

internal fun RemoteSpotifyAlbum.toDomain(): SpotifyAlbum {
    return SpotifyAlbum(
        id = this.id,
        title = this.name,
        albumType = this.album_type.mapAlbumType(),
        image = this.images.maxBy { it.height }!!.url,
        songs = this.total_tracks,
        uri = this.uri
    )
}

internal fun RemoteSpotifyTrack.toDomain(): SpotifyTrack {
    return SpotifyTrack(
        id = this.id,
        name = this.name,
        artist = this.artists.firstOrNull()?.name ?: MediaStore.UNKNOWN_STRING,
        album = this.album?.name ?: MediaStore.UNKNOWN_STRING,
        uri = this.uri,
        image = this.album?.images?.maxBy { it.height }?.url ?: "",
        discNumber = this.disc_number,
        trackNumber = this.track_number,
        duration = this.duration_ms.toLong(),
        isExplicit = this.explicit,
        previewUrl = this.preview_url
    )
}

internal fun RemoteSpotifyTrack?.toEntity(song: Song): SpotifyTrackEntity {
    return SpotifyTrackEntity(
        localId = song.id,
        spotifyId = this?.id ?: "",
        duration_ms = this?.duration_ms ?: -1,
        explicit = this?.explicit ?: false,
        name = this?.name ?: "",
        popularity = this?.popularity ?: -1,
        previewUrl = this?.preview_url ?: "",
        trackNumber = this?.track_number ?: -1,
        discNumber = this?.disc_number ?: -1,
        uri = this?.uri ?: "",
        image = this?.album?.images?.maxBy { it.height }?.url ?: "",
        album = this?.album?.name ?: "",
        albumId = this?.album?.id ?: "",
        albumType = this?.album?.album_type ?: "",
        albumUri = this?.album?.uri ?: "",
        releaseDate = this?.album?.release_date ?: ""
    )
}

internal fun RemoteSpotifyTrackAudioFeature.toEntity(trackEntity: SpotifyTrackEntity): SpotifyTrackAudioFeatureEntity {
    return SpotifyTrackAudioFeatureEntity(
        localId = trackEntity.localId,
        spotifyId = this.id,
        valence = this.valence,
        track_href = this.track_href,
        tempo = this.tempo,
        speechiness = this.speechiness,
        mode = this.mode,
        loudness = this.loudness,
        liveness = this.liveness,
        key = this.key,
        instrumentalness = this.instrumentalness,
        energy = this.energy,
        danceability = this.danceability,
        analysis_url = this.analysis_url,
        acousticness = this.acousticness,
        duration_ms = this.duration_ms,
        uri = this.uri
    )
}