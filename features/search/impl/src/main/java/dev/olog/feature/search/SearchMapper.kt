package dev.olog.feature.search

import android.content.Context
import dev.olog.core.MediaIdCategory
import dev.olog.core.entity.SearchResult
import dev.olog.core.entity.track.*
import dev.olog.feature.base.model.DisplayableAlbum
import dev.olog.feature.base.model.DisplayableItem
import dev.olog.feature.base.model.DisplayableTrack

fun SearchResult.toSearchDisplayableItem(context: Context): DisplayableItem {
    val subtitle = when (mediaId.category) {
        MediaIdCategory.SONGS -> context.getString(localization.R.string.search_type_track)
        MediaIdCategory.ALBUMS -> context.getString(localization.R.string.search_type_album)
        MediaIdCategory.ARTISTS -> context.getString(localization.R.string.search_type_artist)
        MediaIdCategory.PLAYLISTS -> context.getString(localization.R.string.search_type_playlist)
        MediaIdCategory.GENRES -> context.getString(localization.R.string.search_type_genre)
        MediaIdCategory.FOLDERS -> context.getString(localization.R.string.search_type_folder)
        MediaIdCategory.PODCASTS -> context.getString(localization.R.string.search_type_podcast)
        MediaIdCategory.PODCASTS_PLAYLIST -> context.getString(localization.R.string.search_type_podcast_playlist)
        MediaIdCategory.PODCASTS_ALBUMS -> context.getString(localization.R.string.search_type_podcast_album)
        MediaIdCategory.PODCASTS_ARTISTS -> context.getString(localization.R.string.search_type_podcast_artist)
        else -> throw IllegalArgumentException("invalid media id $mediaId")
    }

    val isPlayable = mediaId.isLeaf

    val layout = when (mediaId.category) {
        MediaIdCategory.ARTISTS,
        MediaIdCategory.PODCASTS_ARTISTS -> R.layout.item_search_recent_artist
        MediaIdCategory.ALBUMS,
        MediaIdCategory.PODCASTS_ALBUMS -> R.layout.item_search_recent_album
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
        )
    }
    return DisplayableAlbum(
        type = layout,
        mediaId = this.mediaId,
        title = this.title,
        subtitle = subtitle
    )
}

fun Song.toSearchDisplayableItem(): DisplayableTrack {
    return DisplayableTrack(
        type = R.layout.item_search_song,
        mediaId = getMediaId(),
        title = title,
        artist = artist,
        album = album,
        idInPlaylist = idInPlaylist,
    )
}

fun Album.toSearchDisplayableItem(): DisplayableAlbum {
    return DisplayableAlbum(
        type = R.layout.item_search_album,
        mediaId = getMediaId(),
        title = title,
        subtitle = artist
    )
}

fun Artist.toSearchDisplayableItem(): DisplayableAlbum {
    return DisplayableAlbum(
        type = R.layout.item_search_artist,
        mediaId = getMediaId(),
        title = name,
        subtitle = ""
    )
}

fun Playlist.toSearchDisplayableItem(): DisplayableAlbum {
    return DisplayableAlbum(
        type = R.layout.item_search_album,
        mediaId = getMediaId(),
        title = title,
        subtitle = ""
    )
}

fun Genre.toSearchDisplayableItem(): DisplayableAlbum {
    return DisplayableAlbum(
        type = R.layout.item_search_album,
        mediaId = getMediaId(),
        title = name,
        subtitle = ""
    )
}

fun Folder.toSearchDisplayableItem(): DisplayableAlbum {
    return DisplayableAlbum(
        type = R.layout.item_search_album,
        mediaId = getMediaId(),
        title = title,
        subtitle = ""
    )
}