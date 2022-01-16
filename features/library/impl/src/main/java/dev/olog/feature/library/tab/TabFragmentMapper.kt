package dev.olog.feature.library.tab

import android.content.res.Resources
import dev.olog.core.author.Artist
import dev.olog.core.collection.Album
import dev.olog.core.folder.Folder
import dev.olog.core.genre.Genre
import dev.olog.core.playlist.Playlist
import dev.olog.core.track.Song
import dev.olog.feature.base.adapter.media.MediaListItem
import dev.olog.feature.base.model.DisplayableAlbum

fun Folder.toMediaListItem(
    resources: Resources
): MediaListItem.Collection {
    return MediaListItem.Collection(
        uri = uri,
        title = title,
        subtitle = DisplayableAlbum.readableSongCount(resources, songs)
    )
}

fun Playlist.toMediaListItem(
    resources: Resources
): MediaListItem.Collection {
    return MediaListItem.Collection(
        uri = uri,
        title = title,
        subtitle = DisplayableAlbum.readableSongCount(resources, size)
    )
}

fun Song.toMediaListItem(): MediaListItem {
    return MediaListItem.Track(
        uri = uri,
        title = title,
        author = artist,
        collection = album,
        duration = duration,
    )
}


fun Album.toMediaListItem(): MediaListItem.Collection {
    return MediaListItem.Collection(
        uri = uri,
        title = title,
        subtitle = artist
    )
}

fun Artist.toMediaListItem(
    resources: Resources,
): MediaListItem.Author {
    return MediaListItem.Author(
        uri = uri,
        title = name,
        subtitle = DisplayableAlbum.readableSongCount(resources, songs)
    )
}


fun Genre.toMediaListItem(
    resources: Resources,
): MediaListItem.Collection {
    return MediaListItem.Collection(
        uri = uri,
        title = name,
        subtitle = DisplayableAlbum.readableSongCount(resources, songs)
    )
}