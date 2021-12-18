@file:Suppress("TestFunctionName")

package dev.olog.data

import dev.olog.data.author.Artists_view
import dev.olog.data.author.Podcast_authors_view
import dev.olog.data.collection.Albums_view
import dev.olog.data.collection.Podcast_collections_view
import dev.olog.data.folder.Folders_view
import dev.olog.data.genre.Genres_view
import dev.olog.data.index.Indexed_playables
import dev.olog.data.playlist.Playlists_view
import dev.olog.data.playlist.Podcast_playlists_view

// TODO move to :data:tesing
fun IndexedPlayables(
    id: Long,
    is_podcast: Boolean,
    author_id: Long = 0,
    collection_id: Long = 0,
    title: String = "",
    author: String = "",
    album_artist: String = "",
    collection: String = "",
    duration: Long = 0,
    date_added: Long = 0,
    directory: String = "",
    path: String = "",
    disc_number: Int = 0,
    track_number: Int = 0,
) = Indexed_playables(
    id = id,
    author_id = author_id,
    collection_id = collection_id,
    title = title,
    author = author,
    album_artist = album_artist,
    collection = collection,
    duration = duration,
    date_added = date_added,
    directory = directory,
    path = path,
    disc_number = disc_number,
    track_number = track_number,
    is_podcast = is_podcast,
)

fun ArtistView(
    id: Long,
    songs: Long,
    name: String = "",
    dateAdded: Long = 0,
    directory: String = "",
) = Artists_view(
    id = id,
    name = name,
    songs = songs,
    dateAdded = dateAdded,
    directory = directory
)

fun PodcastAuthorView(
    id: Long,
    episodes: Long,
    name: String = "",
    dateAdded: Long = 0,
    directory: String = "",
) = Podcast_authors_view(
    id = id,
    name = name,
    episodes = episodes,
    dateAdded = dateAdded,
    directory = directory
)

fun AlbumView(
    id: Long,
    songs: Long,
    author_id: Long = 0,
    name: String = "",
    author: String = "",
    dateAdded: Long = 0,
    directory: String = "",
) = Albums_view(
    id = id,
    author_id = author_id,
    title = name,
    author = author,
    songs = songs,
    dateAdded = dateAdded,
    directory = directory
)

fun PodcastCollectionView(
    id: Long,
    songs: Long,
    author_id: Long = 0,
    name: String = "",
    author: String = "",
    dateAdded: Long = 0,
    directory: String = "",
) = Podcast_collections_view(
    id = id,
    author_id = author_id,
    title = name,
    author = author,
    songs = songs,
    dateAdded = dateAdded,
    directory = directory
)

fun FolderView(
    directory: String,
    songs: Long,
    dateAdded: Long = 0,
) = Folders_view(
    directory = directory,
    songs = songs,
    date_added = dateAdded
)

fun GenreView(
    id: Long,
    name: String = "",
    songs: Long = 0
) = Genres_view(
    id = id,
    name = name,
    songs = songs
)

fun PlaylistView(
    id: Long,
    title: String = "",
    songs: Long = 0
) = Playlists_view(
    id = id,
    title = title,
    songs = songs,
    path = ""
)

fun PodcastPlaylistView(
    id: Long,
    title: String = "",
    songs: Long = 0
) = Podcast_playlists_view(
    id = id,
    title = title,
    songs = songs,
    path = "",
)