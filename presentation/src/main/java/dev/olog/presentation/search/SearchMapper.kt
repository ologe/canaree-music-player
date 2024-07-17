package dev.olog.presentation.search

import android.content.Context
import dev.olog.core.RecentSearchesTypes
import dev.olog.core.entity.SearchResult
import dev.olog.core.entity.track.Album
import dev.olog.core.entity.track.Artist
import dev.olog.core.entity.track.Folder
import dev.olog.core.entity.track.Genre
import dev.olog.core.entity.track.Playlist
import dev.olog.core.entity.track.Song
import dev.olog.presentation.R
import dev.olog.presentation.search.adapter.SearchItem
import dev.olog.shared.TextUtils

internal fun SearchResult.toSearchDisplayableItem(context: Context): SearchItem {
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

    return SearchItem.Recent(
        mediaId = this.mediaId,
        title = this.title,
        subtitle = subtitle
    )
}

internal fun Song.toSearchDisplayableItem(): SearchItem.Song {
    return SearchItem.Song(
        mediaId = getMediaId(),
        title = title,
        subtitle = TextUtils.subtitle(artist, album),
    )
}

internal fun Album.toSearchDisplayableItem(): SearchItem.Album {
    return SearchItem.Album(
        mediaId = getMediaId(),
        title = title,
        subtitle = artist
    )
}

internal fun Artist.toSearchDisplayableItem(): SearchItem.Album {
    return SearchItem.Album(
        mediaId = getMediaId(),
        title = name,
        subtitle = null
    )
}

internal fun Playlist.toSearchDisplayableItem(): SearchItem.Album {
    return SearchItem.Album(
        mediaId = getMediaId(),
        title = title,
        subtitle = null
    )
}

internal fun Genre.toSearchDisplayableItem(): SearchItem.Album {
    return SearchItem.Album(
        mediaId = getMediaId(),
        title = name,
        subtitle = null
    )
}

internal fun Folder.toSearchDisplayableItem(): SearchItem.Album {
    return SearchItem.Album(
        mediaId = getMediaId(),
        title = title,
        subtitle = null,
    )
}