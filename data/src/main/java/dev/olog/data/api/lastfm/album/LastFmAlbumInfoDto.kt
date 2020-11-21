package dev.olog.data.api.lastfm.album

import com.google.gson.annotations.SerializedName
import dev.olog.core.entity.LastFmAlbum
import dev.olog.data.api.lastfm.entity.LastFmImageDto
import dev.olog.data.api.lastfm.entity.LastFmWikiDto

internal data class LastFmAlbumInfoDto(
    val album: LastFmAlbumInfoResultDto?
) {

    companion object {

        val EMPTY: LastFmAlbumInfoDto
            get() = LastFmAlbumInfoDto(
                album = null
            )

    }

}

internal data class LastFmAlbumInfoResultDto(
    val artist: String?,
    val image: List<LastFmImageDto>?,
    val mbid: String?,
    @SerializedName("name", alternate=["title"])
    val name: String?,
    val wiki: LastFmWikiDto?
) {

    companion object {

        val EMPTY: LastFmAlbumInfoResultDto
            get() = LastFmAlbumInfoResultDto(
                artist = null,
                image = null,
                mbid = null,
                name = null,
                wiki = null,
            )


    }

}

internal fun LastFmAlbumInfoDto.toDomain(id: Long): LastFmAlbum? {
    val album = this.album ?: return null
    val image = album.image?.findLast { it.text.orEmpty().isNotBlank() }?.text ?: return null

    return LastFmAlbum(
        id = id,
        title = album.name.orEmpty(),
        artist = album.artist.orEmpty(),
        image = image,
        mbid = album.mbid.orEmpty(),
        wiki = album.wiki?.content.orEmpty()
    )
}