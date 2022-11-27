package dev.olog.core.entity

data class LastFmTrack(
    val id: Long,
    val title: String?,
    val artist: String?,
    val album: String?,
    val image: String?,
    val mbid: String?,
    val artistMbid: String?,
    val albumMbid: String?,
)

data class LastFmAlbum(
    val id: Long,
    val title: String?,
    val artist: String?,
    val image: String?,
    val mbid: String?,
    val wiki: String?,
)

data class LastFmArtist(
    val id: Long,
    val image: String?,
    val mbid: String?,
    val wiki: String?,
)