package dev.olog.feature.search.model

import android.content.Context
import dev.olog.domain.RecentSearchesType
import dev.olog.domain.entity.SearchResult
import dev.olog.domain.entity.track.*
import dev.olog.feature.search.R
import dev.olog.shared.android.DisplayableItemUtils

internal fun SearchResult.toSearchDisplayableItem(
    context: Context
): SearchFragmentModel {
    val subtitle = when (this.itemType) {
        RecentSearchesType.SONG -> context.getString(R.string.search_type_track)
        RecentSearchesType.ALBUM -> context.getString(R.string.search_type_album)
        RecentSearchesType.ARTIST -> context.getString(R.string.search_type_artist)
        RecentSearchesType.PLAYLIST -> context.getString(R.string.search_type_playlist)
        RecentSearchesType.GENRE -> context.getString(R.string.search_type_genre)
        RecentSearchesType.FOLDER -> context.getString(R.string.search_type_folder)
        RecentSearchesType.PODCAST -> context.getString(R.string.search_type_podcast)
        RecentSearchesType.PODCAST_PLAYLIST -> context.getString(R.string.search_type_podcast_playlist)
        RecentSearchesType.PODCAST_ALBUM -> context.getString(R.string.search_type_podcast_album)
        RecentSearchesType.PODCAST_ARTIST -> context.getString(R.string.search_type_podcast_artist)
    }

    val isPlayable = this.itemType.isPlayable

    if (isPlayable) {
        return SearchFragmentModel.RecentTrack(
            mediaId = this.mediaId,
            title = this.title,
            subtitle = subtitle,
        )
    }
    val layout = when (this.itemType) {
        RecentSearchesType.ARTIST,
        RecentSearchesType.PODCAST_ARTIST -> R.layout.item_search_recent_artist
        else -> R.layout.item_search_recent_album
    }
    return SearchFragmentModel.RecentAlbum(
        layoutRes = layout,
        mediaId = this.mediaId,
        title = this.title,
        subtitle = subtitle
    )
}

internal fun Track.toSearchDisplayableItem(): SearchFragmentModel.Track {
    return SearchFragmentModel.Track(
        mediaId = getMediaId(),
        title = title,
        subtitle = DisplayableItemUtils.trackSubtitle(artist, album),
    )
}

internal fun Album.toSearchDisplayableItem(): SearchFragmentModel.Album {
    return SearchFragmentModel.Album(
        layoutRes = R.layout.item_search_album,
        mediaId = getMediaId(),
        title = title,
        subtitle = artist
    )
}

internal fun Artist.toSearchDisplayableItem(): SearchFragmentModel.Album {
    return SearchFragmentModel.Album(
        layoutRes = R.layout.item_search_artist,
        mediaId = getMediaId(),
        title = name,
        subtitle = null,
    )
}

internal fun Playlist.toSearchDisplayableItem(): SearchFragmentModel.Album {
    return SearchFragmentModel.Album(
        layoutRes = R.layout.item_search_album,
        mediaId = getMediaId(),
        title = title,
        subtitle = null,
    )
}

internal fun Genre.toSearchDisplayableItem(): SearchFragmentModel.Album {
    return SearchFragmentModel.Album(
        layoutRes = R.layout.item_search_album,
        mediaId = getMediaId(),
        title = name,
        subtitle = null,
    )
}

internal fun Folder.toSearchDisplayableItem(): SearchFragmentModel.Album {
    return SearchFragmentModel.Album(
        layoutRes = R.layout.item_search_album,
        mediaId = getMediaId(),
        title = title,
        subtitle = null,
    )
}