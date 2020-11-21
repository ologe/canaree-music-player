package dev.olog.data.local.last.fm

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey
import dev.olog.core.entity.LastFmTrack

@Entity(
    tableName = "last_fm_track_v2",
    indices = [(Index("id"))]
)
internal data class LastFmTrackEntity(
    @PrimaryKey val id: Long,
    val title: String,
    val artist: String,
    val album: String,
    val image: String,
    val added: String,
    // new from v17
    val mbid: String,
    val artistMbid: String,
    val albumMbid: String
) {

    companion object {

        internal val EMPTY: LastFmTrackEntity
            get() = LastFmTrackEntity(
                id = 0,
                title = "",
                artist = "",
                album = "",
                image = "",
                added = "",
                mbid = "",
                artistMbid = "",
                albumMbid = ""
            )

    }

}

internal fun LastFmTrackEntity.toDomain(): LastFmTrack {
    return LastFmTrack(
        id = this.id,
        title = this.title,
        artist = this.artist,
        album = this.album,
        image = this.image,
        mbid = this.mbid,
        artistMbid = this.artistMbid,
        albumMbid = this.albumMbid
    )
}

internal fun LastFmTrack.toModel(added: String): LastFmTrackEntity {
    return LastFmTrackEntity(
        id = this.id,
        title = this.title,
        artist = this.artist,
        album = this.album,
        image = this.image,
        added = added,
        mbid = this.mbid,
        artistMbid = this.artistMbid,
        albumMbid = this.albumMbid
    )
}