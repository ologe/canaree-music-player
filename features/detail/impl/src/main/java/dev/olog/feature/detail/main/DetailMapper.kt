package dev.olog.feature.detail.main

import android.content.res.Resources
import dev.olog.core.MediaId
import dev.olog.core.entity.AutoPlaylist
import dev.olog.core.entity.sort.SortType
import dev.olog.core.entity.track.*
import dev.olog.feature.detail.R
import dev.olog.ui.model.DisplayableAlbum
import dev.olog.ui.model.DisplayableTrack

internal fun Artist.toRelatedArtist(resources: Resources): DisplayableAlbum {
    return DisplayableAlbum(
        type = R.layout.item_detail_related_artist,
        mediaId = getMediaId(),
        title = this.name,
        subtitle = DisplayableAlbum.readableSongCount(resources, songs)
    )
}

internal fun Song.toDetailDisplayableItem(parentId: MediaId, sortType: SortType): DisplayableTrack {
    val idInPlaylist = if (parentId.isPlaylist || parentId.isPodcastPlaylist){
        this.idInPlaylist
    } else {
        this.trackNumber
    }

    return DisplayableTrack(
        type = computeLayoutType(parentId, sortType),
        mediaId = MediaId.playableItem(parentId, id),
        title = this.title,
        artist = artist,
        album = album,
        idInPlaylist = idInPlaylist,
        dataModified = this.dateModified
    )
}

@Suppress("NOTHING_TO_INLINE")
private inline fun computeLayoutType(parentId: MediaId, sortType: SortType): Int {
    return when {
        parentId.isAlbum || parentId.isPodcastAlbum -> R.layout.item_detail_song_with_track
        (parentId.isPlaylist || parentId.isPodcastPlaylist) && sortType == SortType.CUSTOM -> {
            val playlistId = parentId.categoryValue.toLong()
            if (AutoPlaylist.isAutoPlaylist(playlistId)) {
                R.layout.item_detail_song
            } else R.layout.item_detail_song_with_drag_handle
        }
        parentId.isFolder && sortType == SortType.TRACK_NUMBER -> R.layout.item_detail_song_with_track_and_image
        else -> R.layout.item_detail_song
    }
}

internal fun Song.toMostPlayedDetailDisplayableItem(
    parentId: MediaId,
    position: Int
): DisplayableTrack {

    return DisplayableTrack(
        type = R.layout.item_detail_song_most_played,
        mediaId = MediaId.playableItem(parentId, id),
        title = this.title,
        artist = this.artist,
        album = this.album,
        idInPlaylist = position,
        dataModified = this.dateModified
    )
}

internal fun Song.toRecentDetailDisplayableItem(parentId: MediaId): DisplayableTrack {
    return DisplayableTrack(
        type = R.layout.item_detail_song_recent,
        mediaId = MediaId.playableItem(parentId, id),
        title = this.title,
        artist = this.artist,
        album = this.album,
        idInPlaylist = this.idInPlaylist,
        dataModified = this.dateModified
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
            this.songs,
            this.songs
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