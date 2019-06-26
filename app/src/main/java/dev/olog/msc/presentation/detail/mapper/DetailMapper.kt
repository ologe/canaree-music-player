package dev.olog.msc.presentation.detail.mapper

import android.content.res.Resources
import dev.olog.core.MediaId
import dev.olog.core.entity.AutoPlaylist
import dev.olog.core.entity.sort.SortType
import dev.olog.core.entity.track.*
import dev.olog.msc.R
import dev.olog.presentation.model.DisplayableItem
import dev.olog.shared.utils.TextUtils

internal fun Artist.toRelatedArtist(resources: Resources): DisplayableItem {
    val songs = DisplayableItem.handleSongListSize(resources, songs)
    var albums = DisplayableItem.handleAlbumListSize(resources, albums)
    if (albums.isNotBlank()) albums+= TextUtils.MIDDLE_DOT_SPACED

    return DisplayableItem(
        R.layout.item_detail_related_artist,
        getMediaId(),
        this.name,
        albums + songs
    )
}

internal fun Song.toDetailDisplayableItem(parentId: MediaId, sortType: SortType): DisplayableItem {
    val viewType = when {
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

    val subtitle = when {
        parentId.isArtist || parentId.isPodcastArtist -> DisplayableItem.adjustAlbum(this.album)
        else -> DisplayableItem.adjustArtist(this.artist)
    }

    val track = when {
        parentId.isPlaylist || parentId.isPodcastPlaylist -> this.trackNumber.toString()
        this.trackNumber == 0 -> "-"
        else -> this.trackNumber.toString()
    }

    return DisplayableItem(
        viewType,
        MediaId.playableItem(parentId, id),
        this.title,
        subtitle,
        true,
        track
    )
}

internal fun Song.toMostPlayedDetailDisplayableItem(parentId: MediaId): DisplayableItem {
    return DisplayableItem(
        R.layout.item_detail_song_most_played,
        MediaId.playableItem(parentId, id),
        this.title,
        DisplayableItem.adjustArtist(this.artist),
        true
    )
}

internal fun Song.toRecentDetailDisplayableItem(parentId: MediaId): DisplayableItem {
    return DisplayableItem(
        R.layout.item_detail_song_recent,
        MediaId.playableItem(parentId, id),
        this.title,
        DisplayableItem.adjustArtist(this.artist),
        true
    )
}

internal fun Folder.toDetailDisplayableItem(resources: Resources): DisplayableItem {
    return DisplayableItem(
        R.layout.item_detail_album,
        getMediaId(),
        title,
        resources.getQuantityString(R.plurals.common_plurals_song, this.size, this.size).toLowerCase()
    )
}

internal fun Playlist.toDetailDisplayableItem(resources: Resources): DisplayableItem {
    return DisplayableItem(
        R.layout.item_detail_album,
        getMediaId(),
        title,
        resources.getQuantityString(R.plurals.common_plurals_song, this.size, this.size).toLowerCase()
    )
}

internal fun Album.toDetailDisplayableItem(resources: Resources): DisplayableItem {
    return DisplayableItem(
        R.layout.item_detail_album,
        getMediaId(),
        title,
        resources.getQuantityString(R.plurals.common_plurals_song, this.songs, this.songs).toLowerCase()
    )
}

internal fun Genre.toDetailDisplayableItem(resources: Resources): DisplayableItem {
    return DisplayableItem(
        R.layout.item_detail_album,
        getMediaId(),
        name,
        resources.getQuantityString(R.plurals.common_plurals_song, this.size, this.size).toLowerCase()
    )
}