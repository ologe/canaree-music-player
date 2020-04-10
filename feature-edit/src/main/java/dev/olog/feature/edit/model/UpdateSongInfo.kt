package dev.olog.feature.edit.model

import dev.olog.domain.entity.track.Song

data class UpdateSongInfo(
    val originalSong: Song,
    val title: String,
    val artist: String,
    val albumArtist: String,
    val album: String,
    val genre: String,
    val year: String,
    val disc: String,
    val track: String,
    val isPodcast: Boolean
)