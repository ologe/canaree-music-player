package dev.olog.feature.detail.detail.mapper

import android.content.res.Resources
import dev.olog.core.mediaid.MediaId
import dev.olog.core.mediaid.MediaIdModifier
import dev.olog.core.entity.AutoPlaylist
import dev.olog.core.entity.sort.SortType
import dev.olog.core.entity.track.*
import dev.olog.feature.detail.R
import dev.olog.feature.detail.detail.model.*
import dev.olog.feature.detail.detail.model.DetailFragmentAlbumModel
import dev.olog.feature.detail.detail.model.DetailFragmentModel
import dev.olog.feature.detail.detail.model.DetailFragmentMostPlayedModel
import dev.olog.feature.detail.detail.model.DetailFragmentRecentlyAddedModel
import dev.olog.shared.android.DisplayableItemUtils

internal fun Artist.toRelatedArtist(resources: Resources): DetailFragmentRelatedArtistModel {
    return DetailFragmentRelatedArtistModel(
        mediaId = getMediaId(),
        title = this.name,
        subtitle = DisplayableItemUtils.readableSongCount(resources, songs)
    )
}

internal fun Track.toDetailDisplayableItem(
    parentId: MediaId,
    sortType: SortType
): DetailFragmentModel {
    val layoutRes = computeLayoutType(parentId, sortType)

    if (this is Track.PlaylistSong) {
        return DetailFragmentModel.PlaylistTrack(
            layoutRes = layoutRes,
            mediaId = getMediaId(),
            title = this.title,
            subtitle = DisplayableItemUtils.trackSubtitle(artist, album),
            idInPlaylist = this.idInPlaylist,
        )
    }

    return DetailFragmentModel.Track(
        layoutRes = computeLayoutType(parentId, sortType),
        mediaId = MediaId.playableItem(parentId, id),
        title = this.title,
        subtitle = DisplayableItemUtils.trackSubtitle(artist, album),
        trackNumber = this.trackNumber,
    )
}

private fun computeLayoutType(parentId: MediaId, sortType: SortType): Int {
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

internal fun Track.toMostPlayedDetailDisplayableItem(
    parentId: MediaId,
    position: Int
): DetailFragmentMostPlayedModel {

    val mediaId = MediaId.playableItem(parentId, id).copy(
        modifier = MediaIdModifier.MOST_PLAYED
    )

    return DetailFragmentMostPlayedModel(
        mediaId = mediaId,
        title = this.title,
        subtitle = DisplayableItemUtils.trackSubtitle(artist, album),
        position = position,
    )
}

internal fun Track.toRecentDetailDisplayableItem(
    parentId: MediaId
): DetailFragmentRecentlyAddedModel {
    val mediaId = MediaId.playableItem(parentId, id).copy(
        modifier = MediaIdModifier.RECENTLY_ADDED,
    )

    return DetailFragmentRecentlyAddedModel(
        mediaId = mediaId,
        title = this.title,
        subtitle = DisplayableItemUtils.trackSubtitle(artist, album),
    )
}

internal fun Folder.toDetailDisplayableItem(
    resources: Resources
): DetailFragmentAlbumModel {
    return DetailFragmentAlbumModel(
        mediaId = getMediaId(),
        title = title,
        subtitle = resources.getQuantityString(
            R.plurals.common_plurals_song,
            this.size,
            this.size
        ).toLowerCase()
    )
}

internal fun Playlist.toDetailDisplayableItem(
    resources: Resources
): DetailFragmentAlbumModel {
    return DetailFragmentAlbumModel(
        mediaId = getMediaId(),
        title = title,
        subtitle = resources.getQuantityString(
            R.plurals.common_plurals_song,
            this.size,
            this.size
        ).toLowerCase()
    )
}

internal fun Album.toDetailDisplayableItem(
    resources: Resources
): DetailFragmentAlbumModel {
    return DetailFragmentAlbumModel(
        mediaId = getMediaId(),
        title = title,
        subtitle = resources.getQuantityString(
            R.plurals.common_plurals_song,
            this.songs,
            this.songs
        ).toLowerCase()
    )
}

internal fun Genre.toDetailDisplayableItem(
    resources: Resources
): DetailFragmentAlbumModel {
    return DetailFragmentAlbumModel(
        mediaId = getMediaId(),
        title = name,
        subtitle = resources.getQuantityString(
            R.plurals.common_plurals_song,
            this.size,
            this.size
        ).toLowerCase()
    )
}