package dev.olog.presentation.detail.mapper

import android.content.res.Resources
import dev.olog.domain.entity.AutoPlaylist
import dev.olog.domain.entity.sort.SortType
import dev.olog.domain.entity.track.*
import dev.olog.feature.presentation.base.model.PresentationId
import dev.olog.feature.presentation.base.model.PresentationIdCategory
import dev.olog.presentation.R
import dev.olog.feature.presentation.base.model.DisplayableAlbum
import dev.olog.feature.presentation.base.model.DisplayableTrack
import dev.olog.feature.presentation.base.model.presentationId

internal fun Artist.toRelatedArtist(resources: Resources): DisplayableAlbum {
    return DisplayableAlbum(
        type = R.layout.item_detail_related_artist,
        mediaId = presentationId,
        title = this.name,
        subtitle = DisplayableAlbum.readableSongCount(
            resources,
            songs
        )
    )
}

internal fun Song.toDetailDisplayableItem(parentId: PresentationId.Category, sortType: SortType): DisplayableTrack {
    val idInPlaylist = when (parentId.category) {
        PresentationIdCategory.PLAYLISTS,
        PresentationIdCategory.PODCASTS_PLAYLIST -> this.idInPlaylist
        else -> this.trackNumber
    }

    return DisplayableTrack(
        type = computeLayoutType(parentId, sortType),
        mediaId = parentId.playableItem(id),
        title = this.title,
        artist = artist,
        album = album,
        idInPlaylist = idInPlaylist,
        dataModified = this.dateModified,
        duration = this.duration
    )
}

private fun computeLayoutType(parentId: PresentationId.Category, sortType: SortType): Int {
    val category = parentId.category
    return when {
        parentId.isAnyPodcast -> R.layout.item_detail_podcast
        category == PresentationIdCategory.ALBUMS -> R.layout.item_detail_song_with_track
        category == PresentationIdCategory.PLAYLISTS && sortType == SortType.CUSTOM -> {
            val playlistId = parentId.categoryId.toLong()
            if (AutoPlaylist.isAutoPlaylist(playlistId)) {
                R.layout.item_detail_song
            } else R.layout.item_detail_song_with_drag_handle
        }
        category == PresentationIdCategory.FOLDERS && sortType == SortType.TRACK_NUMBER -> R.layout.item_detail_song_with_track_and_image
        else -> R.layout.item_detail_song
    }
}

internal fun Song.toMostPlayedDetailDisplayableItem(
    parentId: PresentationId.Category,
    position: Int
): DisplayableTrack {

    return DisplayableTrack(
        type = R.layout.item_detail_song_most_played,
        mediaId = parentId.playableItem(id),
        title = this.title,
        artist = this.artist,
        album = this.album,
        idInPlaylist = position,
        dataModified = this.dateModified,
        duration = this.duration
    )
}

internal fun Song.toRecentDetailDisplayableItem(parentId: PresentationId.Category): DisplayableTrack {
    return DisplayableTrack(
        type = R.layout.item_detail_song_recent,
        mediaId = parentId.playableItem(id),
        title = this.title,
        artist = this.artist,
        album = this.album,
        idInPlaylist = this.idInPlaylist,
        dataModified = this.dateModified,
        duration = this.duration
    )
}

internal fun Folder.toDetailDisplayableItem(resources: Resources): DisplayableAlbum {
    return DisplayableAlbum(
        type = R.layout.item_detail_album,
        mediaId = presentationId,
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
        mediaId = presentationId,
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
        mediaId = presentationId,
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
        mediaId = presentationId,
        title = name,
        subtitle = resources.getQuantityString(
            R.plurals.common_plurals_song,
            this.size,
            this.size
        ).toLowerCase()
    )
}