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
import dev.olog.presentation.detail.adapter.DetailItem
import dev.olog.presentation.detail.adapter.DetailSongMode
import dev.olog.presentation.model.DisplayableAlbum
import dev.olog.shared.TextUtils

internal fun Artist.toDetailRelatedArtist(resources: Resources): DetailItem.Album {
    return DetailItem.Album(
        mediaId = getMediaId(),
        title = this.name,
        subtitle = DisplayableAlbum.readableSongCount(resources, songs)
    )
}

internal fun Song.toDetailDisplayableItem(parentId: MediaId, sortType: SortType): DetailItem.Song {
    val trackNumber = if (trackNumber < 1) {
        "-"
    } else {
        trackNumber.toString()
    }
    val mode = when {
        parentId.isAnyAlbum -> DetailSongMode.Album(trackNumber)
        parentId.isAnyPlaylist -> {
            val playlistId = parentId.categoryValue.toLong()
            DetailSongMode.Playlist(
                idInPlaylist = idInPlaylist,
                showDragHandle = sortType == SortType.CUSTOM && !AutoPlaylist.isAutoPlaylist(playlistId)
            )
        }
        parentId.isFolder && sortType == SortType.TRACK_NUMBER -> DetailSongMode.Folder(trackNumber)
        else -> null
    }
    return DetailItem.Song(
        mediaId = MediaId.playableItem(parentId, id),
        title = title,
        subtitle = when (mode) {
            is DetailSongMode.Album -> null
            is DetailSongMode.Folder,
            is DetailSongMode.Playlist,
            null -> TextUtils.subtitle(artist, album)
        },
        mode = mode
    )
}

internal fun Song.toDetailMostPlayed(
    parentId: MediaId,
    position: Int
): DetailItem.MostPlayed {

    return DetailItem.MostPlayed(
        mediaId = MediaId.playableItem(parentId, id),
        title = this.title,
        subtitle = TextUtils.subtitle(artist, album),
        position = "${position + 1}",
    )
}

internal fun Song.toDetailRecentlyAdded(parentId: MediaId): DetailItem.RecentlyAdded {
    return DetailItem.RecentlyAdded(
        mediaId = MediaId.playableItem(parentId, id),
        title = this.title,
        subtitle = TextUtils.subtitle(artist, album),
    )
}

internal fun Folder.toDetailSiblingItem(resources: Resources): DetailItem.Album {
    return DetailItem.Album(
        mediaId = getMediaId(),
        title = title,
        subtitle = resources.getQuantityString(
            R.plurals.common_plurals_song,
            this.size,
            this.size
        ).toLowerCase()
    )
}

internal fun Playlist.toDetailSiblingItem(resources: Resources): DetailItem.Album {
    return DetailItem.Album(
        mediaId = getMediaId(),
        title = title,
        subtitle = resources.getQuantityString(
            R.plurals.common_plurals_song,
            this.size,
            this.size
        ).toLowerCase()
    )
}

internal fun Album.toDetailSiblingItem(resources: Resources): DetailItem.Album {
    return DetailItem.Album(
        mediaId = getMediaId(),
        title = title,
        subtitle = resources.getQuantityString(
            R.plurals.common_plurals_song,
            this.songs,
            this.songs
        ).toLowerCase()
    )
}

internal fun Genre.toDetailSiblingItem(resources: Resources): DetailItem.Album {
    return DetailItem.Album(
        mediaId = getMediaId(),
        title = name,
        subtitle = resources.getQuantityString(
            R.plurals.common_plurals_song,
            this.size,
            this.size
        ).toLowerCase()
    )
}