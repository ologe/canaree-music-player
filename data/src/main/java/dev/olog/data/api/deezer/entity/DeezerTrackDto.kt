package dev.olog.data.api.deezer.entity

internal data class DeezerTrackDto(
    val album: DeezerAlbumDto?,
    val artist: DeezerArtistDto?,
    val id: String?,
    val title: String?,
) {

    companion object {

        val EMPTY: DeezerTrackDto
            get() = DeezerTrackDto(
                album = null,
                artist = null,
                id = null,
                title = null
            )


    }

}