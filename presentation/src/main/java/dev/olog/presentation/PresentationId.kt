package dev.olog.presentation

import android.os.Parcelable
import dev.olog.core.MediaId
import dev.olog.core.MediaIdCategory
import dev.olog.core.entity.track.*
import dev.olog.shared.throwNotHandled
import kotlinx.android.parcel.Parcelize

enum class PresentationIdCategory {
    FOLDERS,
    PLAYLISTS,
    SONGS,
    ALBUMS,
    ARTISTS,
    GENRES,

    PODCASTS_PLAYLIST,
    PODCASTS,
    PODCASTS_AUTHORS,

    SPOTIFY_ALBUMS,
    SPOTIFY_TRACK,

    HEADER
}

sealed class PresentationId(
    open val category: PresentationIdCategory,
    open val categoryId: String
) {

    val isAnyPodcast : Boolean
        get() {
            return category == PresentationIdCategory.PODCASTS_PLAYLIST ||
                    category == PresentationIdCategory.PODCASTS ||
                    category == PresentationIdCategory.PODCASTS_AUTHORS
        }

    companion object {
        @JvmStatic
        fun headerId(value: String): Category {
            return Category(PresentationIdCategory.HEADER, value)
        }
    }

    @Parcelize
    data class Category(
        override val category: PresentationIdCategory,
        override val categoryId: String
    ): PresentationId(category, categoryId), Parcelable {

        fun playableItem(id: Long): Track {
            return playableItem("$id")
        }

        fun playableItem(id: String): Track {
            return Track(
                category = this.category,
                categoryId = this.categoryId,
                id = id
            )
        }

    }

    @Parcelize
    data class Track(
        override val category: PresentationIdCategory,
        override val categoryId: String,
        val id: String
    ): PresentationId(category, categoryId), Parcelable

}

fun PresentationId.toDomain(): MediaId {
    return when (this) {
        is PresentationId.Category -> toDomain()
        is PresentationId.Track -> toDomain()
    }
}

fun PresentationId.Category.toDomain(): MediaId.Category {
    return MediaId.Category(
        category = this.category.toDomain(),
        categoryId = this.categoryId
    )
}

fun PresentationId.Track.toDomain(): MediaId.Track {
    return MediaId.Track(
        category = this.category.toDomain(),
        categoryId = this.categoryId,
        id = this.id
    )
}

fun MediaId.toPresentation(): PresentationId {
    return when (this) {
        is MediaId.Track -> this.toPresentation()
        is MediaId.Category -> this.toPresentation()
    }
}

fun MediaId.Track.toPresentation(): PresentationId.Track {
    return PresentationId.Track(
        category = category.toPresentation(),
        categoryId = categoryId,
        id = id
    )
}

fun MediaId.Category.toPresentation(): PresentationId.Category {
    return PresentationId.Category(
        category = category.toPresentation(),
        categoryId = categoryId
    )
}

// TODO test
fun MediaIdCategory.toPresentation() : PresentationIdCategory {
    return when (this) {
        MediaIdCategory.FOLDERS -> PresentationIdCategory.FOLDERS
        MediaIdCategory.PLAYLISTS -> PresentationIdCategory.PLAYLISTS
        MediaIdCategory.SONGS -> PresentationIdCategory.SONGS
        MediaIdCategory.ALBUMS -> PresentationIdCategory.ALBUMS
        MediaIdCategory.ARTISTS -> PresentationIdCategory.ARTISTS
        MediaIdCategory.GENRES -> PresentationIdCategory.GENRES
        MediaIdCategory.PODCASTS_PLAYLIST -> PresentationIdCategory.PODCASTS_PLAYLIST
        MediaIdCategory.PODCASTS -> PresentationIdCategory.PODCASTS
        MediaIdCategory.PODCASTS_AUTHORS -> PresentationIdCategory.PODCASTS_AUTHORS
        MediaIdCategory.SPOTIFY_ALBUMS -> PresentationIdCategory.SPOTIFY_ALBUMS
        MediaIdCategory.SPOTIFY_TRACK -> PresentationIdCategory.SPOTIFY_TRACK
    }
}

fun PresentationIdCategory.toDomain() : MediaIdCategory {
    return when (this) {
        PresentationIdCategory.FOLDERS -> MediaIdCategory.FOLDERS
        PresentationIdCategory.PLAYLISTS -> MediaIdCategory.PLAYLISTS
        PresentationIdCategory.SONGS -> MediaIdCategory.SONGS
        PresentationIdCategory.ALBUMS -> MediaIdCategory.ALBUMS
        PresentationIdCategory.ARTISTS -> MediaIdCategory.ARTISTS
        PresentationIdCategory.GENRES -> MediaIdCategory.GENRES
        PresentationIdCategory.PODCASTS_PLAYLIST -> MediaIdCategory.PODCASTS_PLAYLIST
        PresentationIdCategory.PODCASTS -> MediaIdCategory.PODCASTS
        PresentationIdCategory.PODCASTS_AUTHORS -> MediaIdCategory.PODCASTS_AUTHORS
        PresentationIdCategory.SPOTIFY_ALBUMS -> MediaIdCategory.SPOTIFY_ALBUMS
        PresentationIdCategory.SPOTIFY_TRACK -> MediaIdCategory.SPOTIFY_TRACK
        PresentationIdCategory.HEADER -> throwNotHandled(this)
    }
}

val Folder.presentationId: PresentationId.Category
    get() = mediaId.toPresentation()

val Playlist.presentationId: PresentationId.Category
    get() = mediaId.toPresentation()

val Song.presentationId: PresentationId.Track
    get() = mediaId.toPresentation()

val Song.artistPresentationId: PresentationId.Category
    get() = artistMediaId.toPresentation()

val Song.albumPresentationId: PresentationId.Category
    get() = albumMediaId.toPresentation()

val Album.presentationId: PresentationId.Category
    get() = mediaId.toPresentation()

val Album.artistPresentationId: PresentationId.Category
    get() = artistMediaId.toPresentation()

val Artist.presentationId: PresentationId.Category
    get() = mediaId.toPresentation()

val Genre.presentationId: PresentationId.Category
    get() = mediaId.toPresentation()