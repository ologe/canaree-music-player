package dev.olog.presentation.detail.mapper

import android.content.res.Resources
import dev.olog.core.MediaId
import dev.olog.core.entity.AutoPlaylist
import dev.olog.core.entity.sort.SortType
import dev.olog.core.entity.track.*
import dev.olog.presentation.R
import dev.olog.presentation.detail.adapter.DetailFragmentItem
import dev.olog.presentation.detail.adapter.DetailMostPlayedItem
import dev.olog.presentation.detail.adapter.DetailRecentlyAddedItem
import dev.olog.presentation.detail.adapter.DetailRelatedArtistItem
import dev.olog.presentation.detail.adapter.DetailSiblingItem
import dev.olog.presentation.model.DisplayableAlbum
import dev.olog.presentation.model.DisplayableTrack

internal fun Artist.toRelatedArtist(resources: Resources): DetailRelatedArtistItem {
    return DetailRelatedArtistItem(
        mediaId = getMediaId(),
        title = this.name,
        subtitle = DisplayableAlbum.readableSongCount(resources, songs)
    )
}

internal fun Song.toDetailDisplayableItem(parentId: MediaId, sortType: SortType): DetailFragmentItem.Track {
    val trackNumber = if (trackNumber < 1) "-" else trackNumber.toString()
    return when {
        parentId.isAlbum || parentId.isPodcastAlbum -> DetailFragmentItem.Track.ForAlbum(
            mediaId = MediaId.playableItem(parentId, id),
            title = this.title,
            subtitle = DisplayableTrack.subtitle(artist, album),
            trackNumber = trackNumber,
        )
        (parentId.isPlaylist || parentId.isPodcastPlaylist) && sortType == SortType.CUSTOM -> {
            val playlistId = parentId.categoryValue.toLong()
            if (AutoPlaylist.isAutoPlaylist(playlistId)) {
                DetailFragmentItem.Track.Default(
                    mediaId = MediaId.playableItem(parentId, id),
                    title = this.title,
                    subtitle = DisplayableTrack.subtitle(artist, album),
                )
            } else DetailFragmentItem.Track.ForPlaylist(
                mediaId = MediaId.playableItem(parentId, id),
                title = this.title,
                subtitle = DisplayableTrack.subtitle(artist, album),
                idInPlaylist = idInPlaylist,
            )
        }
        parentId.isFolder && sortType == SortType.TRACK_NUMBER -> DetailFragmentItem.Track.ForFolder(
            mediaId = MediaId.playableItem(parentId, id),
            title = this.title,
            subtitle = DisplayableTrack.subtitle(artist, album),
            trackNumber = trackNumber,
        )
        else -> DetailFragmentItem.Track.Default(
            mediaId = MediaId.playableItem(parentId, id),
            title = this.title,
            subtitle = DisplayableTrack.subtitle(artist, album),
        )
    }
}

internal fun Song.toMostPlayedDetailDisplayableItem(
    parentId: MediaId,
    position: Int
): DetailMostPlayedItem {

    return DetailMostPlayedItem(
        mediaId = MediaId.playableItem(parentId, id),
        title = this.title,
        subtitle = DisplayableTrack.subtitle(artist, album),
        position = position.toString(),
    )
}

internal fun Song.toRecentDetailDisplayableItem(parentId: MediaId): DetailRecentlyAddedItem {
    return DetailRecentlyAddedItem(
        mediaId = MediaId.playableItem(parentId, id),
        title = this.title,
        subtitle = DisplayableTrack.subtitle(artist, album),
    )
}

internal fun Folder.toDetailDisplayableItem(resources: Resources): DetailSiblingItem {
    return DetailSiblingItem(
        mediaId = getMediaId(),
        title = title,
        subtitle = resources.getQuantityString(
            R.plurals.common_plurals_song,
            this.size,
            this.size
        ).toLowerCase()
    )
}

internal fun Playlist.toDetailDisplayableItem(resources: Resources): DetailSiblingItem {
    return DetailSiblingItem(
        mediaId = getMediaId(),
        title = title,
        subtitle = resources.getQuantityString(
            R.plurals.common_plurals_song,
            this.size,
            this.size
        ).toLowerCase()
    )
}

internal fun Album.toDetailDisplayableItem(resources: Resources): DetailSiblingItem {
    return DetailSiblingItem(
        mediaId = getMediaId(),
        title = title,
        subtitle = resources.getQuantityString(
            R.plurals.common_plurals_song,
            this.songs,
            this.songs
        ).toLowerCase()
    )
}

internal fun Genre.toDetailDisplayableItem(resources: Resources): DetailSiblingItem {
    return DetailSiblingItem(
        mediaId = getMediaId(),
        title = name,
        subtitle = resources.getQuantityString(
            R.plurals.common_plurals_song,
            this.size,
            this.size
        ).toLowerCase()
    )
}