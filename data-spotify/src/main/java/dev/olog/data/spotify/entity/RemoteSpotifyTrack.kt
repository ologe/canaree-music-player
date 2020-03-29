package dev.olog.data.spotify.entity

import dev.olog.data.spotify.entity.complex.RemoteSpotifyArtistAlbum

data class RemoteSpotifyTrack(
    val album: RemoteSpotifyArtistAlbum,
    val artists: List<RemoteSpotifyArtist>,
    val disc_number: Int,
    val duration_ms: Int,
    val explicit: Boolean,
    val href: String,
    val id: String,
    val is_playable: Boolean,
    val name: String,
    val popularity: Int,
    val preview_url: String,
    val track_number: Int
)