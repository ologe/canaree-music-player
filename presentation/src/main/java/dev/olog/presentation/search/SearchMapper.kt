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
import dev.olog.presentation.search.adapter.SearchFragmentItem

internal fun SearchResult.toSearchDisplayableItem(context: Context): SearchFragmentItem.Recent {
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

    return SearchFragmentItem.Recent(
        mediaId = this.mediaId,
        title = this.title,
        subtitle = subtitle,
        isPlayable = this.itemType == RecentSearchesTypes.SONG ||
            this.itemType == RecentSearchesTypes.PODCAST
    )
}

internal fun Song.toSearchDisplayableItem(): SearchFragmentItem.Track {
    return SearchFragmentItem.Track(
        mediaId = getMediaId(),
        title = title,
        artist = artist,
        album = album,
    )
}

internal fun Album.toSearchDisplayableItem(): SearchFragmentItem.Album {
    return SearchFragmentItem.Album(
        mediaId = getMediaId(),
        title = title,
        subtitle = artist
    )
}

internal fun Artist.toSearchDisplayableItem(): SearchFragmentItem.Album {
    return SearchFragmentItem.Album(
        mediaId = getMediaId(),
        title = name,
        subtitle = null
    )
}

internal fun Playlist.toSearchDisplayableItem(): SearchFragmentItem.Album {
    return SearchFragmentItem.Album(
        mediaId = getMediaId(),
        title = title,
        subtitle = null
    )
}

internal fun Genre.toSearchDisplayableItem(): SearchFragmentItem.Album {
    return SearchFragmentItem.Album(
        mediaId = getMediaId(),
        title = name,
        subtitle = null
    )
}

internal fun Folder.toSearchDisplayableItem(): SearchFragmentItem.Album {
    return SearchFragmentItem.Album(
        mediaId = getMediaId(),
        title = title,
        subtitle = null,
    )
}