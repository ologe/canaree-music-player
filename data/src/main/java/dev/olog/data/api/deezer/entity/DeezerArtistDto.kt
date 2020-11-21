package dev.olog.data.api.deezer.entity

internal data class DeezerArtistDto(
    val id: String?,
    val name: String?,
    val picture_big: String?,
    val picture_medium: String?,
    val picture_small: String?,
    val picture_xl: String?,
) {

    companion object {

        val EMPTY: DeezerArtistDto
            get() = DeezerArtistDto(
                id = null,
                name = null,
                picture_big = null,
                picture_medium = null,
                picture_small = null,
                picture_xl = null,
            )

        val EMPTY_WITH_IMAGES: DeezerArtistDto
            get() = EMPTY.copy(
                picture_big = "picture_big",
                picture_medium = "picture_medium",
                picture_small = "picture_small",
                picture_xl = "picture_xl",
            )

    }

    val bestPicture: String?
        get() = picture_xl?.takeIf { it.isNotBlank() } ?:
            picture_big?.takeIf { it.isNotBlank() } ?:
            picture_medium?.takeIf { it.isNotBlank() } ?:
            picture_small?.takeIf { it.isNotBlank() }

}