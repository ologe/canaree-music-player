package dev.olog.presentation.detail.mapper

import android.content.res.Resources
import dev.olog.core.MediaId
import dev.olog.core.MediaIdCategory
import dev.olog.core.entity.sort.SortType
import dev.olog.core.entity.track.*
import dev.olog.presentation.R
import dev.olog.presentation.model.DisplayableAlbum
import dev.olog.presentation.model.DisplayableTrack

internal fun Artist.toRelatedArtist(resources: Resources): DisplayableAlbum {
    return DisplayableAlbum(
        type = R.layout.item_detail_related_artist,
        mediaId = getMediaId(),
        title = this.name,
        subtitle = DisplayableAlbum.readableSongCount(resources, songs)
    )
}

internal fun Song.toDetailDisplayableItem(parentId: MediaId, sortType: SortType): DisplayableTrack {
    val idInPlaylist = when (parentId.category) {
        MediaIdCategory.PLAYLISTS -> this.idInPlaylist
        else -> this.trackNumber
    }

    return DisplayableTrack(
        type = computeLayoutType(parentId, sortType),
        mediaId = getMediaId(), // TODO parent id?
        title = this.title,
        artist = artist,
        album = album,
        idInPlaylist = idInPlaylist,
    )
}

@Suppress("NOTHING_TO_INLINE")
private inline fun computeLayoutType(parentId: MediaId, sortType: SortType): Int {
    return when {
        parentId.category == MediaIdCategory.ALBUMS -> R.layout.item_detail_song_with_track
        parentId.category == MediaIdCategory.PLAYLISTS && sortType == SortType.CUSTOM -> {
            if (parentId.category == MediaIdCategory.AUTO_PLAYLISTS) {
                R.layout.item_detail_song
            } else {
                R.layout.item_detail_song_with_drag_handle
            }
        }
        parentId.category == MediaIdCategory.FOLDERS && sortType == SortType.TRACK_NUMBER -> {
            R.layout.item_detail_song_with_track_and_image
        }
        else -> R.layout.item_detail_song
    }
}

internal fun Song.toMostPlayedDetailDisplayableItem(
    parentId: MediaId,
    position: Int
): DisplayableTrack {

    return DisplayableTrack(
        type = R.layout.item_detail_song_most_played,
        mediaId = getMediaId(), // TODO parent id?
        title = this.title,
        artist = this.artist,
        album = this.album,
        idInPlaylist = position,
    )
}

internal fun Song.toRecentDetailDisplayableItem(parentId: MediaId): DisplayableTrack {
    return DisplayableTrack(
        type = R.layout.item_detail_song_recent,
        mediaId = getMediaId(), // TODO parent id?
        title = this.title,
        artist = this.artist,
        album = this.album,
        idInPlaylist = this.idInPlaylist,
    )
}

internal fun Folder.toDetailDisplayableItem(resources: Resources): DisplayableAlbum {
    return DisplayableAlbum(
        type = R.layout.item_detail_album,
        mediaId = getMediaId(),
        title = title,
        subtitle = resources.getQuantityString(
            R.plurals.common_plurals_song,
            this.size,
            this.size
        ).toLowerCase()
    )
}

internal fun Playlist.toDetailDisplayableItem(resources: Resources): DisplayableAlbum {
    return DisplayableAlbum(
        type = R.layout.item_detail_album,
        mediaId = getMediaId(),
        title = title,
        subtitle = resources.getQuantityString(
            R.plurals.common_plurals_song,
            this.size,
            this.size
        ).toLowerCase()
    )
}

internal fun Album.toDetailDisplayableItem(resources: Resources): DisplayableAlbum {
    return DisplayableAlbum(
        type = R.layout.item_detail_album,
        mediaId = getMediaId(),
        title = title,
        subtitle = resources.getQuantityString(
            R.plurals.common_plurals_song,
            this.size,
            this.size
        ).toLowerCase()
    )
}

internal fun Genre.toDetailDisplayableItem(resources: Resources): DisplayableAlbum {
    return DisplayableAlbum(
        type = R.layout.item_detail_album,
        mediaId = getMediaId(),
        title = name,
        subtitle = resources.getQuantityString(
            R.plurals.common_plurals_song,
            this.size,
            this.size
        ).toLowerCase()
    )
}