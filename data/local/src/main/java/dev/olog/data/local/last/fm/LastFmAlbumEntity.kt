package dev.olog.data.local.last.fm

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey
import dev.olog.domain.entity.LastFmAlbum

@Entity(
    tableName = "last_fm_album_v2",
    indices = [(Index("id"))]
)
internal data class LastFmAlbumEntity(
    @PrimaryKey
    val id: Long,
    val title: String,
    val artist: String,
    val image: String,
    val added: String,
    // new from v17
    val mbid: String,
    val wiki: String
) {

    companion object {
        val EMPTY: LastFmAlbumEntity
            get() = LastFmAlbumEntity(
                id = 0,
                title = "",
                artist = "",
                image = "",
                added = "",
                mbid = "",
                wiki = ""
            )
    }

}

internal fun LastFmAlbumEntity.toDomain(): LastFmAlbum {
    return LastFmAlbum(
        id = this.id,
        title = this.title,
        artist = this.artist,
        image = this.image,
        mbid = this.mbid,
        wiki = this.wiki
    )
}

internal fun LastFmAlbum.toModel(added: String): LastFmAlbumEntity {
    return LastFmAlbumEntity(
        id = this.id,
        title = this.title,
        artist = this.artist,
        image = this.image,
        added = added,
        mbid = this.mbid,
        wiki = this.wiki
    )
}