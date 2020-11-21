package dev.olog.data.api.deezer.entity

internal data class DeezerAlbumDto(
    val artist: DeezerArtistDto?,
    val cover_big: String?,
    val cover_medium: String?,
    val cover_small: String?,
    val cover_xl: String?,
    val id: String?,
    val title: String?,
) {

    companion object {

        val EMPTY: DeezerAlbumDto
            get() = DeezerAlbumDto(
                artist = null,
                cover_big = null,
                cover_medium = null,
                cover_small = null,
                cover_xl = null,
                id = null,
                title = null,
            )

        val EMPTY_WITH_IMAGES: DeezerAlbumDto
            get() = EMPTY.copy(
                cover_big = "cover_big",
                cover_medium = "cover_medium",
                cover_small = "cover_small",
                cover_xl = "cover_xl",
            )

    }

    val bestCover: String?
        get() = cover_xl?.takeIf { it.isNotBlank() } ?:
            cover_big?.takeIf { it.isNotBlank() } ?:
            cover_medium?.takeIf { it.isNotBlank() } ?:
            cover_small?.takeIf { it.isNotBlank() }

}

