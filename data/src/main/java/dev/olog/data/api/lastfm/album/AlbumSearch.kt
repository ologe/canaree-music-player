package dev.olog.data.api.lastfm.album

import kotlinx.serialization.Serializable

@Serializable
class AlbumSearch(
    val results: Results?,
) {

    @Serializable
    class Results(
        val albummatches: Albummatches?,
    )
}