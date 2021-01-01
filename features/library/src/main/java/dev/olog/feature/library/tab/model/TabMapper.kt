package dev.olog.feature.library.tab.model

import android.content.res.Resources
import androidx.annotation.LayoutRes
import dev.olog.domain.mediaid.MediaId
import dev.olog.domain.entity.track.*
import dev.olog.feature.library.R
import dev.olog.shared.android.DisplayableItemUtils
import kotlin.time.milliseconds

@LayoutRes
private fun computeAlbumLayoutId(
    mediaId: MediaId,
    spanSize: Int
): Int {
    if (spanSize == 1) {
        return if (mediaId.isAnyPodcast) R.layout.item_tab_podcast else R.layout.item_tab_podcast
    }
    if (mediaId.isArtist) {
        return R.layout.item_tab_artist
    }
    return R.layout.item_tab_album
}

internal fun Folder.toTabPresentation(
    resources: Resources,
    spanSize: Int
): TabFragmentModel.Album {
    return TabFragmentModel.Album(
        layoutId = computeAlbumLayoutId(getMediaId(), spanSize),
        mediaId = getMediaId(),
        title = title,
        subtitle = DisplayableItemUtils.readableSongCount(resources, size),
    )
}

internal fun Playlist.toTabAutoPlaylist(): TabFragmentModel.Album {
    return TabFragmentModel.Album(
        layoutId = R.layout.item_tab_auto_playlist,
        mediaId = getMediaId(),
        title = title,
        subtitle = null,
    )
}

internal fun Playlist.toTabPresentation(
    resources: Resources,
    spanSize: Int
): TabFragmentModel.Album {

    return TabFragmentModel.Album(
        layoutId = computeAlbumLayoutId(getMediaId(), spanSize),
        mediaId = getMediaId(),
        title = title,
        subtitle = DisplayableItemUtils.readableSongCount(resources, size)
    )
}

internal fun Track.toTabPresentation(): TabFragmentModel {
    if (isPodcast) {
        return TabFragmentModel.Podcast(
            mediaId = getMediaId(),
            title = title,
            artist = artist,
            album = album,
            duration = this.duration.milliseconds
        )
    }
    return TabFragmentModel.Track(
        mediaId = getMediaId(),
        title = title,
        artist = artist,
        album = album
    )
}


internal fun Album.toTabPresentation(
    spanSize: Int
): TabFragmentModel.Album {
    return TabFragmentModel.Album(
        layoutId = computeAlbumLayoutId(getMediaId(), spanSize),
        mediaId = getMediaId(),
        title = title,
        subtitle = artist
    )
}

internal fun Artist.toTabPresentation(
    resources: Resources,
    spanSize: Int
): TabFragmentModel.Album {
    val songs = DisplayableItemUtils.readableSongCount(resources, songs)

    return TabFragmentModel.Album(
        layoutId = computeAlbumLayoutId(getMediaId(), spanSize),
        mediaId = getMediaId(),
        title = name,
        subtitle = songs
    )
}


internal fun Genre.toTabPresentation(
    resources: Resources,
    spanSize: Int
): TabFragmentModel.Album {
    return TabFragmentModel.Album(
        layoutId = computeAlbumLayoutId(getMediaId(), spanSize),
        mediaId = getMediaId(),
        title = name,
        subtitle = DisplayableItemUtils.readableSongCount(resources, size)
    )
}

internal fun Album.toRecentlyPlayedDisplayableItem(): TabFragmentModel.Album {
    return TabFragmentModel.Album(
        layoutId = R.layout.item_tab_album_last_played,
        mediaId = getMediaId(),
        title = title,
        subtitle = artist
    )
}

internal fun Artist.toRecentlyPlayedDisplayableItem(resources: Resources): TabFragmentModel.Album {
    return TabFragmentModel.Album(
        layoutId = R.layout.item_tab_artist_last_played,
        mediaId = getMediaId(),
        title = name,
        subtitle = DisplayableItemUtils.readableSongCount(resources, songs)
    )
}