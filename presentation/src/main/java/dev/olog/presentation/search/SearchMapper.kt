package dev.olog.presentation.search

import android.content.Context
import dev.olog.core.RecentSearchesTypes
import dev.olog.core.entity.SearchResult
import dev.olog.core.entity.track.*
import dev.olog.presentation.R
import dev.olog.presentation.model.DisplayableItem

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

    return DisplayableItem(
        layout,
        this.mediaId,
        this.title,
        subtitle,
        isPlayable
    )
}

internal fun Song.toSearchDisplayableItem(): DisplayableItem {
    return DisplayableItem(
        R.layout.item_search_song,
        getMediaId(),
        title,
        DisplayableItem.adjustArtist(artist),
        true
    )
}

internal fun Album.toSearchDisplayableItem(): DisplayableItem {
    return DisplayableItem(
        R.layout.item_search_album,
        getMediaId(),
        title,
        DisplayableItem.adjustArtist(artist)
    )
}

internal fun Artist.toSearchDisplayableItem(): DisplayableItem {
    return DisplayableItem(
        R.layout.item_search_artist,
        getMediaId(),
        name,
        null
    )
}

internal fun Playlist.toSearchDisplayableItem(): DisplayableItem {
    return DisplayableItem(
        R.layout.item_search_album,
        getMediaId(),
        title,
        null
    )
}

internal fun Genre.toSearchDisplayableItem(): DisplayableItem {
    return DisplayableItem(
        R.layout.item_search_album,
        getMediaId(),
        name,
        null
    )
}

internal fun Folder.toSearchDisplayableItem(): DisplayableItem {
    return DisplayableItem(
        R.layout.item_search_album,
        getMediaId(),
        title,
        null
    )
}