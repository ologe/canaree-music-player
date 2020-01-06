package dev.olog.data.model.lastfm

import java.util.*

/**
 * artist.search
 */
data class LastFmArtistSearch(
    val results: LastFmArtistResults
)

data class LastFmArtistResults(
    val artistmatches: LastFmArtistmatches
)

data class LastFmArtistmatches(
    val artist: List<LastFmArtistmatchesArtist>
)

data class LastFmArtistmatchesArtist(
    val name: String,
    val mbid: String,
    val image: List<LastFmImage> = ArrayList()
)

/**
 * artist.getinfo
 */
data class LastFmArtistInfo(
    val artist: LastFmArtist
)

data class LastFmArtist(
    val name: String,
    val mbid: String,
    val image: List<LastFmImage> = ArrayList(),
    val bio: LastFmWiki? = null
)