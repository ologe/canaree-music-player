package dev.olog.data.api.lastfm.album

class AlbumSearch(
    val results: Results?,
) {

    class Results(
        val albummatches: Albummatches?,
    )
}