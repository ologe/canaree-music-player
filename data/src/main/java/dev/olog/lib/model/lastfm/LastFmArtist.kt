package dev.olog.lib.model.lastfm

import java.util.*

/**
 * artist.getinfo
 */
data class LastFmArtistInfo(
    val artist: LastFmArtistRemote
)

data class LastFmArtistRemote(
    val name: String,
    val mbid: String?,
    val image: List<LastFmImage> = ArrayList(),
    val bio: LastFmWiki? = null
)