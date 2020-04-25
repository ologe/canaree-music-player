package dev.olog.data.model.lastfm

/**
 * artist.getinfo
 */
data class LastFmArtistInfo(
    val artist: LastFmArtistRemote?
)

data class LastFmArtistRemote(
    val name: String?,
    val mbid: String?,
    val image: List<LastFmImage>?,
    val bio: LastFmWiki?
)