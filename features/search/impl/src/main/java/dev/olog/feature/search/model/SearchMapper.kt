package dev.olog.feature.search.model

import android.content.Context
import dev.olog.core.RecentSearchesTypes
import dev.olog.core.entity.SearchResult

internal fun SearchResult.toSearchDisplayableItem(context: Context): SearchRecentItem {
    val subtitle = when (this.itemType) {
        RecentSearchesTypes.SONG -> context.getString(localization.R.string.search_type_track)
        RecentSearchesTypes.ALBUM -> context.getString(localization.R.string.search_type_album)
        RecentSearchesTypes.ARTIST -> context.getString(localization.R.string.search_type_artist)
        RecentSearchesTypes.PLAYLIST -> context.getString(localization.R.string.search_type_playlist)
        RecentSearchesTypes.GENRE -> context.getString(localization.R.string.search_type_genre)
        RecentSearchesTypes.FOLDER -> context.getString(localization.R.string.search_type_folder)
        RecentSearchesTypes.PODCAST -> context.getString(localization.R.string.search_type_podcast)
        RecentSearchesTypes.PODCAST_PLAYLIST -> context.getString(localization.R.string.search_type_podcast_playlist)
        RecentSearchesTypes.PODCAST_ALBUM -> context.getString(localization.R.string.search_type_podcast_album)
        RecentSearchesTypes.PODCAST_ARTIST -> context.getString(localization.R.string.search_type_podcast_artist)
        else -> error("invalid item type $itemType")
    }

    val isPlayable = this.itemType == RecentSearchesTypes.SONG ||
        this.itemType == RecentSearchesTypes.PODCAST

    return SearchRecentItem(
        mediaId = this.mediaId,
        title = this.title,
        subtitle = subtitle,
        isPlayable = isPlayable,
        isPodcast = this.mediaId.isAnyPodcast
    )
}