package dev.olog.data.api.lastfm.entity

internal data class LastFmWikiDto(
    val content: String?,
    val published: String?,
    val summary: String?
) {

    companion object {

        val EMPTY: LastFmWikiDto
            get() = LastFmWikiDto(
                content = null,
                published = null,
                summary = null
            )


    }

}