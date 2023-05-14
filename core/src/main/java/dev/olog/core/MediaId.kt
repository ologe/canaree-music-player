package dev.olog.core

import android.net.Uri
import android.os.Parcelable
import dev.olog.core.entity.track.AutoPlaylist
import kotlinx.android.parcel.Parcelize

enum class MediaIdCategory(val key: String) {
    FOLDERS("folders"),
    PLAYLISTS("playlists"),
    AUTO_PLAYLISTS("auto_playlists"),
    SONGS("tracks"),
    ALBUMS("albums"),
    ARTISTS("artists"),
    GENRES("genres"),

    @Deprecated("")
    HEADER("headers"),
    @Deprecated("")
    PLAYING_QUEUE("playing_queue")
}

@Parcelize
class MediaId private constructor(
    private val opaqueUri: String,
) : Parcelable {

    companion object {
        private const val SCHEME = "canaree"
        private const val IS_PODCAST = "is_podcast"

        @Deprecated("")
        fun headerId(value: String): MediaId {
            return MediaId(buildUri(MediaIdCategory.HEADER, value, false))
        }

        @Deprecated("")
        val playingQueueId: MediaId = MediaId(buildUri(MediaIdCategory.PLAYING_QUEUE, "", false))

        @Deprecated("")
        fun shuffleId(): MediaId {
            return MediaId(buildUri(MediaIdCategory.SONGS, "shuffle", false))
        }

        // TODO better check, rename to `of`
        fun fromString(opaqueUri: String): MediaId {
            require(Uri.parse(opaqueUri).scheme == SCHEME) { opaqueUri }
            return MediaId(opaqueUri)
        }

        fun ofTrack(id: Long, isPodcast: Boolean): MediaId {
            val uri = buildUri(
                category = MediaIdCategory.SONGS,
                id = id.toString(),
                isPodcast = isPodcast,
            )
            return MediaId(uri)
        }

        fun ofFolder(id: Long, isPodcast: Boolean): MediaId {
            val uri = buildUri(
                category = MediaIdCategory.FOLDERS,
                id = id.toString(),
                isPodcast = isPodcast,
            )
            return MediaId(uri)
        }

        fun ofPlaylist(id: Long, isPodcast: Boolean): MediaId {
            val uri = buildUri(
                category = MediaIdCategory.PLAYLISTS,
                id = id.toString(),
                isPodcast = isPodcast,
            )
            return MediaId(uri)
        }

        fun ofAutoPlaylist(id: AutoPlaylist.Id, isPodcast: Boolean): MediaId {
            val uri = buildUri(
                category = MediaIdCategory.AUTO_PLAYLISTS,
                id = id.key.toString(),
                isPodcast = isPodcast,
            )
            return MediaId(uri)
        }

        fun ofAlbum(id: Long, isPodcast: Boolean): MediaId {
            val uri = buildUri(
                category = MediaIdCategory.ALBUMS,
                id = id.toString(),
                isPodcast = isPodcast,
            )
            return MediaId(uri)
        }

        fun ofArtist(id: Long, isPodcast: Boolean): MediaId {
            val uri = buildUri(
                category = MediaIdCategory.ARTISTS,
                id = id.toString(),
                isPodcast = isPodcast,
            )
            return MediaId(uri)
        }

        fun ofGenre(id: Long, isPodcast: Boolean): MediaId {
            val uri = buildUri(
                category = MediaIdCategory.ARTISTS,
                id = id.toString(),
                isPodcast = isPodcast,
            )
            return MediaId(uri)
        }

        private fun buildUri(
            category: MediaIdCategory,
            id: String, // TODO make it Long after removing header and playing queue category?
            isPodcast: Boolean,
        ): String {
            return buildString {
                append(Uri.encode(SCHEME))
                append(":")
                append(Uri.encode(category.key))
                append(":")
                append(Uri.encode(id))
                append(":")
                if (isPodcast) {
                    append(Uri.encode(IS_PODCAST))
                }
            }
        }

    }

    private val parts by lazy {
        opaqueUri.split(":")
    }

    val scheme: String
        get() = parts[0]

    val category: MediaIdCategory
        get() {
            val key = parts[1]
            return MediaIdCategory.values().first { it.key == key }
        }

    val id: Long
        get() = parts[2].toLong()

    // TODO double check usage, now it means is any podcast type (track, album, playlist, ..)
    val isPodcast: Boolean
        get() = parts[3] == IS_PODCAST

    override fun toString(): String = opaqueUri

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as MediaId

        if (opaqueUri != other.opaqueUri) return false

        return true
    }

    override fun hashCode(): Int {
        return opaqueUri.hashCode()
    }

}