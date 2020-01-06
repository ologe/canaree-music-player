package dev.olog.data.model.lastfm

/**
 * track.search
 */
data class LastFmTrackSearch(
    val results: LastFmTrackResults
)

data class LastFmTrackResults(
    val trackmatches: LastFmTrackmatches
)

data class LastFmTrackmatches(
    val track: List<LastFmTrackmatchesTrack>

)

data class LastFmTrackmatchesTrack(
    val name: String,
    val mbid: String,
    val artist: String,
    val image: List<LastFmImage>
)

/**
 * track.getInfo
 */
data class LastFmTrackInfo(
    val track: LastFmTrack
)

data class LastFmTrack(
    val name: String,
    val mbid: String,
    val artist: LastFmArtist,
    val album: LastFmAlbum,
    val image: List<LastFmImage>,
    val wiki: LastFmWiki? = null
)