package dev.olog.presentation.search

import android.content.Context
import dev.olog.core.RecentSearchesTypes
import dev.olog.core.entity.SearchResult
import dev.olog.core.entity.track.*
import dev.olog.presentation.R
import dev.olog.feature.base.model.DisplayableAlbum
import dev.olog.feature.base.model.DisplayableItem
import dev.olog.feature.base.model.DisplayableTrack

internal fun SearchResult.toSearchDisplayableItem(context: Context): DisplayableItem {
    val subtitle = when (this.itemType) {
        RecentSearchesTypes.SONG -> context.getString(R.string.search_type_track)
        RecentSearchesTypes.ALBUM -> context.getString(R.string.search_type_album)
        RecentSearchesTypes.ARTIST -> context.getString(R.string.search_type_artist)
        RecentSearchesTypes.PLAYLIST -> context.getString(R.string.search_type_playlist)
        RecentSearchesTypes.GENRE -> context.getString(R.string.search_type_genre)
        RecentSearchesTypes.FOLDER -> context.getString(R.string.search_type_folder)
        RecentSearchesTypes.PODCAST -> context.getString(R.string.search_type_podcast)
        RecentSearchesTypes.PODCAST_PLAYLIST -> context.getString(R.string.search_type_podcast_playlist)
        RecentSearchesTypes.PODCAST_ALBUM -> context.getString(R.string.search_type_podcast_album)
        RecentSearchesTypes.PODCAST_ARTIST -> context.getString(R.string.search_type_podcast_artist)
        else -> throw IllegalArgumentException("invalid item type $itemType")
    }

    val isPlayable =
        this.itemType == RecentSearchesTypes.SONG || this.itemType == RecentSearchesTypes.PODCAST

    val layout = when (this.itemType) {
        RecentSearchesTypes.ARTIST,
        RecentSearchesTypes.PODCAST_ARTIST -> R.layout.item_search_recent_artist
        RecentSearchesTypes.ALBUM,
        RecentSearchesTypes.PODCAST_ALBUM -> R.layout.item_search_recent_album
        else -> R.layout.item_search_recent
    }

    if (isPlayable){
        return DisplayableTrack(
            type = layout,
            mediaId = this.mediaId,
            title = this.title,
            artist = subtitle,
            album = "",
            idInPlaylist = -1,
            dataModified = -1
        )
    }
    return DisplayableAlbum(
        type = layout,
        mediaId = this.mediaId,
        title = this.title,
        subtitle = subtitle
    )
}

internal fun Song.toSearchDisplayableItem(): DisplayableTrack {
    return DisplayableTrack(
        type = R.layout.item_search_song,
        mediaId = getMediaId(),
        title = title,
        artist = artist,
        album = album,
        idInPlaylist = idInPlaylist,
        dataModified = this.dateModified
    )
}

internal fun Album.toSearchDisplayableItem(): DisplayableAlbum {
    return DisplayableAlbum(
        type = R.layout.item_search_album,
        mediaId = getMediaId(),
        title = title,
        subtitle = artist
    )
}

internal fun Artist.toSearchDisplayableItem(): DisplayableAlbum {
    return DisplayableAlbum(
        type = R.layout.item_search_artist,
        mediaId = getMediaId(),
        title = name,
        subtitle = ""
    )
}

internal fun Playlist.toSearchDisplayableItem(): DisplayableAlbum {
    return DisplayableAlbum(
        type = R.layout.item_search_album,
        mediaId = getMediaId(),
        title = title,
        subtitle = ""
    )
}

internal fun Genre.toSearchDisplayableItem(): DisplayableAlbum {
    return DisplayableAlbum(
        type = R.layout.item_search_album,
        mediaId = getMediaId(),
        title = name,
        subtitle = ""
    )
}

internal fun Folder.toSearchDisplayableItem(): DisplayableAlbum {
    return DisplayableAlbum(
        type = R.layout.item_search_album,
        mediaId = getMediaId(),
        title = title,
        subtitle = ""
    )
}