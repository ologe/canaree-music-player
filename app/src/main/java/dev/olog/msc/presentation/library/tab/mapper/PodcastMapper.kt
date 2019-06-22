package dev.olog.msc.presentation.library.tab.mapper

import android.content.Context
import android.content.res.Resources
import dev.olog.core.MediaId
import dev.olog.core.entity.Podcast
import dev.olog.core.entity.PodcastAlbum
import dev.olog.core.entity.PodcastArtist
import dev.olog.core.entity.PodcastPlaylist
import dev.olog.msc.R
import dev.olog.presentation.model.DisplayableItem
import dev.olog.shared.TextUtils
import java.util.concurrent.TimeUnit

internal fun PodcastPlaylist.toTabDisplayableItem(resources: Resources): DisplayableItem {

    val size = DisplayableItem.handleSongListSize(resources, size)

    return DisplayableItem(
        R.layout.item_tab_album,
        MediaId.podcastPlaylistId(id),
        title,
        size
    )
}


internal fun PodcastPlaylist.toAutoPlaylist(): DisplayableItem {

    return DisplayableItem(
        R.layout.item_tab_auto_playlist,
        MediaId.podcastPlaylistId(id),
        title,
        ""
    )
}

internal fun Podcast.toTabDisplayableItem(context: Context): DisplayableItem {
    val artist = DisplayableItem.adjustArtist(this.artist)

    val duration = context.getString(R.string.tab_podcast_duration, TimeUnit.MILLISECONDS.toMinutes(this.duration))

    return DisplayableItem(
        R.layout.item_tab_podcast,
        MediaId.podcastId(this.id),
        title,
        artist,
        trackNumber = duration,
        isPlayable = true
    )
}

internal fun PodcastArtist.toTabDisplayableItem(resources: Resources): DisplayableItem {
    val songs = DisplayableItem.handleSongListSize(resources, songs)
    var albums = DisplayableItem.handleAlbumListSize(resources, albums)
    if (albums.isNotBlank()) albums+= TextUtils.MIDDLE_DOT_SPACED

    return DisplayableItem(
        R.layout.item_tab_artist,
        MediaId.podcastArtistId(id),
        name,
        albums + songs
    )
}


internal fun PodcastAlbum.toTabDisplayableItem(): DisplayableItem {
    return DisplayableItem(
        R.layout.item_tab_album,
        MediaId.podcastAlbumId(id),
        title,
        DisplayableItem.adjustArtist(artist)
    )
}

internal fun PodcastAlbum.toTabLastPlayedDisplayableItem(): DisplayableItem {
    return DisplayableItem(
        R.layout.item_tab_album_last_played,
        MediaId.podcastAlbumId(id),
        title,
        artist
    )
}

internal fun PodcastArtist.toTabLastPlayedDisplayableItem(resources: Resources): DisplayableItem {
    val songs = DisplayableItem.handleSongListSize(resources, songs)
    var albums = DisplayableItem.handleAlbumListSize(resources, albums)
    if (albums.isNotBlank()) albums += TextUtils.MIDDLE_DOT_SPACED

    return DisplayableItem(
        R.layout.item_tab_artist_last_played,
        MediaId.podcastArtistId(id),
        name,
        albums + songs
    )
}