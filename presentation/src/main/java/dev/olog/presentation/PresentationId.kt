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

    HEADER
}

sealed class PresentationId(
    open val category: PresentationIdCategory,
    open val categoryId: Long
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
            // TODO mmmm bad conversion
            return Category(PresentationIdCategory.HEADER, value.hashCode().toLong())
        }
    }

    @Parcelize
    data class Category(
        override val category: PresentationIdCategory,
        override val categoryId: Long
    ): PresentationId(category, categoryId), Parcelable {

        fun playableItem(id: Long): Track {
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
        override val categoryId: Long,
        val id: Long
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
        is MediaId.Track -> {
            PresentationId.Track(
                category = category.toPresentation(),
                categoryId = categoryId,
                id = id
            )
        }
        is MediaId.Category -> {
            PresentationId.Category(
                category = category.toPresentation(),
                categoryId = categoryId
            )
        }
    }
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
        PresentationIdCategory.HEADER -> throwNotHandled("$this")
    }
}

val Folder.presentationId: PresentationId.Category
    get() {
        return PresentationId.Category(
            PresentationIdCategory.FOLDERS,
            this.id
        )
    }

val Playlist.presentationId: PresentationId.Category
    get() {
        if (isPodcast) {
            return PresentationId.Category(
                PresentationIdCategory.PODCASTS_PLAYLIST,
                this.id
            )
        }
        return PresentationId.Category(
            PresentationIdCategory.PLAYLISTS,
            this.id
        )
    }

val Song.presentationId: PresentationId.Track
    get() {
        if (isPodcast) {
            return PresentationId.Track(
                PresentationIdCategory.PODCASTS,
                -1,
                this.id
            )
        }
        return PresentationId.Track(
            PresentationIdCategory.SONGS,
            -1,
            this.id
        )
    }

val Song.artistPresentationId: PresentationId.Category
    get() {
        if (isPodcast) {
            return PresentationId.Category(
                PresentationIdCategory.PODCASTS_AUTHORS,
                this.artistId
            )
        }
        return PresentationId.Category(
            PresentationIdCategory.ARTISTS,
            this.artistId
        )
    }

val Song.albumPresentationId: PresentationId.Category
    get() {
        return PresentationId.Category(
            PresentationIdCategory.ALBUMS,
            albumId
        )
    }

val Album.presentationId: PresentationId.Category
    get() {
        return PresentationId.Category(
            PresentationIdCategory.ALBUMS,
            this.id
        )
    }

val Album.artistPresentationId: PresentationId.Category
    get() {
        return PresentationId.Category(
            PresentationIdCategory.ARTISTS,
            this.artistId
        )
    }

val Artist.presentationId: PresentationId.Category
    get() {
        if (isPodcast) {
            return PresentationId.Category(
                PresentationIdCategory.PODCASTS_AUTHORS,
                this.id
            )
        }
        return PresentationId.Category(
            PresentationIdCategory.ARTISTS,
            this.id
        )
    }

val Genre.presentationId: PresentationId.Category
    get() {
        return PresentationId.Category(
            PresentationIdCategory.GENRES,
            this.id
        )
    }