package dev.olog.data.api.lastfm.entity

internal data class LastFmBioDto(
    val content: String?,
    val published: String?,
    val summary: String?
) {

    companion object {

        val EMPTY: LastFmBioDto
            get() = LastFmBioDto(
                content = null,
                published = null,
                summary = null
            )


    }

}