package dev.olog.presentation.detail.mapper

import android.content.res.Resources
import dev.olog.core.MediaId
import dev.olog.core.entity.AutoPlaylist
import dev.olog.core.entity.sort.SortType
import dev.olog.core.entity.track.Album
import dev.olog.core.entity.track.Artist
import dev.olog.core.entity.track.Folder
import dev.olog.core.entity.track.Genre
import dev.olog.core.entity.track.Playlist
import dev.olog.core.entity.track.Song
import dev.olog.presentation.R
import dev.olog.presentation.detail.adapter.DetailMostPlayedItem
import dev.olog.presentation.detail.adapter.DetailRecentlyAddedItem
import dev.olog.presentation.detail.adapter.DetailRelatedArtistsItem
import dev.olog.presentation.detail.adapter.DetailSiblingsItem
import dev.olog.presentation.model.DisplayableAlbum
import dev.olog.presentation.model.DisplayableTrack
import dev.olog.shared.TextUtils

internal fun Artist.toDetailRelatedArtist(resources: Resources): DetailRelatedArtistsItem {
    return DetailRelatedArtistsItem(
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

internal fun Song.toDetailMostPlayed(
    parentId: MediaId,
    position: Int
): DetailMostPlayedItem {

    return DetailMostPlayedItem(
        mediaId = MediaId.playableItem(parentId, id),
        title = this.title,
        subtitle = "$artist${TextUtils.MIDDLE_DOT_SPACED}$album",
        position = position + 1,
    )
}

internal fun Song.toDetailRecentlyAdded(parentId: MediaId): DetailRecentlyAddedItem {
    return DetailRecentlyAddedItem(
        mediaId = MediaId.playableItem(parentId, id),
        title = this.title,
        subtitle = "$artist${TextUtils.MIDDLE_DOT_SPACED}$album",
    )
}

internal fun Folder.toDetailSiblingItem(resources: Resources): DetailSiblingsItem {
    return DetailSiblingsItem(
        mediaId = getMediaId(),
        title = title,
        subtitle = resources.getQuantityString(
            R.plurals.common_plurals_song,
            this.size,
            this.size
        ).toLowerCase()
    )
}

internal fun Playlist.toDetailSiblingItem(resources: Resources): DetailSiblingsItem {
    return DetailSiblingsItem(
        mediaId = getMediaId(),
        title = title,
        subtitle = resources.getQuantityString(
            R.plurals.common_plurals_song,
            this.size,
            this.size
        ).toLowerCase()
    )
}

internal fun Album.toDetailSiblingItem(resources: Resources): DetailSiblingsItem {
    return DetailSiblingsItem(
        mediaId = getMediaId(),
        title = title,
        subtitle = resources.getQuantityString(
            R.plurals.common_plurals_song,
            this.songs,
            this.songs
        ).toLowerCase()
    )
}

internal fun Genre.toDetailSiblingItem(resources: Resources): DetailSiblingsItem {
    return DetailSiblingsItem(
        mediaId = getMediaId(),
        title = name,
        subtitle = resources.getQuantityString(
            R.plurals.common_plurals_song,
            this.size,
            this.size
        ).toLowerCase()
    )
}