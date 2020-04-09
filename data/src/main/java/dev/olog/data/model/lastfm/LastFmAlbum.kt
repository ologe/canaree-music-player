package dev.olog.data.model.lastfm

import com.google.gson.annotations.SerializedName
import java.util.*

/**
 * album.search
 */
data class LastFmAlbumSearch(
    var results: LastFmAlbumResults
)

data class LastFmAlbumResults(
    var albummatches: LastFmAlbummatches
)

data class LastFmAlbummatches(
    val album: List<LastFmAlbummatchesAlbum>
)

data class LastFmAlbummatchesAlbum(
    val name: String,
    val artist: String,
    val mbid: String,
    val image: List<LastFmImage> = ArrayList()
)

/**
 * album.getInfo
 */
data class LastFmAlbumInfo(
    val album: LastFmAlbumRemote
)

data class LastFmAlbumRemote(
    @SerializedName(value = "name", alternate = ["title"]) val name: String,
    val artist: String,
    val mbid: String,
    val image: List<LastFmImage> = ArrayList(),
    val wiki: LastFmWiki? = null
)
