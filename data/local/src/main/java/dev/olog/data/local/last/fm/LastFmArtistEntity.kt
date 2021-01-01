package dev.olog.data.local.last.fm

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey
import dev.olog.domain.entity.LastFmArtist

@Entity(
    tableName = "last_fm_artist_v2",
    indices = [(Index("id"))]
)
internal data class LastFmArtistEntity(
    @PrimaryKey
    val id: Long,
    val image: String,
    val added: String,
    // new from v17
    val mbid: String,
    val wiki: String
) {

    companion object {
        internal val EMPTY: LastFmArtistEntity
            get() = LastFmArtistEntity(
                id = 0,
                image = "",
                added = "",
                mbid = "",
                wiki = ""
            )

    }

}

internal fun LastFmArtistEntity.toDomain(): LastFmArtist {
    return LastFmArtist(
        id = this.id,
        image = this.image,
        mbid = this.mbid,
        wiki = this.wiki
    )
}

internal fun LastFmArtist.toModel(added: String) : LastFmArtistEntity {
    return LastFmArtistEntity(
        id = this.id,
        image = this.image,
        added = added,
        mbid = this.mbid,
        wiki = this.wiki
    )
}