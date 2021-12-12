package dev.olog.feature.detail.detail

import android.content.res.Resources
import dev.olog.core.MediaId
import dev.olog.core.entity.sort.SortType
import dev.olog.core.entity.track.*
import dev.olog.feature.base.model.DisplayableAlbum
import dev.olog.feature.base.model.DisplayableTrack
import dev.olog.feature.detail.R

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
    )
}

@Suppress("NOTHING_TO_INLINE")
private inline fun computeLayoutType(parentId: MediaId, sortType: SortType): Int {
    // TODO
    return when {
        parentId.isAlbum || parentId.isPodcastAlbum -> R.layout.item_detail_song_with_track
        (parentId.isPlaylist || parentId.isPodcastPlaylist) && sortType.serialized == "custom" -> {
            val playlistId = parentId.categoryValue.toLong()
            if (Playlist.isAutoPlaylist(playlistId)) {
                R.layout.item_detail_song
            } else R.layout.item_detail_song_with_drag_handle
        }
        parentId.isFolder && sortType.serialized == "track_number" -> R.layout.item_detail_song_with_track_and_image
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
    )
}

internal fun Folder.toDetailDisplayableItem(resources: Resources): DisplayableAlbum {
    return DisplayableAlbum(
        type = R.layout.item_detail_album,
        mediaId = getMediaId(),
        title = title,
        subtitle = resources.getQuantityString(
            localization.R.plurals.common_plurals_song,
            this.songs,
            this.songs
        ).toLowerCase()
    )
}

internal fun Playlist.toDetailDisplayableItem(resources: Resources): DisplayableAlbum {
    return DisplayableAlbum(
        type = R.layout.item_detail_album,
        mediaId = getMediaId(),
        title = title,
        subtitle = resources.getQuantityString(
            localization.R.plurals.common_plurals_song,
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
            localization.R.plurals.common_plurals_song,
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
            localization.R.plurals.common_plurals_song,
            this.songs,
            this.songs
        ).toLowerCase()
    )
}