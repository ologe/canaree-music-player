package dev.olog.core.entity

data class LastFmTrack(
    val id: Long,
    val title: String,
    val artist: String,
    val album: String,
    val image: String,
    val mbid: String,
    val artistMbid: String,
    val albumMbid: String
) {

    companion object

}

data class LastFmAlbum(
    val id: Long,
    val title: String,
    val artist: String,
    val image: String,
    val mbid: String,
    val wiki: String
) {

    companion object

}

data class LastFmArtist(
    val id: Long,
    val image: String,
    val mbid: String,
    val wiki: String
) {

    companion object

}

val LastFmTrack.Companion.EMPTY: LastFmTrack
    get() = LastFmTrack(
        id = 0,
        title = "",
        artist = "",
        album = "",
        image = "",
        mbid = "",
        artistMbid = "",
        albumMbid = ""
    )

val LastFmAlbum.Companion.EMPTY: LastFmAlbum
    get() = LastFmAlbum(
        id = 0,
        title = "",
        artist = "",
        image = "",
        mbid = "",
        wiki = ""
    )

val LastFmArtist.Companion.EMPTY: LastFmArtist
    get() = LastFmArtist(
        id = 0,
        image = "",
        mbid = "",
        wiki = ""
    )

