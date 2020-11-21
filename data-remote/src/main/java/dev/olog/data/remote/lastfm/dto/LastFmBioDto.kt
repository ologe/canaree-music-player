package dev.olog.data.remote.lastfm.dto

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